-- V3: torna email opcional na tabela de usuários
-- Contexto: o fluxo de registro via PSN não fornece email.
-- O campo será preenchido opcionalmente pelo usuário no futuro.

-- Remove a constraint NOT NULL
ALTER TABLE users
    ALTER COLUMN email DROP NOT NULL;

-- Remove o índice UNIQUE gerado pelo Hibernate (nome pode variar, usa IF EXISTS)
ALTER TABLE users
    DROP CONSTRAINT IF EXISTS uk6dotkott2kjsp8vw4d0m25fb7;
