DROP TABLE IF EXISTS `exhibition_media`;

CREATE TABLE `exhibition_media`
(
    `exhibition_media_id` bigint       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `media_type`          varchar(255) NOT NULL,
    `media_url`           varchar(512) NOT NULL,
    `created_time`        datetime     NOT NULL,
    `updated_time`        datetime     NULL,
    `exhibition_id`       bigint       NOT NULL,
    foreign key (`exhibition_id`) REFERENCES `exhibition` (`exhibition_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;