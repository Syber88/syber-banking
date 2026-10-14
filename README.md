# Syber Banking API

Syber Banking is a Spring Boot REST API for a small digital-banking domain. It manages customers and accounts, supports deposits, withdrawals, and transfers, and creates a transaction record for every successful money movement.

The application exposes an API only; there is no browser interface. Requests are authenticated with a server-side session identified by the `JSESSIONID` cookie, managed by Spring Security.

## Features

- Create, list, retrieve, partially update, and delete customers
- Enforce unique customer email addresses and national IDs
- Create savings or credit accounts for existing customers
- Generate account numbers with the `8800` prefix
- Retrieve account details
- Deposit, withdraw, and transfer funds
- Persist `SUCCESS` transaction records for completed money movements
- Validate request bodies and apply core balance rules
- Register a login for a customer, with the password stored as a BCrypt hash
- Authenticate with a session cookie; require authentication on every endpoint except registration, login and the OpenAPI endpoints
- Provide OpenAPI documentation in Swagger UI

## Technology

| Area | Choice |
| --- | --- |
| Language | Java 25 |
| Framework | Spring Boot 4.1.0 |
| Web | Spring Web MVC |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Build | Maven |
| API documentation | Springdoc OpenAPI |
| Security | Spring Security (session authentication, BCrypt) |
| Boilerplate reduction | Lombok |

## Project layout

```text
src/
├── main/
│   ├── java/com/syber/banking/
│   │   ├── config/       # OpenAPI and Spring Security configuration
│   │   ├── controller/   # HTTP endpoints
│   │   ├── dto/          # Request and response contracts
│   │   ├── entity/       # JPA entities and enums
│   │   ├── exception/    # Domain exceptions and error handling
│   │   ├── mapper/       # Entity-to-response mapping
│   │   ├── repository/   # Spring Data repositories
│   │   └── service/      # Business rules and transactions
│   └── resources/
│       └── application.properties
└── test/                 # JUnit and Mockito tests
```

Additional documentation is available in the [documentation hub](docs/README.md).

## Prerequisites

- JDK 25
- Maven 3.9 or later
- PostgreSQL

> Note: `mvnw` and `mvnw.cmd` are present, but the repository does not currently contain `.mvn/wrapper/maven-wrapper.properties`. Use a local Maven installation until the wrapper configuration is restored.

## Configure the database

Create the database:

```sql
CREATE DATABASE banking;
```

The checked-in development configuration expects PostgreSQL on port `5433` and the API listens on port `8081`.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/banking
spring.datasource.username=postgres
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
server.port=8081
spring.jpa.hibernate.ddl-auto=update
```

Set the database password before starting the service:

```powershell
$env:SPRING_DATASOURCE_PASSWORD="your-password"
```

You can also override the URL and username with `SPRING_DATASOURCE_URL` and `SPRING_DATASOURCE_USERNAME`.

`spring.jpa.hibernate.ddl-auto=update` and SQL logging are suitable for local development only. Use managed migrations and disable SQL logging in production.

## Run the application

From the repository root:

```powershell
mvn spring-boot:run
```

When it starts, use:

| Resource | URL |
| --- | --- |
| API base URL | `http://localhost:8081/api/v1` |
| Swagger UI | `http://localhost:8081/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8081/v3/api-docs` |

## API summary

All request bodies are JSON and should include `Content-Type: application/json`, **except** `POST /api/v1/auth/login`, which is handled by Spring Security's form-login filter and takes form-encoded parameters.

Every endpoint below requires an authenticated session except registration and login.

### Authentication

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/register` | Create a login | `201 Created` |
| `POST` | `/api/v1/auth/login` | Start a session | `302 Found` to `/` |
| `POST` | `/api/v1/auth/logout` | Invalidate the session | `302 Found` |
| `GET` | `/api/v1/auth/me` | Return the authenticated user | `200 OK` |

Register:

```json
{
  "username": "syber",
  "password": "password123"
}
```

The password must be at least 8 characters. The role is assigned server-side as `CUSTOMER` and is never read from the request body. A duplicate username returns `409 Conflict`.

Log in and keep the session cookie:

```bash
curl -c jar.txt -X POST http://localhost:8081/api/v1/auth/login \
  -d 'username=syber&password=password123'

