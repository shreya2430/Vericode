# Vericode Backend

Collaborative code review platform backend built with Spring Boot.

## Prerequisites

- Java 17
- Maven 3.8+

## Run

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The server starts at `http://localhost:8080`.

## Verify

- **API:** `GET http://localhost:8080/api/pullrequests`
- **Database:** MySQL running on `localhost:3306`
    - Database: `vericodedb`
    - Configure credentials in `src/main/resources/application.properties`