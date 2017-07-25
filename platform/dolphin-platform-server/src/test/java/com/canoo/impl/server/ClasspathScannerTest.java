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

import com.canoo.impl.server.special.SpecialAnnotatedClass;
import com.canoo.impl.server.util.AnnotatedClassForClasspathScan;
import com.canoo.impl.server.util.AnnotationForClasspathScanTest;
import com.canoo.dp.impl.server.scanner.DefaultClasspathScanner;
import org.testng.annotations.Test;

import javax.annotation.Resources;
import java.lang.annotation.Documented;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ClasspathScannerTest {

    @Test
    public void testSimpleScan() {
        //There can't be a class that is annotated with Inject
        DefaultClasspathScanner scanner = new DefaultClasspathScanner();
        Set<Class<?>> classes = scanner.getTypesAnnotatedWith(Resources.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 0);

        classes = scanner.getTypesAnnotatedWith(AnnotationForClasspathScanTest.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 1);
        assertTrue(classes.contains(AnnotatedClassForClasspathScan.class));
    }

    @Test
    public void testInPackageScan() {
        //There can't be a class that is annotated with Inject
        DefaultClasspathScanner scanner = new DefaultClasspathScanner("com.canoo.impl");
        Set<Class<?>> classes = scanner.getTypesAnnotatedWith(Resources.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 0);

        classes = scanner.getTypesAnnotatedWith(AnnotationForClasspathScanTest.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 1);
        assertTrue(classes.contains(AnnotatedClassForClasspathScan.class));
    }

    @Test
    public void testScanOtherPackage() {
        //There can't be a class that is annotated with Inject
        DefaultClasspathScanner scanner = new DefaultClasspathScanner("com.canoo.impl.server.special");
        Set<Class<?>> classes = scanner.getTypesAnnotatedWith(Resources.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 0);

        classes = scanner.getTypesAnnotatedWith(AnnotationForClasspathScanTest.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 0);

        classes = scanner.getTypesAnnotatedWith(Documented.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 1);
        assertTrue(classes.contains(SpecialAnnotatedClass.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullArgument() {
        DefaultClasspathScanner scanner = new DefaultClasspathScanner();
        Set<Class<?>> classes = scanner.getTypesAnnotatedWith(null);
    }

}
