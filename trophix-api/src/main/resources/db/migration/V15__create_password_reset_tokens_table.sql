-- V15: tokens de redefinição de senha (single-use, com hash)
--
-- O token enviado no link é um UUIDv7; apenas o hash SHA-256 é persistido
-- aqui. Um token por usuário ativo por vez; consumido após o uso.
CREATE TABLE password_reset_tokens
(
    id          UUID        NOT NULL,
    user_id     UUID        NOT NULL,
    token_hash  VARCHAR(64) NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ,

    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id),
    CONSTRAINT uq_password_reset_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_password_reset_tokens_user ON password_reset_tokens (user_id);
CREATE INDEX idx_password_reset_tokens_expires_at ON password_reset_tokens (expires_at);
