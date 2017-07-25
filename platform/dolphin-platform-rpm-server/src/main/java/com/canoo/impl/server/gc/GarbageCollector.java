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

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.impl.DolphinUtils;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.IdentitySet;
import com.canoo.dp.impl.platform.core.ReflectionHelper;
import com.canoo.dp.impl.server.config.RemotingConfiguration;
import com.canoo.platform.server.DolphinModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * The garbage collection for Dolphin Platform models. Whenever a new Dolphin bean {@link com.canoo.dolphin.mapping.DolphinBean}
 * has been created or the hierarchy in a Dolphin model changes the GC will check if the mutated models are still
 * referenced by a root model. In this case a root model is a model as it's defined as a model for a MVC group in
 * Dolphin Platform (see {@link DolphinModel}).
 */
public class GarbageCollector {

    private static final Logger LOG = LoggerFactory.getLogger(GarbageCollector.class);

    private final IdentityHashMap<Instance, Object> removeOnGC = new IdentityHashMap<>();

    private final IdentityHashMap<Object, Instance> allInstances = new IdentityHashMap<>();

    private final IdentityHashMap<Property, Instance> propertyToParent = new IdentityHashMap<>();

    private final IdentityHashMap<ObservableList, Instance> listToParent = new IdentityHashMap<>();

    private final IdentityHashMap<Class, List<Field>> propertyFieldCache = new IdentityHashMap<>();

    private final IdentityHashMap<Class, List<Field>> listFieldCache = new IdentityHashMap<>();

    private final GarbageCollectionCallback onRemoveCallback;

    private long gcCalls = 0;

    private long removedBeansCount = 0;

    private final RemotingConfiguration configuration;

    /**
     * Constructor
     *
     * @param onRemoveCallback callback that will be called for each garbage collection call.
     */
    public GarbageCollector(RemotingConfiguration configuration, GarbageCollectionCallback onRemoveCallback) {
        this.onRemoveCallback = Assert.requireNonNull(onRemoveCallback, "onRemoveCallback");
        this.configuration = Assert.requireNonNull(configuration, "configuration");
    }

    /**
     * This method must be called for each new Dolphin bean (see {@link com.canoo.dolphin.mapping.DolphinBean}).
     * Normally beans are created by {@link com.canoo.dolphin.BeanManager#create(Class)}
     *
     * @param bean     the bean that was created
     * @param rootBean if this is true the bean is handled as a root bean. This bean don't need a reference.
     */
    public synchronized void onBeanCreated(Object bean, boolean rootBean) {
        if (!configuration.isUseGc()) {
            return;
        }
        Assert.requireNonNull(bean, "bean");
        if (allInstances.containsKey(bean)) {
            throw new IllegalArgumentException("Bean instance is already managed!");
        }

        IdentitySet<Property> properties = getAllProperties(bean);
        IdentitySet<ObservableList> lists = getAllLists(bean);
        Instance instance = new Instance(bean, rootBean, properties, lists);
        allInstances.put(bean, instance);
        for (Property property : properties) {
            propertyToParent.put(property, instance);
        }
        for (ObservableList list : lists) {
            listToParent.put(list, instance);
        }

        if (!rootBean) {
            //Until the bean isn't referenced in another bean it will be removed at gc
            addToGC(instance, bean);
        }
    }

    public synchronized void onBeanRemoved(Object bean) {
        if (!configuration.isUseGc()) {
            return;
        }
        Assert.requireNonNull(bean, "bean");
        if (!allInstances.containsKey(bean)) {
            throw new IllegalArgumentException("Bean is not managed by GC");
        }

        Instance instance = allInstances.remove(bean);
        removeOnGC.remove(instance);

        IdentitySet<Property> properties = getAllProperties(bean);
        IdentitySet<ObservableList> lists = getAllLists(bean);

        for (Property property : properties) {
            propertyToParent.remove(property);
            removeReferenceAndCheckForGC(property, property.get());
        }

        for (ObservableList list : lists) {
            listToParent.remove(list);
            for (Object item : list) {
                removeReferenceAndCheckForGC(list, item);
            }
        }
    }

