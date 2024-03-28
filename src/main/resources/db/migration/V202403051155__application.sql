# group 테이블을 생성합니다
# 작성자 : gimdonghyeon (haroya01@naver.com)
# 작성 날짜  : 2024-03-28
# 현재 버전  : V202403051155 (이전 버전 : V202403051154__application.sql)

CREATE TABLE `group`
(
    group_id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    description            TEXT,
    address                varchar(255),
    profile_image    varchar(512) NOT NULL,
    background_image varchar(512) NOT NULL,
    name             varchar(50) NOT NULL,
    created_time           TIMESTAMP                   NOT NULL,
    updated_time           TIMESTAMP                   NOT NULL,
    is_deleted             BOOL DEFAULT FALSE NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