curl -b jar.txt http://localhost:8081/api/v1/auth/me
```

`formLogin` redirects rather than returning a JSON body: `302` to `/` on success, `302` to `/login?error` on failure. Invalid credentials sent to the service layer return `401 Unauthorized`; an unknown username and a wrong password return the same response so that valid usernames cannot be enumerated.

In a browser, Swagger UI sends the session cookie automatically once you have logged in.

### Customers

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `GET` | `/api/v1/customers` | List customers | `200 OK` |
| `GET` | `/api/v1/customers/{customerId}` | Get a customer | `200 OK` |
| `POST` | `/api/v1/customers` | Create a customer | `201 Created` |
| `PATCH` | `/api/v1/customers/{customerId}` | Partially update a customer | `200 OK` |
| `DELETE` | `/api/v1/customers/{customerId}` | Delete a customer | `204 No Content` |

Create a customer:

```json
{
  "firstName": "Siyam",
  "lastName": "Gz",
  "nationalId": "0123456789",
  "email": "siyam@example.com"
}
```

For `PATCH`, provide any combination of `firstName`, `lastName`, and `email`. The national ID cannot currently be changed through the API.

### Accounts

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/v1/accounts` | Create an account for a customer | `201 Created` |
| `GET` | `/api/v1/accounts/{accountId}` | Get account details | `200 OK` |
| `POST` | `/api/v1/accounts/{accountId}/deposit` | Deposit funds | `200 OK` |
| `POST` | `/api/v1/accounts/{accountId}/withdraw` | Withdraw funds | `200 OK` |
| `POST` | `/api/v1/accounts/{accountId}/transfer` | Transfer funds | `200 OK` |
| `DELETE` | `/api/v1/accounts/{accountId}` | Run the guarded close operation | `204 No Content` |

Create an account for customer `1`:

```json
{
  "customerId": 1,
  "accountType": "SAVINGS"
}
```

Supported account types are `SAVINGS` and `CREDIT`. A new account has a `0` balance and `ACTIVE` status. Its account number is `8800` followed by its eight-digit, zero-padded database ID; for example, ID `42` becomes `880000000042`.

### Money movements

Deposit or withdraw:

```json
{
  "amount": 250.00
}
```

Transfer from the account in the URL:

```json
{
  "amount": 125.50,
  "destinationAccountId": 2
}
```

Successful movements return a transaction response containing the transaction ID, source account number, optional destination account number, amount (`depositedAmount`), timestamp, and `SUCCESS` status.

## Current business rules

- A customer email address and national ID must be unique when a customer is created.
- An account can be created only for an existing customer.
- Deposits must be greater than zero.
- Withdrawals and transfers must not be negative. The entity rules reject zero values, so zero-value withdrawal and transfer requests fail.
- Withdrawals and transfers require sufficient source-account funds.
- A transfer cannot use the same source and destination account.
- A deposit, withdrawal, or transfer is executed in a database transaction and records a successful transaction after the account balances are saved.
- Account deletion does not delete the database record. It succeeds only when the balance is zero and the account status is already `CLOSED`; no endpoint currently changes an account to `CLOSED`.

## Errors

Handled domain errors return this shape:

```json
{
  "timestamp": "2026-08-07T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found",
  "path": "/api/v1/customers/999"
}
```

`404 Not Found` is returned for missing customers, accounts and users. Insufficient funds, invalid account-close state and duplicate usernames return `409 Conflict`. Invalid credentials return `401 Unauthorized`. Requests with no session are rejected by the security filter chain with an empty `401` before reaching a controller, so they carry no JSON body. Validation and several other exceptions still use Spring Boot's default error response; see Swagger UI for the generated request contract.

## Build and test

```powershell
mvn clean package
mvn test
```

## What still needs to be done

Priority improvements before using this outside local development:

1. Restore the Maven Wrapper configuration (`.mvn/wrapper/maven-wrapper.properties`) so the documented wrapper commands work.
2. **Add authorization.** Authentication works, but authorization does not. `AppUser.role` is stored and never checked, so no endpoint is admin-only. More seriously, there are no ownership checks: an authenticated user can read and move money on accounts belonging to any other customer. Enforce ownership in the service layer on every endpoint that takes an account or customer ID.
3. **Re-enable CSRF protection.** It is disabled in `SecurityConfig`. Because authentication is cookie-based, the browser attaches the session automatically, including on requests triggered by other sites. This must be fixed before any browser client uses the API.
4. **Link `AppUser` to `Customer` at registration.** `AppUser.customer` is nullable and currently always null, which blocks the ownership checks above. Enrolment should resolve an existing customer by national ID and prove possession of a second factor (an OTP to a phone already on file) before creating the login — a national ID alone identifies a person but does not authenticate them.
5. Set cookie security attributes (`Secure`, `SameSite`) and session timeouts (idle and absolute).
6. Replace in-memory sessions with an external store (Spring Session with Redis) before running more than one instance; a restart currently logs every user out.
7. Complete global error handling for validation failures, duplicate email/national-ID errors, invalid movement amounts, and illegal transfer requests. Return one consistent JSON error format.
8. Fix transfer validation to explicitly require an amount greater than zero before loading accounts, and add tests for zero and same-account transfers.
9. Add account lifecycle endpoints and rules for freezing, closing, and reopening accounts; then make the current guarded close operation meaningful.
10. Prevent unsafe customer deletion when accounts exist, or define and enforce a clear cascade/retention policy.
11. Add transaction-history endpoints with pagination, filtering, and stable transaction terminology (`amount` rather than `depositedAmount` for every movement).
12. Add database migrations (for example, Flyway) and production profiles. Do not use Hibernate `ddl-auto=update` as the production migration strategy.
13. Protect balances from concurrent-update races with database locking or optimistic locking, and add integration tests against PostgreSQL.
14. Add production safeguards: secrets management, HTTPS, observability, rate limiting, audit logging, backups, and CI checks.
