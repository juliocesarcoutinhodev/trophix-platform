-- V4: tabela de roles do sistema (RBAC)
CREATE TABLE roles
(
    id   UUID        NOT NULL,
    name VARCHAR(50) NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name)
);
