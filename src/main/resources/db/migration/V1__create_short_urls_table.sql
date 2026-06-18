CREATE TABLE short_urls (
    id          BIGSERIAL       PRIMARY KEY,
    original_url VARCHAR(2048)  NOT NULL,
    code        VARCHAR(10)     NOT NULL,
    created_at  TIMESTAMP       NOT NULL,
    click_count BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT uq_short_urls_code UNIQUE (code)
);

CREATE INDEX idx_short_urls_code ON short_urls (code);
