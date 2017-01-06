/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.samples.processmonitor.model.ProcessBean;
import com.canoo.dolphin.samples.processmonitor.model.ProcessListBean;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.session.DolphinSession;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.canoo.dolphin.samples.processmonitor.ProcessMonitorConstants.CONTROLLER_NAME;

@DolphinController(CONTROLLER_NAME)
public class ProcessMonitorController {

    private static DecimalFormat format = new DecimalFormat("0.00");

    @Inject
    private BeanManager beanManager;

    @Inject
    private DolphinSession session;

    @DolphinModel
    private ProcessListBean processList;

    private Future<Void> executor;

    private OperatingSystem os;

    private GlobalMemory memory;

    @PostConstruct
    public void onInit() {
        SystemInfo si = new SystemInfo();
        os = si.getOperatingSystem();
        memory = si.getHardware().getMemory();

        update();

        executor = Executors.newSingleThreadExecutor().submit(() -> {
            boolean running = true;
            while(running) {
                try {
                    Thread.sleep(250);
                    session.runLater(() -> update());
                } catch (InterruptedException e) {
                    running =false;
                }
            }
            return null;
        });
    }

    @PreDestroy
    public void onDestroy() {
        Optional.ofNullable(executor).ifPresent(e -> e.cancel(true));
    }


    private void update() {


        List<OSProcess> procs = Arrays.asList(os.getProcesses(10, OperatingSystem.ProcessSort.CPU));
        for (OSProcess process : procs) {
            ProcessBean bean = null;
            if(processList.getItems().size() <= procs.indexOf(process)) {
                bean = beanManager.create(ProcessBean.class);
                processList.getItems().add(bean);
            } else {
                bean = processList.getItems().get(procs.indexOf(process));
            }



            bean.setProcessID(new Integer(process.getProcessID()).toString());
            bean.setCpuPercentage(format.format(100d * (process.getKernelTime() + process.getUserTime()) / process.getUpTime()));
            bean.setMemoryPercentage(format.format(100d * process.getResidentSetSize() / memory.getTotal()));
            bean.setName(process.getName());
        }
    }

}
