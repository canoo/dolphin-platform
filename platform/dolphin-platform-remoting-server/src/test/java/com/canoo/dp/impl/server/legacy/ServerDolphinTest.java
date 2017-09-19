package com.canoo.dp.impl.server.legacy;

import com.canoo.dp.impl.remoting.legacy.commands.Command;
import com.canoo.dp.impl.remoting.legacy.core.PresentationModel;
import com.canoo.dp.impl.remoting.legacy.core.ModelStoreEvent;
import com.canoo.dp.impl.remoting.legacy.core.ModelStoreListener;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerDolphinTest {

    private DefaultServerDolphin dolphin;

    @BeforeMethod
    public void setUp() throws Exception {
        dolphin = new DefaultServerDolphin(new ServerModelStore(), new ServerConnector());
        dolphin.getModelStore().setCurrentResponse(new ArrayList<Command>());
    }

    @Test
    public void testListPresentationModels() {
        Assert.assertTrue(dolphin.getModelStore().listPresentationModelIds().isEmpty());
        Assert.assertTrue(dolphin.getModelStore().listPresentationModels().isEmpty());
        Assert.assertTrue(dolphin.getModelStore().findAllAttributesByQualifier("no-such-qualifier").isEmpty());
        Assert.assertTrue(dolphin.getModelStore().findAllPresentationModelsByType("no-such-type").isEmpty());

        ServerPresentationModel pm1 = new ServerPresentationModel("first", new ArrayList(), dolphin.getModelStore());
        dolphin.getModelStore().add(pm1);

        Assert.assertEquals(Collections.singleton("first"), dolphin.getModelStore().listPresentationModelIds());
        Assert.assertEquals(1, dolphin.getModelStore().listPresentationModelIds().size());
        Assert.assertEquals(pm1, dolphin.getModelStore().listPresentationModels().iterator().next());

        ServerPresentationModel pm2 = new ServerPresentationModel("second", new ArrayList(), dolphin.getModelStore());
        dolphin.getModelStore().add(pm2);

        Assert.assertEquals(2, dolphin.getModelStore().listPresentationModelIds().size());
        Assert.assertEquals(2, dolphin.getModelStore().listPresentationModels().size());


        for (String id : dolphin.getModelStore().listPresentationModelIds()) {
            PresentationModel model = dolphin.getModelStore().findPresentationModelById(id);
            Assert.assertNotNull(model);
            Assert.assertTrue(dolphin.getModelStore().listPresentationModels().contains(model));
        }


    }

    @Test
    public void testAddRemoveModelStoreListener() {
        final AtomicInteger typedListenerCallCount = new AtomicInteger(0);
        final AtomicInteger listenerCallCount = new AtomicInteger(0);
        ModelStoreListener listener = new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                listenerCallCount.incrementAndGet();
            }

        };
        ModelStoreListener typedListener = new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                typedListenerCallCount.incrementAndGet();
            }

        };
        dolphin.getModelStore().addModelStoreListener("person", typedListener);
        dolphin.getModelStore().addModelStoreListener(listener);
        dolphin.getModelStore().add(new ServerPresentationModel("p1", Collections.<ServerAttribute>emptyList(), dolphin.getModelStore()));
        ServerPresentationModel modelWithType = new ServerPresentationModel("person1", Collections.<ServerAttribute>emptyList(), dolphin.getModelStore());
        modelWithType.setPresentationModelType("person");
        dolphin.getModelStore().add(modelWithType);
        dolphin.getModelStore().add(new ServerPresentationModel("p2", Collections.<ServerAttribute>emptyList(), dolphin.getModelStore()));
        dolphin.getModelStore().removeModelStoreListener("person", typedListener);
        dolphin.getModelStore().removeModelStoreListener(listener);
        Assert.assertEquals(3, listenerCallCount.get());
        Assert.assertEquals(1, typedListenerCallCount.get());
    }

    @Test
    public void testAddModelStoreListenerWithClosure() {
        final AtomicInteger typedListenerCallCount = new AtomicInteger(0);
        final AtomicInteger listenerCallCount = new AtomicInteger(0);
        ModelStoreListener listener = new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                listenerCallCount.incrementAndGet();
            }

        };
        ModelStoreListener typedListener = new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                typedListenerCallCount.incrementAndGet();
            }

        };
        dolphin.getModelStore().addModelStoreListener("person", typedListener);
        dolphin.getModelStore().addModelStoreListener(listener);
        dolphin.getModelStore().add(new ServerPresentationModel("p1", Collections.<ServerAttribute>emptyList(), dolphin.getModelStore()));
        ServerPresentationModel modelWithType = new ServerPresentationModel("person1", Collections.<ServerAttribute>emptyList(), dolphin.getModelStore());
        modelWithType.setPresentationModelType("person");
        dolphin.getModelStore().add(modelWithType);
        dolphin.getModelStore().add(new ServerPresentationModel("p2", Collections.<ServerAttribute>emptyList(), dolphin.getModelStore()));
        Assert.assertEquals(3, listenerCallCount.get());
        Assert.assertEquals(1, typedListenerCallCount.get());
    }

    @Test
    public void testHasModelStoreListener() {
        ModelStoreListener listener = getListener();
        Assert.assertFalse(dolphin.getModelStore().hasModelStoreListener(null));
        Assert.assertFalse(dolphin.getModelStore().hasModelStoreListener(listener));
        dolphin.getModelStore().addModelStoreListener(listener);
        Assert.assertTrue(dolphin.getModelStore().hasModelStoreListener(listener));
        listener = getListener();
        dolphin.getModelStore().addModelStoreListener("person", listener);
        Assert.assertFalse(dolphin.getModelStore().hasModelStoreListener("car", listener));
        Assert.assertTrue(dolphin.getModelStore().hasModelStoreListener("person", listener));
    }

    @Test
    public void testRegisterDefaultActions() {
        dolphin.getServerConnector().registerDefaultActions();
        int numDefaultActions = dolphin.getServerConnector().getRegistrationCount();

        // multiple calls should not lead to multiple initializations
        dolphin.getServerConnector().registerDefaultActions();
        Assert.assertEquals(numDefaultActions, dolphin.getServerConnector().getRegistrationCount());
    }

    private ModelStoreListener getListener() {
        return new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                // do nothing
            }

        };
    }
}
