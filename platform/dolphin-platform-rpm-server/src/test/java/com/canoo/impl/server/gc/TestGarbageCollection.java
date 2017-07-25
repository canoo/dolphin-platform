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
package com.canoo.impl.server.gc;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.config.DefaultPlatformConfiguration;
import com.canoo.dp.impl.server.config.RemotingConfiguration;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.testng.Assert.*;

public class TestGarbageCollection {

    @Test
    public void testForRootBean() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithProperties testBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(testBean, true);

        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        assertEquals(garbageCollector.getManagedInstancesCount(), 1);

        garbageCollector.onBeanRemoved(testBean);
        assertEquals(garbageCollector.getManagedInstancesCount(), 0);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        assertEquals(garbageCollector.getManagedInstancesCount(), 0);
    }

    @Test
    public void testForBean() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithProperties testBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(testBean, false);

        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(1));
        assertTrue(removedObjects.get(0) == testBean);
        assertEquals(garbageCollector.getManagedInstancesCount(), 0);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testErrorForMultipleBeanCreation() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithProperties testBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(testBean, false);
        garbageCollector.onBeanCreated(testBean, false);
    }

    @Test
    public void testIgnoreBasicProperties() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithProperties testBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(testBean, true);

        testBean.booleanProperty().set(false);
        testBean.booleanProperty().set(null);
        testBean.doubleProperty().set(0.0);
        testBean.doubleProperty().set(null);
        testBean.stringProperty().set("hello");
        testBean.stringProperty().set(null);

        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        removedObjects.clear();

        testBean.booleanProperty().set(false);
        testBean.booleanProperty().set(true);
        testBean.doubleProperty().set(0.0);
        testBean.doubleProperty().set(24.9);
        testBean.stringProperty().set("hello");
        testBean.stringProperty().set("world");

        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        removedObjects.clear();

        garbageCollector.onBeanRemoved(testBean);
        assertEquals(garbageCollector.getManagedInstancesCount(), 0);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        assertEquals(garbageCollector.getManagedInstancesCount(), 0);
    }

    @Test
    public void testIgnoreBasicLists() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists testBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(testBean, true);

        testBean.getBooleanList().add(false);
        testBean.getDoubleList().add(0.4);
        testBean.getStringList().add("Hello");

        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        removedObjects.clear();

        testBean.getBooleanList().add(true);
        testBean.getDoubleList().add(3.4);
        testBean.getStringList().add("World");

        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        removedObjects.clear();

        testBean.getBooleanList().clear();
        testBean.getDoubleList().clear();
        testBean.getStringList().clear();

        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        removedObjects.clear();

        garbageCollector.onBeanRemoved(testBean);
        assertEquals(garbageCollector.getManagedInstancesCount(), 0);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        assertEquals(garbageCollector.getManagedInstancesCount(), 0);
    }

    @Test
    public void testForBeanProperty() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithProperties parentBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        assertEquals(garbageCollector.getManagedInstancesCount(), 2);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        removedObjects.clear();

        childBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        parentBean.beanProperty().set(childBean);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        assertEquals(garbageCollector.getManagedInstancesCount(), 2);
        removedObjects.clear();

        parentBean.beanProperty().set(null);
        parentBean.beanProperty().set(childBean);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        removedObjects.clear();

        parentBean.beanProperty().set(null);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();

        garbageCollector.onBeanCreated(childBean, false);
        parentBean.beanProperty().set(childBean);
        garbageCollector.onBeanRemoved(parentBean);

        assertEquals(garbageCollector.getManagedInstancesCount(), 1);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(1));
        assertEquals(garbageCollector.getManagedInstancesCount(), 0);

    }

    @Test
    public void testForBeanList() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists parentBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();

        childBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        parentBean.getBeansList2().add(childBean);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        removedObjects.clear();

        parentBean.getBeansList2().clear();
        parentBean.getBeansList2().add(childBean);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        removedObjects.clear();

        parentBean.getBeansList2().clear();

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testCircleBySameProperty() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithProperties bean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(bean, true);

        bean.beanProperty().set(bean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testCircleBySameList() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists bean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(bean, true);

        bean.getBeansList().add(bean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testSimpleCircleByProperty() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithProperties parentBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        parentBean.beanProperty().set(childBean);
        childBean.beanProperty().set(parentBean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testSimpleCircleByList() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists parentBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        BeanWithLists childBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        parentBean.getBeansList().add(childBean);
        childBean.getBeansList().add(parentBean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testMixedCircle() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists parentBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        parentBean.getBeansList2().add(childBean);
        childBean.listBeanProperty().set(parentBean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testDeepCircleByProperty() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithProperties parentBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        BeanWithProperties wrapperBean1 = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(wrapperBean1, false);

        BeanWithProperties wrapperBean2 = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(wrapperBean2, false);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        parentBean.beanProperty().set(wrapperBean1);
        wrapperBean1.beanProperty().set(wrapperBean2);
        wrapperBean2.beanProperty().set(childBean);
        childBean.beanProperty().set(wrapperBean1);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testDeepCircleByList() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists parentBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        BeanWithLists wrapperBean1 = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(wrapperBean1, false);

        BeanWithLists wrapperBean2 = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(wrapperBean2, false);

        BeanWithLists childBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        parentBean.getBeansList().add(wrapperBean1);
        wrapperBean1.getBeansList().add(wrapperBean2);
        wrapperBean2.getBeansList().add(childBean);
        childBean.getBeansList().add(wrapperBean1);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testVeryDeepCircleByProperty() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithProperties parentBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        BeanWithProperties lastWrapperBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(lastWrapperBean, false);
        parentBean.beanProperty().set(lastWrapperBean);

        for (int i = 0; i < 1000; i++) {
            BeanWithProperties wrapperBean = new BeanWithProperties(garbageCollector);
            garbageCollector.onBeanCreated(wrapperBean, false);
            lastWrapperBean.beanProperty().set(wrapperBean);
            lastWrapperBean = wrapperBean;
        }
        lastWrapperBean.beanProperty().set(parentBean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testVeryDeepCircleByList() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists parentBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        BeanWithLists lastWrapperBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(lastWrapperBean, false);
        parentBean.getBeansList().add(lastWrapperBean);

        for (int i = 0; i < 1000; i++) {
            BeanWithLists wrapperBean = new BeanWithLists(garbageCollector);
            garbageCollector.onBeanCreated(wrapperBean, false);
            lastWrapperBean.getBeansList().add(wrapperBean);
            lastWrapperBean = wrapperBean;
        }
        lastWrapperBean.getBeansList().add(parentBean);
    }

    @Test
    public void testLargeList() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists parentBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        for (int i = 0; i < 1000; i++) {
            BeanWithLists wrapperBean = new BeanWithLists(garbageCollector);
            garbageCollector.onBeanCreated(wrapperBean, false);
            parentBean.getBeansList().add(wrapperBean);
        }

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        removedObjects.clear();

        parentBean.getBeansList().clear();

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(1000));
        removedObjects.clear();
    }

    @Test
    public void testRemoveLargeParent() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists parentBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        for (int i = 0; i < 1000; i++) {
            BeanWithLists wrapperBean = new BeanWithLists(garbageCollector);
            garbageCollector.onBeanCreated(wrapperBean, false);
            parentBean.getBeansList().add(wrapperBean);
        }

        assertEquals(garbageCollector.getManagedInstancesCount(), 1001);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        assertEquals(garbageCollector.getManagedInstancesCount(), 1001);
        removedObjects.clear();

        garbageCollector.onBeanRemoved(parentBean);
        assertEquals(garbageCollector.getManagedInstancesCount(), 1000);
        garbageCollector.gc();
        assertThat(removedObjects, hasSize(1000));
        assertEquals(garbageCollector.getManagedInstancesCount(), 0);
        removedObjects.clear();
    }

    @Test
    public void testManyObjects() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists parentBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        BeanWithLists lastWrapperBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(lastWrapperBean, false);
        parentBean.getBeansList().add(lastWrapperBean);

        int beanCount = addSomeContent(lastWrapperBean, 7, 0, garbageCollector);

        System.out.println("Added " + beanCount + " beans");

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        removedObjects.clear();
    }

    @Test
    public void testAddAndRemoveOfManyObjects() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists parentBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBean, true);

        BeanWithLists lastWrapperBean = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(lastWrapperBean, false);
        parentBean.getBeansList().add(lastWrapperBean);

        int beanCount = addSomeContent(lastWrapperBean, 7, 0, garbageCollector);

        System.out.println("Added " + beanCount + " beans");

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        removedObjects.clear();

        parentBean.getBeansList().clear();

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(beanCount + 1));
        removedObjects.clear();
    }

    @Test
    public void testMovingBeans() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists parentBeanA = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBeanA, true);

        BeanWithLists parentBeanB = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBeanB, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        parentBeanA.getBeansList2().add(childBean);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        removedObjects.clear();

        parentBeanB.getBeansList2().add(childBean);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        removedObjects.clear();

        parentBeanA.getBeansList2().remove(childBean);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        removedObjects.clear();

        parentBeanB.getBeansList2().remove(childBean);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();
    }

    @Test
    public void testSubBeans() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollector garbageCollector = createGarbageCollection(gcConsumer);

        BeanWithLists parentBeanA = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBeanA, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        BeanWithProperties childBeanB = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBeanB, false);

        childBean.beanProperty().set(childBeanB);
        parentBeanA.getBeansList2().add(childBeanB);

        parentBeanA.getBeansList2().add(childBean);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(0));
        removedObjects.clear();

        parentBeanA.getBeansList2().remove(childBean);

        garbageCollector.gc();
        assertThat(removedObjects, hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();
    }

    @Test
    public void testDeactivatedGC() {
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                fail("GC should be deactivated!");
            }
        };
        Properties properties = new Properties();
        properties.setProperty("garbageCollectionActive", "false");
        RemotingConfiguration configuration = new RemotingConfiguration(new DefaultPlatformConfiguration(properties));
        GarbageCollector garbageCollector = new GarbageCollector(configuration, gcConsumer);

        BeanWithLists parentBeanA = new BeanWithLists(garbageCollector);
        garbageCollector.onBeanCreated(parentBeanA, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBean, false);

        BeanWithProperties childBeanB = new BeanWithProperties(garbageCollector);
        garbageCollector.onBeanCreated(childBeanB, false);

        childBean.beanProperty().set(childBeanB);
        parentBeanA.getBeansList2().add(childBeanB);

        parentBeanA.getBeansList2().add(childBean);

        assertEquals(garbageCollector.getManagedInstancesCount(), 0);
        garbageCollector.gc();

        parentBeanA.getBeansList2().remove(childBean);

        assertEquals(garbageCollector.getManagedInstancesCount(), 0);
        garbageCollector.gc();
    }

    private int addSomeContent(BeanWithLists parent, int maxDeep, int currentDeep, GarbageCollector garbageCollector) {
        int addedCount = 0;
        if (currentDeep >= maxDeep) {
            return addedCount;
        }

        for (int i = 0; i < maxDeep - currentDeep; i++) {
            BeanWithLists listChild = new BeanWithLists(garbageCollector);
            garbageCollector.onBeanCreated(listChild, false);
            parent.getBeansList().add(listChild);
            addedCount++;
            addedCount = addedCount + addSomeContent(listChild, maxDeep, currentDeep + 1, garbageCollector);

            BeanWithProperties propertyChild = new BeanWithProperties(garbageCollector);
            garbageCollector.onBeanCreated(propertyChild, false);
            parent.getBeansList2().add(propertyChild);
            addedCount++;
            addedCount = addedCount + addSomeContent(propertyChild, maxDeep, currentDeep + 1, true, garbageCollector);

            BeanWithProperties propertyChild2 = new BeanWithProperties(garbageCollector);
            garbageCollector.onBeanCreated(propertyChild2, false);
            parent.getBeansList2().add(propertyChild2);
            addedCount++;
            addedCount = addedCount + addSomeContent(propertyChild2, maxDeep, currentDeep + 1, false, garbageCollector);

        }
        return addedCount;
    }

    private int addSomeContent(BeanWithProperties parent, int maxDeep, int currentDeep, boolean addListBean, GarbageCollector garbageCollector) {
        int addedCount = 0;
        if (currentDeep >= maxDeep) {
            return addedCount;
        }
        if (addListBean) {
            BeanWithLists child = new BeanWithLists(garbageCollector);
            garbageCollector.onBeanCreated(child, false);
            parent.listBeanProperty().set(child);
            addedCount++;
            addedCount = addedCount + addSomeContent(child, maxDeep, currentDeep + 1, garbageCollector);
        } else {
            BeanWithProperties child = new BeanWithProperties(garbageCollector);
            garbageCollector.onBeanCreated(child, false);
            parent.beanProperty().set(child);
            addedCount++;
            addedCount = addedCount + addSomeContent(child, maxDeep, currentDeep + 1, !addListBean, garbageCollector);
        }
        return addedCount;
    }

    private GarbageCollector createGarbageCollection(final GarbageCollectionCallback gcConsumer) {
        Assert.requireNonNull(gcConsumer, "gcConsumer");

        final RemotingConfiguration configuration = new RemotingConfiguration();
        return new GarbageCollector(configuration, gcConsumer);
    }
}
