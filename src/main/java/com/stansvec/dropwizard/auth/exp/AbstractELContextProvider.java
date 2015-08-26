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
 * Allows subclasses to easily define imports and request variables.
 * Adding request variables <strong>user</strong> and <strong>principal</strong> for principal instance and <strong>ctx</strong> for {@link ContainerRequestContext}.
 *
 * @author Stan Svec
 */
public abstract class AbstractELContextProvider<P> implements ELContextProvider<P> {

    // Not thread safe, but the state shouldn't be modified after publication in the current implementation
    private final ELContext globalContext;

    public AbstractELContextProvider() {
        this(Collections.<Variable>emptyList());
    }

    public AbstractELContextProvider(ImportHandler importHandler) {
        this(importHandler, Collections.<Variable>emptyList());
    }

    public AbstractELContextProvider(List<Variable> vars) {
        this(new NoJavaLangImportHandler(), vars);
    }

    public AbstractELContextProvider(ImportHandler importHandler, List<Variable> vars) {
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        this.globalContext = new NoDefImportELContext(expressionFactory, checkNotNull(importHandler));
        this.globalContext.getELResolver(); // init ELResolver eagerly before publication

        for (Variable var : checkNotNull(vars)) {
            ValueExpression exp = expressionFactory.createValueExpression(globalContext, "${" + var.expression + "}", Object.class);
            globalContext.getVariableMapper().setVariable(var.variable, exp);
        }
    }

    protected abstract void defineRequestVariables(Map<String, Object> beans, P principal, ContainerRequestContext ctx);

    @Override
    public ELContext createRequestContext(P principal, ContainerRequestContext ctx) {
        Map<String, Object> beans = new HashMap<>();
        beans.put("user", principal);
        beans.put("principal", principal);
        beans.put("ctx", ctx);
        defineRequestVariables(beans, principal, ctx);
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
