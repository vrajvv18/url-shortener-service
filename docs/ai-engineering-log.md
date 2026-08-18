# AI-Assisted Engineering Log

## Control principle

AI is an accelerator inside bounded engineering tasks. The engineer owns correctness, security, maintainability, performance, and production readiness.

This revision was AI-assisted using ChatGPT, with the repository reviewed against the assignment and the implementation redesigned around the identified gaps.

| ID | Task | AI assistance | Classification | Engineer action | Validation |
|---|---|---|---|---|---|
| AI-001 | Repository assessment | Analyze repository against assignment criteria | GENERATED | EDITED | File/code review |
| AI-002 | Requirement normalization | Draft assumptions and acceptance criteria | GENERATED | EDITED | Requirement review |
| AI-003 | Architecture | Propose modular monolith and data flow | GENERATED | EDITED | Design review |
| AI-004 | Short-code allocation | Generate implementation pattern | GENERATED | EDITED | Collision retry tests + DB uniqueness |
| AI-005 | URL validation | Identify validation and credential edge cases | GENERATED | EDITED | Unit tests |
| AI-006 | Brownfield scenario | Identify concrete legacy flaw and refactor path | GENERATED | EDITED | Regression test plan |
| AI-007 | Ambiguous analytics | Surface semantic/privacy questions | GENERATED | EDITED | Decision record |
| AI-008 | Test expansion | Propose edge and integration tests | GENERATED | EDITED | JUnit + Testcontainers suite |
| AI-009 | Security review | Identify Host-header, embedded-credential, rate-limit risks | GENERATED | EDITED | Threat model + code changes |
| AI-010 | Production readiness | Identify migration, CI, observability, and scaling gaps | GENERATED | EDITED | Flyway, CI, Actuator, docs |

## Example disciplined prompt pattern

For each implementation task, provide:

1. Intent
2. Technical context
3. Constraints
4. Acceptance criteria
5. Failure scenarios
6. Required tests
7. Explicit instruction to explain assumptions

## Rejected/reframed ideas

- Trusting the inbound HTTP Host header for canonical short URLs: rejected because deployment headers can be attacker-controlled.
- `ddl-auto=update` as the production schema mechanism: rejected in favor of versioned Flyway migrations.
- Distributed caching/rate limiting for the prototype: deferred because it adds operational complexity without a measured workload requirement.
- Server-side destination validation/fetching: avoided because it creates an unnecessary SSRF primitive.

## Human sign-off

Before submission, the engineer should run the complete quality gate, inspect the generated diff, verify API behavior manually, and confirm that no AI-generated change is accepted without review.
