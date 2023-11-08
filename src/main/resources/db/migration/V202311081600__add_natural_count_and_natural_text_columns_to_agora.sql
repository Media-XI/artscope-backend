ALTER TABLE agora
    ADD COLUMN natural_count INT          NOT NULL DEFAULT 0 AFTER agree_count,
    ADD COLUMN natural_text  VARCHAR(255) NOT NULL AFTER agree_text;