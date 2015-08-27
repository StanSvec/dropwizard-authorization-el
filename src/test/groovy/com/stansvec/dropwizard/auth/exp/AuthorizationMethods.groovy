/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp

/**
 * Methods used by authorization test expressions.
 */
class AuthorizationMethods {

    static boolean hasName(def user, def name) {
        return user.name == name
    }
}
