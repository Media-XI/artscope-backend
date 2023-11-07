CREATE TABLE agora
(
    agora_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    title             VARCHAR(255) NOT NULL,
    content           TEXT         NOT NULL,
    agree_text        VARCHAR(255) NOT NULL,
    disagree_text     VARCHAR(255) NOT NULL,
    agree_count       INT          NOT NULL DEFAULT 0,
    disagree_count    INT          NOT NULL DEFAULT 0,
    participant_count INT          NOT NULL DEFAULT 0,
    is_anonymous      TINYINT(1)   NOT NULL DEFAULT 0,
    is_deleted        TINYINT(1)   NOT NULL DEFAULT 0,
    created_time      DATETIME     NOT NULl,
    updated_time      DATETIME     NULL,
    author_id         BINARY(16)   NOT NULL,
    FOREIGN KEY (author_id) REFERENCES member (member_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;