# AI-Assisted Engineering Evidence

## Purpose

This document provides traceability for AI-assisted engineering during development of the URL Shortener. AI was used as an engineering accelerator for requirements analysis, design exploration, implementation assistance, testing, security review, and engineering review. The implementation remained engineer-led: AI output was treated as a proposal and was reviewed and validated before acceptance.

## Engineering Principles

1. AI accelerates engineering work; it does not replace engineering judgment.
2. Each AI-assisted task has a clear engineering objective.
3. AI output is reviewed against requirements and constraints.
4. Generated code is modified when necessary rather than accepted blindly.
5. Security-sensitive recommendations receive explicit engineering review.
6. Material rejected/deferred recommendations are documented.
7. Automated tests and quality gates provide validation.
8. The engineer remains accountable for the final implementation.

## AI Usage Classification

| Classification | Meaning |
|---|---|
| `GENERATED` | AI produced an initial implementation/artifact that was reviewed. |
| `EDITED` | AI output was used as a starting point and materially modified. |
| `REJECTED` | AI recommendation was intentionally not used because it did not satisfy requirements or introduced unnecessary risk. |
| `HUMAN` | Decision/implementation was performed directly by the engineer. |

## AI-Assisted Engineering Traceability Matrix

| ID | Engineering Task | AI Role | Outcome | Engineer Decision | Validation |
|---|---|---|---|---|---|
| AI-001 | Normalize URL shortener requirements | Requirements analysis | Requirement decomposition | EDITED | Requirements review |
| AI-002 | Define service architecture | Architecture review | Modular monolith proposal | EDITED | Architecture review |
| AI-003 | Design short-code generation | Design/implementation | Base62 generation approach | EDITED | Unit tests |
| AI-004 | Review URL validation and security | Security analysis | Validation and SSRF recommendations | EDITED | Unit/security tests |
| AI-005 | Review redirect reliability | Design review | Transaction/failure-isolation recommendations | EDITED | Integration tests |
| AI-006 | Design analytics behavior | Design review | Click/visitor tracking approach | EDITED | Integration tests |
| AI-007 | Generate test scenarios | Testing | Positive, negative, boundary cases | EDITED | Automated test suite |
| AI-008 | Perform security review | Security analysis | Security risks/mitigations | EDITED | Security checklist |
| AI-009 | Analyze brownfield implementation | Code review | Legacy risks/incremental refactoring | EDITED / REJECTED | Regression tests |
| AI-010 | Resolve ambiguous analytics requirement | Requirements analysis | Explicit engineering decisions | EDITED | Design review |
| AI-011 | Review production readiness | Engineering review | Reliability/security/operations gaps | EDITED | Quality gates |
| AI-012 | Perform final repository review | Engineering review | Submission readiness review | HUMAN SIGN-OFF | Full validation |

## AI-001 — Requirement Analysis

### Engineering Intent

Convert the URL Shortener requirement into concrete engineering work that can be implemented and validated.

### Requirements Covered

- URL shortening
- Short-code generation
- Redirect behavior
- Expiration
- Disablement
- Click analytics
- Unique visitor analytics
- Validation
- Error handling
- Testing
- Operational considerations

### Constraints

- Keep the implementation appropriate for a prototype.
- Avoid unnecessary distributed infrastructure.
- Preserve a clear path toward production evolution.
- Define ambiguous behavior explicitly.
- Ensure requirements can be validated through tests.

### AI-Assisted Activity

AI was used to identify missing requirements, ambiguities, edge cases, and possible acceptance criteria. The output was reviewed against the assignment and converted into explicit engineering tasks.

**Final Decision: EDITED — APPROVED**

## AI-002 — Architecture

### Architecture

```text
HTTP Controller
      ↓
Service Layer
      ↓
Repository Layer
      ↓
PostgreSQL
```

Supporting components include Flyway, Actuator, OpenAPI, validation, error handling, and metrics.

### AI-Assisted Activity

AI was used to review architecture options and trade-offs between a modular monolith, microservices, caching, distributed analytics, and database-backed persistence.

### Engineer Decisions

**Selected:** modular monolith.

**Rejected:** multiple microservices because independent scaling/deployment boundaries are not required by the assignment.

**Rejected/deferred:** Redis because the prototype does not require distributed caching.

**Final Decision: EDITED — APPROVED**

## AI-003 — Short-Code Generation

### Engineering Intent

Implement compact, collision-safe public short codes without exposing database identifiers.

### Options Reviewed

- UUID-based identifiers
- Random Base62
- Database-ID encoding

### Engineer Decisions

**Rejected — UUID substring:** unnecessary length and truncation concerns.

**Rejected — Database ID encoding:** exposes persistence characteristics and makes identifiers predictable.

