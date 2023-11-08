DROP TABLE IF EXISTS `member`;

CREATE TABLE IF NOT EXISTS `member`
(
    `member_id`    binary(16)   NOT NULL,
    `username`     varchar(50)  NOT NULL UNIQUE,
    `password`     varchar(100) NOT NULL,
    `name`         varchar(50)  NOT NULL,
    `email`        varchar(50)  NOT NULL UNIQUE,
    `activated`    boolean DEFAULT 0,
    `created_time` datetime     NOT NULL,
    PRIMARY KEY (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;