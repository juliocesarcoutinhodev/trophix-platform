-- V8: tabelas de jogos e do progresso do usuário por jogo
CREATE TABLE games
(
    id                  UUID         NOT NULL,
    np_communication_id VARCHAR(64)  NOT NULL,
    name                VARCHAR(255) NOT NULL,
    image_url           VARCHAR(500),
    platform            VARCHAR(100),
    total_trophies      INT          NOT NULL,

    CONSTRAINT pk_games PRIMARY KEY (id),
    CONSTRAINT uq_games_np_communication_id UNIQUE (np_communication_id)
);

CREATE TABLE user_games
(
    id                  UUID        NOT NULL,
    user_id             UUID        NOT NULL,
    game_id             UUID        NOT NULL,
    progress_percentage INT         NOT NULL,
    earned_trophies     INT         NOT NULL,
    last_played_at      TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_user_games PRIMARY KEY (id),
    CONSTRAINT uq_user_games_user_game UNIQUE (user_id, game_id),
    CONSTRAINT fk_user_games_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_games_game FOREIGN KEY (game_id) REFERENCES games (id) ON DELETE CASCADE
);