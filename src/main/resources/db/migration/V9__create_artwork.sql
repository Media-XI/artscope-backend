DROP TABLE IF EXISTS `artwork`;

CREATE TABLE `artwork` (
    `artwork_id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `title` varchar(255) NOT NULL,
    `description` text NULL,
    `visible` tinyint(1) NOT NULL DEFAULT 0,
    `created_time` datetime NOT NULL,
    `updated_time` datetime NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;