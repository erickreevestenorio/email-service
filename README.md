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

## Github
- https://github.com/erickreevestenorio/email-service