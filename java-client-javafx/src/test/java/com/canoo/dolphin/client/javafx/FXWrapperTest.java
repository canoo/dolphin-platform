package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.impl.MockedProperty;
import com.canoo.dolphin.mapping.Property;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by hendrikebbers on 25.02.16.
 */
public class FXWrapperTest {

    private static final double DELTA = 1e-15;

    @Test
    public void WrapDoubleInNumberPropertyEmptyTest() {

        //Given:
        Property<Double> doubleDolphinProperty = new MockedProperty<>();

        //When:
        javafx.beans.property.Property<Number> javafxProperty = FXWrapper.wrapDoubleInNumberProperty(doubleDolphinProperty);

        //Then:
        Assert.assertEquals(doubleDolphinProperty.get().doubleValue(), 0.0, DELTA);
        Assert.assertEquals(javafxProperty.getValue().doubleValue(), 0.0, DELTA);
    }

    @Test
    public void WrapDoubleInNumberPropertyBasicValueTest() {

        //Given:
        Property<Double> doubleDolphinProperty = new MockedProperty<>();
        doubleDolphinProperty.set(12.0);

        //When:
        javafx.beans.property.Property<Number> javafxProperty = FXWrapper.wrapDoubleInNumberProperty(doubleDolphinProperty);

        //Then:
        Assert.assertEquals(doubleDolphinProperty.get().doubleValue(), 12.0, DELTA);
        Assert.assertEquals(javafxProperty.getValue().doubleValue(), 12.0, DELTA);
    }

    @Test
    public void WrapDoubleInNumberPropertySetDPTest() {

        //Given:
        Property<Double> doubleDolphinProperty = new MockedProperty<>();

        //When:
        javafx.beans.property.Property<Number> javafxProperty = FXWrapper.wrapDoubleInNumberProperty(doubleDolphinProperty);
        doubleDolphinProperty.set(12.0);

        //Then:
        Assert.assertEquals(doubleDolphinProperty.get().doubleValue(), 12.0, DELTA);
        Assert.assertEquals(javafxProperty.getValue().doubleValue(), 12.0, DELTA);
    }

    @Test
    public void WrapDoubleInNumberPropertySetJFXTest() {

        //Given:
        Property<Double> doubleDolphinProperty = new MockedProperty<>();

        //When:
        javafx.beans.property.Property<Number> javafxProperty = FXWrapper.wrapDoubleInNumberProperty(doubleDolphinProperty);
        javafxProperty.setValue(12.0);

        //Then:
        Assert.assertEquals(doubleDolphinProperty.get().doubleValue(), 12.0, DELTA);
        Assert.assertEquals(javafxProperty.getValue().doubleValue(), 12.0, DELTA);
    }





    @Test
    public void WrapIntegerInNumberPropertyEmptyTest() {

        //Given:
        Property<Integer> intDolphinProperty = new MockedProperty<>();

        //When:
        javafx.beans.property.Property<Number> javafxProperty = FXWrapper.wrapIntegerInNumberProperty(intDolphinProperty);

        //Then:
        Assert.assertEquals(intDolphinProperty.get().intValue(), 0);
        Assert.assertEquals(javafxProperty.getValue().intValue(), 0);
    }

    @Test
    public void WrapIntegerInNumberPropertyBasicValueTest() {

        //Given:
        Property<Integer> intDolphinProperty = new MockedProperty<>();
        intDolphinProperty.set(12);

        //When:
        javafx.beans.property.Property<Number> javafxProperty = FXWrapper.wrapIntegerInNumberProperty(intDolphinProperty);

        //Then:
        Assert.assertEquals(intDolphinProperty.get().intValue(), 12);
        Assert.assertEquals(javafxProperty.getValue().intValue(), 12);
    }

    @Test
    public void WrapIntegerInNumberPropertySetDPTest() {

        //Given:
        Property<Integer> intDolphinProperty = new MockedProperty<>();

        //When:
        javafx.beans.property.Property<Number> javafxProperty = FXWrapper.wrapIntegerInNumberProperty(intDolphinProperty);
        intDolphinProperty.set(12);

        //Then:
        Assert.assertEquals(intDolphinProperty.get().intValue(), 12);
        Assert.assertEquals(javafxProperty.getValue().intValue(), 12);
    }

    @Test
    public void WrapIntegerInNumberPropertySetJFXTest() {

        //Given:
        Property<Integer> intDolphinProperty = new MockedProperty<>();

        //When:
        javafx.beans.property.Property<Number> javafxProperty = FXWrapper.wrapIntegerInNumberProperty(intDolphinProperty);
        javafxProperty.setValue(12);

        //Then:
        Assert.assertEquals(intDolphinProperty.get().intValue(), 12);
        Assert.assertEquals(javafxProperty.getValue().intValue(), 12);
    }

}
