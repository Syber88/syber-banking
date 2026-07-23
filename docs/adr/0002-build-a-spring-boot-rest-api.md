# ADR 0002: Build a Spring Boot REST API

- **Status:** Accepted
- **Date:** 2026-07-17
- **Deciders:** Syber

## Context

The system provides banking operations for customers, accounts, deposits, withdrawals, and transfers. It must expose those capabilities to clients through a well-defined HTTP interface.

## Decision

Build the application as a Java Spring Boot REST API using Spring Web MVC. The API is versioned below `/api/v1` and exchanges JSON request and response bodies.

## Alternatives considered

- **Server-rendered web application:** Not selected because the current scope is API-first and has no user interface.
- **Desktop application:** Not selected because browser, mobile, and other client applications need a shared network interface.
- **Microservices:** Not selected because the current domain and development scope are small enough to manage as one application.

## Consequences

- Clients can use the service independently of its implementation language or user interface.
- The API needs stable endpoint and JSON-contract management as it evolves.
- Authentication and authorization must be added before exposing the API publicly.
