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
package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ClientBeanManager;
import com.canoo.dolphin.impl.BeanManagerImpl;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.remoting.client.ClientDolphin;

@Deprecated
public class ClientBeanManagerImpl extends BeanManagerImpl implements ClientBeanManager {

    public ClientBeanManagerImpl(BeanRepository beanRepository, BeanBuilder beanBuilder, ClientDolphin dolphin) {
        super(beanRepository, beanBuilder);
    }

}
