# User manual

## Use the API in Swagger UI

1. Complete the [setup guide](setup.md).
2. Start the application.
3. Open `http://localhost:8081/swagger-ui/index.html`.
4. Create a customer with `POST /api/v1/customers`.
5. Create an account for the customer with `POST /api/v1/accounts`.
6. Record the returned account ID and account number.
7. Deposit money.
8. Withdraw or transfer money as necessary.

## Typical workflow

```text
Create customer -> Open account -> Deposit -> Withdraw or transfer -> Inspect account
```

Use account IDs, not account numbers, in the current URL paths. A transfer needs a different destination account ID. The destination account must exist. The API returns a transaction record after a successful money movement.

If an operation fails, read the `message` in the JSON error response. Common causes are a missing account or customer, a balance that is too low, or an invalid amount.
