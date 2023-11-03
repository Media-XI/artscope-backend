DROP TABLE IF EXISTS `exhibition`;

CREATE TABLE `exhibition`
(
    `exhibition_id` bigint       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `title`         varchar(255) NOT NULL,
    `description`   text         NULL,
    `start_date`    datetime     NULL,
    `end_date`      datetime     NULL,
    `created_time`  datetime     NOT NULL,
    `updated_time`  datetime     NULL,
    `member_id`     binary(16)   NOT NULL,
    foreign key (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;