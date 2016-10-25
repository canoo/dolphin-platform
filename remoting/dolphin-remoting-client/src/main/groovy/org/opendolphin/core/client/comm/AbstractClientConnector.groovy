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
package org.opendolphin.core.client.comm
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import org.codehaus.groovy.runtime.StackTraceUtils
import org.opendolphin.core.Attribute
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.comm.*

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Level

@Log
abstract class AbstractClientConnector implements ClientConnector {

    boolean strictMode = true // disallow value changes that are based on improper old values
    Codec codec
    UiThreadHandler uiThreadHandler // must be set from the outside - toolkit specific

    ExecutorService backgroundExecutor = Executors.newCachedThreadPool();

    Closure onException;

    protected final ClientDolphin clientDolphin
    protected final ICommandBatcher commandBatcher

    AbstractClientConnector(ClientDolphin clientDolphin) {
        this(clientDolphin, null)
    }

    AbstractClientConnector(ClientDolphin clientDolphin, ICommandBatcher commandBatcher) {
        this.clientDolphin = clientDolphin
        this.commandBatcher = commandBatcher ?: new CommandBatcher()

        // see https://issues.apache.org/jira/browse/GROOVY-7233 and https://issues.apache.org/jira/browse/GROOVY-5438
        def log = log;
        onException = { Throwable up ->
            def out = new StringWriter()
            up.printStackTrace(new PrintWriter(out))
            log.severe("onException reached, rethrowing in UI Thread, consider setting AbstractClientConnector.onException\n${out.buffer}")

            if (uiThreadHandler) {
                uiThreadHandler.executeInsideUiThread { throw up } // not sure whether this is a good default
            } else {
                log.severe("UI Thread not defined...", up)
            }
        }

        startCommandProcessing()
    }