**Selected — Random Base62:** compact identifiers independent of the database primary key.

### Collision Strategy

```text
Generate candidate
       ↓
Persist
       ↓
Unique constraint conflict?
       ↓
     Yes
       ↓
Retry
       ↓
Maximum attempts reached?
       ↓
Controlled failure
```

**Final Decision: EDITED — APPROVED**

## AI-004 — URL Validation and Security

### Constraints

- HTTP and HTTPS only.
- Reject malformed URLs.
- Reject embedded credentials.
- Do not perform server-side destination fetching.
- Avoid unnecessary sensitive logging.
- Do not trust an inbound Host header for canonical URL generation.

### AI-Assisted Activity

AI was used to review URL validation for SSRF, open redirects, embedded credentials, protocol validation, Host-header trust, and sensitive logging.

### Engineer Decisions

Accepted HTTP/HTTPS validation, embedded credential rejection, no server-side destination fetching, and reduced sensitive logging.

Modified canonical short URLs to use the configured public URL rather than the inbound Host header.

Rejected server-side destination connectivity checks because destination fetching is unnecessary and would introduce SSRF and availability risk.

**Final Decision: EDITED — APPROVED**

## AI-005 — Redirect Reliability

The redirect is the primary user-facing operation; analytics is secondary telemetry.

### Request Flow

```text
Short-code lookup
       ↓
URL state validation
       ↓
Analytics recording
       ↓
HTTP redirect
```

### Engineer Decisions

- Valid redirects remain available.
- Disabled URLs do not redirect.
- Expired URLs do not redirect.
- Analytics failure should not make valid redirects unavailable.
- Analytics persistence uses an independent transaction boundary where applicable.

**Final Decision: EDITED — APPROVED**

## AI-006 — Analytics Design

### Engineering Decisions

**Click:** one successful redirect request counts as one click.

**Unique Visitor:** a privacy-preserving visitor hash is used as the prototype identity signal.

**Privacy:** raw IP addresses are not persisted.

**Analytics Failure:** analytics is secondary to redirect availability.

**Bot Detection:** not treated as a fully defined product requirement.

**Retention:** identified as a future product decision.

AI was used to identify analytics ambiguities and possible models. The final behavior was explicitly chosen by the engineer rather than silently assumed.

**Final Decision: EDITED — APPROVED**

## AI-007 — Testing

### Test Categories

**Unit tests**
- Short-code generation
- URL validation
- Service behavior
- Error handling

**Integration tests**
- URL creation
- Redirect
- Analytics
- Disablement
- Expiration
- PostgreSQL persistence

**Negative tests**
- Invalid URLs
- Unknown short codes
- Expired URLs
- Disabled URLs
- Persistence failures where applicable

AI was used to review the test strategy and identify edge cases. Suggestions were prioritized according to engineering risk and were not accepted blindly.

**Final Decision: EDITED — APPROVED**

## AI-008 — Security Review

### Areas Reviewed

- URL validation
- SSRF
- Host header trust
- Sensitive logging
- Rate limiting
- Security headers
- Privacy
- Database uniqueness
- Error handling

### Protections Reviewed/Implemented

- HTTP/HTTPS URL restrictions
- Embedded credential rejection
- No server-side destination fetching
- Configured public URL
- Security response headers
- Rate limiting
- Privacy-preserving visitor hashing
- Database uniqueness constraints

### Production Limitation

The current rate limiter is process-local. A horizontally scaled production deployment would require a shared/distributed rate-limiting mechanism.

**Final Decision: EDITED — APPROVED**

## AI-009 — Brownfield Scenario

### Representative Legacy Behavior

```java
Url url = urlRepository.findByShortCode(code).get();

return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(url.getOriginalUrl()))
        .build();
```

### Risks Identified

1. `Optional.get()` can produce an uncontrolled exception.
2. Unknown short codes lack explicit 404 behavior.
3. Expiration is not checked.
4. Disabled URLs are not checked.
5. Analytics behavior is absent.
6. Error behavior is not standardized.
7. Observability is limited.

### Engineer Decisions

Accepted explicit not-found handling, URL lifecycle validation, controlled errors, and regression testing.

Modified the refactoring to use the existing service layer instead of introducing a new architectural component.

Rejected distributed caching for the brownfield change because the goal is safe incremental improvement and Redis would expand operational scope without evidence it is required.

**Final Decision: EDITED — APPROVED**

## AI-010 — Ambiguous Requirement

### Requirement

> Add analytics showing clicks and unique visitors.

### Ambiguities

- What constitutes a click?
- What constitutes a unique visitor?
- What is the uniqueness time window?
- Do bots count?
- May raw IP addresses be stored?
- What are retention requirements?
- Should analytics failure block redirects?

### Engineer Decisions

