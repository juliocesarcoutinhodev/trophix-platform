-- V25: flag de destaque manual (Trending Games híbrido na Home)
ALTER TABLE games ADD COLUMN is_featured BOOLEAN NOT NULL DEFAULT FALSE;
