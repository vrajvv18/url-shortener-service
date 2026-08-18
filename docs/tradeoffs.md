# Trade-offs and Limitations

- Modular monolith over microservices: lower operational overhead and enough separation for the prototype.
- PostgreSQL over distributed storage: strong uniqueness guarantees and straightforward local operation.
- Flyway over Hibernate schema mutation: explicit, reviewable database changes.
- Synchronous analytics over asynchronous messaging: simpler failure semantics for the assignment; high-volume production would move analytics off the redirect path.
- Process-local rate limit: useful for a single instance but not sufficient for horizontally scaled production.
- Random Base62 over sequential IDs: avoids exposing record volume and keeps codes opaque; uniqueness is enforced by the database.
- No authentication/authorization: omitted because the assignment does not require user accounts; production management APIs need authn/authz.
- No bot classification: analytics semantics are intentionally simple and documented.
