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

import com.canoo.dp.impl.remoting.BeanDefinitionException;
import com.canoo.dp.impl.remoting.BeanRepository;
import com.canoo.dp.impl.remoting.EventDispatcher;
import com.canoo.dp.impl.remoting.PlatformRemotingConstants;
import com.canoo.dp.impl.remoting.converters.BooleanConverterFactory;
import com.canoo.dp.impl.remoting.converters.ByteConverterFactory;
import com.canoo.dp.impl.remoting.converters.CalendarConverterFactory;
import com.canoo.dp.impl.remoting.converters.DateConverterFactory;
import com.canoo.dp.impl.remoting.converters.DolphinBeanConverterFactory;
import com.canoo.dp.impl.remoting.converters.DoubleConverterFactory;
import com.canoo.dp.impl.remoting.converters.EnumConverterFactory;
import com.canoo.dp.impl.remoting.converters.FloatConverterFactory;
import com.canoo.dp.impl.remoting.converters.IntegerConverterFactory;
import com.canoo.dp.impl.remoting.converters.LongConverterFactory;
import com.canoo.dp.impl.remoting.converters.ShortConverterFactory;
import com.canoo.dp.impl.remoting.converters.StringConverterFactory;
import com.canoo.dp.impl.remoting.legacy.RemotingConstants;
import com.canoo.dp.impl.remoting.legacy.core.Attribute;
import com.canoo.dp.impl.remoting.legacy.core.PresentationModel;
import com.canoo.dp.impl.server.legacy.ServerAttribute;
import com.canoo.dp.impl.server.legacy.ServerDolphin;
import com.canoo.dp.impl.server.legacy.ServerPresentationModel;
import com.canoo.impl.server.util.AbstractDolphinBasedTest;
import com.canoo.impl.server.util.ChildModel;
import com.canoo.impl.server.util.ComplexDataTypesModel;
import com.canoo.impl.server.util.ListReferenceModel;
import com.canoo.impl.server.util.PrimitiveDataTypesModel;
import com.canoo.impl.server.util.SimpleAnnotatedTestModel;
import com.canoo.impl.server.util.SimpleTestModel;
import com.canoo.impl.server.util.SingleReferenceModel;
import com.canoo.platform.remoting.BeanManager;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.testng.Assert.fail;

