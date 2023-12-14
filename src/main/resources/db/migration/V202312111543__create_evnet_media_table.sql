CREATE TABLE event_media
(
    event_media_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    media_type     VARCHAR(255) NOT NULL,
    media_url      VARCHAR(512) NOT NULL,
    created_time   DATETIME     NOT NULL,
    updated_time   DATETIME     NULL,
    event_id       BIGINT       NOT NULL,
    CONSTRAINT event_media_event_id_fk
        FOREIGN KEY (event_id) REFERENCES event (event_id)
);