    /**
     * This method must be called for each value change of a {@link Property}
     *
     * @param property the property
     * @param oldValue the old value
     * @param newValue the new value
     */
    public synchronized void onPropertyValueChanged(Property property, Object oldValue, Object newValue) {
        if (!configuration.isUseGc()) {
            return;
        }
        removeReferenceAndCheckForGC(property, oldValue);

        if (newValue != null && DolphinUtils.isDolphinBean(newValue.getClass())) {
            Instance instance = getInstance(newValue);
            Reference reference = new PropertyReference(propertyToParent.get(property), property, instance);
            if (reference.hasCircularReference()) {
                throw new CircularDependencyException("Circular dependency detected!");
            }
            instance.getReferences().add(reference);
            removeFromGC(instance);
        }
    }

    /**
     * This method must be called for each item that is added to a {@link ObservableList} that is part of a Dolphin bean (see {@link com.canoo.dolphin.mapping.DolphinBean})
     *
     * @param list  the list
     * @param value the added item
     */
    public synchronized void onAddedToList(ObservableList list, Object value) {
        if (!configuration.isUseGc()) {
            return;
        }
        if (value != null && DolphinUtils.isDolphinBean(value.getClass())) {
            Instance instance = getInstance(value);
            Reference reference = new ListReference(listToParent.get(list), list, instance);
            if (reference.hasCircularReference()) {
                throw new CircularDependencyException("Circular dependency detected!");
            }
            instance.getReferences().add(reference);
            removeFromGC(instance);
        }
    }

    /**
     * This method must be called for each item that is removed to a {@link ObservableList} that is part of a Dolphin bean (see {@link com.canoo.dolphin.mapping.DolphinBean})
     *
     * @param list  the list
     * @param value the removed item
     */
    public synchronized void onRemovedFromList(ObservableList list, Object value) {
        if (!configuration.isUseGc()) {
            return;
        }
        removeReferenceAndCheckForGC(list, value);
    }

    /**
     * Calling this method triggers the garbage collection. For all dolphin beans (see {@link com.canoo.dolphin.mapping.DolphinBean}) that
     * are not referenced by a root bean (see {@link DolphinModel}) the defined {@link GarbageCollectionCallback} (see constructor)
     * will be called.
     */
    public synchronized void gc() {
        if (!configuration.isUseGc()) {
            LOG.trace("GC deactivated, no beans will be removed!");
            return;
        }

        LOG.trace("Garbage collection started! GC will remove {} beans!", removeOnGC.size());

        onRemoveCallback.onReject(removeOnGC.keySet());

        for (Map.Entry<Instance, Object> entry : removeOnGC.entrySet()) {
            Instance removedInstance = entry.getKey();
            for (Property property : removedInstance.getProperties()) {
                propertyToParent.remove(property);
            }
            for (ObservableList list : removedInstance.getLists()) {
                listToParent.remove(list);
            }
            allInstances.remove(entry.getValue());
        }

        removedBeansCount = removedBeansCount + removeOnGC.size();
        removeOnGC.clear();
        gcCalls = gcCalls + 1;

        LOG.trace("Garbage collection done! GC currently manages {} referenced beans!", allInstances.size());
    }

    public synchronized int getManagedInstancesCount() {
        return allInstances.size();
    }

    private void removeReferenceAndCheckForGC(ObservableList list, Object value) {
        Assert.requireNonNull(list, "list");
        if (value != null && DolphinUtils.isDolphinBean(value.getClass())) {
            Instance instance = getInstance(value);
            Reference toRemove = null;
            for (Reference reference : instance.getReferences()) {
                if (reference instanceof ListReference && list == ((ListReference) reference).getList()) {
                    toRemove = reference;
                    break;
                }
            }
            if (toRemove == null) {
                throw new RuntimeException("REFERENCE NOT FOUND! ERROR IN GC!!");
            } else {
                instance.getReferences().remove(toRemove);
                if (instance.getReferences().isEmpty()) {
                    addToGC(instance, value);
                }
            }
        }
    }

