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
package com.canoo.dp.impl.server.mbean;

import com.canoo.platform.core.functional.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A general MBean registry
 */
public class MBeanRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(MBeanRegistry.class);

    private static final MBeanRegistry INSTANCE = new MBeanRegistry();

    private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    private AtomicBoolean mbeanSupport = new AtomicBoolean(true);

    private AtomicLong idGenerator = new AtomicLong(0L);

    /**
     * Register the given MBean based on the given description
     * @param mBean the bean
     * @param description the description
     * @return the subscription that can be used to unregister the bean
     */
    public Subscription register(Object mBean, MBeanDescription description) {
        return register(mBean, description.getMBeanName(getNextId()));
    }

    /**
     * Register the given MBean based on the given name
     * @param mBean the bean
     * @param name the name
     * @return the subscription that can be used to unregister the bean
     */
    public Subscription register(final Object mBean, final String name){
        try {
            if (mbeanSupport.get()) {
                final ObjectName objectName = new ObjectName(name);
                server.registerMBean(mBean, objectName);
                return new Subscription() {
                    @Override
                    public void unsubscribe() {
                        try {
                            server.unregisterMBean(objectName);
                        } catch (JMException e) {
                            throw new RuntimeException("Can not unsubscribe!", e);
                        }
                    }
                };
            } else {
                return new Subscription() {
                    @Override
                    public void unsubscribe() {
                    }
                };
            }
        } catch (Exception e) {
            throw new RuntimeException("Can not register MBean!", e);
        }
    }

    /**
     * Returns true if MBean registry is supported / active
     * @return true if MBean registry is supported / active
     */
    public boolean isMbeanSupport() {
        return mbeanSupport.get();
    }

    /**
     * setter for the MBean support
     * @param mbeanSupport new state of MBean support
     */
    public void setMbeanSupport(boolean mbeanSupport) {
        this.mbeanSupport.set(mbeanSupport);
    }

    private String getNextId() {
        return  Long.toString(idGenerator.getAndIncrement());
    }

    /**
     * Returns the single instance
     * @return the single instance
     */
    public static MBeanRegistry getInstance() {
        return INSTANCE;
    }
}
