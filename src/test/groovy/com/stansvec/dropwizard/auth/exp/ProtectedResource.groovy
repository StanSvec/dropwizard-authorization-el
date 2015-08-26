/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp

import com.stansvec.dropwizard.auth.Auth
import com.stansvec.dropwizard.auth.NoAuth

import javax.ws.rs.GET
import javax.ws.rs.Path

/**
 * Testing resource.
 */
@Path("/protectedByExp")
class ProtectedResource {

    @GET
    @Path("/admin-combined")
    @Auth(roles = Admin.class, check = "user.name == 'admin1'")
    void combined() {}

    @GET
    @Path("/admin-exp-only")
    @Auth(check = "name(user, 'admin1') && principal.roles.stream().anyMatch(r -> r == 'ADMIN_ROLE')")
    void expressionOnly() {}

    @GET
    @Path("/int-var")
    @Auth(check = "nameLength == 6")
    void intVariable() {}

    @GET
    @Path("/static-field")
    @Auth(check = "TestUser.USER.name == username")
    void staticField() {}

    @GET
    @Path("/lambda")
    @Auth(check = "(s -> s.toUpperCase().substring(0, s.length() - 1)) (username) == 'ADMIN'")
    void lambda() {}

    @GET
    @Path("/admin-no-exp")
    @Auth(roles = Admin.class)
    void noExpression() {}

    @GET
    @Path("/unprotected")
    @NoAuth
    void unprotectedMethod() {}
}
