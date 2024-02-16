CREATE TABLE `notification_setting`
(
    `member_id` binary(16) NOT NULL,
    `setting_type` varchar(50) NOT NULL,
    `is_receive` boolean NOT NULL,
    PRIMARY KEY (`member_id`, `setting_type`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
