ALTER TABLE event_participant
    ADD FOREIGN KEY (event_schedule_id) REFERENCES event_schedule (event_schedule_id);