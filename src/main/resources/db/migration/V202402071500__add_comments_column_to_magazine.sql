ALTER TABLE `magazine`
    ADD COLUMN `comments` int NOT NULL DEFAULT 0 AFTER `likes`;
