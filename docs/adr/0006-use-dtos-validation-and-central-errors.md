# ADR 0006: Use DTOs, Bean Validation, and central error handling

- **Status:** Accepted
- **Date:** 2026-07-17
- **Deciders:** Syber

## Context

The API needs stable request and response contracts, input validation, and predictable error responses. Exposing JPA entities directly would couple external clients to persistence implementation details.

## Decision

Use request and response DTOs, Jakarta Bean Validation on request DTOs, mapper classes for entity-to-response conversion, and a global exception handler for domain errors.

## Alternatives considered

- **Expose entities directly:** Not selected because it leaks persistence structure into the public API.
- **Validate only in controllers manually:** Not selected because declarative Bean Validation is clearer and reusable.
- **Handle exceptions separately in every controller:** Not selected because it duplicates response logic and can produce inconsistent errors.

## Consequences

- API contracts and database entities can evolve more independently.
- Clients receive consistent JSON error structures for handled domain failures.
- DTOs and mappers introduce extra classes that must be maintained alongside domain changes.
