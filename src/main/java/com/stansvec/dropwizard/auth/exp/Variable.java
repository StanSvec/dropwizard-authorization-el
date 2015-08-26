/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Expression variable.
 *
 * @author Stan Svec
 */
public class Variable {

    final String variable;

    final String expression;

    public Variable(String variable, String expression) {
        this.variable = checkNotNull(variable);
        this.expression = checkNotNull(expression);
    }
}
