-- V13: adiciona title e description aos guias
ALTER TABLE guides ADD COLUMN title VARCHAR(255);
ALTER TABLE guides ADD COLUMN description VARCHAR(1000);

-- Backfill dos guias existentes antes de tornar title NOT NULL
UPDATE guides SET title = 'Guia' WHERE title IS NULL;

ALTER TABLE guides ALTER COLUMN title SET NOT NULL;
