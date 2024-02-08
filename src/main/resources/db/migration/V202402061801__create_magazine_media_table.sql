CREATE TABLE `magazine_media`
(
    `magazine_media_id` bigint       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `media_type`              varchar(50)  NOT NULL,
    `media_url`               varchar(512) NOT NULL,
    `is_deleted`        tinyint(1)   NOT NULL DEFAULT 0,
    `created_time`      datetime     NOT NULL,
    `updated_time`      datetime     NOT NULL,
    `magazine_id`       bigint       NOT NULL,
    `member_id`         binary(16)   NOT NULL,
    FOREIGN KEY (`magazine_id`) REFERENCES `magazine` (`magazine_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;