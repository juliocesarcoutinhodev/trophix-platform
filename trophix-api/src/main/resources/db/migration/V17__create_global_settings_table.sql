-- V17: configurações globais (single-row)
--
-- A tabela guarda uma única linha (id fixo via CHECK), representando as
-- configurações globais da plataforma (identidade, textos, alertas, regras).
CREATE TABLE global_settings
(
    id                    UUID          NOT NULL,
    site_name             VARCHAR(100)  NOT NULL,
    contact_email         VARCHAR(320)  NOT NULL,
    discord_url           VARCHAR(500)  NOT NULL,
    twitter_url           VARCHAR(500)  NOT NULL,
    youtube_url           VARCHAR(500)  NOT NULL,
    instagram_url         VARCHAR(500)  NOT NULL,
    hero_title            VARCHAR(255)  NOT NULL,
    hero_subtitle         VARCHAR(500)  NOT NULL,
    global_alert_enabled  BOOLEAN       NOT NULL,
    global_alert_text     VARCHAR(500)  NOT NULL,
    footer_text           VARCHAR(500)  NOT NULL,
    require_guide_approval BOOLEAN      NOT NULL,
    forbidden_words       VARCHAR(1000) NOT NULL,
    meta_title            VARCHAR(255)  NOT NULL,
    meta_description      VARCHAR(500)  NOT NULL,

    CONSTRAINT pk_global_settings PRIMARY KEY (id),
    CONSTRAINT ck_global_settings_single_row CHECK (id = '00000000-0000-0000-0000-000000000001')
);
