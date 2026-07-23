# ADR 0004: Use Spring Data JPA for data access

- **Status:** Accepted
- **Date:** 2026-07-17
- **Deciders:** Syber

## Context

Customers, accounts, and transactions have relational associations and must be stored in PostgreSQL. The application needs a maintainable way to perform common persistence operations.

## Decision

Use Spring Data JPA and Hibernate. Model the domain with JPA entities and provide repository interfaces extending `JpaRepository`.

## Alternatives considered

- **Hand-written JDBC:** Not selected because it would add repetitive mapping and CRUD code.
- **Raw SQL-only data access:** Not selected for the same reason; specialised SQL can be introduced later where needed.
- **A non-relational mapper:** Not selected because the chosen database and domain are relational.

## Consequences

- Common CRUD operations are concise and consistent.
- Object-relational mapping requires careful fetch, query, and schema design as the application grows.
- Hibernate is currently permitted to update the local schema; production migrations need a dedicated migration tool.