- A successful redirect request counts as one click.
- A privacy-preserving visitor hash is used for prototype identity.
- Raw IP addresses are not persisted.
- Analytics is secondary to redirect availability.
- Retention is a future product requirement.
- Bot classification is not implemented as an implicit assumption.

**Final Decision: EDITED — APPROVED**

## AI-011 — Production Readiness Review

### Implemented/Reviewed

- PostgreSQL persistence
- Flyway migrations
- Health endpoint
- Metrics
- Structured error handling
- Rate limiting
- Security headers
- Automated tests
- Docker support

### Deferred

- Distributed rate limiting
- Authentication/authorization
- Redis caching
- Asynchronous analytics pipeline
- Centralized observability
- Horizontal scaling
- Backup/disaster recovery automation

These are documented as production evolution items rather than represented as completed functionality.

**Final Decision: EDITED — APPROVED**

## AI-012 — Final Engineering Review

### Review Areas

- Requirement coverage
- Correctness
- Architecture
- Security
- Reliability
- Testing
- Maintainability
- Observability
- Documentation
- AI traceability
- Production readiness

Every recommendation was evaluated against assignment requirements, actual implementation, automated tests, technical constraints, prototype scope, and production trade-offs.

AI recommendations were not treated as approval of production readiness.

### Final Human Sign-Off

The engineer remains accountable for functional correctness, security, reliability, maintainability, test quality, and production-readiness decisions.

**Final Status: HUMAN SIGN-OFF**

## AI Rejection Log

| Recommendation | Decision | Reason |
|---|---|---|
| Use database IDs as short codes | REJECTED | Exposes persistence characteristics and creates predictable identifiers |
| Use UUID substrings | REJECTED | Unnecessary length/entropy trade-off |
| Fetch destination URLs server-side | REJECTED | Introduces unnecessary SSRF and availability risk |
| Build public URL from inbound Host header | REJECTED | Host header should not determine canonical application URL |
| Introduce Redis immediately | REJECTED/DEFERRED | Unnecessary infrastructure for prototype scope |
| Split into microservices | REJECTED | No independent scaling/deployment requirement |
| Make analytics mandatory for redirect success | REJECTED | Analytics is secondary to the primary redirect operation |
| Persist raw visitor IP addresses | REJECTED | Unnecessary privacy exposure |
| Add complex bot detection | DEFERRED | Requirement does not define bot classification |
| Introduce distributed rate limiting | DEFERRED | Required for horizontal scale but unnecessary for current prototype |

## AI-Assisted Engineering Quality Gates

### Gate 1 — Requirements

- [x] Requirements decomposed.
- [x] Ambiguous requirements identified.
- [x] Acceptance criteria defined.
- [x] Scope controlled.

### Gate 2 — Architecture

- [x] Architecture documented.
- [x] Major trade-offs identified.
- [x] Unnecessary infrastructure avoided.
- [x] Production evolution documented.

### Gate 3 — Implementation

- [x] Core functionality implemented.
- [x] Validation implemented.
- [x] Error handling implemented.
- [x] Persistence implemented.
- [x] Migration strategy implemented.

### Gate 4 — Security

- [x] URL validation.
- [x] SSRF risk reviewed.
- [x] Host header trust reviewed.
- [x] Security headers.
- [x] Rate limiting.
- [x] Privacy considerations.

### Gate 5 — Testing

- [x] Unit tests.
- [x] Service tests.
- [x] Integration tests.
- [x] PostgreSQL integration validation.
- [x] Positive and negative scenarios.

### Gate 6 — Reliability

- [x] Expiration handling.
- [x] Disablement.
- [x] Bounded short-code collision retries.
- [x] Analytics isolation.
- [x] Controlled errors.

### Gate 7 — Documentation

- [x] Architecture documentation.
- [x] Greenfield scenario.
- [x] Brownfield scenario.
- [x] Ambiguous scenario.
- [x] AI traceability.
- [x] Production limitations.
- [x] Engineering summary.

## Final Engineering Position

AI accelerated the engineering workflow in requirement decomposition, design exploration, edge-case identification, security review, test-case discovery, documentation, and final code review.

AI output was not treated as authoritative. The final implementation reflects engineer decisions regarding architecture, security, reliability, data modeling, transaction boundaries, error behavior, scope, and production trade-offs.

The resulting system is intentionally a production-oriented prototype rather than an over-engineered distributed system.

## Final Statement

> AI was used to accelerate engineering execution, not to replace engineering ownership.
>
> Every material AI-assisted recommendation was reviewed against requirements, constraints, security implications, and validation evidence. Where AI recommendations introduced unnecessary complexity or unacceptable risk, they were rejected or deferred.
>
> The engineer remains accountable for the final implementation, test results, security posture, reliability characteristics, and production-readiness assessment.
