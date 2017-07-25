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
package com.canoo.dp.impl.server.scanner;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.server.spi.ClasspathScanner;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * This class can be used to search for a set of classes in the classpath. Currently all classes that are annotated
 * with a specific annotation can be found.
 */
public class DefaultClasspathScanner implements ClasspathScanner {

    private final Reflections reflections;

    public DefaultClasspathScanner() {
        this(null);
    }

    public DefaultClasspathScanner(final String rootPackage) {
        ConfigurationBuilder configuration = ConfigurationBuilder.build(DefaultClasspathScanner.class.getClassLoader());

        if(rootPackage != null && !rootPackage.trim().isEmpty()) {
            configuration = configuration.forPackages(rootPackage);
            configuration = configuration.setUrls(ClasspathHelper.forPackage(rootPackage));
            configuration = configuration.filterInputsBy(new FilterBuilder().includePackage(rootPackage));
        }

        //Special case for JBOSS Application server to get all classes
        try {
            Enumeration<URL> res = DefaultClasspathScanner.class.getClassLoader().getResources("");
            configuration.getUrls().addAll(Collections.list(res));
        } catch (IOException e) {
            throw new RuntimeException("Error in Dolphin Platform controller class scan", e);
        }


        //Remove native libs (will be added on Mac in a Spring Boot app)
        Set<URL> urls = configuration.getUrls();
        List<URL> toRemove = new ArrayList<>();
        for (URL url : urls) {
            if (url.toString().endsWith(".jnilib")) {
                toRemove.add(url);
            }
        }
        for (URL url : toRemove) {
            configuration.getUrls().remove(url);
        }

        reflections = new Reflections(configuration);
    }

    /**
     * Returns a set that contains all classes in the classpath that are annotated with the given annotation
     * @param annotation the annotation
     * @return the set of annotated classes
     */
    public synchronized Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
        Assert.requireNonNull(annotation, "annotation");
        return reflections.getTypesAnnotatedWith(annotation);
    }
}
