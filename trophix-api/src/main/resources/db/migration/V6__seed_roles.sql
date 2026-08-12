-- V6: seed das roles padrão do sistema
-- IDs fixos para garantir idempotência e referência estável
INSERT INTO roles (id, name)
VALUES ('01970000-0000-7000-8000-000000000001', 'ROLE_USER'),
       ('01970000-0000-7000-8000-000000000002', 'ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;
