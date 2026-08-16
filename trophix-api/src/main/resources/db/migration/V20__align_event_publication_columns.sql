-- V20: alinha colunas do journal de eventos ao mapeamento JPA do Modulith
--
-- As entidades do spring-modulith-events-jpa mapeiam listener_id, event_type,
-- serialized_event e status como String sem length -> varchar(255) no Hibernate
-- 6. A V19 criou como TEXT; alinhar evita ALTERs recorrentes do ddl-auto=update
-- e garante o validate no prod.
ALTER TABLE event_publication
    ALTER COLUMN listener_id TYPE VARCHAR(255),
    ALTER COLUMN event_type TYPE VARCHAR(255),
    ALTER COLUMN serialized_event TYPE VARCHAR(255),
    ALTER COLUMN status TYPE VARCHAR(255);

ALTER TABLE event_publication_archive
    ALTER COLUMN listener_id TYPE VARCHAR(255),
    ALTER COLUMN event_type TYPE VARCHAR(255),
    ALTER COLUMN serialized_event TYPE VARCHAR(255),
    ALTER COLUMN status TYPE VARCHAR(255);
