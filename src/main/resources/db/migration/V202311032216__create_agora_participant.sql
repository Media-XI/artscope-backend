CREATE TABLE agora_participant
(
    agora_id       BIGINT       NOT NULL,
    member_id      BINARY(16)   NOT NULL,
    vote           VARCHAR(255) NULL,
    agora_sequence INT          NOT NULL,
    is_deleted     TINYINT(1)   NOT NULL DEFAULT 0,
    created_time   DATETIME     NOT NULL,
    updated_time   DATETIME     NULL,
    PRIMARY KEY (agora_id, member_id),
    FOREIGN KEY (agora_id) REFERENCES agora (agora_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;