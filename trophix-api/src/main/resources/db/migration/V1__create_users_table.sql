-- V1: tabela de usuários registrados via PSN
CREATE TABLE users
(
    id         UUID         NOT NULL,
    username   VARCHAR(20)  NOT NULL,
    email      VARCHAR(320),
    avatar_url VARCHAR(500),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE (username)
);
