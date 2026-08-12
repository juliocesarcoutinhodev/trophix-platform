-- V2: tabela de tickets pendentes de vinculação de conta PSN
CREATE TABLE account_link_tickets
(
    id                 UUID        NOT NULL,
    psn_id             VARCHAR(20) NOT NULL,
    verification_token VARCHAR(9)  NOT NULL,
    expires_at         TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_account_link_tickets PRIMARY KEY (id),
    CONSTRAINT uq_account_link_tickets_psn_id UNIQUE (psn_id)
);
