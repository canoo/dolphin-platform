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
package com.canoo.dolphin.client.legacy.communication;

import com.canoo.dp.impl.client.legacy.communication.BlindCommandBatcher;
import com.canoo.dp.impl.client.legacy.communication.CommandAndHandler;
import com.canoo.dp.impl.client.legacy.communication.OnFinishedHandler;
import com.canoo.dp.impl.remoting.legacy.commands.CreatePresentationModelCommand;
import com.canoo.dp.impl.remoting.legacy.commands.ValueChangedCommand;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BlindCommandBatcherTest {

    @BeforeMethod
    protected void setUp() throws Exception {
        batcher = new BlindCommandBatcher();
        batcher.setDeferMillis(50);
    }

    @Test
    public void testMultipleBlindsAreBatchedNonMerging() {
        doMultipleBlindsAreBatched();
    }

    @Test
    public void testMultipleBlindsAreBatchedMerging() {
        batcher.setMergeValueChanges(true);
        doMultipleBlindsAreBatched();
    }

    public void doMultipleBlindsAreBatched() {
        Assert.assertTrue(batcher.isEmpty());
        List<CommandAndHandler> list = Arrays.asList(new CommandAndHandler(null), new CommandAndHandler(null), new CommandAndHandler(null));
        for (CommandAndHandler commandAndHandler : list) {
            batcher.batch(commandAndHandler);
        }


        try {
            Assert.assertEquals(list, batcher.getWaitingBatches().getVal());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void testNonBlindForcesBatchNonMerging() {
        doNonBlindForcesBatch();
    }

    @Test
    public void testNonBlindForcesBatchMerging() {
        batcher.setMergeValueChanges(true);
        doNonBlindForcesBatch();
    }

    public void doNonBlindForcesBatch() {
        Assert.assertTrue(batcher.isEmpty());

        List<CommandAndHandler> list = new ArrayList<CommandAndHandler>();
        list.add(new CommandAndHandler(null));
        list.add(new CommandAndHandler(null));
        list.add(new CommandAndHandler(null));
        list.add(new CommandAndHandler(null, new OnFinishedHandler() {
            @Override
            public void onFinished() {

            }

        }));
        for (CommandAndHandler commandAndHandler : list) {
            batcher.batch(commandAndHandler);
        }


        Assert.assertEquals(4, ((ArrayList<CommandAndHandler>) list).size());
        try {
            Assert.assertEquals(list.subList(0, 3), batcher.getWaitingBatches().getVal());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            Assert.assertEquals(Collections.singletonList(list.get(3)), batcher.getWaitingBatches().getVal());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }
    @Test
    public void testMaxBatchSizeNonMerging() {
        doMaxBatchSize();
    }

    @Test
    public void testMaxBatchSizeMerging() {
        batcher.setMergeValueChanges(true);
        doMaxBatchSize();
    }

    public void doMaxBatchSize() {
        //given:
        batcher.setMaxBatchSize(4);
        ArrayList<CommandAndHandler> list = new ArrayList<CommandAndHandler>();
        for (int i = 0; i < 17; i++) {
            list.add(new CommandAndHandler(null));
        }


        //when:
        for (CommandAndHandler commandAndHandler : list) {
            batcher.batch(commandAndHandler);
        }


        //then:
        try {
            Assert.assertEquals(4, batcher.getWaitingBatches().getVal().size());
            Assert.assertEquals(4, batcher.getWaitingBatches().getVal().size());
            Assert.assertEquals(4, batcher.getWaitingBatches().getVal().size());
            Assert.assertEquals(4, batcher.getWaitingBatches().getVal().size());
            Assert.assertEquals(1, batcher.getWaitingBatches().getVal().size());
            Assert.assertTrue(batcher.isEmpty());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }


    }

    @Test
    public void testMergeInOneCommand() {

        //given:
        batcher.setMergeValueChanges(true);
        List<CommandAndHandler> list = new ArrayList<CommandAndHandler>();
        ValueChangedCommand command = new ValueChangedCommand();
        command.setAttributeId("0");
        command.setNewValue(1);
        list.add(new CommandAndHandler(command));

        ValueChangedCommand command1 = new ValueChangedCommand();
        command1.setAttributeId("0");
        command1.setNewValue(2);
        list.add(new CommandAndHandler(command1));

        ValueChangedCommand command2 = new ValueChangedCommand();
        command2.setAttributeId("0");
        command2.setNewValue(3);
        list.add(new CommandAndHandler(command2));

        //when:
        for (CommandAndHandler commandAndHandler : list) {
            batcher.batch(commandAndHandler);
        }


        //then:
        try {
            List<CommandAndHandler> nextBatch = batcher.getWaitingBatches().getVal();
            Assert.assertEquals(1, nextBatch.size());
            Assert.assertEquals(ValueChangedCommand.class, nextBatch.get(0).getCommand().getClass());
            ValueChangedCommand cmd = (ValueChangedCommand) nextBatch.get(0).getCommand();
            Assert.assertEquals(3, cmd.getNewValue());
            Assert.assertTrue(batcher.isEmpty());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }


    }

    @Test
    public void testMergeCreatePmAfterValueChange() {

        //given:
        batcher.setMergeValueChanges(true);
        List<CommandAndHandler> list = new ArrayList<CommandAndHandler>();
        ValueChangedCommand command = new ValueChangedCommand();
        command.setAttributeId("0");
        command.setNewValue(1);


        list.add(new CommandAndHandler(command));
        list.add(new CommandAndHandler(new CreatePresentationModelCommand()));

        //when:
        for (CommandAndHandler commandAndHandler : list) {
            batcher.batch(commandAndHandler);
        }


        //then:
        try {
            List<CommandAndHandler> nextBatch = batcher.getWaitingBatches().getVal();
            Assert.assertEquals(2, nextBatch.size());
            Assert.assertEquals(ValueChangedCommand.class, nextBatch.get(0).getCommand().getClass());
            Assert.assertEquals(CreatePresentationModelCommand.class, nextBatch.get(1).getCommand().getClass());
            Assert.assertTrue(batcher.isEmpty());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }

    private BlindCommandBatcher batcher;
}
