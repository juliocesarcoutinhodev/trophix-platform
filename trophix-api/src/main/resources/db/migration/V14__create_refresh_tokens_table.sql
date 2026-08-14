-- V14: refresh tokens rotacionáveis (sessões server-side)
--
-- O refresh token é um valor opaco gerado com CSPRNG. Apenas o hash SHA-256
-- é persistido aqui; o valor em claro nunca toca o banco. Tokens de uma mesma
-- família descendem do mesmo login; a rotação revoga o anterior e o reuso de
-- um token já rotacionado revoga a família inteira (detecção de roubo).
CREATE TABLE refresh_tokens
(
    id           UUID         NOT NULL,
    family_id    UUID         NOT NULL,
    user_id      UUID         NOT NULL,
    token_hash   VARCHAR(64)  NOT NULL,
    expires_at   TIMESTAMPTZ  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL,
    revoked_at   TIMESTAMPTZ,
    last_used_at TIMESTAMPTZ,
    user_agent   VARCHAR(512),
    ip_address   VARCHAR(64),

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uq_refresh_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_family ON refresh_tokens (family_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);
