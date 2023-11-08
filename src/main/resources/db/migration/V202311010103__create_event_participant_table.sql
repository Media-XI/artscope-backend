CREATE TABLE `event_participant`
(
    `exhibition_participant_id` bigint     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `event_schedule_id`         bigint     NOT NULL,
    `member_id`                 binary(16) NULL,
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
