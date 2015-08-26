/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * Evaluates authorization expressions using Java EL 3.0 (JSR-341).
 * @see <a href="https://jcp.org/aboutJava/communityprocess/final/jsr341/index.html"></a>
 *
 * @author Stan Svec
 */
public class ELEvaluation<P> implements ExpressionEngine<P> {

    private final ELContextProvider<? super P> contextProvider;

    private final ExpressionFactory expressionFactory;

    public ELEvaluation(ELContextProvider<? super P> contextProvider) {
        this.contextProvider = contextProvider;
        this.expressionFactory = ExpressionFactory.newInstance();
    }

    @Override
    public void registerExpression(String expression) {
        // No action required as expressions are already cached in ExpressionFactoryImpl class.
    }

    @Override
    public boolean evaluate(String expression, P principal, ContainerRequestContext ctx) {
        ELContext reqCtx = contextProvider.createRequestContext(principal, ctx);
        ValueExpression exp = expressionFactory.createValueExpression(reqCtx, "${" + expression + "}", Boolean.class);
        return (boolean) exp.getValue(reqCtx);
    }
}
