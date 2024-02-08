CREATE TABLE `magazine_like_member`
(
    `magazine_id` bigint     NOT NULL,
    `member_id`   binary(16) NOT NULL,
    `liked_time`  datetime   NOT NULL,
    PRIMARY KEY (`magazine_id`, `member_id`),
    FOREIGN KEY (`magazine_id`) REFERENCES `magazine` (`magazine_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;