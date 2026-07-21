# Testing guide

Run the test suite from the repository root:

```powershell
.\mvnw.cmd test
```

Create a distributable build and run tests:

```powershell
.\mvnw.cmd clean package
```

The current tests use JUnit 5 and Mockito. The tests focus on `AccountService` business rules. Add these tests next:

- Unit tests for `CustomerService`, all transfer cases, and zero or negative amounts.
- Controller tests for JSON validation and HTTP status codes.
- Repository and integration tests with a temporary PostgreSQL instance, for example Testcontainers.
- Tests for concurrent withdrawals and transfers. These tests prevent lost balance updates.
- Coverage reports and a CI workflow that runs the tests for each change.
