/*
 * Copyright 2015-2017 Canoo Engineering AG.
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
package com.canoo.dolphin.samples.processmonitor;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.javafx.binding.FXBinder;
import com.canoo.dolphin.client.javafx.binding.FXWrapper;
import com.canoo.dolphin.client.javafx.view.AbstractViewBinder;
import com.canoo.dolphin.samples.processmonitor.model.ProcessBean;
import com.canoo.dolphin.samples.processmonitor.model.ProcessListBean;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import static com.canoo.dolphin.samples.processmonitor.ProcessMonitorConstants.CONTROLLER_NAME;

public class ProcessMonitorView extends AbstractViewBinder<ProcessListBean> {

    private TableView<ProcessBean> table;


    public ProcessMonitorView(ClientContext clientContext) {
        super(clientContext, CONTROLLER_NAME);
        table = new TableView<>();

        TableColumn<ProcessBean, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(e -> FXWrapper.wrapObjectProperty(e.getValue().processIDProperty()));

        TableColumn<ProcessBean, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(e -> FXWrapper.wrapObjectProperty(e.getValue().nameProperty()));

        TableColumn<ProcessBean, String> cpuColumn = new TableColumn<>("CPU %");
        cpuColumn.setCellValueFactory(e -> FXWrapper.wrapObjectProperty(e.getValue().cpuPercentageProperty()));

        TableColumn<ProcessBean, String> memoryColumn = new TableColumn<>("Memory %");
        memoryColumn.setCellValueFactory(e -> FXWrapper.wrapObjectProperty(e.getValue().memoryPercentageProperty()));

        table.getColumns().addAll(idColumn, nameColumn, cpuColumn, memoryColumn);
    }

    @Override
    protected void init() {
        FXBinder.bind(table.getItems()).bidirectionalTo(getModel().getItems());
    }

    @Override
    public Node getRootNode() {
        return table;
    }

}
