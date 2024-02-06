ALTER TABLE `follow`
    CHANGE COLUMN `follower_id` `following_id` BINARY(16) NOT NULL ,
    CHANGE COLUMN `follow_id` `follower_id` BINARY(16) NOT NULL ;