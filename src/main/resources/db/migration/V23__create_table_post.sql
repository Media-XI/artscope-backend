create table `post` (
    `post_id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `title` varchar(255) NOT NULL,
    `content` text NOT NULL,
    `created_time` datetime NOT NULL,
    `updated_time` datetime NULL,
    `author_id` binary(16) NOT NULL,
    foreign key (`author_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;