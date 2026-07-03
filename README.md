# Syber Banking API

Syber Banking is a Spring Boot REST API for managing customers, bank accounts, deposits, and withdrawals. It uses Spring Web MVC, Spring Data JPA, PostgreSQL, Lombok, and Springdoc OpenAPI for interactive API documentation.

## Features

- Create, view, update, and delete customers
- Create bank accounts for existing customers
- Generate account numbers with the `8800` bank prefix
- Deposit funds into an account
- Withdraw funds from an account
- Persist customers, accounts, and transactions with PostgreSQL
- Explore endpoints through Swagger UI

## Tech Stack

- Java 25
- Spring Boot 4.1.0
- Maven
- PostgreSQL
- Spring Data JPA
- Lombok
- Springdoc OpenAPI

## Project Structure

```text
src/main/java/com/syber/banking
├── config        # OpenAPI configuration
├── controller    # REST controllers
├── dto           # Request and response DTOs
├── entity        # JPA entities and enums
├── exception     # Custom exceptions
├── mapper        # Entity/DTO mappers
├── repository    # Spring Data repositories
└── service       # Business logic
```

## Prerequisites

Make sure you have the following installed:

- Java 25 or compatible JDK
- PostgreSQL
- Maven, or use the included Maven wrapper

## Database Setup

Create a PostgreSQL database named `banking`.

The current application configuration expects PostgreSQL to run on port `5433`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/banking
spring.datasource.username=postgres
spring.datasource.password=accelerate
server.port=8081
```

Update `src/main/resources/application.properties` if your local database username, password, port, or database name is different.

## Running the Application

From the project root, run:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

The API will start at:

```text
http://localhost:8081
```

Swagger UI is available at:

```text
http://localhost:8081/swagger-ui.html
```

## API Endpoints

### Customers

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/v1/customers` | Get all customers |
| `GET` | `/api/v1/customers/{customerId}` | Get a customer by ID |
| `POST` | `/api/v1/customers` | Create a customer |
| `PUT` | `/api/v1/customers/{customerId}` | Update a customer |
| `DELETE` | `/api/v1/customers/{customerId}` | Delete a customer |

Create customer request:

```json
{
  "firstName": "Siyam",
  "lastName": "Gz",
  "email": "siyam@example.com",
  "passwordHash": "hashed-password"
}
```

Update customer request:

```json
{
  "firstName": "Siyam",
  "lastName": "Gz",
  "email": "new-email@example.com",
  "passwordHash": "hashed-password"
}
```

### Accounts

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/v1/accounts` | Create an account for a customer |
| `GET` | `/api/v1/accounts/{accountId}` | Get account details |
| `POST` | `/api/v1/accounts/{accountId}/deposit` | Deposit money |
| `POST` | `/api/v1/accounts/{accountId}/withdraw` | Withdraw money |

Create account request:

```json
{
  "customerId": 1,
  "accountType": "SAVINGS"
}
```

Supported account types:

```text
SAVINGS
CREDIT
```

Deposit request:

```json
{
  "amount": 250.00
}
```

Withdraw request:

```json
{
  "amount": 100.00
}
```

## Build and Test

Build the project:

```bash
./mvnw clean package
```

Run tests:

```bash
./mvnw test
```

On Windows PowerShell, use `.\mvnw.cmd` instead of `./mvnw`.

## Notes

- Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`, so tables are updated automatically during development.
- SQL logging is enabled with `spring.jpa.show-sql=true`.
- Account numbers are generated from the account ID using the `8800` prefix.
- Withdrawals require sufficient funds and deposits must be greater than zero.
