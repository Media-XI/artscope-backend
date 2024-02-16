CREATE TABLE `notification`
(
    `notification_id` bigint      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `member_id`       binary(16),
    `message`         TEXT       NOT NULL,
    `type`            varchar(50) NOT NULL,
    `created_time`    datetime    NOT NULL,
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;