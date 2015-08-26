/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp;

import javax.el.ELContext;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * The implementing class provides request scoped context for authorization expression evaluation.
 *
 * @author Stan Svec
 */
public interface ELContextProvider<P> {

    ELContext createRequestContext(P principal, ContainerRequestContext ctx);
}