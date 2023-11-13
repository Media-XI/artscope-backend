ALTER TABLE event_schedule
    MODIFY COLUMN start_time TIME NOT NULL,
    MODIFY COLUMN end_time TIME NULL;

ALTER TABLE event_schedule
    ADD CONSTRAINT uk_event_id_start_time unique key (event_id, event_date);
