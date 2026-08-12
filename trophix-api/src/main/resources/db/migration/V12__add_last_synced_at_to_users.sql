-- V12: controle de ultima sincronizacao do perfil do usuario
ALTER TABLE users
    ADD COLUMN last_synced_at TIMESTAMPTZ;