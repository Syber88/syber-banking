# ADR 0003: Use a layered monolith architecture

- **Status:** Accepted
- **Date:** 2026-07-17
- **Deciders:** Syber

## Context

The API has HTTP endpoints, banking rules, persistence access, and data representations that need clear responsibilities. The project currently runs as one deployable Spring Boot application.

## Decision

Organise the application as a layered monolith:

- **Controllers** expose and validate HTTP contracts.
- **Services** own business rules and transactional operations.
- **Repositories** access persisted entities through Spring Data JPA.
- **Entities, DTOs, and mappers** separate database models from API representations.

## Alternatives considered

- **Microservices:** Deferred; separate deployment and distributed-consistency concerns are unnecessary at the current scale.
- **Controllers directly accessing repositories:** Not selected because it would mix transport, business, and persistence concerns.
- **A domain-driven, multi-module architecture:** Deferred because the present application is small and does not yet need that additional structure.

## Consequences

- Business rules can be tested independently of HTTP controllers.
- API DTOs can change without directly exposing database entities.
- The single deployment is simpler to develop and run, but later scaling individual domains independently would require architectural change.
