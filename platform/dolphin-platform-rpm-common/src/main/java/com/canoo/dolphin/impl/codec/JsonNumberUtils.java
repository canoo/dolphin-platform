package com.canoo.dolphin.impl.codec;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.ReflectionHelper;

/**
 * Some helper classes since GSON uses {@link com.google.gson.internal.LazilyParsedNumber} internally.
 */
public class JsonNumberUtils {

    public static Number convert(final Class<?> neededType, final Object value) {
        Assert.requireNonNull(neededType, "neededType");
        if(!ReflectionHelper.isNumber(neededType)) {
            throw new IllegalArgumentException("given type is not a number type: " + neededType.getSimpleName());
        }
        if(value == null && ReflectionHelper.isPrimitiveNumber(neededType)) {
            throw new IllegalArgumentException("null can not be converted for a primitive type");
        }

        if(value == null) {
            return null;
        }

        if(!Number.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Given value is not a number! Type " + value.getClass().getSimpleName());
        }
        Number numberValue = (Number) value;
        if (neededType.equals(Integer.class) || neededType.equals(Integer.TYPE)) {
            return numberValue.intValue();
        } else if (neededType.equals(Long.class) || neededType.equals(Long.TYPE)) {
            return numberValue.longValue();
        } else if (neededType.equals(Double.class) || neededType.equals(Double.TYPE)) {
            return numberValue.doubleValue();
        } else if (neededType.equals(Float.class) || neededType.equals(Float.TYPE)) {
            return numberValue.floatValue();
        } else if (neededType.equals(Byte.class) || neededType.equals(Byte.TYPE)) {
            return numberValue.byteValue();
        } else if (neededType.equals(Short.class) || neededType.equals(Short.TYPE)) {
            return numberValue.shortValue();
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + neededType);
        }
    }


}
