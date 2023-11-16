CREATE TABLE post_media
(
    post_media_id bigint       NOT NULL AUTO_INCREMENT,
    media_type    varchar(100) NOT NULL,
    media_url     varchar(255) NOT NULL,
    media_width   int          NULL,
    media_height  int          NULL,
    created_time  datetime     NOT NULL,
    post_id       bigint       NOT NULL,
    PRIMARY KEY (post_media_id),
    CONSTRAINT fk_post_media_post_id FOREIGN KEY (post_id) REFERENCES post (post_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;