    protected void startCommandProcessing() {
        // see https://issues.apache.org/jira/browse/GROOVY-7233 and https://issues.apache.org/jira/browse/GROOVY-5438
        def log = log;
        backgroundExecutor.execute(new Runnable() {
            @Override
            void run() {
                while (true) {
                    doExceptionSafe(new Runnable() {
                        @Override
                        void run() {
                            List<CommandAndHandler> toProcess = commandBatcher.getWaitingBatches().getVal();
                            List<Command> commands = toProcess.collect { it.command }

                            if (log.isLoggable(Level.INFO)) {
                                log.info "C: sending batch of size " + commands.size()
                                for (command in commands) { log.info("C:           -> " + command) }
                            }

                            List<Command> answer = transmit(commands);



                            doSafelyInsideUiThread(new Runnable() {
                                @Override
                                void run() {
                                    processResults(answer, toProcess);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    protected ClientModelStore getClientModelStore() {
        clientDolphin.clientModelStore
    }

    abstract List<Command> transmit(List<Command> commands)

    @CompileStatic
    public void send(Command command, OnFinishedHandler callback = null) {
        // we have some change so regardless of the batching we may have to release a push
        if (command != pushListener) {
            release()
        }
        // we are inside the UI thread and events calls come in strict order as received by the UI toolkit
        commandBatcher.batch(new CommandAndHandler(command: command, handler: callback))
    }

    @CompileStatic
    void processResults(List<Command> response, List<CommandAndHandler> commandsAndHandlers) {
        def me = this
        // see http://jira.codehaus.org/browse/GROOVY-6946
        def commands = response?.id
        me.info "C: server responded with ${response?.size()} command(s): ${commands}"

        List<ClientPresentationModel> touchedPresentationModels = new LinkedList<ClientPresentationModel>()
        List<Map> touchedDataMaps = new LinkedList<Map>()
        for (Command serverCommand in response) {
            def touched = me.dispatchHandle serverCommand
            if (touched && touched instanceof ClientPresentationModel) {
                touchedPresentationModels << (ClientPresentationModel) touched
            } else if (touched && touched instanceof Map) {
                touchedDataMaps << (Map) touched
            }
        }
        def callback = commandsAndHandlers.first().handler // there can only be one relevant handler anyway
        // added != null check instead of using simple Groovy truth because of NPE through GROOVY-7709
        if (callback !=null) {
            callback.onFinished((List<ClientPresentationModel>) touchedPresentationModels.unique { ((ClientPresentationModel) it).id })
            if (callback instanceof OnFinishedData) {
                callback.onFinishedData(touchedDataMaps)
            }
        }
    }

    void info(Object message) {
        log.info message
    }

    Object dispatchHandle(Command command) {
        handle command
    }

    @CompileStatic
    void doExceptionSafe(Runnable processing, Runnable atLeast = null) {
        try {
            processing.run()
        } catch (e) {
            StackTraceUtils.deepSanitize(e)
            onException e
        } finally {
            if (atLeast) atLeast.run()
        }
    }

    @CompileStatic
    void doSafelyInsideUiThread(Runnable whatToDo) {
        // see https://issues.apache.org/jira/browse/GROOVY-7233 and https://issues.apache.org/jira/browse/GROOVY-5438
        def log = log;
        Runnable doInside = {
            if (uiThreadHandler) {
                uiThreadHandler.executeInsideUiThread whatToDo
            } else {
                log.warning "please provide howToProcessInsideUI handler"
                whatToDo.run()
            }
        }
        doExceptionSafe doInside
    }

    def handle(Command serverCommand) {
        log.severe "C: cannot handle unknown command '$serverCommand'"
    }

    Map handle(DataCommand serverCommand) {
        return serverCommand.data
    }

    ClientPresentationModel handle(DeletePresentationModelCommand serverCommand) {
        ClientPresentationModel model = clientDolphin.findPresentationModelById(serverCommand.pmId)
        if (!model) return null
        clientModelStore.delete(model)
        return model
    }

    ClientPresentationModel handle(DeleteAllPresentationModelsOfTypeCommand serverCommand) {
        clientDolphin.deleteAllPresentationModelsOfType(serverCommand.pmType)
        return null // we cannot really return a single pm here
    }

    @CompileStatic
    ClientPresentationModel handle(CreatePresentationModelCommand serverCommand) {
        if (clientModelStore.containsPresentationModel(serverCommand.pmId)) {
            throw new IllegalStateException("There already is a presentation model with id '$serverCommand.pmId' known to the client.")
        }
        List<ClientAttribute> attributes = []
        for (attr in serverCommand.attributes) {
            ClientAttribute attribute = new ClientAttribute(
                attr.propertyName.toString(),
                attr.value,
                attr.qualifier?.toString(),
                attr.tag ? Tag.tagFor[(String) attr.tag] : Tag.VALUE)
            if(attr.id?.toString()?.endsWith('S')) {
                attribute.id = attr.id
            }
            attribute.baseValue = attr.baseValue
            attributes << attribute
        }
        ClientPresentationModel model = new ClientPresentationModel(serverCommand.pmId, attributes)
        model.presentationModelType = serverCommand.pmType
        if (serverCommand.clientSideOnly) {
            model.clientSideOnly = true
        }
        clientModelStore.add(model)
        clientDolphin.updateQualifiers(model)
        return model
    }

    ClientPresentationModel handle(ValueChangedCommand serverCommand) {
        Attribute attribute = clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) {
            log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot update old value '$serverCommand.oldValue' to new value '$serverCommand.newValue'"
            return null
        }
        if (attribute.value?.toString() == serverCommand.newValue?.toString()) {
            return null
        }
        if (strictMode && attribute.value?.toString() != serverCommand.oldValue?.toString()) {
            // todo dk: think about sending a RejectCommand here to tell the server about a possible lost update
            log.warning "C: attribute with id '$serverCommand.attributeId' and value '$attribute.value' cannot be set to new value '$serverCommand.newValue' because the change was based on an outdated old value of '$serverCommand.oldValue'."
            return null
        }
        log.info "C: updating '$attribute.propertyName' id '$serverCommand.attributeId' from '$attribute.value' to '$serverCommand.newValue'"
        attribute.value = serverCommand.newValue
        return null // this command is not expected to be sent explicitly, so no pm needs to be returned
    }

    ClientPresentationModel handle(SwitchPresentationModelCommand serverCommand) {
        def switchPm = clientModelStore.findPresentationModelById(serverCommand.pmId)
        if (!switchPm) {
            log.warning "C: switch pm with id '$serverCommand.pmId' not found, cannot switch"
            return null
        }
        def sourcePm = clientModelStore.findPresentationModelById(serverCommand.sourcePmId)
        if (!sourcePm) {
            log.warning "C: source pm with id '$serverCommand.sourcePmId' not found, cannot switch"
            return null
        }
        switchPm.syncWith sourcePm                  // ==  clientDolphin.apply sourcePm to switchPm
        return (ClientPresentationModel) switchPm
    }

    ClientPresentationModel handle(InitializeAttributeCommand serverCommand) {
        def attribute = new ClientAttribute(serverCommand.propertyName, serverCommand.newValue, serverCommand.qualifier, serverCommand.tag)

        // todo: add check for no-value; null is a valid value
        if (serverCommand.qualifier) {
            def copies = clientModelStore.findAllAttributesByQualifier(serverCommand.qualifier)
            if (copies) {
                if (null == serverCommand.newValue) {
                    attribute.value = copies.first()?.value
                } else {
                    copies.each { attr ->
                        attr.value = attribute.value
                    }
                }
            }
        }
        ClientPresentationModel presentationModel = null
        if (serverCommand.pmId) presentationModel = clientModelStore.findPresentationModelById(serverCommand.pmId)
        // here we could have a pmType conflict and we may want to throw an Exception...
        // if there is no pmId, it is most likely an error and CreatePresentationModelCommand should have been used
        if (!presentationModel) {
            presentationModel = new ClientPresentationModel(serverCommand.pmId, [])
            presentationModel.setPresentationModelType(serverCommand.pmType)
            clientModelStore.add(presentationModel)
        }
        // if we already have the attribute, just update the value
        def existingAtt = presentationModel.getAt(serverCommand.propertyName, serverCommand.tag)
        if (existingAtt) {
            existingAtt.value = attribute.value
        } else {
            clientDolphin.addAttributeToModel(presentationModel, attribute)
        }
        clientDolphin.updateQualifiers(presentationModel)
        return presentationModel // todo dk: check and test
    }

    ClientPresentationModel handle(SavedPresentationModelNotification serverCommand) {
        if (!serverCommand.pmId) return null
        ClientPresentationModel model = clientModelStore.findPresentationModelById(serverCommand.pmId)
        if (null == model) {
            log.warning("model with id '$serverCommand.pmId' not found, cannot rebase")
            return null
        }
        model.attributes*.rebase() // rebase sends update command if needed through PCL
        return model
    }

    ClientPresentationModel handle(PresentationModelResetedCommand serverCommand) {
        if (!serverCommand.pmId) return null
        PresentationModel model = clientModelStore.findPresentationModelById(serverCommand.pmId)
        // reset locally first
        if (!model) return null
        model.attributes*.reset()
        return model
    }

    ClientPresentationModel handle(AttributeMetadataChangedCommand serverCommand) {
        ClientAttribute attribute = clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) return null
        attribute[serverCommand.metadataName] = serverCommand.value
        return null
    }

    ClientPresentationModel handle(CallNamedActionCommand serverCommand) {
        clientDolphin.send(serverCommand.actionName)
        return null
    }


    //////////////////////////////// push support ////////////////////////////////////////

    /** The named command that waits for pushes on the server side */
    NamedCommand  pushListener   = null;
    /** The signal command that publishes a "release" event on the respective bus */
    SignalCommand releaseCommand = null;

    /** whether listening for push events should be done at all. */
    protected boolean pushEnabled = false;

    /** whether we currently wait for push events (internal state) and may need to release */
    protected boolean waiting = false;

    /** listens for the pushListener to return. The pushListener must be set and pushEnabled must be true. */
    public void listen() {
        if (!pushEnabled) return // allow the loop to end
        if (waiting) return      // avoid second call while already waiting (?) -> two different push actions not supported
        waiting = true
        send(pushListener, new OnFinishedHandlerAdapter() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                // we do nothing here nor do we register a special handler.
                // The server may have sent commands, though, even CallNamedActionCommand.
                waiting = false
                listen() // not a real recursion; is added to event queue
            }
        })
    }

    /** Release the current push listener, which blocks the sending queue.
     *  Does nothing in case that the push listener is not active.
     * */
    protected void release() {
        if (!waiting) {
            return      // there is no point in releasing if we do not wait. Avoid excessive releasing.
        }
        waiting = false // release is under way
        backgroundExecutor.execute(new Runnable() {
            @Override
            void run() {
                transmit(Collections.<Command>singletonList(releaseCommand));
            }
        });
    }

    @Override
    void setPushEnabled(boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    @Override
    boolean isPushEnabled() {
        return this.pushEnabled;
    }
}
