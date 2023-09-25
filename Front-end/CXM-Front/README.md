

# Project Set Up

### Install dependency

<br>

    yarn install
<br>

### Set up the environment

- Go to directory : libs\shared\app-config\src\lib\app.config.ts
    - Change the host ip 
    - Change the Keycloak credential and clientId

***Note:*** This application use Oauth access token and refresh token for authentication and authorization to ensure the app working properly you have to configure access token lifetime. The token life time should not be too short or to long.

In Keycloak: Go to **client settings page** > **Advanced Settings** > change **Access Token Lifespan** to 5 or 10 minutes.


### Run application with nx

Each micro-apps can be executed independently:

- Main app (cxm-smartflow) : 

<br>
F
    nx serve cxm-smartflow
<br>

- Micro app (cxm-campaign): 

<br>

    nx serve cxm-campaign
<br>

- Micro app (cxm-template): 

<br>

    nx serve cxm-template
<br>

# Run Storybook

<br>

    nx storybook shared-storybook
<br>

or

<br>

    nx shared-storybook:storybook
<br>

# Remove an application or library in NX

<br>

    nx g rm [your_library_name]
<br>

# Rename Library

<br>

    nx g @nrwl/workspace:mv --project [old_library] --destination [new_library]
<br>

# Create Component Without Directory

<br>

    nx generate component [component_name] --flat
<br>

# Run All Test with Coverage

<br>

    nx  affected:test --codeCoverage
<br>

# CXM-SMARTFLOW Structure

```
.
└── root
    ├── apps
    │   |── cxm-smartflow 
    |   |__ cxm-campaign
    |   |__ cxm-template
    |
    └── libs
        ├── auth (dir)
        |   |__ data-access (angular: lib)
        |   |__ feature (angular: lib)
        |   |__ ui (dir)
        |   |__ util (angular: lib)
        |   |
        |___manage-my-campaign (dir)
        |   |__ data-access (angular:lib)
        |   |__ feature (angular: lib)
        |   |__ ui (dir)
        |   |  |__ feature-model (angular: lib)
        |   |  |__ feature-setting (angular: lib)
        |   |  |__ feature-destination (angular: lib)
        |   |__ util (angular: lib)
        |   |
        |___follow-my-campaign
        |   |__ data-access (angular: lib)
        |   |__ feature (angular: lib)
        |   |__ ui (dir)
        |   |__ util (angular: lib)
        |   |
        |___shared (dri)
        |   |__ data-access (dir)
        |   |   |__api ( angular: lib)
        |   |   |  |__ api.service
        |   |   |  |__ cxm-campaign.service
        |   |   |  |__ cxm-template.service
        |   |   |  |
        |   |   |__model (dir)
        |   |   |   |__ cxm-campaign (dir)
        |   |   |   |__ cxm-template (dir)
        |   |   |   |
        |   |__ directives (dir)
        |   |__ pipes (dir)
        |   |__ ui (dir)
        |   |__ utils (angular: lib)
        |   |__ assets (dir)
        
```   

# Project Configuration from Scratch

**Step 1:** Install nx for global environment 

<br>

    npm i nx -g
<br>

**Step 2:** Create workspace

<br>

    npx create-nx-workspace@latest
<br>

**Step3:** Install Angular

<br>

    npm install --save-dev @nrwl/angular
<br>

**Step 4:** Create Auth in Lib

 - Create Auth (dir)

<br>

    nx g @nrwl/angular:lib feature --directory=auth 
    nx g @nrwl/angular:lib util--directory=auth 
    nx g @nrwl/angular:lib data-access--directory=auth
<br> 

+ ui (dir)

**Step 5:** Create Home in lib

- Create home (dir)

<br>

    nx g @nrwl/angular:lib data-access --directory=home
    nx g @nrwl/angular:lib feature --directory=home
<br>

+ ui(dir)

**Step 6:** Install Angular Material

<br>

    npm install @angular/material @angular/cdk @angular/flex-layout @angular/animations 
    nx g @nrwl/angular:lib material --directory=shared
<br>

**Step 7:** Create Shared

+ create directives (dir)
+ pipes (dir)
+ ui (dir)
+ utils (angular: lib)

<br>

    nx g @nrwl/angular:lib data-access --directory=shared
    nx g @nrwl/angular:lib utils --directory=shared
<br>

+ assets (dir)

**Step 8:** Create navbar lib in Home/ui

<br>

    nx g @nrwl/angular:lib navbar --directory=home/ui
    nx g @nrwl/angular:lib footer --directory=home/ui
<br>

**Step 9:** Configure in home/feature(angular: lib)

<br>

    nx g @nrwl/angular:component home
<br>

**Step 10:** Install Material-ui/core

<br>

    npm install material-design-icons
<br>

**Step 11:** Create manage-my-campaign in lib

<br>

    nx g @nrwl/angular:lib data-access --directory=manage-my-campaign
    nx g @nrwl/angular:lib feature --directory=manage-my-campaign
    nx g @nrwl/angular:lib util --directory=manage-my-campaign
<br>

**Step 12:** Create cxm-campaign module in cxm-campaign (app)

<br>

    nx g @nrwl/angular:module cxm-campaign  
<br>

# View Graph in workspace

<br>

    nx dep-graph
<br>

**Create New Application** create new application in the workspace project.

<br>

    yarn nx g @nrwl/angular:application flow-traceability --e2eTestRunner=cypress  
<br>

# Test Deposit File Server

In order to test deposit file component in "Déposer un flux" you need to run a small test server that you can download here: https://drive.google.com/file/d/1U-Tot12gR84zdozV_fYAjQnU-JmZL5PE/view?usp=sharing

This small test server can either run on Linux and Windows. There is an instruction within the file Readme in the zip folder.
