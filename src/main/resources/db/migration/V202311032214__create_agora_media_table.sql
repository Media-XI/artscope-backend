CREATE TABLE agora_media
(
    agora_media_id bigint       NOT NULL AUTO_INCREMENT,
    media_type     varchar(100) NOT NULL,
    media_url      varchar(255) NOT NULL,
    media_width    int          NULL,
    media_height   int          NULL,
    created_time   datetime     NOT NULL,
    agora_id       bigint       NOT NULL,
    PRIMARY KEY (agora_media_id),
    CONSTRAINT fk_agora_id_agora_id FOREIGN KEY (agora_id) REFERENCES agora (agora_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;