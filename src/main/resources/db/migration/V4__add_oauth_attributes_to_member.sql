ALTER TABLE member
    ADD COLUMN `oauth_provider` VARCHAR(255) NULL;

ALTER TABLE member
    ADD COLUMN `oauth_provider_id` VARCHAR(255) NULL;