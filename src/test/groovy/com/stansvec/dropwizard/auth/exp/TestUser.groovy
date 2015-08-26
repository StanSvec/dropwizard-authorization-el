/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp

import groovy.transform.ToString

/**
 * Test principal.
 */
@ToString
class TestUser {

    static def ADMIN = new TestUser("admin1", Collections.singleton("ADMIN_ROLE"))

    public static def USER = new TestUser("user1", Collections.singleton("USER_ROLE"))

    static def USERS = [ADMIN, USER]

    final String name;

    final Set<String> roles;

    public TestUser(String name, Set<String> roles) {
        this.name = name;
        this.roles = roles
    }

    boolean hasRole(String role) {
        return roles.contains(role)
    }
}
