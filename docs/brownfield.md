# Brownfield Scenario

The repository must demonstrate actual brownfield reasoning, not merely claim that a flaw could be introduced.

## Existing implementation to review

A representative legacy redirect implementation would look like:

```java
Url url = urlRepository.findByShortCode(code).get();
return ResponseEntity.status(302).location(URI.create(url.getLongUrl())).build();
```

## Problems identified

1. `Optional.get()` converts a normal 404 into an uncontrolled exception.
2. Disabled links are not checked.
3. Expiration is not checked.
4. Analytics is absent.
5. There is no explicit error contract.
6. No metric distinguishes redirect failures from analytics failures.
7. The behavior is difficult to regression-test.

## Refactored design in this repository

`RedirectController -> RedirectService -> UrlService.get()` centralizes not-found behavior, then the redirect service checks status and expiry before producing a 302. Analytics is best-effort and instrumented separately.

## Regression validation

The integration test covers:

- create;
- redirect;
- analytics count;
- disable;
- disabled redirect returns 410.

The unit suite covers invalid URL, embedded credentials, expiry, and database collision retry.

## Interview explanation

The important brownfield behavior is: **preserve the API contract, change the smallest necessary surface, add regression coverage before/with the refactor, and validate that the failure modes are safer than before.**
