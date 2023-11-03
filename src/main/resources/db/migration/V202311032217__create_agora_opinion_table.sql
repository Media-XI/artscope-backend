CREATE TABLE agora_opinion
(
    agora_opinion_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content          TEXT       NOT NULL,
    is_deleted       TINYINT(1) NOT NULL DEFAULT 0,
    created_time     DATETIME   NOT NULL,
    updated_time     DATETIME   NULL,
    agora_id         BIGINT     NOT NULL,
    author_id        BINARY(16) NOT NULL,
    FOREIGN KEY (agora_id) REFERENCES agora (agora_id),
    FOREIGN KEY (agora_id, author_id) REFERENCES agora_participant (agora_id, participant_id)
)