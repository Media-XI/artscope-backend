ALTER TABLE `notification`
    DROP FOREIGN KEY notification_ibfk_1,
    DROP COLUMN member_id,
    CHANGE COLUMN `message` `message` TEXT NOT NULL;


rename table notification_read_status to notification_received_status;

ALTER TABLE `notification_received_status`
    ADD COLUMN `member_id` binary (16) NOT NULL,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`notification_id`, `member_id`),
    add constraint notification_received_status_member_member_id_fk
        foreign key (member_id) references member (member_id);
