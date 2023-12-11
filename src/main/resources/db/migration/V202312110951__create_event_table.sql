create table event
(
    event_id          bigint auto_increment
        primary key,
    title             VARCHAR(255)         not null,
    description       text                 null,
    detail_location   varchar(255)         null,
    price             varchar(255)         null,
    link              varchar(200)         null,
    type              varchar(100)         not null,
    enabled           tinyint(1) default 1 not null,
    created_time      datetime             not null,
    updated_time      datetime             null,
    member_id         binary(16)           not null,
    seq               int                  null,
    start_date        datetime             not null,
    end_date          date                 not null,
    detailed_schedule varchar(255)         null,
    location_id       bigint               null,
    constraint event_ibfk_1
        foreign key (member_id) references member (member_id),
    constraint event_ibfk_2
        foreign key (location_id) REFERENCES location (location_id),
    constraint uk_event_id_start_date_time
        unique (event_id, start_date)
);
