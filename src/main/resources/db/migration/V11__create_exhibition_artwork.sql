DROP TABLE IF EXISTS `exhibition_artwork`;

CREATE TABLE `exhibition_artwork`
(
    `exhibition_artwork_id` bigint                           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `status`                varchar(255) DEFAULT 'SUBMITTED' NOT NULL,
    `created_time`          datetime                         NOT NULL,
    `updated_time`          datetime                         NULL,
    `exhibition_id`         bigint                           NOT NULL,
    `artwork_id`            bigint                           NOT NULL,
    FOREIGN KEY (`exhibition_id`) REFERENCES `exhibition` (`exhibition_id`),
    FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`artwork_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;