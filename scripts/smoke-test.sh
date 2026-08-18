#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"

create_response=$(curl -fsS -X POST "$BASE_URL/api/v1/urls" -H 'Content-Type: application/json' -d '{"longUrl":"https://example.com/smoke"}')
code=$(python3 -c 'import json,sys; print(json.load(sys.stdin)["shortCode"])' <<< "$create_response")

echo "Created short code: $code"
curl -fsSI "$BASE_URL/$code" | grep -E '^HTTP/|^location:'
curl -fsS "$BASE_URL/api/v1/urls/$code/analytics"
printf '\nSmoke test passed.\n'
