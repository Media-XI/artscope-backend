DROP TABLE IF EXISTS `exhibition`;

CREATE TABLE `exhibition`
(
    `exhibition_id` bigint       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `title`         varchar(255) NOT NULL,
    `description`   text         NULL,
    `price`         int          NOT NULL,
    `link`          varchar(500) NOT NULL,
    `type`          varchar(100) NOT NULL,
    `enabled`       tinyint(1)   NOT NULL DEFAULT 1,
    `created_time`  datetime     NOT NULL,
    `updated_time`  datetime     NULL,
    `member_id`     binary(16)   NOT NULL,
    foreign key (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;