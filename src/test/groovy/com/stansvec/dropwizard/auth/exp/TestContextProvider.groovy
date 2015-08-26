/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp

import com.stansvec.dropwizard.auth.exp.custom.NoJavaLangImportHandler

import javax.el.ImportHandler
import javax.ws.rs.container.ContainerRequestContext

/**
 * Variable provider for tests.
 */
class TestContextProvider extends AbstractELContextProvider<TestUser> {

    def TestContextProvider() {
        super(createImportHandler(), createVariables())
    }

    static ImportHandler createImportHandler() {
        NoJavaLangImportHandler imports = new NoJavaLangImportHandler()
        imports.importClass("com.stansvec.dropwizard.auth.exp.TestUser")
        imports.importStatic("com.stansvec.dropwizard.auth.exp.AuthorizationMethods.name")
        return imports
    }

    static List<Variable> createVariables() {
        List<Variable> vars = new ArrayList<>()
        vars.add(new Variable("username", "user.name"))
        vars.add(new Variable("nameLength", "username.length()"))
        return vars
    }

    @Override
    protected void defineRequestVariables(Map<String, Object> beans, TestUser principal, ContainerRequestContext ctx) {

    }
}
