/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp;

import com.stansvec.dropwizard.auth.exp.custom.CustomContextELManager;
import com.stansvec.dropwizard.auth.exp.custom.NoDefImportELContext;
import com.stansvec.dropwizard.auth.exp.custom.NoJavaLangImportHandler;

import javax.el.*;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides requested scoped context extended with global scope context.
 *
 * Global scope context can contain:
 *  1. Custom imports by providing {@link ImportHandler}.
 *  2. Custom variables by providing list of {@link Variable}s.
 *
 * Subclasses can define request scope bean variables by overriding {@link #defineRequestBeans(Map, Object, ContainerRequestContext)} method.
 * Following bean variables are added to every request context:
 *  1. <strong>user</strong> - containing principal instance
 *  2. <strong>principal</strong> - alias for user, i.e. containing same principal instance as user variable
 *  3. <strong>ctx</strong> - containing  {@link ContainerRequestContext}
 *
 * @author Stan Svec
 */
public class DefaultELContextProvider<P> implements ELContextProvider<P> {

    // Not thread safe class, but in the current implementation the state is not modified after publication
    private final ELContext globalContext;

    public DefaultELContextProvider() {
        this(Collections.<Variable>emptyList());
    }

    public DefaultELContextProvider(ImportHandler importHandler) {
        this(importHandler, Collections.<Variable>emptyList());
    }

    public DefaultELContextProvider(List<Variable> vars) {
        this(new NoJavaLangImportHandler(), vars);
    }

    public DefaultELContextProvider(ImportHandler importHandler, List<Variable> vars) {
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        this.globalContext = new NoDefImportELContext(expressionFactory, checkNotNull(importHandler));
        this.globalContext.getELResolver(); // init ELResolver eagerly before publication

        for (Variable var : checkNotNull(vars)) {
            ValueExpression exp = expressionFactory.createValueExpression(globalContext, "${" + var.expression + "}", Object.class);
            globalContext.getVariableMapper().setVariable(var.variable, exp);
        }
    }

    protected void defineRequestBeans(Map<String, Object> beans, P principal, ContainerRequestContext ctx) {
        // subclass may override and add another bean variables
    }

    @Override
    public ELContext createRequestContext(P principal, ContainerRequestContext ctx) {
        Map<String, Object> beans = new HashMap<>();
        beans.put("user", principal);
        beans.put("principal", principal);
        beans.put("ctx", ctx);
        defineRequestBeans(beans, principal, ctx);
        return createContext(beans);
    }

    private ELContext createContext(Map<String, Object> beans) {
        ELManager manager = new CustomContextELManager(new NoDefImportELContext(globalContext));
        for (Map.Entry<String, Object> e : beans.entrySet()) {
            manager.defineBean(e.getKey(), e.getValue());
        }

        return manager.getELContext();
    }
}
