-- V16: denúncias (reports)
--
-- Denúncias de conteúdo (guias, usuários, comentários). target_type/target_id
-- são polimórficos (não há FK para o alvo). Status: OPEN, RESOLVED, DISMISSED.
CREATE TABLE reports
(
    id          UUID         NOT NULL,
    reporter_id UUID         NOT NULL,
    target_type VARCHAR(20)  NOT NULL,
    target_id   UUID         NOT NULL,
    reason      VARCHAR(500) NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL,
    resolved_at TIMESTAMPTZ,

    CONSTRAINT pk_reports PRIMARY KEY (id),
    CONSTRAINT fk_reports_reporter FOREIGN KEY (reporter_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_reports_status ON reports (status);
CREATE INDEX idx_reports_reporter ON reports (reporter_id);
CREATE INDEX idx_reports_target ON reports (target_type, target_id);
