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
package org.opendolphin.core.client.comm

import org.opendolphin.LogConfig
import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.comm.ValueChangedCommand

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
        def list = [new CommandAndHandler(null), new CommandAndHandler(null), new CommandAndHandler(null)]

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
        def list = [new CommandAndHandler(null), new CommandAndHandler(null), new CommandAndHandler(null)]
        list << new CommandAndHandler(null, new OnFinishedHandler() {

            @Override
            void onFinished() {

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
        def list = [new CommandAndHandler(null)] * 17

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
          new CommandAndHandler(new ValueChangedCommand(attributeId: 0, oldValue: 0, newValue: 1)),
          new CommandAndHandler(new ValueChangedCommand(attributeId: 0, oldValue: 1, newValue: 2)),
          new CommandAndHandler(new ValueChangedCommand(attributeId: 0, oldValue: 2, newValue: 3)),
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
          new CommandAndHandler(new ValueChangedCommand(attributeId: 0, oldValue: 0, newValue: 1)),
          new CommandAndHandler(new CreatePresentationModelCommand()),
        ]

        list.each { commandAndHandler -> batcher.batch(commandAndHandler) }

        def nextBatch = batcher.waitingBatches.val
        assert nextBatch.size() == 2
        assert nextBatch[0].command instanceof ValueChangedCommand
        assert nextBatch[1].command instanceof CreatePresentationModelCommand
        assert batcher.empty

    }

}
