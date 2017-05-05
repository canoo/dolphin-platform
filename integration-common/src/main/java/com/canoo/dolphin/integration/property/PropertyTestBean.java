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
package com.canoo.dolphin.integration.property;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@DolphinBean
public class PropertyTestBean {

    private Property<String> stringValue;

    private Property<Boolean> booleanValue;

    private Property<Integer> integerValue;

    private Property<Long> longValue;

    private Property<Float> floatValue;

    private Property<Double> doubleValue;

    private Property<BigDecimal> bigDecimalValue;

    private Property<BigInteger> bigIntegerValue;

    private Property<Byte> byteValue;

    private Property<Calendar> calendarValue;

    private Property<Date> dateValue;

    private Property<Short> shortValue;

    private Property<UUID> uuidValue;

    private Property<ElementType> enumValue;

    private Property<Boolean> bigDecimalValueChanged;

    private Property<Boolean> bigIntegerValueChanged;

    private Property<Boolean> booleanValueChanged;

    private Property<Boolean> byteValueChanged;

    private Property<Boolean> calenderValueChanged;

    private Property<Boolean> dateValueChanged;

    private Property<Boolean> doubleValueChanged;

    private Property<Boolean> enumValueChanged;

    private Property<Boolean> floatValueChanged;

    private Property<Boolean> integerValueChanged;

    private Property<Boolean> longValueChanged;

    private Property<Boolean> shortValueChanged;

    private Property<Boolean> stringValueChanged;

    private Property<Boolean> uuidValueChanged;

    public Property<String> stringValueProperty() {
        return stringValue;
    }

    public Property<Boolean> booleanValueProperty() {
        return booleanValue;
    }

    public Property<Integer> integerValueProperty() {
        return integerValue;
    }

    public Property<Long> longValueProperty() {
        return longValue;
    }

    public Property<Float> floatValueProperty() {
        return floatValue;
    }

    public Property<Double> doubleValueProperty() {
        return doubleValue;
    }

    public Property<BigDecimal> bigDecimalValueProperty() {
        return bigDecimalValue;
    }

    public Property<BigInteger> bigIntegerValueProperty() {
        return bigIntegerValue;
    }

    public Property<Byte> byteValueProperty() {
        return byteValue;
    }

    public Property<Calendar> calendarValueProperty() {
        return calendarValue;
    }

    public Property<Date> dateValueProperty() {
        return dateValue;
    }

    public Property<Short> shortValueProperty() {
        return shortValue;
    }

    public Property<UUID> uuidValueProperty() {
        return uuidValue;
    }

    public Property<ElementType> enumValueProperty() {
        return enumValue;
    }

    public String getStringValue() {
        return stringValue.get();
    }

    public Boolean getBooleanValue() {
        return booleanValue.get();
    }

    public Integer getIntegerValue() {
        return integerValue.get();
    }

    public Long getLongValue() {
        return longValue.get();
    }

    public Float getFloatValue() {
        return floatValue.get();
    }

    public Double getDoubleValue() {
        return doubleValue.get();
    }

    public BigDecimal getBigDecimalValue() {
        return bigDecimalValue.get();
    }

    public BigInteger getBigIntegerValue() {
        return bigIntegerValue.get();
    }

    public Byte getByteValue() {
        return byteValue.get();
    }

    public Calendar getCalendarValue() {
        return calendarValue.get();
    }

    public Date getDateValue() {
        return dateValue.get();
    }

    public Short getShortValue() {
        return shortValue.get();
    }

    public UUID getUuidValue() {
        return uuidValue.get();
    }

    public ElementType getEnumValue() {
        return enumValue.get();
    }

    public void setStringValue(String stringValue) {
        this.stringValue.set(stringValue);
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue.set(booleanValue);
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue.set(integerValue);
    }

    public void setLongValue(Long longValue) {
        this.longValue.set(longValue);
    }

    public void setFloatValue(Float floatValue) {
        this.floatValue.set(floatValue);
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue.set(doubleValue);
    }

    public void setBigDecimalValue(BigDecimal bigDecimalValue) {
        this.bigDecimalValue.set(bigDecimalValue);
    }

    public void setBigIntegerValue(BigInteger bigIntegerValue) {
        this.bigIntegerValue.set(bigIntegerValue);
    }

    public void setByteValue(Byte byteValue) {
        this.byteValue.set(byteValue);
    }

    public void setCalendarValue(Calendar calendarValue) {
        this.calendarValue.set(calendarValue);
    }

    public void setDateValue(Date dateValue) {
        this.dateValue.set(dateValue);
    }

    public void setShortValue(Short shortValue) {
        this.shortValue.set(shortValue);
    }

