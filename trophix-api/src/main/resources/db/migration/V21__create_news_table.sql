-- V21: agregador de notícias (módulo news)
CREATE TABLE news_articles
(
    id           UUID         NOT NULL,
    title        VARCHAR(300) NOT NULL,
    link         VARCHAR(1000) NOT NULL,
    image_url    VARCHAR(1000),
    source       VARCHAR(100) NOT NULL,
    is_featured  BOOLEAN      NOT NULL DEFAULT FALSE,
    published_at TIMESTAMPTZ  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_news_articles PRIMARY KEY (id),
    CONSTRAINT uq_news_articles_link UNIQUE (link)
);

CREATE INDEX idx_news_articles_published_at ON news_articles (published_at DESC);
