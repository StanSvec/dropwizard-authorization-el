/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp

import com.stansvec.dropwizard.auth.Role

import javax.ws.rs.container.ContainerRequestContext

/**
 * Test role.
 */
class Admin implements Role<TestUser> {

    @Override
    public boolean hasRole(TestUser user, ContainerRequestContext ctx) {
        return user.hasRole("ADMIN_ROLE")
    }
}
