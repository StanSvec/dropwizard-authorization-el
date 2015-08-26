/*
 * This code is licensed under "The MIT License"
 * Copyright (c) 2015 by Stan Svec
 *
 * Please see the included 'LICENSE.txt' file for the full text of the license.
 */
package com.stansvec.dropwizard.auth.exp

import com.stansvec.dropwizard.auth.AuthConfiguration
import com.stansvec.dropwizard.auth.ProtectionPolicy
import io.dropwizard.testing.junit.ResourceTestRule
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory
import org.junit.Rule
import spock.lang.Specification

import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import javax.xml.bind.DatatypeConverter

/**
 * Tests for authentication and authorization.
 */
class AuthorizationTest extends Specification {

    @Rule
    ResourceTestRule rule = ResourceTestRule
            .builder()
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .addResource(new ProtectedResource())
            .addProvider(createConfiguration())
            .build();

    static AuthConfiguration createConfiguration() {
        return new AuthConfiguration.Builder<TestUser>()
                .setPolicy(ProtectionPolicy.PROTECT_ANNOTATED_ONLY)
                .supportExpressions(new ELEvaluation<>(new TestContextProvider()))
                .addRole(new Admin())
                .setAuthentication(TestAuthenticator.AUTH_FACT)
                .build();
    }

    def "test authorization for both policies"() {
        expect:
        status.statusCode == getStatus(rule, resource, user)

        where:
        resource                              | user                | status
        "/protectedByExp/admin-combined"      | TestUser.ADMIN      | Response.Status.NO_CONTENT
        "/protectedByExp/admin-combined"      | TestUser.USER       | Response.Status.UNAUTHORIZED
        "/protectedByExp/admin-exp-only"      | TestUser.ADMIN      | Response.Status.NO_CONTENT
        "/protectedByExp/admin-exp-only"      | TestUser.USER       | Response.Status.UNAUTHORIZED
        "/protectedByExp/admin-no-exp"        | TestUser.ADMIN      | Response.Status.NO_CONTENT
        "/protectedByExp/admin-no-exp"        | TestUser.USER       | Response.Status.UNAUTHORIZED
        "/protectedByExp/int-var"        | TestUser.ADMIN      | Response.Status.NO_CONTENT
        "/protectedByExp/int-var"        | TestUser.USER       | Response.Status.UNAUTHORIZED
        "/protectedByExp/static-field"        | TestUser.USER      | Response.Status.NO_CONTENT
        "/protectedByExp/static-field"        | TestUser.ADMIN       | Response.Status.UNAUTHORIZED
        "/protectedByExp/lambda"        | TestUser.ADMIN      | Response.Status.NO_CONTENT
        "/protectedByExp/lambda"        | TestUser.USER       | Response.Status.UNAUTHORIZED
    }

    int getStatus(ResourceTestRule resources, String resource, TestUser user) {
        return resources.getJerseyTest()
                .target(resource)
                .request()
                .header(HttpHeaders.AUTHORIZATION, user != null ? "Basic " + DatatypeConverter.printBase64Binary((user.name + ":pass").getBytes("UTF-8")) : "")
                .get()
                .getStatus()
    }
}