public class TestModelCreation extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getMyProperty(), notNullValue());
        assertThat(model.getMyProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(SimpleAnnotatedTestModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<ServerAttribute> attributes = dolphinModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("myProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(PlatformRemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(SimpleAnnotatedTestModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("myProperty")),
                                hasProperty("value", is(StringConverterFactory.FIELD_TYPE_STRING)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }

    @Test
    public void testWithSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<ServerAttribute> attributes = dolphinModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("text")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(PlatformRemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(SimpleTestModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("text")),
                                hasProperty("value", is(StringConverterFactory.FIELD_TYPE_STRING)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }

    @Test(expectedExceptions = BeanDefinitionException.class)
    public void testWithWrongModelType() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        String model = manager.create(String.class);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testWithNull() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);
        String model = manager.create(null);
    }


    @Test
    public void testWithAllPrimitiveDatatypes() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        PrimitiveDataTypesModel model = manager.create(PrimitiveDataTypesModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(PrimitiveDataTypesModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<ServerAttribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, hasSize(9));

        for (Attribute attribute : attributes) {
            if (RemotingConstants.SOURCE_SYSTEM.equals(attribute.getPropertyName())) {
                assertThat(attribute.getValue(), Matchers.<Object>is(RemotingConstants.SOURCE_SYSTEM_SERVER));
            } else {
                assertThat(attribute.getValue(), nullValue());
            }
            assertThat(attribute.getQualifier(), nullValue());
        }

        final List<ServerPresentationModel> classModels = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN);
        assertThat(classModels, hasSize(1));

        final PresentationModel classModel = classModels.get(0);

        final List<Attribute> classAttributes = classModel.getAttributes();
        assertThat(classAttributes, hasSize(10));

        for (Attribute attribute : classAttributes) {
            if (PlatformRemotingConstants.JAVA_CLASS.equals(attribute.getPropertyName())) {
                assertThat(attribute.getValue(), Matchers.<Object>is(PrimitiveDataTypesModel.class.getName()));
            } else if (RemotingConstants.SOURCE_SYSTEM.equals(attribute.getPropertyName())) {
                assertThat(attribute.getValue(), Matchers.<Object>is(RemotingConstants.SOURCE_SYSTEM_SERVER));
            } else {
                switch (attribute.getPropertyName()) {
                    case "byteProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(ByteConverterFactory.FIELD_TYPE_BYTE));
                        break;
                    case "shortProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(ShortConverterFactory.FIELD_TYPE_SHORT));
                        break;
                    case "integerProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(IntegerConverterFactory.FIELD_TYPE_INT));
                        break;
                    case "longProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(LongConverterFactory.FIELD_TYPE_LONG));
                        break;
                    case "floatProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(FloatConverterFactory.FIELD_TYPE_FLOAT));
                        break;
                    case "doubleProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(DoubleConverterFactory.FIELD_TYPE_DOUBLE));
                        break;
                    case "booleanProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(BooleanConverterFactory.FIELD_TYPE_BOOLEAN));
                        break;
                    case "textProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(StringConverterFactory.FIELD_TYPE_STRING));
                        break;
                    default:
                        fail("Unknown attribute found: " + attribute);
                        break;
                }
            }
            assertThat(attribute.getQualifier(), nullValue());
        }
    }


    @Test
    public void testWithComplexDataTypesModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        ComplexDataTypesModel model = manager.create(ComplexDataTypesModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getDateProperty(), notNullValue());
        assertThat(model.getDateProperty().get(), nullValue());
        assertThat(model.getCalendarProperty(), notNullValue());
        assertThat(model.getCalendarProperty().get(), nullValue());
        assertThat(model.getEnumProperty(), notNullValue());
        assertThat(model.getEnumProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(ComplexDataTypesModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("dateProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is("calendarProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is("enumProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(PlatformRemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(ComplexDataTypesModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("dateProperty")),
                                hasProperty("value", is(DateConverterFactory.FIELD_TYPE_DATE)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("calendarProperty")),
                                hasProperty("value", is(CalendarConverterFactory.FIELD_TYPE_CALENDAR)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("enumProperty")),
                                hasProperty("value", is(EnumConverterFactory.FIELD_TYPE_ENUM)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }


    @Test
    public void testWithSingleReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getReferenceProperty(), notNullValue());
        assertThat(model.getReferenceProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<ServerAttribute> attributes = dolphinModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("referenceProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(PlatformRemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(SingleReferenceModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("referenceProperty")),
                                hasProperty("value", is(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }

    @Test
    public void testWithListReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getObjectList(), empty());
        assertThat(model.getPrimitiveList(), empty());

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<ServerAttribute> attributes = dolphinModel.getAttributes();

        assertThat(attributes, contains(
                allOf(
                        hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(PlatformRemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(ListReferenceModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("objectList")),
                                hasProperty("value", is(DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("primitiveList")),
                                hasProperty("value", is(StringConverterFactory.FIELD_TYPE_STRING)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }

    @Test
    public void testWithInheritedModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        ChildModel model = manager.create(ChildModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getParentProperty(), notNullValue());
        assertThat(model.getParentProperty().get(), nullValue());
        assertThat(model.getChildProperty(), notNullValue());
        assertThat(model.getChildProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(ChildModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<ServerAttribute> attributes = dolphinModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("childProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is("parentProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = dolphin.getModelStore().findAllPresentationModelsByType(PlatformRemotingConstants.DOLPHIN_BEAN);
        assertThat(classModels, hasSize(1));
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(PlatformRemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(ChildModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("childProperty")),
                                hasProperty("value", is(StringConverterFactory.FIELD_TYPE_STRING)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("parentProperty")),
                                hasProperty("value", is(StringConverterFactory.FIELD_TYPE_STRING)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(RemotingConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }

}
