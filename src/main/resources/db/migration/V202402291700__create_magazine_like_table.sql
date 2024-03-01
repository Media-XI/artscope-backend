DROP TABLE IF EXISTS `magazine_like_member`;

DROP TABLE IF EXISTS `magazine_like`;

CREATE TABLE `magazine_like` (
    `magazine_like_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `magazine_id` BIGINT NOT NULL,
    `member_id` BIGINT NOT NULL,
    `liked_time` DATETIME NOT NULL,
    FOREIGN KEY (`magazine_id`) REFERENCES `magazine` (`magazine_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
)