ALTER TABLE `magazine`
    ADD `metadata` JSON NULL DEFAULT NULL AFTER `content`;