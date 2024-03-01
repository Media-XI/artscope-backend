CREATE TABLE `magazine_comment`
(
    `magazine_comment_id` bigint AUTO_INCREMENT PRIMARY KEY,
    `comment`             text                               NOT NULL,
    `mention_username`    varchar(50)                        null,
    `likes`               int      default 0                 not null,
    `comments`            int      default 0                 not null,
    `is_deleted`          tinyint                            NOT NULL DEFAULT 0,
    `created_time`        datetime default CURRENT_TIMESTAMP not null,
    `updated_time`        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    `member_id`           binary(16)                         not null,
    `magazine_id`         bigint                             not null,
    `parent_comment_id`   bigint                             null,
    foreign key (`member_id`) references `member` (`member_id`),
    foreign key (`magazine_id`) references `magazine` (`magazine_id`),
    foreign key (`parent_comment_id`) references `magazine_comment` (`magazine_comment_id`)
)