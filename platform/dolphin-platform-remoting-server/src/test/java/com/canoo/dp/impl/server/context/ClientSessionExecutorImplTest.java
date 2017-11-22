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
package com.canoo.dp.impl.server.context;

import com.canoo.platform.remoting.server.ClientSessionExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ClientSessionExecutorImplTest {

    @Test
    public void testCreation() {
        try {
            ClientSessionExecutor executor = new ClientSessionExecutorImpl(Executors.newSingleThreadExecutor());
        } catch (Exception e) {
            Assert.fail("Can not create executor", e);
        }
    }

    @Test
    public void testInvalidCreation() {
        try {
            ClientSessionExecutor executor = new ClientSessionExecutorImpl(null);
            Assert.fail("Creating executor with null value should not be possible!");
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void testPassingTask() {
        //given:
        ClientSessionExecutor executor = new ClientSessionExecutorImpl(Executors.newSingleThreadExecutor());

        //then:
        executor.runLaterInClientSession(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Test
    public void testPassingInvalidTask() {
        //given:
        ClientSessionExecutor executor = new ClientSessionExecutorImpl(Executors.newSingleThreadExecutor());

        //then:
        try {
            executor.runLaterInClientSession(null);
            Assert.fail("Passing a null value should not be possible!");
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void testIfExceptionWillThrown() throws ExecutionException, InterruptedException {
        final String message = "Test if this exception will thrown";
        final ClientSessionExecutor executor = new ClientSessionExecutorImpl(Executors.newSingleThreadExecutor());

        try {
            executor.runLaterInClientSession(new Runnable() {
                @Override
                public void run() {
                    throw new RuntimeException(message);
                }
            }).get();
        } catch (ExecutionException e) {
            Assert.assertTrue(e.getCause() instanceof RuntimeException);
            Assert.assertEquals(e.getCause().getMessage(), message);
        }

    }

}
