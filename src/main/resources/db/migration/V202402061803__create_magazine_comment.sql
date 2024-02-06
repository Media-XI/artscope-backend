
CREATE TABLE `magazine_comment`
(
    `magazine_comment_id` bigint     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `comment`             text       NOT NULL,
    `is_deleted`          tinyint    NOT NULL DEFAULT 0,
    `created_time`        datetime   NOT NULL,
    `updated_time`        datetime   NOT NULL,
    `member_id`           binary(16) NOT NULL,
    `magazine_id`         bigint     NOT NULL,
    `parent_comment_id`   bigint     NOT NULL,
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
    FOREIGN KEY (`magazine_id`) REFERENCES `magazine` (`magazine_id`),
    FOREIGN KEY (`parent_comment_id`) REFERENCES `magazine_comment` (`magazine_comment_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
