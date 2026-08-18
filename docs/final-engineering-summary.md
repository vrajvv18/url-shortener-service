# Final Engineering Summary

## Objective

Build a runnable URL shortener while demonstrating requirement understanding, decomposition, AI-assisted execution, validation, risk control, and engineer ownership.

## Architecture

A modular Spring Boot monolith backed by PostgreSQL. The database owns short-code uniqueness. Flyway owns schema changes. Redirects perform status/expiry checks and best-effort analytics recording in a separate transaction.

## Key engineering decisions

1. 8-character random Base62 short codes.
2. Database unique constraint plus bounded collision retry.
3. Configured canonical base URL rather than request Host header.
4. HTTP/HTTPS-only URL validation and embedded-credential rejection.
5. HTTP 302 redirects.
6. HTTP 410 for expired/disabled links.
7. Privacy-preserving visitor hash; no raw IP persistence.
8. Best-effort analytics so analytics degradation does not break redirects.
9. Process-local create rate limit for basic abuse resistance.
10. Flyway migrations and Hibernate schema validation.

## Validation

Unit tests cover code generation and core service rules. Testcontainers integration tests exercise the create -> redirect -> analytics -> disable lifecycle against PostgreSQL.

## Scenarios

- Greenfield: full implementation from normalized requirement.
- Brownfield: concrete unsafe legacy redirect pattern, identified risks, refactored behavior, regression validation.
- Ambiguous: analytics semantics converted into explicit definitions and privacy decisions.

## AI-assisted execution

AI was used as a task-level accelerator for analysis, implementation patterns, test ideas, security review, documentation, and refactoring. Outputs were reviewed and edited; the engineer remains responsible for the final result. See `ai-engineering-log.md`.

## Risks and limitations

The prototype does not include authentication, distributed rate limiting, asynchronous analytics, centralized log aggregation, or automated backup/restore. These are explicitly identified as production evolution items rather than hidden gaps.

## Submission principle

The repository is intended to be reviewable: a reviewer can run it, inspect the architecture, follow the task decomposition, see the AI decision trail, execute the quality gates, and understand the trade-offs without relying on undocumented claims.
