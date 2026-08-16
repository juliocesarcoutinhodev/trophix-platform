-- V18: fórum (categories, topics, replies)
--
-- Módulo Forums (Monolito Modular). FKs de autor referenciam users (cascade),
-- assim como no módulo de reports.
CREATE TABLE forum_categories
(
    id          UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    order_index INT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_forum_categories PRIMARY KEY (id)
);

CREATE TABLE forum_topics
(
    id            UUID         NOT NULL,
    category_id   UUID         NOT NULL,
    author_id     UUID         NOT NULL,
    title         VARCHAR(200) NOT NULL,
    content       TEXT         NOT NULL,
    views_count   INT          NOT NULL DEFAULT 0,
    replies_count INT          NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_forum_topics PRIMARY KEY (id),
    CONSTRAINT fk_forum_topics_category FOREIGN KEY (category_id) REFERENCES forum_categories (id) ON DELETE CASCADE,
    CONSTRAINT fk_forum_topics_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE forum_replies
(
    id         UUID        NOT NULL,
    topic_id   UUID        NOT NULL,
    author_id  UUID        NOT NULL,
    content    TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_forum_replies PRIMARY KEY (id),
    CONSTRAINT fk_forum_replies_topic FOREIGN KEY (topic_id) REFERENCES forum_topics (id) ON DELETE CASCADE,
    CONSTRAINT fk_forum_replies_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_forum_topics_category_updated ON forum_topics (category_id, updated_at DESC);
CREATE INDEX idx_forum_replies_topic_created ON forum_replies (topic_id, created_at ASC);

-- Categorias iniciais para o fórum ficar utilizável.
INSERT INTO forum_categories (id, name, description, order_index) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Dúvidas Gerais', 'Perguntas e discussões gerais sobre a plataforma e troféus.', 0),
    ('22222222-2222-2222-2222-222222222222', 'Guias e Estratégias', 'Compartilhe e discuta guias, rotas e estratégias de platina.', 1),
    ('33333333-3333-3333-3333-333333333333', 'Off-Topic', 'Assuntos fora do universo de troféus.', 2);
