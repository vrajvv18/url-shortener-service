# Greenfield Scenario

## Requirement

Build a URL shortener with creation, redirection, analytics, expiration, and disablement.

## Execution

1. Normalize ambiguous requirements.
2. Define REST contract and error model.
3. Choose a modular monolith with PostgreSQL.
4. Create a migration-managed schema.
5. Implement collision-safe short-code allocation.
6. Implement create and redirect paths.
7. Add analytics and privacy controls.
8. Add validation, rate limiting, and security headers.
9. Add unit and PostgreSQL integration tests.
10. Add Docker Compose, CI, OpenAPI, and engineering evidence.

## Acceptance evidence

- Valid URL returns HTTP 201 and an 8-character code.
- Code returns HTTP 302 with the original destination.
- Analytics count increases after redirect.
- Disabled/expired links return HTTP 410.
- Invalid URLs return HTTP 400.
- Database collision triggers bounded retry rather than exposing a database error.
- Integration tests exercise the end-to-end lifecycle against PostgreSQL.
