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
package com.canoo.impl.server.binding;

import com.canoo.dolphin.binding.Binding;
import com.canoo.dolphin.binding.BindingException;
import com.canoo.dolphin.impl.PropertyImpl;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.ReflectionHelper;
import com.canoo.platform.server.binding.PropertyBinder;
import com.canoo.platform.server.binding.Qualifier;
import org.opendolphin.core.server.ServerAttribute;

import java.lang.reflect.Field;

public class PropertyBinderImpl implements PropertyBinder {

    public <T> Binding bind(final Property<T> property, final Qualifier<T> qualifier) {
        Assert.requireNonNull(property, "property");
        Assert.requireNonNull(qualifier, "qualifier");

        if(property instanceof PropertyImpl) {
            try {
                final PropertyImpl p = (PropertyImpl) property;

                final Field attributeField = ReflectionHelper.getInheritedDeclaredField(PropertyImpl.class, "attribute");
                final ServerAttribute attribute = (ServerAttribute) ReflectionHelper.getPrivileged(attributeField, p);
                if(attribute == null) {
                    throw new NullPointerException("attribute == null");
                }
                attribute.setQualifier(qualifier.getIdentifier());
                return new Binding() {
                    @Override
                    public void unbind() {
                        attribute.setQualifier(null);
                    }
                };
            } catch (Exception e) {
                throw new BindingException("Can not bind the given property to the qualifier! Property: " + property + ", qualifier: " + qualifier , e);
            }
        } else {
            throw new BindingException("Can not bind the given property to the qualifier! Property: " + property + ", qualifier: " + qualifier);
        }
    }

}
