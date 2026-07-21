# Database documentation

PostgreSQL stores the application data. Hibernate creates or updates the schema when the application starts. This behavior uses `spring.jpa.hibernate.ddl-auto=update`. Use this setting for local development only.

```text
Customer (1) ----- (*) Account

The application writes a transaction record for a deposit, withdrawal, or transfer.
```

## Entities

| Entity | Key fields |
| --- | --- |
| `Customer` | `id`, name, email, and password hash. |
| `Account` | `id`, customer reference, account number, balance, type, and status. |
| `Transaction` | `id`, source and destination account numbers, amount, type, status, and creation time. |

Supported account types are `SAVINGS` and `CREDIT`. Account statuses are `ACTIVE`, `CLOSED`, and `FROZEN`.

## Production requirements

Replace Hibernate schema updates with versioned database migrations, for example Flyway. Add unique and not-null constraints. Add indexes for account number and email. Add a database concurrency control method for balances. Do these actions before a production deployment.
