# Installation:

***

- Clone the ``lib-common-utils`` repository
- Add the following dependency to your project's ``pom.xml``:

> ```xml
> <dependency>
>    <dependency>
>        <groupId>com.innovationandtrust.signature</groupId>
>        <artifactId>lib-common-utils</artifactId>
>        <version>${lib-common-utils.version}</version>
>    </dependency>
> </dependency>
> ```
  
- Click on ``maven`` on the right hand side and select ``Reload All Maven Projects`` to download the dependency.
- Note: On the application requires Java ``17`` in your system.

***

# Usage:

***

### To use ``ApiNg`` library you need to do in the following steps:

- Navigate to your application ``properties`` or ``yml`` file and added the following example properties:
> ```yaml
> signature:
>   api-ng:
>      url: ${API_NG_URL:xxx}
>      access-token: ${API_NG_ACCESS_TOKEN:hfjksadhflasldf}
>      certigna-user: pps#test
>      certigna-hash: ySsPUR23
>      certigna-role: 2
>      default-language: fr
>      test-file-path: ${API_NG_TEST_FILE:Get_Started_With_Smallpdf.pdf}
>      front-end-url: ${FRONT_END_URL:http://localhost:3000}
> ```

- Add the following ``annotation`` to your class:
>```
> @Configuration
> @EnableFeignClients(clients = {ApiNgFeignClient.class})
> @EnableConfigurationProperties(value = {ApiNGProperty.class}) 
> ```
- Extends ``ApiNgServiceProviderConfigurer`` class and do the following example:
> ```
> public class YourClass extends ApiNgServiceProviderConfigurer {}
> ```
> That's it ! You can inject ``ApiNgProvider`` into your class and be able to use the functionalities.
***
### To use ``Keycloak`` library you need to do in the following step:

- Navigate to your application ``.properties`` or ``.yml`` file and add the following example properties:

> ```yaml
> signature:
>   keycloak:
>   auth-server-url: ${signature.security.keycloak-base-url}
>   realm: ${signature.security.keycloak-realm}
>   resource: ${SIGNATURE_PROCESS_CONTROL_CLIENT_ID}
>   secret: ${SIGNATURE_PROCESS_CONTROL_CLIENT_SECRET}
>```

- Add the following ``annotation`` to your class:

> ```
> @Configuration
> @EnableConfigurationProperties(value =  {KeycloakProperties.class})
> ```

- Extends ``KeycloakConfig`` and do the following example:

> ```
> public class YourClass extends KeycloakConfig {
>    public KeycloakConfigurer(final KeycloakProperties properties) {
>        super(properties);
>    }
>  // Additional implementation
> }
>```
***

### To use ``File`` library you need to do in the following steps:

- Navigate to your application ``.properties`` or ``.yml`` file and add the following properties:

> ```yaml
> signature:
>   file:
>     base-path: your path
>     max-upload-size: 20
> # Available value of `data-unit` property are B, KB, MB, GB and TB.
>     data-unit: MB
>```
>

- Add the following ``annotation`` and do the following in your class:

> ```
> @Configuration
> @EnableConfigurationProperties(value =  {FileProperties.class})
>```

- Extends ``FileServiceConfigurer`` and do the following example:

> ```
> public class FileConfiguration extends FileServiceConfigurer {
>      public FileConfiguration(final FileProperties fileProperties) {
>           super(fileProperties);
>      }
>  // Additional implementation
> }
>```
> Note: The file path is the path where the ``file`` will be processed

> That's it ! You can inject ``FileProvider`` into your class and be able to use the functionalities.

***

### To use ``Mail`` library you need to do in the following step:

- Navigate to your application ``.properties`` or ``.yml`` file and added the following properties:

> ```yaml
> #Mail setting configuration
> mail:
> # If you using gmail of Google, no need to set host and port
> #host: smtp.gmail.com
> #port: 587
>   username: your username
>   password: your app password
>```

- Add the following ``annotation``:

> ```
> @Configuration
> @EnableConfigurationProperties(value = {MailSmtpProperty.class})
> ```

- Extends ``MailStmpConfigurer`` class and do the following example:

> ```
> public class YourClass extends MailSmtpConfigurer {
>   @Autowired
>   public EmailServiceConfig(final MailSmtpProperty smtpProperty) {
>       super(smtpProperty);
>   }
>  // Additional implementation
> }
> ```
> Note: The ``username`` and ``password`` are the credentials of the email account that will be used to send the email.

> That's it ! You can inject ``MailServiceProvider`` into your class and be able to use the functionalities.

***
### To use ``SMS`` library you need to do in the following step:

- Navigate to your application ``.properties`` or ``.yml`` file and added the following example properties:

> ```yaml
> signature:
>   sms-service:
>   is-enable: ${SMS_SERVICE_ENABLE:false}
>   url: ${SMS_SERVICE_URL:http://xx.x.xx.xxx:xxxx/sms-service/v1.0}
>   access-token: ${SMS_SERVICE_ACCESS_TOKEN}
>   product-token: ${SMS_SERVICE_PRODUCT_TOKEN}
>   sender: cm.com 
> ```

- Add the following ``annotation``:

> ``` 
> @Configuration
> @EnableFeignClients(clients = {SmsFeignClient.class})
> @EnableConfigurationProperties(value = {SMSProperty.class})
> ```

- Extends ``SmsServiceProviderConfigurer`` class and do the following example:

> ``` 
> public class SmsServiceProviderConfig extends SmsServiceProviderConfigurer {
>     public SmsServiceProviderConfig(final SMSProperty smsProperty) {
>       super(smsProperty);
>     }
>  // Additional implementation
> }
> ```
> That's it ! You can inject ``SmsServiceProvider`` into your class and be able to use the functionalities.
***
### To use ``Pdf`` library you just need to inject ``PdfProvider`` class in your class, and then you are good to go