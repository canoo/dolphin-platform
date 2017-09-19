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

import com.canoo.dp.impl.remoting.legacy.core.Attribute;
import com.canoo.dp.impl.remoting.legacy.core.PresentationModel;
import com.canoo.dp.impl.server.legacy.ServerDolphin;
import com.canoo.dp.impl.server.legacy.ServerPresentationModel;
import com.canoo.impl.server.util.AbstractDolphinBasedTest;
import com.canoo.impl.server.util.ChildModel;
import com.canoo.impl.server.util.ComplexDataTypesModel;
import com.canoo.impl.server.util.PrimitiveDataTypesModel;
import com.canoo.impl.server.util.SimpleAnnotatedTestModel;
import com.canoo.impl.server.util.SimpleTestModel;
import com.canoo.impl.server.util.SingleReferenceModel;
import com.canoo.platform.remoting.BeanManager;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static com.canoo.impl.server.util.ComplexDataTypesModel.EnumValues.VALUE_1;
import static com.canoo.impl.server.util.ComplexDataTypesModel.EnumValues.VALUE_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class TestPropertyValue extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        ServerPresentationModel dolphinModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleAnnotatedTestModel.class.getName()).get(0);

        Attribute textAttribute = dolphinModel.getAttribute("myProperty");
        assertThat(textAttribute.getValue(), nullValue());

        model.getMyProperty().set("Hallo Platform");
        assertThat((String) textAttribute.getValue(), is("Hallo Platform"));
        assertThat(model.getMyProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hallo Dolphin");
        assertThat((String) textAttribute.getValue(), is("Hallo Dolphin"));
        assertThat(model.getMyProperty().get(), is("Hallo Dolphin"));
    }

    @Test
    public void testWithSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        ServerPresentationModel dolphinModel = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        Attribute textAttribute = dolphinModel.getAttribute("text");
        assertThat(textAttribute.getValue(), nullValue());

        model.getTextProperty().set("Hallo Platform");
        assertThat((String) textAttribute.getValue(), is("Hallo Platform"));
        assertThat(model.getTextProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hallo Dolphin");
        assertThat((String) textAttribute.getValue(), is("Hallo Dolphin"));
        assertThat(model.getTextProperty().get(), is("Hallo Dolphin"));
    }

    @Test
    public void testWithAllPrimitiveDataTypesModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        PrimitiveDataTypesModel model = manager.create(PrimitiveDataTypesModel.class);

        ServerPresentationModel dolphinModel = dolphin.getModelStore().findAllPresentationModelsByType(PrimitiveDataTypesModel.class.getName()).get(0);

        Attribute textAttribute = dolphinModel.getAttribute("textProperty");
        assertThat(textAttribute.getValue(), nullValue());

        model.getTextProperty().set("Hallo Platform");
        assertThat((String) textAttribute.getValue(), is("Hallo Platform"));
        assertThat(model.getTextProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hallo Dolphin");
        assertThat((String) textAttribute.getValue(), is("Hallo Dolphin"));
        assertThat(model.getTextProperty().get(), is("Hallo Dolphin"));


        Attribute intAttribute = dolphinModel.getAttribute("integerProperty");
        assertThat(intAttribute.getValue(), nullValue());

        model.getIntegerProperty().set(1);
        assertThat((Integer) intAttribute.getValue(), is(1));
        assertThat(model.getIntegerProperty().get(), is(1));

        intAttribute.setValue(2);
        assertThat((Integer) intAttribute.getValue(), is(2));
        assertThat(model.getIntegerProperty().get(), is(2));


        Attribute booleanAttribute = dolphinModel.getAttribute("booleanProperty");
        assertThat(booleanAttribute.getValue(), nullValue());

        model.getBooleanProperty().set(true);
        assertThat((Boolean) booleanAttribute.getValue(), is(true));
        assertThat(model.getBooleanProperty().get(), is(true));

        model.getBooleanProperty().set(false);
        assertThat((Boolean) booleanAttribute.getValue(), is(false));
        assertThat(model.getBooleanProperty().get(), is(false));

    }


    @Test
    public void testWithComplexDataTypesModel() {
        final Calendar date1 = new GregorianCalendar(2016, Calendar.MARCH, 1, 0, 1, 2);
        date1.set(Calendar.MILLISECOND, 3);
        date1.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
        final Calendar date2 = new GregorianCalendar(2016, Calendar.FEBRUARY, 29, 0, 1, 2);
        date2.set(Calendar.MILLISECOND, 3);
        date2.setTimeZone(TimeZone.getTimeZone("UTC"));

        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        ComplexDataTypesModel model = manager.create(ComplexDataTypesModel.class);

        PresentationModel dolphinModel = dolphin.getModelStore().findAllPresentationModelsByType(ComplexDataTypesModel.class.getName()).get(0);


        Attribute dateAttribute = dolphinModel.getAttribute("dateProperty");
        assertThat(dateAttribute.getValue(), nullValue());

        model.getDateProperty().set(date1.getTime());
        assertThat((String) dateAttribute.getValue(), is("2016-02-29T22:01:02.003Z"));
        assertThat(model.getDateProperty().get(), is(date1.getTime()));

        dateAttribute.setValue("2016-02-29T00:01:02.003Z");
        assertThat((String) dateAttribute.getValue(), is("2016-02-29T00:01:02.003Z"));
        assertThat(model.getDateProperty().get(), is(date2.getTime()));


        Attribute calendarAttribute = dolphinModel.getAttribute("calendarProperty");
        assertThat(calendarAttribute.getValue(), nullValue());

        model.getCalendarProperty().set(date1);
        assertThat((String) calendarAttribute.getValue(), is("2016-02-29T22:01:02.003Z"));
        assertThat(model.getCalendarProperty().get().getTimeInMillis(), is(date1.getTimeInMillis()));

        calendarAttribute.setValue("2016-02-29T00:01:02.003Z");
        assertThat((String) calendarAttribute.getValue(), is("2016-02-29T00:01:02.003Z"));
        assertThat(model.getCalendarProperty().get(), is(date2));


        Attribute enumAttribute = dolphinModel.getAttribute("enumProperty");
        assertThat(enumAttribute.getValue(), nullValue());

        model.getEnumProperty().set(VALUE_1);
        assertThat((String) enumAttribute.getValue(), is("VALUE_1"));
        assertThat(model.getEnumProperty().get(), is(VALUE_1));

        enumAttribute.setValue("VALUE_2");
        assertThat((String) enumAttribute.getValue(), is("VALUE_2"));
        assertThat(model.getEnumProperty().get(), is(VALUE_2));
    }


    @Test
    public void testWithSingleReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final SimpleTestModel ref1 = manager.create(SimpleTestModel.class);
        ref1.getTextProperty().set("ref1_text");
        final SimpleTestModel ref2 = manager.create(SimpleTestModel.class);
        ref2.getTextProperty().set("ref2_text");
        final List<ServerPresentationModel> refPMs = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName());
        final ServerPresentationModel ref1PM = "ref1_text".equals(refPMs.get(0).getAttribute("text").getValue()) ? refPMs.get(0) : refPMs.get(1);
        final ServerPresentationModel ref2PM = "ref2_text".equals(refPMs.get(0).getAttribute("text").getValue()) ? refPMs.get(0) : refPMs.get(1);

        final SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        final ServerPresentationModel dolphinModel = dolphin.getModelStore().findAllPresentationModelsByType(SingleReferenceModel.class.getName()).get(0);

        final Attribute referenceAttribute = dolphinModel.getAttribute("referenceProperty");
        assertThat(referenceAttribute.getValue(), nullValue());

        model.getReferenceProperty().set(ref1);
        assertThat((String) referenceAttribute.getValue(), is(ref1PM.getId()));
        assertThat(model.getReferenceProperty().get(), is(ref1));

        referenceAttribute.setValue(ref2PM.getId());
        assertThat((String) referenceAttribute.getValue(), is(ref2PM.getId()));
        assertThat(model.getReferenceProperty().get(), is(ref2));
    }

    @Test
    public void testWithInheritedModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        ChildModel model = manager.create(ChildModel.class);

        ServerPresentationModel dolphinModel = dolphin.getModelStore().findAllPresentationModelsByType(ChildModel.class.getName()).get(0);

        Attribute childAttribute = dolphinModel.getAttribute("childProperty");
        assertThat(childAttribute.getValue(), nullValue());
        Attribute parentAttribute = dolphinModel.getAttribute("parentProperty");
        assertThat(parentAttribute.getValue(), nullValue());

        model.getChildProperty().set("Hallo Platform");
        assertThat((String) childAttribute.getValue(), is("Hallo Platform"));
        assertThat(model.getChildProperty().get(), is("Hallo Platform"));
        assertThat(parentAttribute.getValue(), nullValue());
        assertThat(model.getParentProperty().get(), nullValue());

        parentAttribute.setValue("Hallo Dolphin");
        assertThat((String) childAttribute.getValue(), is("Hallo Platform"));
        assertThat(model.getChildProperty().get(), is("Hallo Platform"));
        assertThat((String) parentAttribute.getValue(), is("Hallo Dolphin"));
        assertThat(model.getParentProperty().get(), is("Hallo Dolphin"));
    }


}
