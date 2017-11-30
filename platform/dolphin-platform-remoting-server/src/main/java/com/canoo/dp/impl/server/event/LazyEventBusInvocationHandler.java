package com.canoo.dp.impl.server.event;

import com.canoo.dp.impl.server.bootstrap.PlatformBootstrap;
import com.canoo.platform.remoting.server.event.RemotingEventBus;
import com.canoo.platform.server.spi.ServerCoreComponents;
import org.apiguardian.api.API;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class LazyEventBusInvocationHandler implements InvocationHandler {

    private final static String DUMMY_OBJECT = "";

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final ServerCoreComponents serverCoreComponents = PlatformBootstrap.getServerCoreComponents();
        if(serverCoreComponents != null) {
            final RemotingEventBus instance = serverCoreComponents.getInstance(RemotingEventBus.class).orElse(null);
            if (instance != null) {
                return method.invoke(instance, args);
            }
        }
        if(method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(DUMMY_OBJECT, args);
        }
        if (method.getName().equals("subscribe")) {
            throw new IllegalStateException("Subscription can only be done from Dolphin Context! Current thread: " + Thread.currentThread().getName());
        } else {
            return null;
        }
    }
}
