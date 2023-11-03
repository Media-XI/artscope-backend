ALTER TABLE `artwork`
    ADD COLUMN `member_id` binary(16) NOT NULL;

ALTER TABLE `artwork`
    ADD CONSTRAINT `fk_artwork_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`);