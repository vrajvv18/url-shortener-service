CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(8) NOT NULL UNIQUE,
    long_url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ,
    status VARCHAR(16) NOT NULL
);

CREATE INDEX idx_urls_expires_at ON urls(expires_at);

CREATE TABLE click_events (
    id BIGSERIAL PRIMARY KEY,
    url_id BIGINT NOT NULL REFERENCES urls(id),
    clicked_at TIMESTAMPTZ NOT NULL,
    visitor_hash VARCHAR(64),
    user_agent VARCHAR(512),
    referrer VARCHAR(2048)
);

CREATE INDEX idx_click_events_url_time ON click_events(url_id, clicked_at);
