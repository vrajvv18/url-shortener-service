# Ambiguous Scenario

## Ambiguous requirement

> Add analytics showing clicks and unique visitors.

## Questions raised

- What is a click?
- What is a unique visitor?
- Lifetime or daily counts?
- Exact or approximate uniqueness?
- How should bots be treated?
- What data can be stored?
- How long should events be retained?
- Should analytics failure block redirects?

## Normalized decisions

- A click is one redirect request that reaches the redirect service and attempts analytics persistence.
- The prototype exposes lifetime click count.
- Unique visitors are approximate and privacy-oriented.
- Raw client IPs are not persisted; a SHA-256 visitor hash is stored.
- Bot detection is out of scope and documented as a limitation.
- Analytics retention is not implemented in the prototype.
- Analytics persistence is best-effort; a valid redirect remains successful if analytics persistence fails.

## Why

These decisions optimize for a defensible interview prototype: clear semantics, privacy minimization, simple operational behavior, and no coupling between analytics availability and core redirect availability.
