package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.server.event.Topic;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestDolphinEventBusImpl {

    private static final Topic<String> NO_MATTER = Topic.create("noMatter");

    private static final Topic<String> CHAT_ABOUT_DOLPHIN = Topic.create("chatAboutDolphin");

    private static final Topic<String> CHAT_ABOUT_SWING = Topic.create("chatAboutSwing");


    @Test(expectedExceptions = {IllegalStateException.class})
    public void testSubscribeWithoutDolphinSession() {
        DolphinEventBus dolphinEventBus = createBus(null);
        dolphinEventBus.subscribe(NO_MATTER, new MessageListener<String>() {
            @Override
            public void onMessage(Message<String> message) {

            }
        });
    }

    @Test(expectedExceptions = {IllegalStateException.class})
    public void testLongpollWithoutDolphinSession() throws InterruptedException {
        DolphinEventBusImpl dolphinEventBus = createBus(null);
        dolphinEventBus.longPoll();
    }

    @Test
    public void testPublishEvent() throws InterruptedException {
        final DolphinEventBus horst = createAndStartLongPoll("session1");

        final CountDownLatch latch = new CountDownLatch(2);
        final Set<Object> messsages = new HashSet<>();
        horst.subscribe(CHAT_ABOUT_DOLPHIN, new MessageListener() {
            @Override
            public void onMessage(Message message) {
                assertEquals(CHAT_ABOUT_DOLPHIN, message.getTopic());
                messsages.add(message.getData());
                latch.countDown();
            }
        });
        horst.subscribe(CHAT_ABOUT_SWING, new MessageListener() {
            @Override
            public void onMessage(Message message) {
                assertEquals(CHAT_ABOUT_SWING, message.getTopic());
                messsages.add(message.getData());
                latch.countDown();
            }
        });
        horst.publish(CHAT_ABOUT_SWING, "old school");
        horst.publish(CHAT_ABOUT_DOLPHIN, "trendy");

        latch.await(500, MILLISECONDS);

        assertEquals(2, messsages.size());
        assertTrue(messsages.contains("trendy"));
        assertTrue(messsages.contains("old school"));
    }

    @Test
    public void testUnsubscribe() throws InterruptedException {
        final DolphinEventBus horst = createAndStartLongPoll("session1");

        final AtomicInteger atomicInteger = new AtomicInteger();
        Subscription subscription = horst.subscribe(CHAT_ABOUT_DOLPHIN, new MessageListener() {
            @Override
            public void onMessage(Message message) {
                assertEquals(CHAT_ABOUT_DOLPHIN, message.getTopic());
                atomicInteger.incrementAndGet();
            }
        });

        horst.publish(CHAT_ABOUT_DOLPHIN, "trendy");
        Thread.sleep(200);
        assertEquals(1, atomicInteger.get());

        horst.publish(CHAT_ABOUT_DOLPHIN, "trendy");
        Thread.sleep(200);
        assertEquals(2, atomicInteger.get());

        subscription.unsubscribe();

        horst.publish(CHAT_ABOUT_DOLPHIN, "trendy");
        Thread.sleep(200);
        assertEquals(2, atomicInteger.get());

    }

    @Test
    public void testUnsubscribeSession() throws InterruptedException {
        final DolphinEventBusImpl horst = createAndStartLongPoll("session1");

        final AtomicInteger atomicInteger = new AtomicInteger();
        horst.subscribe(CHAT_ABOUT_DOLPHIN, new MessageListener() {
            @Override
            public void onMessage(Message message) {
                assertEquals(CHAT_ABOUT_DOLPHIN, message.getTopic());
                atomicInteger.incrementAndGet();
            }
        });
        horst.publish(CHAT_ABOUT_DOLPHIN, "trendy");
        Thread.sleep(200);
        assertEquals(1, atomicInteger.get());

        horst.unsubscribeSession("session1");
        horst.publish(CHAT_ABOUT_DOLPHIN, "trendy");
        Thread.sleep(200);
        assertEquals(1, atomicInteger.get());
    }

    private DolphinEventBusImpl createAndStartLongPoll(String dolphinId) throws InterruptedException {
        final DolphinEventBusImpl horst = createBus(dolphinId);
        startLongPoll(horst);
        return horst;
    }


    private void startLongPoll(final DolphinEventBusImpl eventBus) throws InterruptedException {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                eventBus.longPoll();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();
        //make sure that longPoll is called before returning
        Thread.sleep(100);
    }

    private DolphinEventBusImpl createBus(final String dolphinId) {
        return new DolphinEventBusImpl() {
            @Override
            protected String getDolphinId() {
                return dolphinId;
            }
        };
    }
}
