ALTER TABLE `follow`
    CHANGE COLUMN `follower_id` `following_id` BINARY(16) NOT NULL ;

ALTER TABLE `follow`
    CHANGE COLUMN `follow_id` `follower_id` BINARY(16) NOT NULL ;
