CREATE TABLE event_schedule (
    event_schedule_id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    event_date date NOT NULL,
    start_time datetime NOT NULL,
    end_time datetime NOT NULL,
    location_id bigint NOT NULL,
    detail_location varchar(255) NULL,
    event_id bigint NOT NULL,
    created_time datetime DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time datetime NULL,
    foreign key (event_id) REFERENCES exhibition (exhibition_id),
    foreign key (location_id) REFERENCES location (location_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;