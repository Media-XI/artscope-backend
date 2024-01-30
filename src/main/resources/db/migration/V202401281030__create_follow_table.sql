CREATE TABLE `follow`
(
    follow_id BINARY(16),
    follower_id BINARY(16),
    follow_time DATETIME NOT NULL,
    PRIMARY KEY (follow_id, follower_id),
    INDEX idx_follow_id (follow_id),
    INDEX idx_follower_id (follower_id),
    constraint fk_follow_follow_id
        foreign key (follow_id) references member (member_id),
    constraint fk_follow_follower_id
        foreign key (follower_id) references member(member_id)
) ENGINE =InnoDB
  DEFAULT CHARSET = utf8mb4;
