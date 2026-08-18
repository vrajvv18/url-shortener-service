# Architecture

## Style

Modular monolith. This keeps operational complexity low while preserving clear boundaries that could later become services.

```text
Client
  |
  v
REST Controllers
  |
  +--> UrlService ---------> UrlRepository -----> PostgreSQL
  |        |
  |        +---------------> ClickEventRepository -> PostgreSQL
  |
  +--> RedirectService ----> ClickEventRepository
  |
  +--> Actuator / OpenAPI

Cross-cutting:
  RateLimitFilter -> SecurityHeadersFilter -> Controllers
```

## Key decisions

- PostgreSQL is the source of truth because short-code uniqueness must be strongly enforced.
- The database unique constraint is authoritative; application code retries rare allocation collisions.
- Short codes are random Base62 values and never expose database IDs.
- Flyway owns schema evolution; Hibernate validates rather than mutating production schema.
- The canonical public URL is configuration-driven, preventing Host-header-derived link injection.
- Redirect analytics are best-effort: a click-event persistence problem must not prevent a valid redirect.
- Raw IP addresses are never persisted.
- No server-side fetching of destination URLs is performed, so URL creation does not create an SSRF fetch primitive.
- The create API has a process-local rate limit to reduce trivial abuse; distributed deployments require a shared limiter.

## Request flow

### Create

`POST /api/v1/urls -> validation -> rate limit -> random code -> PostgreSQL unique constraint -> response`

### Redirect

`GET /{code} -> lookup -> status/expiry checks -> separate analytics transaction -> 302`

### Analytics

`GET /api/v1/urls/{code}/analytics -> lookup -> aggregate click data`
