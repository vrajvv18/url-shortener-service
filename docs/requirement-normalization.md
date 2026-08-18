# Requirement Normalization

## Original intent

Build a URL shortener from scratch with core APIs, analytics, and reliability features, while demonstrating engineer-led AI-assisted execution.

## Normalized engineering problem

Provide a small HTTP service that:

1. accepts valid HTTP/HTTPS destinations;
2. allocates a collision-safe 8-character Base62 short code;
3. redirects a short code to its destination;
4. records redirect analytics without persisting raw client IPs;
5. supports optional expiration and explicit disablement;
6. exposes health, metrics, and API documentation;
7. fails safely with structured errors;
8. is reproducible locally through Docker Compose.

## Explicit assumptions

- HTTP and HTTPS are supported; other schemes are rejected.
- URLs containing embedded username/password credentials are rejected.
- Every create request receives a new code; URL de-duplication is out of scope.
- Redirects use HTTP 302.
- Clicks are redirect events successfully processed by the redirect path.
- Unique visitors are an estimate based on a privacy-preserving SHA-256 hash of request attributes; this is not a durable identity system.
- Expired or disabled links return HTTP 410.
- The public URL is configured with `PUBLIC_BASE_URL`; the service does not trust the inbound Host header to construct links.
- Analytics persistence must not turn a valid redirect into a failed redirect.
