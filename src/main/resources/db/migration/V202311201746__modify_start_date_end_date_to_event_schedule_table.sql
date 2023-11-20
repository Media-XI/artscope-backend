ALTER TABLE event_schedule CHANGE COLUMN end_time end_date_time DATETIME NULL;
ALTER TABLE event_schedule CHANGE COLUMN start_time start_date_time DATETIME NOT NULL;