    private void removeReferenceAndCheckForGC(Property property, Object value) {
        Assert.requireNonNull(property, "property");
        if (value != null && DolphinUtils.isDolphinBean(value.getClass())) {
            Instance instance = getInstance(value);
            Reference toRemove = null;
            for (Reference reference : instance.getReferences()) {
                if (reference instanceof PropertyReference && property == ((PropertyReference) reference).getProperty()) {
                    toRemove = reference;
                    break;
                }
            }
            if (toRemove == null) {
                throw new RuntimeException("REFERENCE NOT FOUND! ERROR IN GC!!");
            } else {
                instance.getReferences().remove(toRemove);
                if (instance.getReferences().isEmpty()) {
                    addToGC(instance, value);
                }
            }
        }
    }

    private void addToGC(Instance instance, Object value) {

        LOG.trace("Bean of type {} added to GC and will be removed on next GC run", value.getClass());

        removeOnGC.put(instance, value);

        LOG.trace("GC will remove {} beans at next GC run", removeOnGC.size());


        for (Property property : instance.getProperties()) {
            Object propertyValue = property.get();
            if (propertyValue != null && DolphinUtils.isDolphinBean(propertyValue.getClass())) {
                Instance childInstance = getInstance(propertyValue);
                if (!childInstance.isReferencedByRoot()) {
                    addToGC(childInstance, propertyValue);
                }
            }
        }
        for (ObservableList list : instance.getLists()) {
            for (Object listValue : list) {
                if (listValue != null && DolphinUtils.isDolphinBean(listValue.getClass())) {
                    Instance childInstance = getInstance(listValue);
                    if (!childInstance.isReferencedByRoot()) {
                        addToGC(childInstance, listValue);
                    }
                }
            }
        }
    }

    private void removeFromGC(Instance instance) {
        LOG.trace("Bean of type {} removed from GC and will not be removed on next GC run", instance.getBean().getClass());

        Object removed = removeOnGC.remove(instance);

        LOG.trace("GC will remove {} beans at next GC run", removeOnGC.size());


        if (removed != null) {
            for (Property property : instance.getProperties()) {
                Object value = property.get();
                if (value != null && DolphinUtils.isDolphinBean(value.getClass())) {
                    Instance childInstance = getInstance(value);
                    removeFromGC(childInstance);
                }
            }
            for (ObservableList list : instance.getLists()) {
                for (Object value : list) {
                    if (value != null && DolphinUtils.isDolphinBean(value.getClass())) {
                        Instance childInstance = getInstance(value);
                        removeFromGC(childInstance);
                    }
                }
            }
        }
    }

    private IdentitySet<Property> getAllProperties(Object bean) {
        IdentitySet<Property> ret = new IdentitySet<>();

        List<Field> fields = propertyFieldCache.get(bean.getClass());
        if (fields == null) {
            fields = new ArrayList<>();
            for (Field field : ReflectionHelper.getInheritedDeclaredFields(bean.getClass())) {
                if (Property.class.isAssignableFrom(field.getType())) {
                    fields.add(field);
                }
            }
            propertyFieldCache.put(bean.getClass(), fields);
        }
        for (Field field : fields) {
            ret.add((Property) ReflectionHelper.getPrivileged(field, bean));
        }
        return ret;
    }

    private IdentitySet<ObservableList> getAllLists(Object bean) {
        IdentitySet<ObservableList> ret = new IdentitySet<>();

        List<Field> fields = listFieldCache.get(bean.getClass());
        if (fields == null) {
            fields = new ArrayList<>();
            for (Field field : ReflectionHelper.getInheritedDeclaredFields(bean.getClass())) {
                if (ObservableList.class.isAssignableFrom(field.getType())) {
                    fields.add(field);
                }
            }
            listFieldCache.put(bean.getClass(), fields);
        }
        for (Field field : fields) {
            ret.add((ObservableList) ReflectionHelper.getPrivileged(field, bean));
        }
        return ret;
    }

    private Instance getInstance(Object bean) {
        Instance instance = allInstances.get(bean);
        if (instance == null) {
            throw new IllegalArgumentException("Can't find reference for " + bean);
        }
        return instance;
    }

    public long getGcCalls() {
        return gcCalls;
    }

    public long getRemovedBeansCount() {
        return removedBeansCount;
    }
}
