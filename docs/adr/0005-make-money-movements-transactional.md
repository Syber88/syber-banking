# ADR 0005: Make money movements transactional

- **Status:** Accepted
- **Date:** 2026-07-17
- **Deciders:** Syber

## Context

A deposit, withdrawal, or transfer changes an account balance and creates a transaction record. A transfer changes two account balances. Partial completion could leave the financial state inconsistent.

## Decision

Execute balance-changing service operations in Spring-managed database transactions using `@Transactional`. A transfer updates both accounts and creates its successful transaction record atomically.

## Alternatives considered

- **Independent repository saves:** Not selected because a failure after one save could leave an incomplete transfer.
- **Application-level compensation only:** Not selected because PostgreSQL transactions already provide atomicity for this single database.
- **Eventual consistency:** Not selected because immediate balance consistency is required for the current banking operations.

## Consequences

- Failed operations roll back their related balance and transaction changes.
- Transaction boundaries must remain in the service layer.
- Future multi-service or multi-database workflows would need an additional consistency strategy.
