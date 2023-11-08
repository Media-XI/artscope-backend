CREATE TABLE post_like_member
(
    post_id    bigint     NOT NULL,
    member_id  binary(16) NOT NULL,
    liked_time DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, member_id),
    CONSTRAINT fk_post_like_member_post_id FOREIGN KEY (post_id) REFERENCES post (post_id),
    CONSTRAINT fk_post_like_member_member_id FOREIGN KEY (member_id) REFERENCES member (member_id)
);