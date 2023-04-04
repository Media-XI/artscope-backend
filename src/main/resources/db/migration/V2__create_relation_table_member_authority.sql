DROP TABLE IF EXISTS `member_authority`;

CREATE TABLE IF NOT EXISTS `member_authority` (
    `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `member_id` binary(16) NOT NULL ,
    `authority_name` varchar(50) NOT NULL ,
    foreign key (`member_id`) REFERENCES member(`member_id`),
    foreign key (`authority_name`) REFERENCES authority(`authority_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;