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
package org.opendolphin.core.comm

import org.opendolphin.LogConfig
import org.opendolphin.RemotingConstants
import org.opendolphin.core.ModelStoreConfig
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.OnFinishedHandler
import org.opendolphin.core.server.*
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level
/**
 * Functional tests for the server-side state changes.
 */

class ServerPresentationModelTests extends GroovyTestCase {

    private final class SetValueCommand extends Command {}
    private final class AssertValueCommand extends Command {}
    private final class ChangeValueCommand extends Command {}
    private final class AttachListenerCommand extends Command {}
    private final class ChangeBaseValueCommand extends Command {}
    private final class CreateCommand extends Command {}
    private final class AssertVisibleCommand extends Command {}
    private final class RegisterMSLCommand extends Command {}
    private final class RemoveCommand extends Command {}
    private final class AssertValueChangeCommand extends Command {}




    volatile TestInMemoryConfig context
    DefaultServerDolphin serverDolphin
    ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin
        LogConfig.logOnLevel(Level.ALL);
    }

    @Override
    protected void tearDown() {
        assert context.done.await(10, TimeUnit.SECONDS)
    }

    void testServerModelStoreAcceptsConfig() {
        new ServerModelStore(new ModelStoreConfig())
        context.assertionsDone()
    }

    public <T extends Command> void registerAction(ServerDolphin serverDolphin, Class<T> commandType, CommandHandler<T> handler) {
        serverDolphin.getServerConnector().register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(commandType, handler);
            }
        });
    }


    void testServerPresentationModelDoesNotRejectAutoId() {
        // re-enable the shouldFail once we have proper Separation of commands and notifications
//        shouldFail IllegalArgumentException, {
        assert new ServerPresentationModel("1${RemotingConstants.SERVER_PM_AUTO_ID_SUFFIX}", [], new ServerModelStore())
//        }
        context.assertionsDone()
    }

    void testSecondServerActionCanRelyOnAttributeValueChange() {
        ClientPresentationModel model = new ClientPresentationModel("PM1", Arrays.asList(new ClientAttribute("att1", null)));
        clientDolphin.getModelStore().add(model);

        registerAction serverDolphin, SetValueCommand.class, { cmd ->
            serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").value = 1
        }

        registerAction serverDolphin, AssertValueCommand.class, { cmd ->
            assert 1 == serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").value
        }

        clientDolphin.getClientConnector().send(new SetValueCommand(), null)
        clientDolphin.getClientConnector().send new AssertValueCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                assert 1 == model.getAttribute("att1").value
                context.assertionsDone()
            }
        }
    }

    void testServerSideValueChangesUseQualifiers() {
        ClientPresentationModel model = new ClientPresentationModel("PM1", Arrays.asList(new ClientAttribute("att1", "base"), new ClientAttribute("att2", "base")));
        clientDolphin.getModelStore().add(model);

        model.getAttribute("att1").qualifier = 'qualifier'
        model.getAttribute("att2").qualifier = 'qualifier'

        registerAction serverDolphin, ChangeValueCommand.class, { cmd ->
            def at1 = serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1")
            assert at1.value == 'base'
            at1.value = 'changed'
            assert serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att2").value == 'changed'
        }

        clientDolphin.getClientConnector().send(new ChangeValueCommand(), null)

        clientDolphin.sync {
            context.assertionsDone()
        }
    }

    void testServerSideEventListenerCanChangeSelfValue() {
        ClientPresentationModel model = new ClientPresentationModel("PM1", Arrays.asList(new ClientAttribute("att1", "base")));
        clientDolphin.getModelStore().add(model);

        registerAction serverDolphin, AttachListenerCommand.class, { cmd ->
            ServerAttribute at1 = serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1")
            at1.addPropertyChangeListener("value") { event ->
                at1.setValue("changed from PCL")
            }
        }

        registerAction serverDolphin, ChangeBaseValueCommand.class, { cmd ->
            def at1 = serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1")
            assert at1.baseValue == 'base'
            at1.baseValue = 'changedBase'
            assert serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att2").baseValue == 'changedBase'
        }

        clientDolphin.sync {
            //...
        }

        clientDolphin.getClientConnector().send(new AttachListenerCommand(), null)

        clientDolphin.sync {
            model.getAttribute("att1").setValue("changed")
            clientDolphin.sync {
                assert model.getAttribute("att1").getValue() == "changed from PCL"
                context.assertionsDone()
            }
        }
    }


    void testSecondServerActionCanRelyOnPmCreate() {

        def pmWithNullId

        registerAction serverDolphin, CreateCommand.class, { cmd ->
            def dto = new DTO(new Slot("att1", 1))
            def pm = serverDolphin.getModelStore().presentationModel("PM1", null, dto)
            pmWithNullId = serverDolphin.getModelStore().presentationModel(null, "pmType", dto)
            assert pm
            assert pmWithNullId
            assert serverDolphin.getModelStore().findPresentationModelById("PM1")
            assert serverDolphin.getModelStore().findAllPresentationModelsByType("pmType").first() == pmWithNullId
        }

        registerAction serverDolphin, AssertVisibleCommand.class, { cmd ->
            assert serverDolphin.getModelStore().findPresentationModelById("PM1")
            assert serverDolphin.getModelStore().findAllPresentationModelsByType("pmType").first() == pmWithNullId
        }

        clientDolphin.getClientConnector().send(new CreateCommand(), null)
        clientDolphin.getClientConnector().send(new AssertVisibleCommand(), null)

        clientDolphin.sync {
            assert clientDolphin.getModelStore().findPresentationModelById("PM1")
            assert clientDolphin.getModelStore().findAllPresentationModelsByType("pmType").size() == 1
            println clientDolphin.getModelStore().findAllPresentationModelsByType("pmType").first().id
            context.assertionsDone()
        }
    }

    void testServerCreatedAttributeChangesValueOnTheClientSide() {

        AtomicBoolean pclReached = new AtomicBoolean(false)

        registerAction serverDolphin, CreateCommand.class, { cmd ->
            def dto = new DTO(new Slot("att1", 1))
            serverDolphin.getModelStore().presentationModel("PM1", null, dto)
            serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").addPropertyChangeListener("value",{ pclReached.set(true) })
        }

        registerAction serverDolphin, AssertValueChangeCommand.class, { cmd ->
            assert pclReached.get()
            assert serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").value == 2
            context.assertionsDone()
        }

        clientDolphin.getClientConnector().send new CreateCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                assert clientDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").value == 1
                clientDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").value = 2

                clientDolphin.getClientConnector().send(new AssertValueChangeCommand(), null)
            }
        }

    }

    void testServerSidePmRemoval() {

        ClientPresentationModel model = new ClientPresentationModel("client-side-with-id", Arrays.asList(new ClientAttribute("attr1", 1)));
        clientDolphin.getModelStore().add(model);

        registerAction serverDolphin, RemoveCommand.class, { cmd ->
            def pm = serverDolphin.getModelStore().findPresentationModelById("client-side-with-id")
            assert pm
            serverDolphin.getModelStore().remove(pm)
            assert null == serverDolphin.getModelStore().findPresentationModelById("client-side-with-id") // immediately removed on server
        }

        assert clientDolphin.getModelStore().findPresentationModelById("client-side-with-id")

        clientDolphin.getClientConnector().send new RemoveCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                assert null == clientDolphin.getModelStore().findPresentationModelById("client-side-with-id") // removed from client before callback
                context.assertionsDone()
            }
        }
    }

    void testServerSideBaseValueChange() {
        ClientPresentationModel source = new ClientPresentationModel("source", Arrays.asList(new ClientAttribute("attr1", "sourceValue")));
        clientDolphin.getModelStore().add(source);

        registerAction serverDolphin, ChangeBaseValueCommand.class, { cmd ->
            def attribute = serverDolphin.getModelStore().findPresentationModelById("source").getAttribute("attr1")
        }

        clientDolphin.getClientConnector().send new ChangeBaseValueCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                assert source.getAttribute("attr1").value     == "sourceValue"
                context.assertionsDone()
            }
        }
    }

    void testServerSideQualifierChange() {
        ClientPresentationModel source = new ClientPresentationModel("source", Arrays.asList(new ClientAttribute("attr1", "sourceValue")));
        clientDolphin.getModelStore().add(source);

        source.getAttribute("attr1").qualifier = "qualifier"

        registerAction serverDolphin, ChangeBaseValueCommand.class, { cmd ->
            def attribute = serverDolphin.getModelStore().findPresentationModelById("source").getAttribute("attr1")
            attribute.qualifier = "changed"
            // immediately applied on server
            assert attribute.qualifier == "changed"
        }

        clientDolphin.getClientConnector().send new ChangeBaseValueCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                assert source.getAttribute("attr1").value     == "sourceValue"
                assert source.getAttribute("attr1").qualifier == "changed"
                context.assertionsDone()
            }
        }
    }

    // todo dk: think about these use cases:
    // dolphin.copy(pm) on client and server (done) - todo: make js version use the same approach
    // server-side tagging

}
