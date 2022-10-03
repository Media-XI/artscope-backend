DROP TABLE IF EXISTS `member`;

CREATE TABLE IF NOT EXISTS `member` (
    `member_id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL,
    `password` varchar(100),
    `name` varchar(50) NOT NULL,
    PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;