ALTER TABLE `member`
    ADD INDEX `idx_member_username` (`username`);

ALTER TABLE `member`
    ADD INDEX `idx_member_email` (`email`);