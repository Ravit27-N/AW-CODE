# Allweb Recruitment Project (Angular + Java Spring)

This document intent to provide API specification to define client (Angular) and server (Spring boot) for RMS project.

## API specification

### Consideration for [CORS](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing)

We assume that client and server will host on different host:port so backend have to allow CORS request.

### Authentication

Protocol : OAuth2 ([PKCE flow](https://auth0.com/docs/flows/call-your-api-using-the-authorization-code-flow-with-pkce))

IDP: Keycloak

Authentication Header : `Authorization: Bearer <access_token_here>`

Refresh Token: `// TODO: Need to discuss about refresh token`

Oauth2 library for Angular: [angular-oauth2-oidc](https://github.com/manfredsteyer/angular-oauth2-oidc)

Oauth2 library for Java : `// TODO: to be added`

### Response content and status code

We will use JSON format for Request / Response object with correct content type like `Content-Type: application/json; charset=utf-8`

### Response status code should followed by below Http convention

200 : Ok, Accept, Created

400 : Bad request (Invalid / missing parameter)

401 : Unauthorized (required login)

403 : Forbidden (Not enough access right)

404 : Not found

422 : Validation error only

## API Endpoint

`Base url:`

`http://{host}[:port]/api/v1/*`

### Candidate

- Add new candidate
  > **POST** /candidate/

  `Request body`

    ```js
    {
      "candidate": {
        "photoUrl": "photo url",
        "firstname": "Sok",
        "lastname": "san",
        "gender": "male",
        "telephone": "012998877",
        "email": "soksan@gmailcom",
        "gpa": 3.4,
        "from": "RUPP",
        "priority": "Hight",
        "description": "...",ac
        "statusId": 1, // status id
        "active": true
      }
    }
    ```

- Edit candidate
  > **PATCH** /candidate/{id}

  `Request body`

  ```json
  {
    "photoUrl": "...",
    "firstname": "Fallen",
    "lastname": "Angel",
    "gender": "male",
    "telephone": "(855) 88 712 4996",
    "gpa": 4.0,
    "from": "RUPP",
    "priority": "HIGH",
    "description": "...",
    "statusId": 1,
  }
  ```

- view all candidates

  > **GET** /candidate?size=5&page=0

  `Request parame : size and page`

  &

  `Response multi candidates as list`

  ```js
  {
    "candidates": [
      {
        "id": "101",
        "photoUrl": "...",
        "firstname": "Fallen",
        "lastname": "Angel",
        "gender": "male",
        "telephone": "(855) 88 712 4996",
        "email": "jake@jake.com",
        "gpa": 4.0,
        "from": "RUPP",
        "priority": "HIGH",
        "description": "...",
        "createdAt": "03-Nov-2020 03:39 PM",
        "updatedAt": "03-Nov-2020 03:39 PM",
        "status": "In Progress"
      },
      ...
    ],
    "count": 5,
    "total": 20,
    "pageSize":5
  }
  ```

- View candidate detail

  > **GET** /candidate/{id}
  
  `Respone single candidate`

  ```js
  {
    "id": "101",
    "photoUrl": "...",
    "firstname": "Fallen",
    "lastname": "Angel",
    "gender": "male",
    "telephone": "(855) 88 712 4996",
    "email": "jake@jake.com",
    "gpa": 4.0,
    "from": "RUPP",
    "priority": "HIGH",
    "description": "...",
    "createdAt": "03-Nov-2020 03:39 PM",
    "updatedAt": "03-Nov-2020 03:39 PM",
    "status": "In Progress"
  }
  ```

- Update status detail candidate
  > **PATCH** /candidate/{id}/status

  `Request body :`

  ```js
    {
      "statusId": 1 // status id
    }
  ```

- Candidate's advance search

  > **GET** /candidate?name=ABC&gender=FEMALE&from=RUPP&gpa=1&position=java&page=0&size=5

  `Search request param : name, gender, from, gpa and position`

  `Pagination request param : page and size`

  `Response`

  ```js
  {
    "candidates": [
      {
        "id": "101",
        "photoUrl": "...",
        "firstname": "Fallen",
        "lastname": "Angel",
        "gender": "male",
        "telephone": "(855) 88 712 4996",
        "email": "jake@jake.com",
        "gpa": 4.0,
        "from": "RUPP",
        "priority": "HIGH",
        "description": "...",
        "createdAt": "03-Nov-2020 03:39 PM",
        "updatedAt": "03-Nov-2020 03:39 PM",
        "status": "In Progress"
    },
    ...
  ],
  "count": 5,
  "total": 12,
  "pageSize": 5
  }
  ```

### Candidate Report

- Get candidate report

  > **GET** /candidate/report?from=22/10/2020&to=22/11/2020&page=1&size=10

  `Request param`
  - from
  - to
  - page
  - size

  `Response`

  ```json
  {
    "candidates": [
      {
        "id": "101",
        "photoUrl": "...",
        "firstname": "Fallen",
        "lastname": "Angel",
        "gender": "Male",
        "telephone": "(855) 88 712 4996",
        "gpa": 4.0,
        "from": "RUPP",
        "priority": "HIGH",
        "title": "Interview for web developer position",
        "interviewDate": "03/11/2020",
        "english": "good",
        "flexibility":"good",
        "oral": "good",
        "logical":"good",
        "remark": ".......",
        "quiz": 80,
        "coding": 90
      },
      ...
    ],
    "count": 5,
    "total": 20,
    "pageSize":5
  }
  ```

### Candidate's Status

- Get status detail by id
  > **GET** /candidate/status/{id}

  `Response single status`

  ```json
    {
      "status-candidate" : {
        "id": 2,
        "title": "in progress",
        "description": ".....",
        "active": true,
        "created_at": "05/11/2020 - 15:23",
        "updated_ad": "05/11/2020 - 15:23",
        "isAbleDelete": false
      }
    }
  ```

  - Get all status

  > **GET** /candidate/status?page=1&size=10

  ` Response single status-candidate: `

  ```json
    {
      "status-candidate" : [
        {
          "id": 2,
          "title": "in progress",
          "description": ".....",
          "active": 1,
          "isAbleDelete": false,
          "createdAt": "22-10-2020",
          "updatedAt": "22-10-2020"
        },
        ...
      ],
      "total":10,
      "count": 2,
      "page": 1,
      "pageSize": 10
    }
  ```

- Create new status

  > **POST** /candidate/status

  ` Request body: `

  ```json
    {
      "title": "in progress",
      "description": "....."
    }
  ```

  `Response`

  ```json
  {
    "id": 1
  }
  ```

- Update status detail

  > **PATCH** /candidate/status/{id}

    `Request body`

    ```json
      {
        "title": "in progress",
        "description": "....."
      }
  ```

- Update status active state

  > **PATCH** /candidate/status/{id}/active

  `Request body`

  ```json
    {
      "active": true
    }
  ```

- Make status able to delete or not

  > **PATCH** /candidate/status/{id}/delectable

  `Request body`

  ```json
    {
      "delectable": false
    }
  ```

### Interview

- Get a single interview

  > **GET** /interview/{id}

  `Response`

  ```json
  {
    "id": 1,
    "title": "Php developer",
    "description": "...",
    "candidate": { "id": 1, "fullname": "Mr. Sok san" },
    "datetime": "22-12-2020 04:30",
    "status": "attended",
    "createdAt": "22-11-2020 04:30",
    "updatedAt": "22-11-2020 04:30"
  }
  ```

- Get interview list

  > **GET** /interview?page=1&size=20

  `Response`

  ```js
  {
    "interviews": [
      {
        "id": 1,
        "title": "Php developer",
        "description": "...",
        "candidate": { "id": 1, "fullname": "Mr Sok san" },
        "datetime": "22-11-2020 04:30",
        "statusId": 1,
        "reminderCount":10
      },
      ...
    ],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
  ```

- Get interview's result

  > **GET** /interview/{id}/result

  `Response`

  ```js
  {
    "score": "{\"quiz\":{\"score\": 10,\"max\":10},\"coding\":{\"score\": 10,\"max\":10}}",
    "avarage": float,
    "english": "Good",
    "logical": "Good",
    "flexibility": "Good",
    "oral": "Good",
    "remark": "comments ..."
  }
  ```

- Create new interview

  > **POST** /interview

  Request body:

  ```js
  {
    "candidateId": 1,
    "statusId": 1,
    "datetime": "10/11/2020 02:57",
    "sendInvite": false,
    "setReminder": false,
    "reminderTime": 10, //minutes
    "title": "PHP developer",
    "description": "..."
  }
  ```

  `Reponse`

  ```json
  {
    "id": 1
  }
  ```

- Update Interview by id

  > **PATCH** /interview/{id}

  Request body :

  ```js
  {
    "candidateId": 1,
    "statusId": 1,
    "title": "Php developer",
    "description": "...",
    "datetime": "10/11/2020 02:57"
  }
  ```

- Update Interview's status by id

  > **PATCH** /interview/{id}/status

  Request body :

  ```js
  {
    "statusId": 1
  }
  ```

- Soft delete interview

  > **PATCH** /interview/{id}/delete

  `Request body`

  ```json
  {
    "isDeleted": true
  }
  ```

- Add/ update result to interview

  > **POST** /interview/{id}/result

  `request body`

  ```json
  {
    "score": "{\"quiz\":{\"score\": 10,\"max\":10},\"coding\":{\"score\": 10,\"max\":10}}",
    "english": "Good",
    "logical": "Good",
    "flexibility": "Good",
    "remark": "comments ..."
  }
  ```

- Delete interview

  > **DELETE** /interview/{id}

### Interview's status

- Get a single status

  > **GET** /interview/status/{id}

  `Response`

  ```js
  {
    "id": 1,
    "name": "Attended",
    "createdAt":"10/11/2020 02:57",
    "updatedAt": "10/11/2020 02:57"
  }
  ```

- Get status list

  > **GET** /interview/status/

  `Response`

  ```js
  {
      "status":[
        {
          "id": 1,
          "name": "Attended",
          "createdAt": "10/11/2020 02:57",
          "updatedAt": "10/11/2020 02:57"
        },
        ...
      ]
  }
  ```

- Create Status

  > **POST** /interview/status

  `Request body :`

  ```js
  {
      "name":"In Progress",
  }
  ```

  `Response :`

  ```js
  {
      "id": 1
  }
  ```

- Update Status

  > **PATCH** /interview/status/{id}

  `Request body :`

  ```js
  {
      "name": "In Progress"
  }
  ```

- Delete Status

  > **DELETE** /interview/status/{id}

### Activity

- List all activities
  > **GET** /activity?page=1&size=20

  ```json
  {
    "activities": [
      {
        "id": "1",
        "date": "06-11-2020",
        "title": "New Request",
        "description": "...",
        "candidate": {
          "id": "1"
          "full_name": "Mr. Sok san"
        }
        "author": {
          "id": "1",
          "full_name": "Mr. Admin Admin"
        }
      }, ...
    ],
    "total":"100",
    "page":"1",
    "pageSize":"10"
  }
  ```

- Add new activity
  > **POST** /activity/

  `Request body`

  ```json
  {
    "candidate_id": 1,
    "statusId": 1,
    "title": "New Request",
    "description": "..."
  }
  ```

  `Response`

  ```json
  {
    "activityId": 1
  }
  ```

- Update activity
  > **PATCH** /activity/

  'Request body'

  ```json
  {
    "activityId":1,
    "candidateId": 1,
    "statusId": 1,
    "description": "..."
  }
  ```

### Result

- List all results
  > **GET** /result?page=1&size=10

  `Request Params`

  ```json
  page=1, size=10
  ```

  `Response:`

  ```json
  {
    "results":  [
      {
        "id": 1,
        "oral": "Good",
        "average":"90.00%",
        "interview_date":"22-11-2020",
        "interview": {
          "id": 1,
          "title": "PHP"
        }
        "candidate": {
          "id": 1,
          "full_name": "Mr. Sok san"
        }
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
  ```

- Update result
  > **Patch** /result/

  `Request body`

  ```json
  {
    "resultId": 1,
    "candidateId": 1,
    "english": "Good",
    "logical": "Good",
    "flexibility": "Good",
    "oral": "Good",
    "score": "45",
    "remark": "...",
    "files": [
      {
        "file: "..."
      }
    ]
  }
  ```
  
### Reminder

- List all reminders
  
  > **GET** /reminder?page=1&size=10

  `Request Params`

    ```json
    {
      page=1, size=10
    }
    ```

  `Response Body`

    ```json
    {
      "reminders": [
          {
            "id": "1",
            "candidate": {
              "id": "1",
              "fullName": "Mr. Sok san",
            },
            "reminderType": "Normal",
            "title": "Meet with new candidate",
            "description": "Contact the candidate for sign contract",
            "dateReminder": "23-10-2018 05:33", //12h format: dd-MM-yyyy hh:mm
            "active": true,
          }, ...
      ],
      "page":1,
      "PageSize":20,
      "total":100,
    }
    ```

- Get reminder's detail
  
  > **GET** /reminder/{id}

  `Response Body`

  ```js
    {
      "reminder": {
        "id": "001",
        "candidate": {
          "id": "001",
          "fullName": "Mr. Sok San"
        }
        "reminderType": "Normal",
        "title": "Meet with new candidate",
        "description": "...",
        "dateReminder": "23-10-2018 05:33",
        "createdAt": "17-10-2018 02:56", //12h format: dd-MM-yyyy hh:mm
        "updatedAt": "22-10-2018 05:32" //12h format: dd-MM-yyyy hh:mm
      }
    }
      ```

- Get reminder by Cadidate's id
  
  > **GET** /reminder/candidate/{id}?page=0&size=10
​
  `Request Params`

  ```json
  {
    page=0, size=10
  }
  ```

  `Response Body`
  
  ```json
  {
    "reminders": [
        {
          "id": "1",
          "candidate": {
            "id": "1",
            "fullName": "Mr. Sok san"
          },
          "reminderType": "Normal",
          "title": "Meet with new candidate",
          "description": "Contact the candidate for sign contract",
          "dateReminder": "23-10-2018 05:33", //12h format: dd-MM-yyyy hh:mm
          "active": true,
        }, ...
    ],
    "page":1,
    "PageSize":20,
    "total":100,
  }
  ```

- Manage Reminder detail
  
  - [delete]
  
    > **DELETE** /reminder/{id}

  - Activate/deactivate reminder
  
    > **Patch** /reminder/{id}/active

    `Request body`

    ```json
    {
      "active": true
    }
    ```

- Update remider detail
  
  > **Patch** /reminder/{id}

  `Request Body`

  `Normal Reminder type:`

  ```js
  {
    "userId": 1,
    "reminderType": "normal",
    "title": "new interview",
    "description": "new description",
    "dateReminder": "22-11-2020",
    "active": true
  }
  ```

  `Special Reminder type:`

  ```js
  {
    "userId": 1,
    "reminderType": "special",
    "candidateId": 1,
    "title": "new interview",
    "description": "new description",
    "dateReminder": "22-11-2020",
    "active": true
  }
  ```

  `Interview Reminder type:`

  ```js
  {
    "userId": 1,
    "reminderType": "interview",
    "interviewId": 1,
    "title": "new interview",
    "description": "new description",
    "dateReminder": "22-11-2020",
    "active": true
  }
  ```

- create a new reminder

  > **Post** /reminder

  `Request Body by reminder type`

  `Normal type:`

  ```js
  {
    "remindeType": "Normal",
    "title": "title",
    "description": "discription",
    "dateReminder": "22-11-2020 4:30",
  }
  ```

  `Special type:`

  ```js
  {
    "reminderType": "Special",
    "candidateId": 1,
    "title": "title",
    "description": "discription",
    "dateReminder": "22-11-2020 4:30",
  }
  ```

  `Interview type:`

  ```js
  {
    "reminderType": "Interview",
    "interviewId": 1,
    "title": "title",
    "description": "discription",
    "dateReminder": "22-11-2020 4:30",
  }
  ```

  `Response Body`

  ```json
  {
    "reminderId": "1"
  }
  ```
