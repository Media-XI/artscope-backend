alter table member
    change artist_status role_status varchar(255) default 'NONE' not null;

