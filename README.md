# Online Library management

- This application serves API's for library management.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine and on Cloud.

### Prerequisites

What things you need to install before running app.

```
Java 8, Postgres, Maven
```
```
Login to postgres.
Run: create database library
```
### Deployment

Steps to deploy on local machine(Linux machine)

```
- Clone project into local machine.
- Goto root project directory.
- Run LibraryService.java 

------ or -------
- Clone project into local machine.
- Goto root project directory.
- cd /target
- Run: java -jar library-service-1.0.0.jar

```

Steps to deploy on cloud(Linux machine)

```
- Clone project into local machine.
- Goto root project directory.
- To build projet Run: mvn package 
- cd /target
- Run: java -jar library-service-1.0.0.jar

```
```
Note: App Port and database connection details can be changed from application.yml
```

##Swagger Url

[ApiDocs](http://ec2-13-58-223-152.us-east-2.compute.amazonaws.com:8080/swagger-ui.html#/)

## Built With

* [SpringBoot](https://spring.io/projects/spring-boot) - The java web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Swagger](https://swagger.io/) - document, and consume RESTful Web services

# Application Architecture

![alt text](https://github.com/utsav91092/library/blob/master/architecture.jpg)