    public void setUuidValue(UUID uuidValue) {
        this.uuidValue.set(uuidValue);
    }

    public void setEnumValue(ElementType enumValue) {
        this.enumValue.set(enumValue);
    }

    public Property<Boolean> bigDecimalValueChangedProperty() {
        return bigDecimalValueChanged;
    }

    public Property<Boolean> bigIntegerValueChangedProperty() {
        return bigIntegerValueChanged;
    }

    public Property<Boolean> booleanValueChangedProperty() {
        return booleanValueChanged;
    }

    public Property<Boolean> byteValueChangedProperty() {
        return byteValueChanged;
    }

    public Property<Boolean> calenderValueChangedProperty() {
        return calenderValueChanged;
    }

    public Property<Boolean> dateValueChangedProperty() {
        return dateValueChanged;
    }

    public Property<Boolean> doubleValueChangedProperty() {
        return doubleValueChanged;
    }

    public Property<Boolean> enumValueChangedProperty() {
        return enumValueChanged;
    }

    public Property<Boolean> floatValueChangedProperty() {
        return floatValueChanged;
    }

    public Property<Boolean> integerValueChangedProperty() {
        return integerValueChanged;
    }

    public Property<Boolean> longValueChangedProperty() {
        return longValueChanged;
    }

    public Property<Boolean> shortValueChangedProperty() {
        return shortValueChanged;
    }

    public Property<Boolean> stringValueChangedProperty() {
        return stringValueChanged;
    }

    public Property<Boolean> uuidValueChangedProperty() {
        return uuidValueChanged;
    }

    public Boolean getBigDecimalValueChanged() {
        return bigDecimalValueChanged.get();
    }

    public void setBigDecimalValueChanged(Boolean bigDecimalValueChanged) {
        this.bigDecimalValueChanged.set(bigDecimalValueChanged);
    }

    public Boolean getBigIntegerValueChanged() {
        return bigIntegerValueChanged.get();
    }

    public void setBigIntegerValueChanged(Boolean bigIntegerValueChanged) {
        this.bigIntegerValueChanged.set(bigIntegerValueChanged);
    }

    public Boolean getBooleanValueChanged() {
        return booleanValueChanged.get();
    }

    public void setBooleanValueChanged(Boolean booleanValueChanged) {
        this.booleanValueChanged.set(booleanValueChanged);
    }

    public Boolean getByteValueChanged() {
        return byteValueChanged.get();
    }

    public void setByteValueChanged(Boolean byteValueChanged) {
        this.byteValueChanged.set(byteValueChanged);
    }

    public Boolean getCalenderValueChanged() {
        return calenderValueChanged.get();
    }

    public void setCalenderValueChanged(Boolean calenderValueChanged) {
        this.calenderValueChanged.set(calenderValueChanged);
    }

    public Boolean getDateValueChanged() {
        return dateValueChanged.get();
    }

    public void setDateValueChanged(Boolean dateValueChanged) {
        this.dateValueChanged.set(dateValueChanged);
    }

    public Boolean getDoubleValueChanged() {
        return doubleValueChanged.get();
    }

    public void setDoubleValueChanged(Boolean doubleValueChanged) {
        this.doubleValueChanged.set(doubleValueChanged);
    }

    public Boolean getEnumValueChanged() {
        return enumValueChanged.get();
    }

    public void setEnumValueChanged(Boolean enumValueChanged) {
        this.enumValueChanged.set(enumValueChanged);
    }

    public Boolean getFloatValueChanged() {
        return floatValueChanged.get();
    }

    public void setFloatValueChanged(Boolean floatValueChanged) {
        this.floatValueChanged.set(floatValueChanged);
    }

    public Boolean getIntegerValueChanged() {
        return integerValueChanged.get();
    }

    public void setIntegerValueChanged(Boolean integerValueChanged) {
        this.integerValueChanged.set(integerValueChanged);
    }

    public Boolean getLongValueChanged() {
        return longValueChanged.get();
    }

    public void setLongValueChanged(Boolean longValueChanged) {
        this.longValueChanged.set(longValueChanged);
    }

    public Boolean getShortValueChanged() {
        return shortValueChanged.get();
    }

    public void setShortValueChanged(Boolean shortValueChanged) {
        this.shortValueChanged.set(shortValueChanged);
    }

    public Boolean getStringValueChanged() {
        return stringValueChanged.get();
    }

    public void setStringValueChanged(Boolean stringValueChanged) {
        this.stringValueChanged.set(stringValueChanged);
    }

    public Boolean getUuidValueChanged() {
        return uuidValueChanged.get();
    }

    public void setUuidValueChanged(Boolean uuidValueChanged) {
        this.uuidValueChanged.set(uuidValueChanged);
    }
}
