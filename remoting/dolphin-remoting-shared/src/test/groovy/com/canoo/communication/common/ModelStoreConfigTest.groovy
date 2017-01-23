/*
 * Copyright 2015-2016 Canoo Engineering AG.
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
package com.canoo.communication.common

import com.canoo.communication.common.ModelStoreConfig

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import java.util.logging.StreamHandler

class ModelStoreConfigTest extends GroovyTestCase {

    private ModelStoreConfig modelStoreConfig

    @Override
    void setUp() {
        modelStoreConfig = new ModelStoreConfig()
    }

    void testDefaultCapacitiesPowerOfTwo() {
        // no warn message should be logged
        assert getLog {new ModelStoreConfig()}.isEmpty()
    }

    void testAttributeCapacity() {
        // no warn message should be logged
        assert getLog {modelStoreConfig.setAttributeCapacity(4)}.isEmpty()
        assert 4 == modelStoreConfig.getAttributeCapacity()

        // a warn message should be logged
        assert getLog {modelStoreConfig.setAttributeCapacity(5)}.contains('attributeCapacity')
        assert 5 == modelStoreConfig.getAttributeCapacity()
    }

    void testSetPmCapacity() {
        // no warn message should be logged
        assert getLog {modelStoreConfig.setPmCapacity(4)}.isEmpty()
        assert 4 == modelStoreConfig.getPmCapacity()

        // a warn message should be logged
        assert getLog {modelStoreConfig.setPmCapacity(5)}.contains('pmCapacity')
        assert 5 == modelStoreConfig.getPmCapacity()
    }

    void testQualifierCapacity() {
        // no warn message should be logged
        assert getLog {modelStoreConfig.setQualifierCapacity(4)}.isEmpty()
        assert 4 == modelStoreConfig.getQualifierCapacity()

        // a warn message should be logged
        assert getLog {modelStoreConfig.setQualifierCapacity(5)}.contains('qualifierCapacity')
        assert 5 == modelStoreConfig.getQualifierCapacity()
    }

    void testTypeCapacity() {
        // no warn message should be logged
        assert getLog {modelStoreConfig.setTypeCapacity(4)}.isEmpty()
        assert 4 == modelStoreConfig.getTypeCapacity()

        // a warn message should be logged
        assert getLog {modelStoreConfig.setTypeCapacity(5)}.contains('typeCapacity')
        assert 5 == modelStoreConfig.getTypeCapacity()
    }


    private static String getLog(Closure inClosure) {
        return stringLog(Level.WARNING, ModelStoreConfig.class.getName(), inClosure)
    }

    // Apparently the cobertura plugin does not run tests from groovy.lang.GroovyLogTestCase
    // This method provides a simplified version of the code used in GroovyLogTestCase
    private static String stringLog(Level level, String qualifier, Closure yield){
        Logger logger = Logger.getLogger(qualifier)

        def out = new ByteArrayOutputStream(1024)
        Handler stringHandler = new StreamHandler(out, new SimpleFormatter())
        stringHandler.level = level
        logger.addHandler(stringHandler)

        yield()

        stringHandler.flush()
        out.close()
        logger.removeHandler(stringHandler)
        return out.toString()
    }
}
