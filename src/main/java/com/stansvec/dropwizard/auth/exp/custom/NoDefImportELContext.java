/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp.custom;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ImportHandler;
import javax.el.StandardELContext;

/**
 * Due to security reasons this context prevents creation of default {@link ImportHandler} which imports java.lang package.
 *
 * @author Stan Svec
 */
public class NoDefImportELContext extends StandardELContext {

    private final ImportHandler importHandler;

    public NoDefImportELContext(ExpressionFactory expFact, ImportHandler importHandler) {
        super(expFact);
        this.importHandler = importHandler;
    }

    public NoDefImportELContext(ELContext context) {
        super(context);
        this.importHandler = context.getImportHandler();
    }

    @Override
    public ImportHandler getImportHandler() {
        return importHandler;
    }
}
