/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp.custom;

import javax.el.ELManager;
import javax.el.StandardELContext;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Prevents creation of {@link StandardELContext}.
 *
 * @author Stan Svec
 */
public class CustomContextELManager extends ELManager {

    private StandardELContext elContext;

    public CustomContextELManager(StandardELContext elContext) {
        checkNotNull(elContext);
        this.elContext = elContext;
    }

    @Override
    public StandardELContext getELContext() {
        return elContext;
    }
}
