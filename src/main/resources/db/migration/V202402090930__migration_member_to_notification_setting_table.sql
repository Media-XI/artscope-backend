INSERT INTO `notification_setting` (`member_id`, `setting_type`, `is_receive`)
SELECT `member_id`,
       'MENTION',
       TRUE
FROM `member`
UNION
SELECT `member_id`,
       'UPDATE',
       TRUE
FROM `member`
UNION
SELECT `member_id`,
       'ANNOUNCEMENT',
       TRUE
FROM `member`
UNION
SELECT `member_id`,
       'NEW_FOLLOWER',
       TRUE
FROM `member`
UNION
SELECT `member_id`,
       'PROMOTIONAL_NEWS',
       TRUE
FROM `member`;
