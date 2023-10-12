# Add column
ALTER TABLE post
    ADD COLUMN parent_post_id bigint;

ALTER TABLE post
    ADD COLUMN comments int default 0;

# 부모 게시글의 id는 post_id를 참조한다.
ALTER TABLE post
    ADD CONSTRAINT fk_post_parent_post_id FOREIGN KEY (parent_post_id) REFERENCES post (post_id);