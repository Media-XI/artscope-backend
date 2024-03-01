CREATE TABLE `temp_notification_setting` AS
SELECT
    member_id,
    MAX(CASE WHEN setting_type = 'MENTION' THEN is_receive ELSE 0 END) AS receive_mention,
    MAX(CASE WHEN setting_type = 'UPDATE' THEN is_receive ELSE 0 END) AS receive_update,
    MAX(CASE WHEN setting_type = 'ANNOUNCEMENT' THEN is_receive ELSE 0 END) AS receive_announcement,
    MAX(CASE WHEN setting_type = 'NEW_FOLLOWER' THEN is_receive ELSE 0 END) AS receive_new_follower,
    MAX(CASE WHEN setting_type = 'PROMOTIONAL_NEWS' THEN is_receive ELSE 0 END) AS receive_promotional_news
FROM `notification_setting`
GROUP BY member_id;

-- 기존 테이블 삭제
DROP TABLE `notification_setting`;

-- 임시 테이블 이름 변경
RENAME TABLE temp_notification_setting TO notification_setting;

ALTER TABLE `notification_setting`
    ADD CONSTRAINT `member_id_fk`
        FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`);
