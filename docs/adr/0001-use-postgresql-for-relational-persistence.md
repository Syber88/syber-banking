# ADR 0001: Use PostgreSQL for relational persistence

- **Status:** Accepted
- **Date:** 2026-07-17
- **Deciders:** Syber

## Context

Syber Banking is a Spring Boot REST API that manages customers, accounts, and money movements. A transfer changes the balances of two accounts and records a transaction. These related changes must succeed or fail together so that account balances and the transaction history remain consistent.

The application uses Spring Data JPA/Hibernate and has relational domain relationships: one customer can own multiple accounts, and accounts create transaction records. It needs a durable database that supports ACID transactions, relational integrity, SQL querying, and straightforward integration with the Spring ecosystem.

## Decision

Use **PostgreSQL** as the application's relational database, accessed through Spring Data JPA and Hibernate.

Balance-changing operations will execute in Spring-managed database transactions. In particular, a transfer will update the source account, update the destination account, and persist its transaction record as one atomic unit of work.

## Alternatives considered

### MySQL

MySQL provides relational storage and transaction support, and would be a viable choice. PostgreSQL was selected for this project because it is well suited to an API that depends on strong relational consistency and offers a rich SQL feature set for future reporting, auditing, and query needs.

### H2 in-memory database

H2 is convenient for fast local tests and prototypes, but its in-memory default mode does not provide the same operational persistence or production behaviour as the chosen database. It is not the primary runtime database.

### NoSQL document database

A document database can store customer, account, and transaction data, but it would make cross-entity relationships and multi-record money transfers more complex. It does not match the strongly relational domain as directly as a relational database.

## Consequences

### Positive

- ACID transactions protect transfer consistency.
- Foreign-key-style relationships map naturally through JPA entities.
- PostgreSQL provides durable transaction history and supports future reporting and auditing queries.
- The PostgreSQL JDBC driver and Spring Data JPA are already part of the project stack.

### Negative

- Developers need access to a PostgreSQL instance for normal local execution.
- Schema changes need to be managed carefully as the API evolves.
- SQL/database operations must be monitored and tuned as data volume grows.

## Follow-up decisions

- Adopt an explicit schema-migration tool (for example, Flyway or Liquibase) before production deployment; `spring.jpa.hibernate.ddl-auto=update` is suitable only for local development.
- Define production backup, recovery, and database credential-management procedures.
- Decide whether PostgreSQL should also be used for integration tests, or whether H2/Testcontainers will be used for those tests.
