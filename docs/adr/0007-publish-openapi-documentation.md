# ADR 0007: Publish OpenAPI documentation with Swagger UI

- **Status:** Accepted
- **Date:** 2026-07-17
- **Deciders:** Syber

## Context

An API-first application needs accessible documentation so developers can discover and exercise endpoints, request bodies, response structures, and validation constraints.

## Decision

Use Springdoc OpenAPI to publish OpenAPI JSON at `/v3/api-docs` and interactive Swagger UI at `/swagger-ui/index.html`.

## Alternatives considered

- **Hand-maintained API documentation only:** Not selected because it can drift from the running implementation.
- **No interactive documentation:** Not selected because it makes testing and adoption harder.
- **A separate API-management platform:** Deferred because Springdoc meets the current local-development needs with less operational overhead.

## Consequences

- Developers can inspect and invoke the API during development.
- OpenAPI documentation needs review when endpoint semantics change.
- Swagger UI should be protected, restricted, or disabled as appropriate in a production deployment.
