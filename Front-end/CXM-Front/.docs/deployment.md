# Deployments

This document intend to provide guide for configuration and deployment smartflow apps version 3.2 onward.

**Table of Contents**

- [Breaking changes](#breaking-changes)
- [Build](#configuration)
- [Configuration properties](#configuration-props)
- [Deployment](#deployment)


## Breaking changes (version 3.3)

  - Remove file `app.config.ts`
  - Add files `config.json`
  - Add files `config.development.json` (development only)

## Build

Build all microapps using the following command 

```powershell
ng build cxm-smartflow --prod --baseHref=/portal/cxm-smartflow/ --skip-nx-cache
ng build cxm-template --prod --baseHref=/portal/cxm-template/ --skip-nx-cache
ng build cxm-campaign --prod --baseHref=/portal/cxm-campaign/ --skip-nx-cache
ng build cxm-profile --prod --baseHref=/portal/cxm-profile/ --skip-nx-cache
ng build cxm-flow-traceability --prod --baseHref=/portal/cxm-flow-traceability/ --skip-nx-cache
ng build cxm-deposit --prod --baseHref=/portal/cxm-deposit/ --skip-nx-cache
ng build cxm-directory --prod --baseHref=/portal/cxm-directory/ --skip-nx-cache
ng build cxm-setting --prod --baseHref=/portal/cxm-setting/ --skip-nx-cache
ng build cxm-analytics --prod --baseHref=/portal/cxm-analytics/ --skip-nx-cache
```

The built package can be found under directory ```./dist/apps/```


## Configuration

Starting from version 3.3 onward the configuration can be done in file named `config.json` in smartflow microapp.


The config file can be found in `./dist/apps/cxm-smartflow/config.json`

```json
  // ./dist/apps/cxm-smartflow/config.json`
  {
  "apiGateway": "<Smartflow gateway url>",
  "keycloak": "<keycloak realm url>",
  "clientId": "<Keycloak client id>",
  "dummyClientSecret": "<Keycloak client secret>",
  "context": "<application context path>",
  "apps": []
 }
```

## Configuration properties

Property | Descriptions | Example value
---|---|---
apiGateway | base URL of gateway. It can be IP or domain name | http://smartflow-exmple.com
keycloak | url of keycloak realm | http://keycloak-exmple.com/auth/realms/example-realms
clientId | Angular client id from keycloak | angular-cxm-smartflow
dummyClientSecret | Angular client secret from keycloak | clientcridential
apps | list of remote microapps | check Microapps configurations




### Microapps configurations

Each microapps have configure according to following properties

Property | Descriptions | Example value
---|---|---
name | Name of the microapp | 
remoteEntry | URL to remote entry file of a microapp. Each microapp contain one remoteEntry.js file in root folder | http://localhost:3000/context-path/remoteEntry.js
routePath | Routing path of a microapp | Fixed value. do not change
ngModuleName | Root module of a microapp | Fixed value. do not change
remoteName | Unique name of microapp. Must be unique value |  cxmTemplate


### Deployement

- Step 1: Run the following command to build all microapps

```js
  // Powershell
  .\.docs\build.ps1

  // Linux
  ./.docs/build.sh
```

- Step 2: After build, copy all bundle packages in ```dist/apps/``` and configure deploy to nginx

- Step 3: Configure microapp

  change configuration in ```/portal/cxm-smartflow/config.json``` accoding hosting IP or domain.
  


  ```json
  // /portal/cxm-smartflow/config.json
  {
    "version": "0.32",
    "apiGateway": "http://localhost:5000",
    "keycloak": "<keycloak realm url>",
    "clientId": "<Keycloak client id>",
    "dummyClientSecret": "<Keycloak client secret>",
    "apps": [
     {
      "name": "Campaign",
      "remoteEntry": "http://localhost:8080/portal/cxm-campaign/remoteEntry.js",

      "routePath": "cxm-campaign",
      "ngModuleName": "CxmCampaignModule",
      "remoteName": "cxmCampaign"
     },
    {
      "name": "Template",
      "remoteEntry": "http://localhost:8080/portal/cxm-template/remoteEntry.js",

      "routePath": "cxm-template",
      "ngModuleName": "CxmTemplateModule",
      "remoteName": "cxmTemplate"
    },
     ...
     ...
    ]
  }

  ```
