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
package com.canoo.dolphin.server.controller;

import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.Param;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class ControllerValidatorTest {

    private ControllerValidator controllerValidator;

    @BeforeTest
    public void setUp(){
        controllerValidator = new ControllerValidator();
    }

    @Test
    public void testValidController() throws ControllerValidationException{
        controllerValidator.validate(ValidController.class);
    }

    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCannotBeAnInterface() throws ControllerValidationException{
        controllerValidator.validate(ControllerAsInterface.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCannotBeAbstract() throws ControllerValidationException{
        controllerValidator.validate(ControllerAsAbstract.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCannotBeFinal() throws ControllerValidationException{
        controllerValidator.validate(ControllerAsFinal.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testPostConstructCannotContainParameter() throws ControllerValidationException{
        controllerValidator.validate(ControllerPostConstructWithParam.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testPreDestroyCannotContainParameter() throws ControllerValidationException{
        controllerValidator.validate(ControllerPreDestroyWithParam.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCannotHaveMoreThanOnePostConstruct() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithMoreThanOnePostConstruct.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCannotHaveMoreThanOnePreDestroy() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithMoreThanOnePreDestroy.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testDolphinActionMustBeVoid() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithNonVoidDolphinAction.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testDolphinActionParameterAnnotation() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithDolphinActionParam.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerMusthaveDolphinModel() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithoutDolphinModel.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCannoHaveMoreThanOneDolphinModel() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithMoreThanOneDolphinModel.class);
    }

}

class ValidController {

    @DolphinModel
    private TestBean testBean;

    @PostConstruct
    public void postConstruct() {
    }

    @PreDestroy
    public void preDestroy() {
    }

    @DolphinAction
    public void dolphinAction(@Param String test) {
    }

    @DolphinAction
    public void dolphinAction2(@Param @Nonnull String test) {
    }

    @DolphinAction
    public void dolphinAction3(@Nonnull @Param String test) {
    }
}

interface ControllerAsInterface{}

abstract class ControllerAsAbstract{}
final class ControllerAsFinal{}
class ControllerPostConstructWithParam{

    @DolphinModel
    private TestBean testBean;

    @PostConstruct
    public void postConstruct(String param) {
    }
}
class ControllerWithMoreThanOnePostConstruct{

    @DolphinModel
    private TestBean testBean;

    @PostConstruct
    public void postConstruct1() {
    }
    @PostConstruct
    public void postConstruct2() {
    }
}
class ControllerPreDestroyWithParam{

    @DolphinModel
    private TestBean testBean;

    @PreDestroy
    public void preDestroy(String param) {
    }
}
class ControllerWithMoreThanOnePreDestroy{

    @DolphinModel
    private TestBean testBean;

    @PreDestroy
    public void preDestroy1() {
    }
    @PreDestroy
    public void preDestroy2() {
    }
}
class ControllerWithNonVoidDolphinAction{

    @DolphinModel
    private TestBean testBean;

    @DolphinAction
    public String dolphinAction(@Param String test) {
        return "";
    }
}
class ControllerWithDolphinActionParam{

    @DolphinModel
    private TestBean testBean;

    @DolphinAction
    public void dolphinAction(String test) {
    }
}

class ControllerWithoutDolphinModel{}
class ControllerWithMoreThanOneDolphinModel{
    @DolphinModel
    private TestBean testBean1;

    @DolphinModel
    private TestBean testBean2;
}

