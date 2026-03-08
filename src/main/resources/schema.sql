CREATE TABLE IF NOT EXISTS product (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    seller_id       VARCHAR(36) NOT NULL,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    category        VARCHAR(50) NOT NULL,
    condition_grade VARCHAR(20) NOT NULL,
    starting_price  BIGINT NOT NULL,
    instant_price   BIGINT,
    reserve_price   BIGINT,
    min_bid_unit    BIGINT NOT NULL DEFAULT 1000,
    end_type        VARCHAR(20) NOT NULL,
    duration_hours  INTEGER,
    max_bid_count   INTEGER,
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    image_urls      JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_product_seller ON product(seller_id);
CREATE INDEX IF NOT EXISTS idx_product_status ON product(status);
CREATE INDEX IF NOT EXISTS idx_product_category_status ON product(category, status);
CREATE INDEX IF NOT EXISTS idx_product_created ON product(created_at DESC);

CREATE TABLE IF NOT EXISTS processed_event (
    event_id     VARCHAR(36) PRIMARY KEY,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
