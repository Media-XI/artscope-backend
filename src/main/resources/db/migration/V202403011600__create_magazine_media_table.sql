CREATE TABLE `magazine_media`
(
    `magazine_media_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `type`              varchar(10) NOT NULL,
    `url`               varchar(512) NOT NULL,
    `created_time`      datetime     NOT NULL,
    `updated_time`      datetime     NOT NULL,
    `magazine_id`       bigint       NOT NULL,
    FOREIGN KEY (`magazine_id`) REFERENCES `magazine` (`magazine_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;