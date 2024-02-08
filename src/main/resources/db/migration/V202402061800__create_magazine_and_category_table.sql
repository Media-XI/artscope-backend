CREATE TABLE `magazine_category`
(
    `category_id`  bigint       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name`         varchar(255) NOT NULL,
    `is_deleted`   tinyint      NOT NULL DEFAULT 0,
    `created_time` datetime     NOT NULL,
    `updated_time` datetime     NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `magazine`
(
    `magazine_id`  bigint       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `title`        varchar(255) NOT NULL,
    `content`      text         NOT NULL,
    `views`        int          NOT NULL DEFAULT 0,
    `likes`        int          NOT NULL DEFAULT 0,
    `is_deleted`   tinyint      NOT NULL DEFAULT 0,
    `created_time` datetime     NOT NULL,
    `updated_time` datetime     NOT NULL,
    `member_id`    binary(16) NOT NULL,
    `category_id`  bigint       NOT NULL,
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
    FOREIGN KEY (`category_id`) REFERENCES `magazine_category` (`category_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
