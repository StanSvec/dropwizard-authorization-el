[![Build Status](https://travis-ci.org/StanSvec/dropwizard-authorization-mvel.svg?branch=master)](https://travis-ci.org/StanSvec/dropwizard-authorization-mvel)

### MVEL Expression Engine for dropwizard-authorization
```
For Dropwizard 0.8.x use 0.1.4 version from jCenter:
group: 'com.stansvec', name: 'dropwizard-authorization-mvel', version: '0.1.4'
Dependency to MVEL library must be provided. Tested with MVEL version 2.2.5.Final.
```
This is MVEL Expression Engine for [Authorization for Dropwizard](https://github.com/StanSvec/dropwizard-authorization) extension allowing using expressions in `@Auth#check()` element.

#### Expression usage examples
```java
@Path("/protectedByExp")
public class ProtectedResource {

    @GET
    @Path("/admin1")
    @Auth(check = "user.name == 'admin1'")
    public void nameChecked() {}

    @GET
    @Path("/admin1-admin-role")
    @Auth(check = "name('admin1') && user.roles contains 'ADMIN_ROLE'")
    public void nameByFunctionAndRoleChecked() {}
}

public class TestUser {

    private final String name;

    private final Set<String> roles;

    public TestUser(String name, Set<String> roles) {
        this.name = name;
        this.roles = roles
    }

    public String getName() {
        return name
    }

    public Set<String> getRoles() {
        return roles
    }
}
```

#### Defining functions and request variables
Functions and request variables can be defined by implementing `MvelVariableProvider` interface. Easier solution is to use `DefaultMvelVariableProvider` class which for every request already defines variables **user** and **principal** for principal instance and variable **ctx** for `ContainerRequestContext`. Another variables and custom functions can be added by subclassing `DefaultMvelVariableProvider` like this:
```java
public class CustomVariableProvider extends DefaultMvelVariableProvider<TestUser> {

    @Override
    public void defineFunctions(Set<String> functions) {
        functions.add("def name(userName) { user.name == userName }");
    }

    @Override
    public void defineRequestVariables(Map<String, Object> vars, TestUser principal, ContainerRequestContext ctx) {
        vars.put("roles", principal.getRoles());
        vars.put("uri", ctx.getUriInfo().getPath());
    }
}
```

#### Expression engine creation and registration
```java
new AuthConfiguration.Builder<TestUser>()
    .setPolicy(..)
    .supportExpressions(new MvelExpressionEvaluation<>(new CustomVariableProvider()))
    .addRole(..)
    .setAuthentication(..)
    .build();
```