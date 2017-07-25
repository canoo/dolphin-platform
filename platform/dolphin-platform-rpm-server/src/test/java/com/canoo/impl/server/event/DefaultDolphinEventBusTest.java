package com.canoo.impl.server.event;

import com.canoo.dp.impl.server.beans.ManagedBeanFactory;
import com.canoo.dp.impl.server.beans.PostConstructInterceptor;
import com.canoo.dp.impl.server.client.ClientSessionLifecycleHandlerImpl;
import com.canoo.dp.impl.server.client.ClientSessionProvider;
import com.canoo.dp.impl.server.client.HttpClientSessionImpl;
import com.canoo.dp.impl.server.config.RemotingConfiguration;
import com.canoo.impl.server.context.DolphinContext;
import com.canoo.impl.server.context.DolphinContextProvider;
import com.canoo.impl.server.controller.ControllerRepository;
import com.canoo.dp.impl.server.scanner.DefaultClasspathScanner;
import com.canoo.platform.core.functional.Callback;
import com.canoo.platform.core.functional.Subscription;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.server.event.DolphinEventBus;
import com.canoo.platform.server.event.Message;
import com.canoo.platform.server.event.MessageListener;
import com.canoo.platform.server.event.Topic;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultDolphinEventBusTest {

    private final static Topic<String> TEST_TOPIC = Topic.create();

    @Test
    public void TestPublishOutsideSession() {
        DolphinEventBus eventBus = create(null);
        eventBus.publish(TEST_TOPIC, "huhu");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void TestCanNotSubscribeOutsideSession() {
        DolphinEventBus eventBus = create(null);
        eventBus.subscribe(TEST_TOPIC, new MessageListener<String>() {
            @Override
            public void onMessage(Message<String> message) {
            }
        });
        Assert.fail();
    }


    @Test
    public void TestPublishInsideSession() {
        DolphinEventBus eventBus = create(createContext());
        eventBus.publish(TEST_TOPIC, "huhu");
    }

    @Test
    public void TestPublishInsideSessionCallsSubscriptionsDirectly() {
        //given
        final AtomicBoolean calledCheck = new AtomicBoolean(false);
        DolphinEventBus eventBus = create(createContext());
        eventBus.subscribe(TEST_TOPIC, new MessageListener<String>() {
            @Override
            public void onMessage(Message<String> message) {
                calledCheck.set(true);
            }
        });

        //when
        eventBus.publish(TEST_TOPIC, "huhu");

        //then
        Assert.assertTrue(calledCheck.get());
    }

    @Test
    public void TestRemoveSubscription() {
        //given
        final AtomicBoolean calledCheck = new AtomicBoolean(false);
        DolphinEventBus eventBus = create(createContext());
        Subscription subscription = eventBus.subscribe(TEST_TOPIC, new MessageListener<String>() {
            @Override
            public void onMessage(Message<String> message) {
                calledCheck.set(true);
            }
        });

        //when
        subscription.unsubscribe();
        eventBus.publish(TEST_TOPIC, "huhu");

        //then
        Assert.assertFalse(calledCheck.get());
    }

    private DefaultDolphinEventBus create(final DolphinContext context) {
        DefaultDolphinEventBus eventBus = new DefaultDolphinEventBus();
        eventBus.init(new DolphinContextProvider() {
            @Override
            public DolphinContext getContext(ClientSession clientSession) {
                return getContextById(clientSession.getId());
            }

            @Override
            public DolphinContext getContextById(String clientSessionId) {
                if (context != null && context.getId().equals(clientSessionId)) {
                    return context;
                }
                return null;
            }

            @Override
            public DolphinContext getCurrentDolphinContext() {
                return context;
            }
        }, new ClientSessionLifecycleHandlerImpl());
        return eventBus;
    }

    private final DefaultClasspathScanner classpathScanner = new DefaultClasspathScanner("com.canoo.dolphin");

    private DolphinContext createContext() {
        try {
            final ClientSession session = new HttpClientSessionImpl();
            return new DolphinContext(new RemotingConfiguration(), session, new ClientSessionProvider() {
                @Override
                public ClientSession getCurrentClientSession() {
                    return session;
                }
            }, new ManagedBeanFactoryMock(), new ControllerRepository(classpathScanner), new DestroyCallbackMock());
        } catch (Exception e) {
            throw new RuntimeException("FAIL", e);
        }
    }

    private class DestroyCallbackMock implements Callback<DolphinContext> {

        @Override
        public void call(DolphinContext dolphinContext) {

        }
    }

    private class ManagedBeanFactoryMock implements ManagedBeanFactory {

        @Override
        public void init(ServletContext servletContext) {

        }

        @Override
        public <T> T createDependendInstance(Class<T> cls) {
            return null;
        }

        @Override
        public <T> T createDependendInstance(Class<T> cls, PostConstructInterceptor<T> interceptor) {
            return null;
        }

        @Override
        public <T> void destroyDependendInstance(T instance, Class<T> cls) {

        }


    }

}
