# Interview Demo Script

## 1. Start the system

```bash
docker compose up --build
```

Open Swagger and show the endpoints.

## 2. Demonstrate greenfield behavior

Create a URL, redirect it, show analytics, then disable it.

## 3. Demonstrate failure handling

Try:
- `ftp://example.com`
- expired timestamp
- unknown short code
- disabled short code

Explain the structured errors and HTTP 410 behavior.

## 4. Explain brownfield reasoning

Show `brownfield.md`. Explain why `Optional.get()` is unsafe and how the current service centralizes not-found handling and status/expiry checks.

## 5. Explain ambiguous requirement

Show `ambiguous-requirement.md`. Explain why click and unique-visitor semantics were explicitly defined before implementation.

## 6. Explain AI usage

Show `ai-engineering-log.md`. Emphasize that AI produced suggestions inside bounded tasks, while the engineer reviewed, edited, rejected, tested, and approved the output.

## 7. Explain production readiness

Show the quality gates and threat model. Mention Flyway, CI, Testcontainers, metrics, rate limiting, privacy controls, and documented scaling limitations.
