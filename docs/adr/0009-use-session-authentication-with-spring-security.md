# ADR 0009: Use server-side session authentication with Spring Security

- **Status:** Accepted
- **Date:** 2026-10-12
- **Deciders:** Syber

## Context

The API had no authentication layer. Every endpoint was reachable by any caller, and `Customer` carried a `passwordHash` field that was never populated.

Two questions needed answering: where login credentials live in the domain model, and what proves identity on subsequent requests.

For the second question the realistic options are a server-side session identified by a cookie, or a stateless bearer token such as a JWT. Sessions are the simpler mechanism and the one the servlet container already provides; tokens matter when multiple independent services must verify identity without a shared session store, which this single application does not need.

## Decision

Authenticate with a server-side `HttpSession`, identified by the container's `JSESSIONID` cookie, and delegate the authentication mechanics to Spring Security.

Credentials live on a separate `AppUser` entity, not on `Customer`. `AppUser` owns a nullable one-to-one reference to `Customer`.

Passwords are stored as BCrypt hashes in `AppUser.passwordHash`, produced and verified through a single `PasswordEncoder` bean.

`AppUserDetailsService` implements Spring Security's `UserDetailsService` and loads users from `app_user`. A `SecurityFilterChain` bean permits registration, login and the OpenAPI endpoints and requires authentication for everything else. Login and logout are handled by Spring Security's `formLogin` and `logout` filters, mapped onto `/api/v1/auth/login` and `/api/v1/auth/logout`.

Each `AppUser` carries a `role` string, assigned server-side at registration and never accepted from the request body.

## Alternatives considered

- **Credentials on `Customer`:** Not selected. A customer is a person the bank holds accounts for and may never log in; a login may belong to staff who hold no accounts. One customer may also need several logins. Merging the two makes those cases unrepresentable and couples credential lifecycle to records that must be retained.
- **JWT / stateless bearer tokens:** Not selected for now. They solve distribution across services, which this monolith does not have, and they move revocation and expiry from the server into token design. Sessions keep revocation trivial — destroy the server-side session.
- **Hand-rolled authentication over `HttpSession` only:** Built first deliberately, as a learning step, then replaced. Keeping it would mean maintaining password comparison, session-fixation protection, and per-controller authorization checks that the framework provides and keeps correct.
- **HTTP Basic on every request:** Not selected. It requires the client to hold and resend credentials continuously and offers no logout.

## Consequences

- Session state lives in the application's memory. A restart logs every user out, and a second instance behind a load balancer cannot see the first instance's sessions. Horizontal scaling will require an external session store (Spring Session with Redis).
- Authorization rules now live in configuration rather than controller code, so a route can be protected without touching the handler.
- Spring Security rotates the session ID on login, which closes session fixation without application code.
- Because authentication is cookie-based, the browser attaches credentials automatically and CSRF protection becomes necessary. It is currently **disabled** and must be re-enabled before any browser client uses the API.
- Role-based rules exist as a `role` column, but no endpoint checks it yet, and no ownership checks exist: an authenticated user can currently read and move money on accounts belonging to other customers. Tracked in the README.
- `Customer.passwordHash` was removed, and `AppUser.role` is non-null, so both required a manual schema change against existing rows. `ddl-auto=update` cannot backfill a non-null column and only logs a warning when it fails.
