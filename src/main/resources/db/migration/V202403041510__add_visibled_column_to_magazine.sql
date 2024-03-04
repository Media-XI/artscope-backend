ALTER TABLE `magazine`
    ADD `visibled` tinyint(1) NOT NULL DEFAULT '1' AFTER `is_deleted`;