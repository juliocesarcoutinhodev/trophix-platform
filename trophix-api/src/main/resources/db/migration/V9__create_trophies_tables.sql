-- V9: tabelas de troféus e de troféus conquistados pelo usuário
CREATE TABLE trophies
(
    id            UUID         NOT NULL,
    game_id       UUID         NOT NULL,
    psn_trophy_id INT          NOT NULL,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(500),
    type          VARCHAR(20)  NOT NULL,
    icon_url      VARCHAR(500),

    CONSTRAINT pk_trophies PRIMARY KEY (id),
    CONSTRAINT uq_trophies_game_psn UNIQUE (game_id, psn_trophy_id),
    CONSTRAINT fk_trophies_game FOREIGN KEY (game_id) REFERENCES games (id) ON DELETE CASCADE
);

CREATE TABLE user_trophies
(
    id        UUID        NOT NULL,
    user_id   UUID        NOT NULL,
    trophy_id UUID        NOT NULL,
    earned_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_user_trophies PRIMARY KEY (id),
    CONSTRAINT uq_user_trophies_user_trophy UNIQUE (user_id, trophy_id),
    CONSTRAINT fk_user_trophies_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_trophies_trophy FOREIGN KEY (trophy_id) REFERENCES trophies (id) ON DELETE CASCADE
);