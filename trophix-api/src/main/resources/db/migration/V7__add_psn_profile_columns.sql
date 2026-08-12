-- V7: adiciona os campos de perfil PSN na tabela users
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS account_id VARCHAR(64),
    ADD COLUMN IF NOT EXISTS psn_level INT,
    ADD COLUMN IF NOT EXISTS level_progress INT,
    ADD COLUMN IF NOT EXISTS total_platinum INT,
    ADD COLUMN IF NOT EXISTS total_gold INT,
    ADD COLUMN IF NOT EXISTS total_silver INT,
    ADD COLUMN IF NOT EXISTS total_bronze INT;