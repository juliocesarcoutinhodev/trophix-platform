-- V23: analytics de cliques (módulo offers)
ALTER TABLE offers ADD COLUMN click_count BIGINT NOT NULL DEFAULT 0;
