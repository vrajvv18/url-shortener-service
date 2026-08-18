# Quality Gates

## Required before submission

- [ ] Java 17 or 20 build succeeds
- [ ] Unit tests pass
- [ ] PostgreSQL integration tests pass with Testcontainers
- [ ] Flyway migration succeeds on a clean database
- [ ] Invalid input returns structured 400 responses
- [ ] Unknown codes return 404
- [ ] Expired/disabled codes return 410
- [ ] Redirect returns 302 and correct Location
- [ ] Analytics failure does not block redirect
- [ ] Rate limiting is exercised manually or with a filter test
- [ ] No raw IP is persisted
- [ ] Public URL does not come from Host header
- [ ] CI runs `mvn verify`
- [ ] Docker Compose starts the service and database
- [ ] Swagger is available
- [ ] Health endpoint is available
- [ ] Risks and limitations are documented
- [ ] Engineer reviews all AI-assisted changes

## CI command

```bash
mvn -B verify
```
