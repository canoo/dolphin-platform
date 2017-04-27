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
package com.canoo.dolphin.client;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.client.util.ListReferenceModel;
import com.canoo.dolphin.client.util.SimpleTestModel;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.impl.converters.DolphinBeanConverterFactory;
import mockit.Mocked;
import org.opendolphin.RemotingConstants;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestObservableListSync extends AbstractDolphinBasedTest {

    private static class PresentationModelBuilder {

        private final List<ClientAttribute> attributes = new ArrayList<>();
        private final ClientDolphin dolphin;
        private final String type;

        public PresentationModelBuilder(ClientDolphin dolphin, String type) {
            this.dolphin = dolphin;
            this.type = type;
            this.attributes.add(new ClientAttribute(RemotingConstants.SOURCE_SYSTEM, RemotingConstants.SOURCE_SYSTEM_SERVER));
        }

        public PresentationModelBuilder withAttribute(String name, Object value) {
            attributes.add(new ClientAttribute(name, value));
            return this;
        }

        public PresentationModel create() {
            ClientPresentationModel result = new ClientPresentationModel(UUID.randomUUID().toString(), attributes);
            result.setPresentationModelType(type);
            dolphin.getModelStore().add(result);
            return result;
        }

    }

    //////////////////////////////////////////////////////////////
    // Adding, removing, and replacing all element types as user
    //////////////////////////////////////////////////////////////
    @Test
    public void addingObjectElementAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        // when :
        model.getObjectList().add(object);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void addingObjectNullAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        model.getObjectList().add(null);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void addingPrimitiveElementAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        // when :
        model.getPrimitiveList().add(value);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void addingPrimitiveNullAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        model.getPrimitiveList().add(null);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void deletingObjectElementAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);

        model.getObjectList().add(object);
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getObjectList().remove(0);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingObjectNullAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getObjectList().add(null);
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getObjectList().remove(0);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingPrimitiveElementAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add("Hello");
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingPrimitiveNullAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add(null);
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void replaceObjectElementAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final PresentationModel newObjectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);

        model.getObjectList().add(oldObject);
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, newObject);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void replaceObjectElementWithNullAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);

        model.getObjectList().add(oldObject);
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, null);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void replaceObjectNullWithElementAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final PresentationModel newObjectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        model.getObjectList().add(null);
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, newObject);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void replacePrimitiveElementAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye World";

        model.getPrimitiveList().add("Hello World");
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void replacePrimitiveElementWithNullAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add("Hello World");
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, null);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void replacePrimitiveNullWithElementAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye World";

        model.getPrimitiveList().add(null);
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void addingMultipleElementsInEmptyListAsUser_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        // when :
        model.getPrimitiveList().addAll(0, Arrays.asList(newElement));

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void addingSingleElementInBeginningAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(0, newElement);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void addingMultipleElementsInBeginningAsUser_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(0, Arrays.asList(newElement));

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void addingSingleElementInMiddleAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(1, newElement);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void addingMultipleElementsInMiddleAsUser_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(1, Arrays.asList(newElement));

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void addingSingleElementAtEndAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(newElement);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void addingMultipleElementsAtEndAsUser_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(Arrays.asList(newElement));

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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


    //////////////////////////////////////////////////////////////
    // Removing elements from different positions as user
    //////////////////////////////////////////////////////////////
    @Test
    public void deletingSingleElementInBeginningAsUser_shouldRemoveElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void deletingMultipleElementInBeginningAsUser_shouldRemoveElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(0, 3).clear();

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingSingleElementInMiddleAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(1);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void deletingMultipleElementInMiddleAsUser_shouldDeleteElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(1, 4).clear();

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 4)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingSingleElementAtEndAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(2);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void deletingMultipleElementAtEndAsUser_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(3, 6).clear();

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void replacingSingleElementAtBeginningAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void replacingSingleElementInMiddleAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(1, newValue);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void replacingSingleElementAtEndAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(2, newValue);

        // then :
        final List<ClientPresentationModel> changes = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE);
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
    public void addingObjectElementFromDolphin_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.DOLPHIN_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", objectModel.getId())
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList(object)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingObjectNullFromDolphin_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.DOLPHIN_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList((SimpleTestModel) null)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingPrimitiveElementFromDolphin_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", value)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(value)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingPrimitiveNullFromDolphin_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList((String) null)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingObjectElementFromDolphin_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", objectModel.getId())
                .create();
        assertThat(model.getObjectList(), hasSize(1));

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getObjectList(), empty());
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingObjectNullFromDolphin_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getObjectList(), hasSize(1));

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getObjectList(), empty());
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingPrimitiveElementFromDolphin_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", value)
                .create();
        assertThat(model.getPrimitiveList(), hasSize(1));

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), empty());
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingPrimitiveNullFromDolphin_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getPrimitiveList(), hasSize(1));

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), empty());
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectElementFromDolphin_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.DOLPHIN_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);
        final PresentationModel oldObjectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final List<ClientPresentationModel> models = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName());
        final PresentationModel newObjectModel = oldObjectModel == models.get(1) ? models.get(0) : models.get(1);

        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldObjectModel.getId())
                .create();
        assertThat(model.getObjectList(), is(Collections.singletonList(oldObject)));

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
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
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectElementWithNullFromDolphin_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.DOLPHIN_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);
        final PresentationModel oldObjectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldObjectModel.getId())
                .create();
        assertThat(model.getObjectList(), is(Collections.singletonList(oldObject)));

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList((SimpleTestModel) null)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectNullWithElementFromDolphin_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.DOLPHIN_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final PresentationModel newObjectModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getObjectList(), is(Collections.singletonList((SimpleTestModel) null)));

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newObjectModel.getId())
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList(newObject)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveElementFromDolphin_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String oldValue = "Hello";
        final String newValue = "Goodbye";

        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldValue)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(oldValue)));

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(newValue)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveElementWithNullFromDolphin_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String oldValue = "Hello";

        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldValue)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(oldValue)));

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList((String) null)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveNullWithElementFromDolphin_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye";

        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.singletonList((String) null)));

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(newValue)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }


    //////////////////////////////////////////////////////////////
    // Adding elements at different positions from dolphin
    //////////////////////////////////////////////////////////////
    @Test
    public void addingMultipleElementsInEmptyListFromDolphin_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
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
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementInBeginningFromDolphin_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList(newElement, "1", "2", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsInBeginningFromDolphin_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
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
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementInMiddleFromDolphin_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", newElement, "2", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsInMiddleFromDolphin_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
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
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementAtEndFromDolphin_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 3)
                .withAttribute("to", 3)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", newElement)));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsAtEndFromDolphin_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
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
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }


    //////////////////////////////////////////////////////////////
    // Removing elements from different positions from dolphin
    //////////////////////////////////////////////////////////////
    @Test
    public void deletingSingleElementInBeginningFromDolphin_shouldRemoveElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("2", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementInBeginningFromDolphin_shouldRemoveElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 3)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("4", "5", "6")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingSingleElementInMiddleFromDolphin_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 2)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementInMiddleFromDolphin_shouldRemoveElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 4)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "5", "6")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingSingleElementAtEndFromDolphin_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 3)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementAtEndFromDolphin_shouldRemoveElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 4)
                .withAttribute("to", 6)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", "4")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    private void deleteAllPresentationModelsOfType(ClientDolphin dolphin, String listSplice) {
        List<ClientPresentationModel> toDelete = new ArrayList<>(dolphin.getModelStore().findAllPresentationModelsByType(listSplice));
        for (ClientPresentationModel model : toDelete) {
            dolphin.getModelStore().remove(model);
        }
    }


    //////////////////////////////////////////////////////////////
    // Replacing elements from different positions from dolphin
    //////////////////////////////////////////////////////////////
    @Test
    public void replacingSingleElementInBeginningFromDolphin_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "2", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingMultipleElementsInBeginningFromDolphin_shouldReplaceElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
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
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingSingleElementInMiddleFromDolphin_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 2)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "42", "3")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingMultipleElementsInMiddleFromDolphin_shouldReplaceElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
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
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingSingleElementAtEndFromDolphin_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 3)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "42")));
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }


    @Test
    public void replacingMultipleElementsAtEndFromDolphin_shouldReplaceElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        deleteAllPresentationModelsOfType(dolphin, PlatformConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(dolphin, PlatformConstants.LIST_SPLICE)
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
        assertThat(dolphin.getModelStore().findAllPresentationModelsByType(PlatformConstants.LIST_SPLICE), empty());
    }
}
