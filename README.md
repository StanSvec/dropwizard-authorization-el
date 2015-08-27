[![Build Status](https://travis-ci.org/StanSvec/dropwizard-authorization-mvel.svg?branch=master)](https://travis-ci.org/StanSvec/dropwizard-authorization-mvel)

### Expression Engine using Java Expression Language for dropwizard-authorization
```
For Dropwizard 0.8.x use 0.1.4 version from jCenter:
group: 'com.stansvec', name: 'dropwizard-authorization-el', version: '0.1.4'
```
This is [JSR 341: Expression Language 3.0](https://jcp.org/en/jsr/detail?id=341) Expression Engine for [Authorization for Dropwizard](https://github.com/StanSvec/dropwizard-authorization) extension allowing using expressions in `@Auth#check()` element.

#### Imports, variables and beans definition
Every expression is evaluated using context which is created for every request. This context may contain beans defined for current request as well as imports and variables defined globally. A class implementing `ELContextProvider` interface is responsible for providing such context. The easiest way is to use `DefaultELContextProvider` implementation. This implemantation already defines for every request following beans:
1. **user** - containing principal instance
2. **principal** - alias for user, i.e. containing same principal instance as user variable
3. **ctx** - containing  `ContainerRequestContext` instance

`DefaultELContextProvider` class has two optional constructor parameters `ImportHandler` *importHandler* and `List<Variable>` *vars*.
* `ImportHandler` class can be used for defining custom imports.
* List of `Variable`s can be used for defining custom variables.

`DefaultELContextProvider` can be subclassed and custom request bean variables can be defined by overriding *defineRequestBeans* method.

```
Note: There is NoJavaLangImportHandler class which doesn't import java.lang package in default. This is due to security reasons as otherwise methods like System.exit can be called.
```

#### DefaultELContextProvider usage example

```java
public class TestContextProvider extends DefaultELContextProvider<TestUser> {

    public TestContextProvider() {
        super(createImportHandler(), createVariables());
    }

    private static ImportHandler createImportHandler() {
        NoJavaLangImportHandler imports = new NoJavaLangImportHandler();
        imports.importClass("com.stansvec.dropwizard.auth.exp.TestUser");
        imports.importStatic("com.stansvec.dropwizard.auth.exp.AuthorizationMethods.hasName");
        return imports;
    }

    private static List<Variable> createVariables() {
        List<Variable> vars = new ArrayList<>();
        vars.add(new Variable("username", "user.name"));
        vars.add(new Variable("nameLength", "username.length()"));
        vars.add(new Variable("path", "uriInfo.path"));
        return vars;
    }

    @Override
    protected void defineRequestBeans(Map<String, Object> beans, TestUser principal, ContainerRequestContext ctx) {
        beans.put("roles", principal.getRoles());
        beans.put("uriInfo", ctx.getUriInfo());
    }
}

public class TestUser {

    public static TestUser USER = new TestUser("user1", Collections.singleton("USER_ROLE"));
    
    private final String name;

    private final Set<String> roles;

    public TestUser(String name, Set<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return roles;
    }
}

public class AuthorizationMethods {

    public static boolean hasName(TestUser user, String name) {
        return name.equals(user.name);
    }
}
```

#### Expression usage examples
```java
@Path("/protectedByExp")
public class ProtectedResource {

    @GET
    @Path("/admin-combined")
    @Auth(roles = Admin.class, check = "user.name == 'admin1'")
    public void combined() {}

    @GET
    @Path("/admin-exp-only")
    @Auth(check = "hasName(user, 'admin1') && roles.stream().anyMatch(r -> (r == 'ADMIN_ROLE'))")
    public void methodExecutionAndStreamUsage() {}

    @GET
    @Path("/int-var")
    @Auth(check = "nameLength == 6")
    public void variableUsage() {}

    @GET
    @Path("/static-field")
    @Auth(check = "TestUser.USER.name == username")
    public void staticFieldUsage() {}

    @GET
    @Path("/lambda")
    @Auth(check = "(s -> s.toUpperCase().substring(0, s.length() - 1)) (username) == 'ADMIN'")
    public void lambdaUsage() {}

    @GET
    @Path("/user/admin1")
    @Auth(check = "path == 'protectedByExp/user/' += username")
    public void pathVariable() {}
}
```

#### Expression engine creation and registration
```java
new AuthConfiguration.Builder<TestUser>()
    .setPolicy(..)
    .supportExpressions(new ELEvaluation<>(new TestContextProvider()))
    .addRole(..)
    .setAuthentication(..)
    .build();
```