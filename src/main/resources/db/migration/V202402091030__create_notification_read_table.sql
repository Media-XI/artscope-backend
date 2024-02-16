CREATE TABLE `notification_read_status`
(
    `notification_id` bigint NOT NULL,
    `is_read`         boolean NOT NULL DEFAULT FALSE,
    PRIMARY KEY (`notification_id`),
    FOREIGN KEY (`notification_id`) REFERENCES `notification` (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
