# Installation:

***

- Clone the ``lib-common-configuration`` repository
- Add the following dependency to your project's ``pom.xml``:

> ```xml
> <dependency>
>    <groupId>com.innovationandtrust.signature</groupId>
>   <artifactId>lib-common-configuration</artifactId>
>  <version>${lib-common-configuration.version}</version>
> </dependency>
> ```

- Click on ``maven`` on the right hand side and select ``Reload All Maven Projects`` to download the dependency.
- Note: On the application requires Java ``17`` in your system

# Usage:

***
### To use ``security configuration`` library you need to do in the following step:
- Navigate to ``.yml`` file and add the following example properties:
> ```yaml
> signature:
>   security:
>      keycloak-realm: ${SIGNATURE_KEYCLOAK_REALM:signature-identification}
>      keycloak-base-url: ${SIGNATURE_KEYCLOAK_URL:http://127.0.0.1:8080}
> ```
- Add the following ``annotation`` to your class:
> ```
> @Configuration
> @EnableConfigurationProperties({SecurityProperty.class})
> ```
- Extends ``SecurityConfiguration`` class and do in the following:
> ```
> public class YourClass extends CommonSecurityConfiguration {
>     @Autowired
>     public YourClass(SecurityProperty keycloakProperty) {
>          super(property.getKeycloakBaseUrl(), property.getKeycloakRealm(), property.getOrigins());
>     }
>  // Additional implementation
> }
> ```

***


### To use the ``swagger configuration`` library you need to do in the following step:

- Navigate to ``.yml`` file and add the following example properties:

> ```yaml
> #configure the information for swagger-ui 
> #to enabled security for api in the swagger ui 
> #you need to add security key
> signature:
>   swagger:
>     info:
>       security-key: your key 
> #note: if you want to add more properties you can navigate
> #to SwaggerProperty
>
> #configure for swagger-ui security
> springdoc:
>   swagger-ui:
>       oauth:
>       client-id: ${signature.keycloak.resource}
>       client-secret: ${signature.keycloak.secret}
>       realm: ${signature.keycloak.realm}
>```

- Add the following ``annotation`` to your class:

> ```
>  @Configuration
>  @EnableConfigurationProperties({SecurityProperty.class, SwaggerProperty.class})
> ```

- Extend the class ``SwaggerConfiguration`` and do in the following

> ```
> public class YourClass extends SwaggerConfiguration {
>   public YourClass(SecurityProperty keycloakProperty, SwaggerProperty swaggerProperty) {
>    super(keycloakProperty, swaggerProperty);
>   }
> // Additional implementation
> }
> ```

- If you want to group your api in the ``swagger`` documentation you need to do in the following:

> ``` 
> @Bean
> GroupedOpenApi methodName() {
> return super.groupedOpenApi(groupName, "/**/path/**");
> }
> //Note: If you have several groups you need to do as above for each group.
>```

***

### To use the web mvc configuration library you need to do in the following step:
- Add the following ``annotation`` to your class:
> ```
> // Additional Annotations
> @Configuration
> ```

- Extends ``WebMvcConfiguration`` class:
> ```
> public class YourClass extends WebMvcConfiguration {
> // Additional implementation
> }
> ```
***
### To use the ``common exception`` library you need to do in the following step:
- Add the following ``annotation`` to your class:
> ```
> // Additional Annotations
> @ControllerAdvice
>```

- Extends the class ``CommonExceptionController``:
> ```
> public class YourClass extends CommonExceptionHandler {
> // Additional implementation
> }
> ```

***
### To use ``LowerCaseClassNameResolver`` class
- Add the following ``annotation`` to your class:
> ```
> @JsonTypeIdResolver(LowerCaseClassNameResolver.class)
>```
