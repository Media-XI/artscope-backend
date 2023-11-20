alter table event_schedule
    drop foreign key event_schedule_ibfk_1; # event_scheudle -> (event_id, location_id)

alter table event_schedule
    drop key uk_event_id_start_time;

alter table event_schedule
    drop column event_date;

ALTER TABLE event_schedule
    ADD FOREIGN KEY event_schedule(event_id) references exhibition(exhibition_id);

ALTER TABLE event_schedule
    ADD unique key uk_event_id_start_date_time(event_id, start_date_time);