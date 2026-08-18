# Task Decomposition

| ID | Task | Dependency | Acceptance criteria |
|---|---|---|---|
| T01 | Normalize requirements | None | Assumptions documented |
| T02 | Define API contract | T01 | Create, redirect, analytics, disable endpoints documented |
| T03 | Design persistence | T01 | Unique short code and click indexes defined |
| T04 | Implement schema migration | T03 | Flyway creates schema deterministically |
| T05 | Implement short-code generation | T03 | 8-char Base62, collision retry |
| T06 | Implement create API | T02,T04,T05 | Valid URL creates code |
| T07 | Implement redirect | T06 | Active code returns 302 |
| T08 | Implement analytics | T06,T07 | Click and unique visitor counts available |
| T09 | Add validation/errors | T06,T07,T08 | Stable 4xx/5xx response shape |
| T10 | Add security controls | T06 | Rate limit, safe URL validation, security headers |
| T11 | Add unit tests | T05,T06,T08 | Core business rules covered |
| T12 | Add integration tests | T06,T07,T08 | Real PostgreSQL workflow passes |
| T13 | Add observability | T07,T08 | Health and redirect/analytics metrics available |
| T14 | Add CI/Docker | T11,T12 | `mvn verify` and Compose are reproducible |
| T15 | Brownfield review | T07,T09 | Existing flaw analyzed and regression test defined |
| T16 | Ambiguity analysis | T01,T08 | Analytics ambiguity converted into measurable decisions |
| T17 | Final review | All | Risks, trade-offs, limitations, AI traceability documented |
