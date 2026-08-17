-- V24: percentual de jogadores que conquistaram cada troféu (trophyEarnedRate da PSN)
ALTER TABLE trophies ADD COLUMN rarity DOUBLE PRECISION NOT NULL DEFAULT 0;
