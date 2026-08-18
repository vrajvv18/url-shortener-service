# Threat Model

| Threat | Control | Residual risk |
|---|---|---|
| Host-header link injection | Configured `PUBLIC_BASE_URL` | Deployment must set correct canonical URL |
| Embedded credentials in URL | Reject URLs with user info | Destination itself may still be malicious/phishing content |
| SSRF through destination validation | Service never fetches destinations | Browser/client follows redirects |
| Create endpoint abuse | Process-local rate limit | Not distributed; requires shared limiter at scale |
| SQL injection | JPA parameterization | Keep dependencies patched |
| Raw IP privacy exposure | Store only derived SHA-256 visitor hash | Hash may still be considered personal data depending on context |
| Code collision | DB unique constraint + bounded retry | Extremely rare exhaustion returns 503 |
| Sensitive actuator details | Health details hidden | Metrics remain operational data and should be access-controlled in production |
| Browser framing/content sniffing | Security headers | Full CSP/HSTS requires deployment-specific policy |
