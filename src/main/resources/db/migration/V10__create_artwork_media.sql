DROP TABLE IF EXISTS `artwork_media`;

CREATE TABLE `artwork_media` (
    `artwork_media_id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `media_type` varchar(255) NOT NULL,
    `media_url` varchar(512) NOT NULL,
    `created_time` datetime NOT NULL,
    `updated_time` datetime NULL,
    `artwork_id` bigint NOT NULL,
    foreign key (`artwork_id`) REFERENCES `artwork` (`artwork_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;