# Email Service
- This is an API which enables the client to send emails.
- It is using two email providers from Mailgun and SendGrid. 
- Two email providers are being used to make sure high accessibility, Mailgun is being used as the default provider while SendGrid is for fallback. 

## Pre-requisition
- Java 11

## Profiles
- local
- test
- dev
- prod

## Build & run
```
1) mvn clean install -Dspring.profiles.active=local
2) java -jar -Dspring.profiles.active=local email-service/target/email-service-0.0.1.jar
``` 

## Swagger Doc
- http://localhost:8080/swagger-ui/index.html
- https://erbt-email-service.herokuapp.com/swagger-ui/index.html

## Sample Request
```
 POST - /api/v1/emails

{
    "subject" :"Test 2 subject",
    "body": "Test body",
    "to": [
        "erbt.email.service1@gmail.com"
    ],
    "cc": [
        "erbt.email.service2@gmail.com"
    ],
    "bcc": [
        "erbt.email.service3@gmail.com"
    ]
}
```

## Response
```
200 - Success
{
    "requestId": "03b85ef1181547cdb7db1ce19bce2092"
}

400 - Bad Request
{
    "code": 400,
    "status": "BAD_REQUEST",
    "timestamp": "24-04-2022 16:23:48",
    "message": "Validation error",
    "subErrors": [
        {
            "field": "to",
            "message": "should be at least 1 email"
        },
        {
            "field": "subject",
            "message": "must not be empty"
        },
        {
            "field": "body",
            "message": "must not be empty"
        }
    ],
    "requestId": "8d624b04fce545f79f9cb9c96cc79b31"
}
```

## Github
- https://github.com/erickreevestenorio/email-service