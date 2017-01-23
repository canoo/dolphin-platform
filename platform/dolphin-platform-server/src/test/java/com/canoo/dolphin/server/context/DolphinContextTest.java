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
package com.canoo.dolphin.server.context;

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.container.ModelInjector;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.impl.ClasspathScanner;
import com.canoo.common.Callback;
import com.canoo.remoting.server.communication.CommandHandler;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class DolphinContextTest {

    @Test
    public void testUniqueId() {
        //given:
        List<DolphinContext> contextList = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
            DolphinContext dolphinContext = createContext();
            contextList.add(dolphinContext);
        }

        //then:
        assertEquals(contextList.size(), 1000);
        while (!contextList.isEmpty()) {
            DolphinContext dolphinContext = contextList.remove(0);
            for(DolphinContext toCompare : contextList) {
                assertFalse(dolphinContext.getId().equals(toCompare.getId()));
                assertTrue(dolphinContext.hashCode() != toCompare.hashCode());
                assertFalse(dolphinContext.equals(toCompare));
            }
        }
    }

    @Test
    public void testUniqueBeanManager() {
        //given:
        List<DolphinContext> contextList = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            DolphinContext dolphinContext = createContext();
            contextList.add(dolphinContext);
        }

        //then:
        while (!contextList.isEmpty()) {
            DolphinContext dolphinContext = contextList.remove(0);
            for(DolphinContext toCompare : contextList) {
                assertFalse(dolphinContext.getBeanManager().equals(toCompare.getBeanManager()));
            }
        }
    }

    @Test
    public void testUniqueDolphin() {
        //given:
        List<DolphinContext> contextList = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            DolphinContext dolphinContext = createContext();
            contextList.add(dolphinContext);
        }

        //then:
        while (!contextList.isEmpty()) {
            DolphinContext dolphinContext = contextList.remove(0);
            for(DolphinContext toCompare : contextList) {
                assertFalse(dolphinContext.getDolphin().equals(toCompare.getDolphin()));
            }
        }
    }

    @Test
    public void testUniqueDolphinSession() {
        //given:
        List<DolphinContext> contextList = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            DolphinContext dolphinContext = createContext();
            contextList.add(dolphinContext);
        }

        //then:
        while (!contextList.isEmpty()) {
            DolphinContext dolphinContext = contextList.remove(0);
            for(DolphinContext toCompare : contextList) {
                assertFalse(dolphinContext.getDolphinSession().equals(toCompare.getDolphinSession()));
            }
        }
    }

    @Test
    public void testGetterReturnValue() {
        //given:
        DolphinContext dolphinContext = createContext();

        //then:
        assertNotNull(dolphinContext.getId());
        assertNotNull(dolphinContext.getBeanManager());
        assertNotNull(dolphinContext.getDolphinSession());
        assertNotNull(dolphinContext.getDolphin());
    }

    @Test
    public void testNewDolphinCommands() {
        //given:
        DolphinContext dolphinContext = createContext();

        //then:
        Map<String, List<CommandHandler>> dolphinActions = dolphinContext.getDolphin().getServerConnector().getRegistry().getActions();
        assertNotNull(dolphinActions.containsKey(PlatformConstants.INIT_CONTEXT_COMMAND_NAME));
        assertNotNull(dolphinActions.containsKey(PlatformConstants.DESTROY_CONTEXT_COMMAND_NAME));
        assertNotNull(dolphinActions.containsKey(PlatformConstants.REGISTER_CONTROLLER_COMMAND_NAME));
        assertNotNull(dolphinActions.containsKey(PlatformConstants.DESTROY_CONTROLLER_COMMAND_NAME));
        assertNotNull(dolphinActions.containsKey(PlatformConstants.CALL_CONTROLLER_ACTION_COMMAND_NAME));
        assertNotNull(dolphinActions.containsKey(PlatformConstants.POLL_EVENT_BUS_COMMAND_NAME));
        assertNotNull(dolphinActions.containsKey(PlatformConstants.RELEASE_EVENT_BUS_COMMAND_NAME));
    }

    private final ClasspathScanner classpathScanner = new ClasspathScanner("com.canoo.dolphin");

    private DolphinContext createContext() {
        return new DolphinContext(new DolphinPlatformConfiguration(), new DolphinContextProvider() {
            @Override
            public DolphinContext getCurrentContext() {
                return null;
            }

            @Override
            public DolphinSession getCurrentDolphinSession() {
                return null;
            }
        }, new ContainerManagerMock(), new ControllerRepository(classpathScanner), new DefaultOpenDolphinFactory(), new DestroyCallbackMock(), new DestroyCallbackMock());
    }

    private class DestroyCallbackMock implements Callback<DolphinContext> {

        @Override
        public void call(DolphinContext dolphinContext) {

        }
    }

    private class ContainerManagerMock implements ContainerManager {

        @Override
        public void init(ServletContext servletContext) {

        }

        @Override
        public <T> T createManagedController(Class<T> controllerClass, ModelInjector modelInjector) {
            return null;
        }

        @Override
        public <T> T createListener(Class<T> listenerClass) {
            return null;
        }

        @Override
        public void destroyController(Object instance, Class controllerClass) {

        }
    }

}
