# AI-Assisted URL Shortener — Interview Submission

Production-oriented Java 17 or 20 / Spring Boot URL shortener demonstrating engineer-led AI-assisted software engineering.

## Why this version is stronger

This revision is intentionally designed around the assignment's evaluation criteria, not only the functional API:

- requirement normalization and explicit assumptions
- dependency-aware task decomposition
- greenfield, brownfield, and ambiguous scenarios
- AI engineering traceability with generated/edited/rejected decisions
- unit + PostgreSQL integration tests
- quality gates and CI
- Flyway-managed schema instead of `ddl-auto=update`
- configured public base URL instead of trusting the HTTP Host header
- URL validation and embedded-credential rejection
- privacy-preserving visitor hash
- create-endpoint rate limiting
- security headers
- metrics and health checks
- explicit risks, trade-offs, limitations, and production evolution

## Technology

Java 17 or 20, Spring Boot 3.5.5, Spring Web, Spring Data JPA, PostgreSQL, Flyway, JUnit 5, Mockito, Testcontainers, Docker, Actuator, OpenAPI.

## Run locally

### Option A — Docker Compose

```bash
docker compose up --build
```

Then open:
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

### Option B — Run PostgreSQL separately

```bash
docker compose up -d postgres
mvn spring-boot:run
```

## Create a short URL

```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H 'Content-Type: application/json' \
  -d '{"longUrl":"https://example.com/products/123"}'
```

Optional expiry:

```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H 'Content-Type: application/json' \
  -d '{"longUrl":"https://example.com/products/123","expiresAt":"2027-12-31T23:59:59Z"}'
```

Response contains `shortCode` and `shortUrl`.

## Redirect

```bash
curl -i http://localhost:8080/{shortCode}
```

The service returns HTTP 302 and a `Location` header.

## Analytics

```bash
curl http://localhost:8080/api/v1/urls/{shortCode}/analytics
```

## Disable

```bash
curl -X DELETE http://localhost:8080/api/v1/urls/{shortCode}
```

## Test and quality gates

```bash
mvn clean verify
```

After starting the application, run the end-to-end smoke test:

```bash
./scripts/smoke-test.sh
```

The integration tests use Testcontainers and a real PostgreSQL container. Docker is required for the integration test suite.

Quality gate sequence:

`compile -> unit tests -> integration tests -> dependency/build validation -> CI -> human review`

## Configuration

| Variable | Default | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/urlshortener` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `urluser` | Database username |
| `DB_PASSWORD` | `urlpass` | Database password |
| `PUBLIC_BASE_URL` | `http://localhost:8080` | Canonical short URL base |
| `RATE_LIMIT_CAPACITY` | `30` | Create requests per window per source IP |
| `RATE_LIMIT_WINDOW_SECONDS` | `60` | Rate-limit window |
| `PORT` | `8080` | HTTP port |

## Engineering evidence

See `docs/` for the interview evidence package:

- `requirement-normalization.md`
- `task-plan.md`
- `architecture.md`
- `greenfield.md`
- `brownfield.md`
- `ambiguous-requirement.md`
- `ai-engineering-log.md`
- `quality-gates.md`
- `threat-model.md`
- `tradeoffs.md`
- `final-engineering-summary.md`
- `interview-demo.md`

## Important production limitations

This is an interview prototype, not a fully operated internet-scale service. The rate limiter is process-local, authentication is intentionally omitted, analytics are synchronous, and PostgreSQL remains the source of truth. At larger scale, add distributed rate limiting, authentication/authorization, Redis caching, asynchronous analytics, centralized observability, backup/restore automation, and horizontal scaling based on measured workload.

### Existing PostgreSQL database

If you are upgrading a database created by an earlier version, Flyway will baseline the existing schema at version 1 and then apply subsequent migrations such as `V2__add_visitor_hash.sql`. Do not manually delete tables.
