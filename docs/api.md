# API documentation

The base URL is `http://localhost:8081/api/v1`. Swagger UI shows the current API contract at `http://localhost:8081/swagger-ui/index.html`.

## Customers

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/customers` | Get the customer list. |
| `GET` | `/customers/{customerId}` | Get one customer. |
| `POST` | `/customers` | Create one customer. |
| `PUT` | `/customers/{customerId}` | Update one customer. |
| `DELETE` | `/customers/{customerId}` | Delete one customer. |

Use this JSON body to create or update a customer:

```json
{"firstName":"Siyam","lastName":"Gz","email":"siyam@example.com"}
```

## Accounts and money movements

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/accounts` | Create an account for an existing customer. |
| `GET` | `/accounts/{accountId}` | Get account details. |
| `POST` | `/accounts/{accountId}/deposit` | Deposit money. |
| `POST` | `/accounts/{accountId}/withdraw` | Withdraw money. |
| `POST` | `/accounts/{accountId}/transfer` | Transfer money to another account. |
| `DELETE` | `/accounts/{accountId}` | Run the protected account-close operation. |

Use this JSON data to create an account: `{"customerId":1,"accountType":"SAVINGS"}`

Use this JSON data to deposit or withdraw money: `{"amount":250.00}`

Use this JSON data to transfer money: `{"amount":125.50,"destinationAccountId":2}`

## Errors

Domain errors use a JSON response with `timestamp`, `status`, `error`, `message`, and `path`. The API uses `404 Not Found` when a resource does not exist. The API uses `409 Conflict` when funds are not sufficient or the account-close state is not valid.

Use the OpenAPI document at `/v3/api-docs` for all response schemas and validation rules.
