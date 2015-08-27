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
 * Context provider for tests.
 */
class TestContextProvider extends DefaultELContextProvider<TestUser> {

    def TestContextProvider() {
        super(createImportHandler(), createVariables())
    }

    static ImportHandler createImportHandler() {
        NoJavaLangImportHandler imports = new NoJavaLangImportHandler()
        imports.importClass("com.stansvec.dropwizard.auth.exp.TestUser")
        imports.importStatic("com.stansvec.dropwizard.auth.exp.AuthorizationMethods.hasName")
        return imports
    }

    static List<Variable> createVariables() {
        return [new Variable("username", "user.name"),
                new Variable("nameLength", "username.length()"),
                new Variable("path", "uriInfo.path")]
    }

    @Override
    protected void defineRequestBeans(Map<String, Object> beans, TestUser principal, ContainerRequestContext ctx) {
        beans.put("roles", principal.getRoles())
        beans.put("uriInfo", ctx.getUriInfo())
    }
}
