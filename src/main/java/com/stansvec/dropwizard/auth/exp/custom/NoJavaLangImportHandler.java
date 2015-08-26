/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp.custom;

import javax.el.ImportHandler;

/**
 * Forbids java.lang import.
 *
 * @author Stan Svec
 */
public class NoJavaLangImportHandler extends ImportHandler {

    @Override
    public void importPackage(String packageName) {
        if ("java.lang".equals(packageName)) {
            return;
        }

        super.importPackage(packageName);
    }

    public void importJavaLangPackage() {
        super.importPackage("java.lang");
    }
}
