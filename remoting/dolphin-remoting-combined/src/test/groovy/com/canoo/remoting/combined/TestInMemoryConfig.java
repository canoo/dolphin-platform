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
package com.canoo.remoting.combined;

import com.canoo.remoting.combined.DefaultInMemoryConfig;
import com.canoo.remoting.client.communication.RunLaterUiThreadHandler;

import java.util.concurrent.CountDownLatch;

public class TestInMemoryConfig extends DefaultInMemoryConfig {


    /**
     * needed since tests should run fully asynchronous but we have to wait at the end of the test
     */
    private CountDownLatch done = new CountDownLatch(1);

    public TestInMemoryConfig() {
        getServerDolphin().registerDefaultActions();
        getClientConnector().setSleepMillis(0);
        getClientConnector().setUiThreadHandler(new RunLaterUiThreadHandler());
    }

    public void assertionsDone() {
        done.countDown();
    }

    /**
     * make sure we continue only after all previous commands have been executed
     */
    public void syncPoint(final int soManyRoundTrips) {
        if (soManyRoundTrips < 1) {
            return;

        }

        final CountDownLatch latch = new CountDownLatch(1);
        getClientDolphin().sync(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                syncPoint((int) soManyRoundTrips - 1);
            }

        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CountDownLatch getDone() {
        return done;
    }

}
