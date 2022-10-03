DROP TABLE IF EXISTS `authority`;

CREATE TABLE IF NOT EXISTS `authority` (
    `authority_name` varchar(50) NOT NULL,
    PRIMARY KEY (`authority_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;