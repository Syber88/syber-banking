# ADR 0008: Use Maven and Java 25

- **Status:** Accepted
- **Date:** 2026-07-17
- **Deciders:** Syber

## Context

The system needs a standard way to compile, test, package, and manage its Java dependencies.

## Decision

Use Java 25 and Maven, with the Maven Wrapper committed to the repository so contributors can run the project without a separately installed Maven distribution.

## Alternatives considered

- **Gradle:** Not selected; Maven is already configured and provides the required build lifecycle.
- **An older Java release:** Not selected; the project is configured for Java 25.
- **A system-installed Maven requirement:** Not selected because the wrapper gives contributors a repeatable entry point.

## Consequences

- Builds use a familiar Maven lifecycle and wrapper commands.
- Contributors and CI must use a compatible JDK 25 installation.
- Upgrading Java or Spring Boot requires validating the dependency and test suite compatibility.
