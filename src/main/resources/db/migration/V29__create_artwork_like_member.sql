CREATE TABLE `artwork_like_member` (
    `artwork_id` BIGINT NOT NULL,
    `member_id` BINARY(16) NOT NULL,
    `liked_time` DATETIME NOT NULL,
    PRIMARY KEY (`artwork_id`, `member_id`),
    CONSTRAINT `fk_artwork_like_members_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`artwork_id`),
    CONSTRAINT `fk_artwork_like_members_member_id` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
)