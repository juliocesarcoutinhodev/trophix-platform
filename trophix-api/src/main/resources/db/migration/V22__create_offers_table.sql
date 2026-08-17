-- V22: ofertas (módulo offers / LootBox)
CREATE TABLE offers
(
    id                  UUID          NOT NULL,
    title               VARCHAR(255)  NOT NULL,
    image_url           VARCHAR(1000) NOT NULL,
    original_price      NUMERIC(10,2) NOT NULL,
    discount_price      NUMERIC(10,2) NOT NULL,
    discount_percentage INTEGER       NOT NULL,
    store_name          VARCHAR(50)   NOT NULL,
    affiliate_link      VARCHAR(1000) NOT NULL,
    category            VARCHAR(50)   NOT NULL,
    is_flash_deal       BOOLEAN       NOT NULL DEFAULT FALSE,
    is_active           BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ   NOT NULL,
    updated_at          TIMESTAMPTZ   NOT NULL,

    CONSTRAINT pk_offers PRIMARY KEY (id)
);

CREATE INDEX idx_offers_category_active ON offers (category, is_active);
CREATE INDEX idx_offers_created_at ON offers (created_at DESC);
