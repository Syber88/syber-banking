# Deployment guide

This API is set for development. Do the following before you deploy it:

1. Put database credentials in the secret store of the hosting platform.
2. Use separate `dev`, `test`, and `prod` Spring profiles.
3. Replace `ddl-auto=update` with Flyway migrations.
4. Disable SQL logging in production.
5. Put the API behind TLS and add authentication and role-based authorization.
6. Restrict CORS, validate inputs, and add rate limiting.
7. Add health checks, structured logs, metrics, backups, and alerting.
8. Run the application as a non-root user with a supported JDK runtime.

WARNING: Do not deploy this application to the Internet until you add authentication and authorization.

Build the application with:

```powershell
.\mvnw.cmd clean package
```

The build writes the executable JAR to `target`. Set the production database URL, user name, and password as environment variables before you run it.
