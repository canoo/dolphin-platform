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
package org.opendolphin.core;

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import java.util.logging.*

public class ModelStoreConfigTest {

    private ModelStoreConfig modelStoreConfig;

    @Before
    public void setUp() {
        modelStoreConfig = new ModelStoreConfig();
    }

    private String getLog(Runnable runnable) {
        Logger logger = Logger.getLogger(ModelStoreConfig.class.getName());
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            Handler stringHandler = new StreamHandler(out, new SimpleFormatter());
            stringHandler.setLevel(Level.WARNING);
            logger.addHandler(stringHandler);

            runnable.run();

            ((StreamHandler) stringHandler).flush();
            logger.removeHandler(stringHandler);
            return out.toString();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
            throw new RuntimeException("error", e);
        }
    }

    @Test
    public void testDefaultCapacitiesPowerOfTwo() {
        // no warn message should be logged
        String log1 = getLog(new Runnable() {
            @Override
            public void run() {
                new ModelStoreConfig();
            }

        });
        Assert.assertTrue(log1.isEmpty());
    }

    @Test
    public void testAttributeCapacity() {
        // no warn message should be logged
        String log1 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setAttributeCapacity(4);
            }

        });
        Assert.assertTrue(log1.isEmpty());
        Assert.assertEquals(4, modelStoreConfig.getAttributeCapacity());

        // a warn message should be logged
        String log2 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setAttributeCapacity(5);
            }

        });
        Assert.assertTrue(log2.contains("attributeCapacity"));
        Assert.assertEquals(5, modelStoreConfig.getAttributeCapacity());
    }

    @Test
    public void testSetPmCapacity() {
        // no warn message should be logged
        String log1 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setPmCapacity(4);
            }

        });
        Assert.assertTrue(log1.isEmpty());
        Assert.assertEquals(4, modelStoreConfig.getPmCapacity());

        // a warn message should be logged
        String log2 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setPmCapacity(5);
            }

        });
        Assert.assertTrue(log2.contains("pmCapacity"));
        Assert.assertEquals(5, modelStoreConfig.getPmCapacity());
    }

    @Test
    public void testQualifierCapacity() {
        // no warn message should be logged
        String log1 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setQualifierCapacity(4);
            }

        });
        Assert.assertTrue(log1.isEmpty());
        Assert.assertEquals(4, modelStoreConfig.getQualifierCapacity());

        // a warn message should be logged
        String log2 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setQualifierCapacity(5);
            }

        });
        Assert.assertTrue(log2.contains("qualifierCapacity"));
        Assert.assertEquals(5, modelStoreConfig.getQualifierCapacity());
    }

    @Test
    public void testTypeCapacity() {
        // no warn message should be logged
        String log1 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setTypeCapacity(4);
            }

        });
        Assert.assertTrue(log1.isEmpty());
        Assert.assertEquals(4, modelStoreConfig.getTypeCapacity());

        // a warn message should be logged
        String log2 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setTypeCapacity(5);
            }

        });
        Assert.assertTrue(log2.contains("typeCapacity"));
        Assert.assertEquals(5, modelStoreConfig.getTypeCapacity());
    }
}
