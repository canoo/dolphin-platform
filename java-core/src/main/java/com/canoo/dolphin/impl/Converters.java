/*
 * Copyright 2015 Canoo Engineering AG.
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
package com.canoo.dolphin.impl;

import com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.util.Assert;

import static com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType.*;

/**
 * The class {@code Converters} contains all {@link Converters.Converter} that are
 * used in the Dolphin Platform (three at present). It is mostly a helper class to minimize the memory footprint
 * of a {@link PropertyImpl}, because all functionality related to a {@link FieldType} are summed up here.
 *
 * Each {@code Converter} is responsible for converting a property of a given {@code FieldType}. The {@code FieldType}
 * that is supported by the current Converter can be requested with {@link Converter#getFieldType()}.
 *
 * Initially we do not know
 * the {@code FieldType} of a property. Only after we set it to a non-{@code null} value can we determine the type.
 * The implementations of {@code Converter} support switching the converter with the method
 * {@link Converter#updateConverter(FieldType)}.
 */
public class Converters {

    public abstract class Converter {
        public Object convertFromDolphin(Object value) {
            return value;
        }
        public Object convertToDolphin(Object value) {
            return value;
        }
        public Converter updateConverter(FieldType value) {
            return this;
        }
        public abstract FieldType getFieldType();
    }

    private final BeanRepository beanRepository;

    public Converter getUnknownTypeConverter() {
        return unknownTypeConverter;
    }

    protected Converters(BeanRepository beanRepository) {
        Assert.requireNonNull(beanRepository, "beanRepository");
        this.beanRepository = beanRepository;
    }

    private final Converter unknownTypeConverter = new Converter() {
        @Override
        public Converter updateConverter(ClassRepositoryImpl.FieldType fieldType) {
            return fieldType == DOLPHIN_BEAN ? dolphinBeanTypeConverter : basicTypeConverter;
        }

        @Override
        public FieldType getFieldType() {
            return UNKNOWN;
        }
    };

    private final Converter basicTypeConverter = new Converter() {
        @Override
        public FieldType getFieldType() {
            return BASIC_TYPE;
        }
    };

    private final Converter dolphinBeanTypeConverter = new Converter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return beanRepository.getBean((String) value);
        }

        @Override
        public Object convertToDolphin(Object value) {
            return beanRepository.getDolphinId(value);
        }

        @Override
        public FieldType getFieldType() {
            return DOLPHIN_BEAN;
        }
    };


}
