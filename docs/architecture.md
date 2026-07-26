# Architecture document

## Overview

Syber Banking is a Spring Boot REST API. It uses a layered design. It runs as one application. PostgreSQL stores the data.

```text
Client / Swagger UI
        |
   Controllers       HTTP routing, request validation, responses
        |
    Services         business rules and transaction boundaries
        |
 Repositories        Spring Data JPA persistence access
        |
 PostgreSQL          customers, accounts, transactions
```

## Packages

| Package | Responsibility |
| --- | --- |
| `controller` | Provides the `/api/v1` HTTP endpoints. |
| `dto` | Defines request and response data. The API does not return entities directly. |
| `service` | Applies customer, account, and money-movement rules. |
| `entity` | Defines JPA entities and domain value lists. |
| `repository` | Reads and writes data with Spring Data JPA. |
| `mapper` | Changes entities into API response data. |
| `exception` | Defines domain errors and the central error handler. |
| `config` | Sets OpenAPI and Swagger data. |

## Important design rules

- Controllers validate input. Controllers call one service. Controllers return the HTTP response.
- Balance-change methods in `AccountService` use database transactions. A transfer updates both accounts and writes one transaction record in the same operation.
- An account number uses the `8800` prefix and the account ID with leading zeros.
- The [ADRs](adr) record the project decisions.

## Boundaries to add as the project grows

The application does not yet have authentication, authorization, page controls, database migrations, audit logs, or an account-status lifecycle. Add these functions before you use the application as a production banking system.
