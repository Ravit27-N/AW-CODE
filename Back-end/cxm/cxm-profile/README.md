# cxm-profile

## Run Junit test

### Controller test

- Test a specific controller test class:

    - CXM_DISCOVERY_TYPE: Type of service discovery
    - CXM_DISCOVERY_HOST: IP/domain of service discovery server
    - CXM_DISCOVERY_PORT: Port of service discovery server
    - CXM_DISCOVERY_INSTANCE_HOSTNAME: IP/domain of server which the testing microservice is running
      on

    ```
    mvn test "-Dmaven.test.skip=false" "-Dtest=UserControllerTest" "-DCXM_DISCOVERY_TYPE=eureka" "-DCXM_DISCOVERY_HOST=10.2.50.89"  "-DCXM_DISCOVERY_PORT=8761" "-DCXM_DISCOVERY_INSTANCE_HOSTNAME=10.2.50.89"
    ```

- Test a specific controller test method:

    ```
    mvn test "-Dmaven.test.skip=false" "-Dtest=UserControllerTest#testRequestResetPassword" "-DCXM_DISCOVERY_TYPE=eureka" "-DCXM_DISCOVERY_HOST=10.2.50.89"  "-DCXM_DISCOVERY_PORT=8761" "-DCXM_DISCOVERY_INSTANCE_HOSTNAME=10.2.50.89"
    ```


