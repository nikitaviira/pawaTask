# pawaTask Test assignment

## Stack
#### Backend: ```Spring Boot (JPA, Web, Validation), Spring Cloud Gateway, Liquibase, Lombok, Auth0 JWT```
#### Frontend: ```Vue 3, Vite, TypeScript, SCSS```
#### Database: ```MySQL```
#### Message broker: ```Kafka```

## How to run

1. Install and run [Docker Desktop](https://docs.docker.com/get-docker/)
2. Run "run-app.sh" script
3. Open "localhost:5173"

## Architecture

Gradle multi-project setup with 3 services:
- Authentication service
  > Responsible for login and register of users. Creates JWT token which is passed to frontend for further authentication 
- Task service
  > Responsible for creation and editing of tasks as well as their retrieval and creation of task comments
- Email service
  > Responsible for sending out emails using Sendgrid API. Email messages are formatted using Sendgrid templates. 
    **(Emails are redirected to spam because domain is not verified by Sendgrid)**
- API Gateway
  > Aggregates all requests to backend and applies authentication verification before proxying the request to the needed microservice