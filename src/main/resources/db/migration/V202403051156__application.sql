# group_user 테이블을 생성
# 작성자 : gimdonghyeon (haroya01@naver.com)
# 작성 날짜  : 2024-03-28
# 현재 버전  : V202403051156 (이전 버전 : V202403051155__application.sql)


CREATE TABLE `team_user`
(
    `team_user_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `member_id`    BINARY(16)   NOT NULL,
    `team_id`      BIGINT       NOT NULL,
    `position`     varchar(100) NOT NULL,
    `user_role`    VARCHAR(10),
    `created_time` DATETIME     NOT NULL,
    `updated_time` DATETIME     NOT NULL,
    UNIQUE (`member_id`, `team_id`),
    CONSTRAINT `fk_team_user_member_id` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
    CONSTRAINT `fk_team_user_team_id` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;