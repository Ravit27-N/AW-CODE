# Allweb Recruitment Project (Angular + Java Spring)

This document intent to provide API specification to define client (Angular) and server (Spring boot) for RMS project.



# API specification

## Consideration for [CORS](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing)

We assume that client and server will host on different host:port so backend have to allow CORS request.

## Authentication

Protocol : OAuth2 ([PKCE flow](https://auth0.com/docs/flows/call-your-api-using-the-authorization-code-flow-with-pkce))

IDP: Keycloak

Authentication Header : `Authorization: Bearer <access_token_here>`

Refresh Token: `// TODO: Need to discuss about refresh token`

Oauth2 library for Angular: [angular-oauth2-oidc](https://github.com/manfredsteyer/angular-oauth2-oidc)

Oauth2 library for Java : `// TODO: to be added`

## Response content and status code
We will use JSON format for Request / Response object with correct content type like `Content-Type: application/json; charset=utf-8`

### Response status code should followed by below Http convention : 

200 : Ok, Accept, Created

400 : Bad request (Invalid / missing parameter)

401 : Unauthorized (required login)

403 : Forbidden (Not enough access right)

404 : Not found

422 : Validation error only

## JSON Object return by API : 

### Single Candidate

```js
{
  "candidate": {
    "id": "101",
    "img": "...",
    "firstname": "Fallen",
    "lastname": "Angel",
    "telephone": "(855) 88 712 4996",
    "email": "jake@jake.com",
    "gpa": 4.0,
    "from": "RUPP",
    "priority": "HIGH",
    "description": "...",
    "createdAt": "03-Nov-2020 03:39 PM",
    "modified": "03-Nov-2020 03:39 PM",
    "status": "In Progress",
    "interview": 2,
    "reminder": 1
  }
}
```

### Multiple Candidate

`TODO: need to discuss about last interview in candidate response. Should we include interviews in candidate  or seperate request`
```js
{
  "candidates": [
    {
      "id": "101",
      "img": "...",
      "firstname": "Fallen",
      "lastname": "Angel",
      "telephone": "(855) 88 712 4996",
      "email": "jake@jake.com",
      "gpa": 4.0,
      "from": "RUPP",
      "priority": "HIGH",
      "description": "...",
      "createdAt": "03-Nov-2020 03:39 PM",
      "modified": "03-Nov-2020 03:39 PM",
      "status": "In Progress",
      "interviewCount": 2,
      "reminderCount": 1
    },
    {
      "id": "101",
      "img": "...",
      "firstname": "Fallen",
      "lastname": "Angel",
      "telephone": "(855) 88 712 4996",
      "email": "jake@jake.com",
      "gpa": 4.0,
      "from": "RUPP",
      "priority": "HIGH",
      "description": "...",
      "createdAt": "03-Nov-2020 03:39 PM",
      "modified": "03-Nov-2020 03:39 PM",
      "status": "In Progress",
      "interviewCount": 2,
      "reminderCount": 1
    }
  ],
  "count": 2
}
```

### Single Interview

```js
{
  "interview": {
    "id": "1",
    "apply": "Interview for web developer position",
    "description": "...",
    "candidate": {
      ...candidation object
    },
    "datetime": "18-Oct-2018 02:28 PM",
    "status": "Missed"
  }
}
```

### multiple Interview
```js
{
  "interviews": [
      {
      "id": "1",
      "apply": "Interview for web developer position",
      "description": "...",
      "candidate": {
        ...candidate object
      },
      "datetime": "18-Oct-2018 02:28 PM",
      "status": "Missed"
    },
    {
      "id": "1",
      "apply": "Interview for web developer position",
      "description": "...",
      "candidate": {
        ...candidate object
      },
      "datetime": "18-Oct-2018 02:28 PM",
      "status": "Missed"
    }
  ],
  "count": 2
}
```

### Interview Result
```js
{
  "result": {
    "quiz": { "score": "30", "max": "100" },
    "coding": { "score": "30", "max": "100" },
    "avarage": 0.6,
    "english": "Good",
    "logical": "Good",
    "flexibility": "Poor",
    "qa": "Good",
    "remark": "...<comments>..."
  }
}
```

### status list
```js
{
  "statuses": [
    {
      "id": "1",
      "title": "Pass",
      "active": true,
      "isRemovable": false
      "createdAt": "18-Oct-2018 02:28 PM",
      "modified": "18-Oct-2018 02:28 PM",
      "usage": {
        "candidate": 1,
        "mail": 0
      }
    },
    {
      "id": "2",
      "title": "Fail",
      "active": false,
      "isRemovable": true
      "createdAt": "18-Oct-2018 02:28 PM",
      "modified": "18-Oct-2018 02:28 PM",
      "usage": {
        "candidate": 0,
        "mail": 0
      }
    }
  ]
}
```
### Mail Template list
```js
{
  "templates": [
    {
      "id": "1",
      "subject": "Mail Password recovery",
      "body": "...",
      "createdAt": "18-Oct-2018 02:28 PM",
      "modified": "18-Oct-2018 02:28 PM",
      "usage": 0
    },
    {
      "id": "1",
      "subject": "Mail Password recovery",
      "body": "...",
      "createdAt": "18-Oct-2018 02:28 PM",
      "modified": "18-Oct-2018 02:28 PM",
      "usage": 1
    }
  ]
}
```

### Errors and Status Codes

400 (Bad requesst): return error object

```js
{
  errors: "error message"
}
```
401 (Unauthorized): return nothing

403 (Forbidden ): return nothing

400 (Not found): return nothing

422 (validation): return error validation description
```js
{
  errors: {
    "firstname": [
      "can't be empty"
    ],
    "lastname": [
      "can't contain number or symbol",
      "maximun 50 character"
    ]
  }
}
```

## API Endpoint


### Create candidate

`POST: /api/candidate`

Request body :
```js
{
  "candidate": {
    "img": '...', // to discuss
    "firstname": "angel",
    "lastname": "fallen",
    "gender": "Male",
    "phone": "855 12457898",
    "email": "example.a@gmail.com",
    "gpa": 4.0,
    "from": "RUPP",
    "priority": "Normal",
    "status": "1" // status id,
    "description": "..."
  }
}
```
Response: Single Candidate



### Update candidate
`PUT: /api/candidate/{id}`

Request body and response : should be the same as POST 

### Change candidate status

Response: Single Candidate



`PATCH: /api/candidate/{id}/status`

Request body : 

```js
{
  "status": "2" // new status id
}
```

### Soft delete candidate
`DELETE: /api/candidate/{id}`


### View a candidate
`GET: /api/candidate/{id}`

Response: Single Candidate

### Get candidate list
`GET: /api/candidate`

Response: Multiple Candidates

TODO: Discuse about pagination and filtering


### Create Interview

`POST: /api/interview`

Request body: 
```js
{
  "interview": {
    "candidate": "101", // candidate id
    "status": "1", // status id,
    "datetime": "05/11/2020 03:46 PM",
    "isSendInvite": false,
    "isRemind": true,
    "apply": "Web developer",
    "description": "..."
  }
}
```

Response: Single Interview

### Update Interview
`PATCH: /api/interview/{id}`

Request body :
```js
{
  "interview": {
    "status": "1",
    "apply": "...",
    "description": "..."
  }
}
```
Response: Single Interview

Note: only these three fields are allowed

### Get a single interview
`GET: /api/interview/{id}`

response: Single interview


### Get interview list

`GET: /api/interview`

response: Multiple interview

### Add result to interview

`POST: /api/interview/{id}/result`

request body:
```js
{
  "result": {
    "quiz": {
      "score": "60",
      "max": "100"
    },
    "coding": {
      "score": "60",
      "max": "100"
    },
    "english": "Good",
    "logical": "Good",
    "flexibility": "Good",
    "qa": "Good",
    "remark": "..."
  }
}
```

response: Interview Result

### Update result of an interview
`PUT: /api/interview/{id}/result`

request body: should be the same as post

response: Interview Result



### Soft delete interview
`DELETE: /api/interview/{id}`


### Create status
`POST: /api/status`

requst body: 
```js
{
  "status": {
    "title": "...",
    "description": "...",
    "active": true
  }
}
```

### Update status
`PUT: /api/status/{id}`

request body: same as post

### active / deactive status

`PATCH: /api/status/{id}/active`
```js
{
  "active": true
}
```

### Get Status list
`GET: /api/status`

response: statuses list

### Soft delete status
`DELETE: /api/delete/{id}`
