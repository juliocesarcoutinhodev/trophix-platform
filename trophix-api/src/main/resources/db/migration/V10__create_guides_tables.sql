-- V10: tabelas de guias e de votos (upvotes) dos guias
CREATE TABLE guides
(
    id            UUID         NOT NULL,
    trophy_id     UUID         NOT NULL,
    author_id     UUID         NOT NULL,
    content       TEXT         NOT NULL,
    video_url     VARCHAR(500),
    status        VARCHAR(20)  NOT NULL,
    upvotes_count INT          NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_guides PRIMARY KEY (id),
    CONSTRAINT fk_guides_trophy FOREIGN KEY (trophy_id) REFERENCES trophies (id) ON DELETE CASCADE,
    CONSTRAINT fk_guides_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE guide_votes
(
    id        UUID        NOT NULL,
    guide_id  UUID        NOT NULL,
    user_id   UUID        NOT NULL,
    voted_at  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_guide_votes PRIMARY KEY (id),
    CONSTRAINT uq_guide_votes_guide_user UNIQUE (guide_id, user_id),
    CONSTRAINT fk_guide_votes_guide FOREIGN KEY (guide_id) REFERENCES guides (id) ON DELETE CASCADE,
    CONSTRAINT fk_guide_votes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);