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
package com.canoo.impl.server;

import com.canoo.dp.impl.remoting.PlatformRemotingConstants;
import com.canoo.dp.impl.remoting.converters.DolphinBeanConverterFactory;
import com.canoo.dp.impl.remoting.legacy.RemotingConstants;
import com.canoo.dp.impl.remoting.legacy.core.PresentationModel;
import com.canoo.dp.impl.server.legacy.ServerAttribute;
import com.canoo.dp.impl.server.legacy.ServerDolphin;
import com.canoo.dp.impl.server.legacy.ServerPresentationModel;
import com.canoo.impl.server.util.AbstractDolphinBasedTest;
import com.canoo.impl.server.util.ListReferenceModel;
import com.canoo.impl.server.util.SimpleTestModel;
import com.canoo.platform.remoting.BeanManager;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class TestObservableListSync extends AbstractDolphinBasedTest {

    private static class BasePresentationModelBuilder {

        private final String type;
        private final List<ServerAttribute> serverAttributes = new ArrayList<>();
        private final ServerDolphin dolphin;

        public BasePresentationModelBuilder(ServerDolphin dolphin, String type) {
            this.dolphin = dolphin;
            this.type = type;
            withAttribute(RemotingConstants.SOURCE_SYSTEM, RemotingConstants.SOURCE_SYSTEM_CLIENT);
        }

        public BasePresentationModelBuilder withAttribute(String name, Object value) {
            serverAttributes.add(new ServerAttribute(name, value));
            return this;
        }

        public PresentationModel create() {
            return dolphin.getModelStore().presentationModel(UUID.randomUUID().toString(), type, serverAttributes);
        }

    }

    //////////////////////////////////////////////////////////////
    // Adding, removing, and replacing all element types as user
    //////////////////////////////////////////////////////////////
    @Test
    public void addingObjectElementAsUser_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        // when :
        model.getObjectList().add(object);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) objectModel.getId())));
    }

    @Test
    public void addingObjectNullAsUser_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        model.getObjectList().add(null);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void addingPrimitiveElementAsUser_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        // when :
        model.getPrimitiveList().add(value);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) value)));
    }

    @Test
    public void addingPrimitiveNullAsUser_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        model.getPrimitiveList().add(null);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void deletingObjectElementAsUser_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);

        model.getObjectList().add(object);
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().remove(0);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingObjectNullAsUser_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getObjectList().add(null);
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().remove(0);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingPrimitiveElementAsUser_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add("Hello");
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingPrimitiveNullAsUser_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add(null);
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void replaceObjectElementAsUser_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final PresentationModel newObjectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);

        model.getObjectList().add(oldObject);
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, newObject);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newObjectModel.getId())));
    }

    @Test
    public void replaceObjectElementWithNullAsUser_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);

        model.getObjectList().add(oldObject);
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, null);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void replaceObjectNullWithElementAsUser_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final PresentationModel newObjectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        model.getObjectList().add(null);
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, newObject);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newObjectModel.getId())));
    }

    @Test
    public void replacePrimitiveElementAsUser_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye World";

        model.getPrimitiveList().add("Hello World");
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }

    @Test
    public void replacePrimitiveElementWithNullAsUser_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add("Hello World");
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, null);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void replacePrimitiveNullWithElementAsUser_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye World";

        model.getPrimitiveList().add(null);
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }


    //////////////////////////////////////////////////////////////
    // Adding elements at different positions as user
    //////////////////////////////////////////////////////////////
    @Test
    public void addingMultipleElementInEmptyListAsUser_shouldAddElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        // when :
        model.getPrimitiveList().addAll(0, Arrays.asList(newElement));

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("0").getValue(), is((Object) "42"));
        assertThat(change.getAttribute("1").getValue(), is((Object) "4711"));
        assertThat(change.getAttribute("2").getValue(), is((Object) "Hello World"));
    }

    @Test
    public void addingSingleElementInBeginningAsUser_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(0, newElement);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), is((Object) newElement));
    }

    @Test
    public void addingMultipleElementInBeginningAsUser_shouldAddElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(0, Arrays.asList(newElement));

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("0").getValue(), is((Object) "42"));
        assertThat(change.getAttribute("1").getValue(), is((Object) "4711"));
        assertThat(change.getAttribute("2").getValue(), is((Object) "Hello World"));
    }

    @Test
    public void addingSingleElementInMiddleAsUser_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(1, newElement);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), is((Object) newElement));
    }

    @Test
    public void addingMultipleElementInMiddleAsUser_shouldAddElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(1, Arrays.asList(newElement));

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("0").getValue(), is((Object) "42"));
        assertThat(change.getAttribute("1").getValue(), is((Object) "4711"));
        assertThat(change.getAttribute("2").getValue(), is((Object) "Hello World"));
    }

    @Test
    public void addingSingleElementAtEndAsUser_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(newElement);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), is((Object) newElement));
    }

    @Test
    public void addingMultipleElementAtEndAsUser_shouldAddElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(Arrays.asList(newElement));

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("0").getValue(), is((Object) "42"));
        assertThat(change.getAttribute("1").getValue(), is((Object) "4711"));
        assertThat(change.getAttribute("2").getValue(), is((Object) "Hello World"));
    }

    private void removeAllPresentationModelsOfType(ServerDolphin dolphin, String type) {
        List<ServerPresentationModel> toRemove = new ArrayList<>();
        for (ServerPresentationModel model : dolphin.getModelStore().findAllPresentationModelsByType(type)) {
            toRemove.add(model);
        }
        for (ServerPresentationModel model : toRemove) {
            dolphin.getModelStore().remove(model);
        }
    }


    //////////////////////////////////////////////////////////////
    // Removing elements from different positions as user
    //////////////////////////////////////////////////////////////
    @Test
    public void deletingSingleElementInBeginningAsUser_shouldRemoveElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test(enabled = false)
    public void deletingMultipleElementInBeginningAsUser_shouldRemoveElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(0, 3).clear();

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingSingleElementInMiddleAsUser_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(1);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test(enabled = false)
    public void deletingMultipleElementInMiddleAsUser_shouldDeleteElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(1, 4).clear();

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 4)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingSingleElementAtEndAsUser_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(2);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test(enabled = false)
    public void deletingMultipleElementAtEndAsUser_shouldAddElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(3, 6).clear();

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 6)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }


    //////////////////////////////////////////////////////////////
    // Replacing elements from different positions as user
    //////////////////////////////////////////////////////////////
    @Test
    public void replacingSingleElementAtBeginningAsUser_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }

    @Test
    public void replacingSingleElementInMiddleAsUser_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(1, newValue);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }

    @Test
    public void replacingSingleElementAtEndAsUser_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(2, newValue);

        // then :
        final List<ServerPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }


    ///////////////////////////////////////////////////////////////////
    // Adding, removing, and replacing all element types from dolphin
    ///////////////////////////////////////////////////////////////////
    @Test
    public void addingObjectElementFromDolphin_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", objectModel.getId())
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList(object)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingObjectNullFromDolphin_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.<SimpleTestModel>singletonList(null)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingPrimitiveElementFromDolphin_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", value)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(value)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingPrimitiveNullFromDolphin_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.<String>singletonList(null)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingObjectElementFromDolphin_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", objectModel.getId())
                .create();
        assertThat(model.getObjectList(), hasSize(1));

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getObjectList(), empty());
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingObjectNullFromDolphin_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getObjectList(), hasSize(1));

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getObjectList(), empty());
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingPrimitiveElementFromDolphin_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", value)
                .create();
        assertThat(model.getPrimitiveList(), hasSize(1));

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), empty());
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingPrimitiveNullFromDolphin_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getPrimitiveList(), hasSize(1));

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), empty());
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectElementFromDolphin_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);
        final PresentationModel oldObjectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final List<ServerPresentationModel> models = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName());
        final PresentationModel newObjectModel = oldObjectModel == models.get(1) ? models.get(0) : models.get(1);

        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldObjectModel.getId())
                .create();
        assertThat(model.getObjectList(), is(Collections.singletonList(oldObject)));

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("pos", 0)
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newObjectModel.getId())
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList(newObject)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectElementWithNullFromDolphin_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);
        final PresentationModel oldObjectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldObjectModel.getId())
                .create();
        assertThat(model.getObjectList(), is(Collections.singletonList(oldObject)));

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.<SimpleTestModel>singletonList(null)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectNullWithElementFromDolphin_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final PresentationModel newObjectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getObjectList(), is(Collections.<SimpleTestModel>singletonList(null)));

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newObjectModel.getId())
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList(newObject)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveElementFromDolphin_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String oldValue = "Hello";
        final String newValue = "Goodbye";

        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldValue)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(oldValue)));

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(newValue)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveElementWithNullFromDolphin_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String oldValue = "Hello";

        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldValue)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(oldValue)));

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.<String>singletonList(null)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveNullWithElementFromDolphin_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye";

        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.<String>singletonList(null)));

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(newValue)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }


    //////////////////////////////////////////////////////////////
    // Adding elements at different positions from dolphin
    //////////////////////////////////////////////////////////////
    @Test
    public void addingMultipleElementsInEmptyListFromDolphin_shouldAddElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "4711", "Hello World")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementInBeginningFromDolphin_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList(newElement, "1", "2", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsInBeginningFromDolphin_shouldAddElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "4711", "Hello World", "1", "2", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementInMiddleFromDolphin_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", newElement, "2", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsInMiddleFromDolphin_shouldAddElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 1)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "42", "4711", "Hello World", "2", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementAtEndFromDolphin_shouldAddElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 3)
                .withAttribute("to", 3)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", newElement)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsAtEndFromDolphin_shouldAddElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 3)
                .withAttribute("to", 3)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", "42", "4711", "Hello World")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }


    //////////////////////////////////////////////////////////////
    // Removing elements from different positions from dolphin
    //////////////////////////////////////////////////////////////
    @Test
    public void deletingSingleElementInBeginningFromDolphin_shouldRemoveElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("2", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementInBeginningFromDolphin_shouldRemoveElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 3)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("4", "5", "6")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingSingleElementInMiddleFromDolphin_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 2)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementInMiddleFromDolphin_shouldRemoveElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 4)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "5", "6")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingSingleElementAtEndFromDolphin_shouldDeleteElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 3)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementAtEndFromDolphin_shouldRemoveElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 4)
                .withAttribute("to", 6)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", "4")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }


    //////////////////////////////////////////////////////////////
    // Replacing elements from different positions from dolphin
    //////////////////////////////////////////////////////////////
    @Test
    public void replacingSingleElementInBeginningFromDolphin_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "2", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingMultipleElementsInBeginningFromDolphin_shouldReplaceElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 2)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "4711", "Hello World", "3", "4")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingSingleElementInMiddleFromDolphin_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 2)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "42", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingMultipleElementsInMiddleFromDolphin_shouldReplaceElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 3)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "42", "4711", "Hello World", "4")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingSingleElementAtEndFromDolphin_shouldReplaceElement() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 3)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "42")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }


    @Test
    public void replacingMultipleElementsAtEndFromDolphin_shouldReplaceElements() {
        // given :
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        removeAllPresentationModelsOfType(dolphin, PlatformRemotingConstants.LIST_SPLICE);

        // when :
        new BasePresentationModelBuilder(dolphin, PlatformRemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 4)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "42", "4711", "Hello World")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.LIST_SPLICE), empty());
    }
}
