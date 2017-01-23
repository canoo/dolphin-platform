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
package com.canoo.remoting.client.communication

import com.canoo.communication.common.LogConfig
import com.canoo.remoting.client.ClientPresentationModel
import com.canoo.communication.common.commands.Command
import com.canoo.communication.common.commands.CreatePresentationModelCommand
import com.canoo.communication.common.commands.GetPresentationModelCommand
import com.canoo.communication.common.commands.ValueChangedCommand

import java.util.concurrent.TimeUnit
import java.util.logging.Level

class BlindCommandBatcherTest extends GroovyTestCase {

    BlindCommandBatcher batcher

    @Override
    protected void setUp() throws Exception {
        batcher = new BlindCommandBatcher()
        batcher.deferMillis = 50
    }

    void testMultipleBlindsAreBatchedNonMerging() {
        doMultipleBlindsAreBatched()
    }
    void testMultipleBlindsAreBatchedMerging() {
        batcher.mergeValueChanges = true
        doMultipleBlindsAreBatched()
    }

    void doMultipleBlindsAreBatched() {
        assert batcher.isEmpty()
        def list = [new CommandAndHandler(), new CommandAndHandler(), new CommandAndHandler()]

        list.each { commandAndHandler -> batcher.batch(commandAndHandler) }
        assert batcher.waitingBatches.val == list
    }

    void testNonBlindForcesBatchNonMerging() {
        doNonBlindForcesBatch()
    }
    void testNonBlindForcesBatchMerging() {
        batcher.mergeValueChanges = true
        doNonBlindForcesBatch()
    }

    void doNonBlindForcesBatch() {
        assert batcher.isEmpty()
        def list = [new CommandAndHandler(), new CommandAndHandler(), new CommandAndHandler()]
        list << new CommandAndHandler(handler: new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {

            }
        })

        list.each { commandAndHandler -> batcher.batch(commandAndHandler) }
        assert batcher.waitingBatches.val == list[0..2]
        assert batcher.waitingBatches.val == [list[3]]
    }


    void testMaxBatchSizeNonMerging() {
        doMaxBatchSize()
    }
    void testMaxBatchSizeMerging() {
        batcher.mergeValueChanges = true
        doMaxBatchSize()
    }

    void doMaxBatchSize() {
        batcher.maxBatchSize = 4
        def list = [new CommandAndHandler()] * 17

        list.each { commandAndHandler -> batcher.batch(commandAndHandler) }

        4.times {
            assert batcher.waitingBatches.val.size() == 4
        }
        assert batcher.waitingBatches.val.size() == 1
        assert batcher.empty
    }

    void testMergeInOneCommand() {
        LogConfig.logOnLevel(Level.ALL)

        batcher.mergeValueChanges = true
        def list = [
          new CommandAndHandler(command: new ValueChangedCommand(attributeId: 0, oldValue: 0, newValue: 1)),
          new CommandAndHandler(command: new ValueChangedCommand(attributeId: 0, oldValue: 1, newValue: 2)),
          new CommandAndHandler(command: new ValueChangedCommand(attributeId: 0, oldValue: 2, newValue: 3)),
        ]

        list.each { commandAndHandler -> batcher.batch(commandAndHandler) }

        def nextBatch = batcher.waitingBatches.val
        assert nextBatch.size() == 1
        assert nextBatch.first().command.oldValue == 0
        assert nextBatch.first().command.newValue == 3
        assert batcher.empty

    }

    void testMergeCreatePmAfterValueChange() {

        batcher.mergeValueChanges = true
        def list = [
          new CommandAndHandler(command: new ValueChangedCommand(attributeId: 0, oldValue: 0, newValue: 1)),
          new CommandAndHandler(command: new CreatePresentationModelCommand()),
        ]

        list.each { commandAndHandler -> batcher.batch(commandAndHandler) }

        def nextBatch = batcher.waitingBatches.val
        assert nextBatch.size() == 2
        assert nextBatch[0].command instanceof ValueChangedCommand
        assert nextBatch[1].command instanceof CreatePresentationModelCommand
        assert batcher.empty

    }

    void testDropMultipleGetPmCommands() {
        Command cmd1 = new GetPresentationModelCommand(pmId: 1)
        Command cmd2 = new GetPresentationModelCommand(pmId: 1)
        OnFinishedHandler sameHandler = [onFinished: { /* do nothing*/ }] as OnFinishedHandler

        def list = [
          new CommandAndHandler(command: cmd1, handler: sameHandler),
          new CommandAndHandler(command: cmd2, handler: sameHandler), // same handler can be dropped
          new CommandAndHandler(command: cmd2, handler: null),        // null handler can be dropped
        ]

        list.each { commandAndHandler -> batcher.batch(commandAndHandler) }

        def nextBatch = batcher.waitingBatches.val
        assert nextBatch.size() == 1
        assert nextBatch[0].command instanceof GetPresentationModelCommand
        assert batcher.empty
    }

    void testVeryManyGetPmCommands() {
        def list = []
        300.times {
            Command cmd1 = new GetPresentationModelCommand(pmId: it)
            Command cmd2 = new GetPresentationModelCommand(pmId: it)
            list << new CommandAndHandler(command: cmd1, handler: null) // will be batched
            list << new CommandAndHandler(command: cmd2, handler: null) // will be dropped
        }

        list.each { commandAndHandler -> batcher.batch(commandAndHandler) }

        def resultCount = 0
        def nextBatch = batcher.waitingBatches.getVal(1, TimeUnit.SECONDS)
        while (nextBatch ) {
            resultCount += nextBatch.size()
            nextBatch = batcher.waitingBatches.getVal(100, TimeUnit.MILLISECONDS)
        }
        assert resultCount == 300
    }

}
