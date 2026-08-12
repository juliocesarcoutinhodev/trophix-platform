-- V11: guias passam a suportar roadmaps de jogo (game_id) alem de trofeu (trophy_id)
-- Pelo menos um dos alvos deve estar preenchido.

ALTER TABLE guides
    ADD COLUMN game_id UUID,
    ALTER COLUMN trophy_id DROP NOT NULL;

ALTER TABLE guides
    ADD CONSTRAINT ck_guides_target CHECK (game_id IS NOT NULL OR trophy_id IS NOT NULL);

ALTER TABLE guides
    ADD CONSTRAINT fk_guides_game FOREIGN KEY (game_id) REFERENCES games (id) ON DELETE CASCADE;