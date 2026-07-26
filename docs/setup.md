# Setup guide

## Requirements

- JDK 25
- PostgreSQL

Create a database:

```sql
CREATE DATABASE banking;
```

Set the database password in the current PowerShell session. The local settings use PostgreSQL port `5433`, database `banking`, user `postgres`, and API port `8081`.

```powershell
$env:SPRING_DATASOURCE_PASSWORD="your-password"
.\mvnw.cmd spring-boot:run
```

You can change the other connection values without changing tracked files:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/banking"
$env:SPRING_DATASOURCE_USERNAME="postgres"
```

When the application starts, open Swagger UI at `http://localhost:8081/swagger-ui/index.html`.

WARNING: Do not put a database password in `application.properties`. Use an environment variable or a secret store.
