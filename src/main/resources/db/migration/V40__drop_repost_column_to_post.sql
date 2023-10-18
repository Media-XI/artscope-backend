ALTER TABLE post
    DROP COLUMN parent_post_id,
    DROP COLUMN comments,
    DROP COLUMN mention_username,
    DROP CONSTRAINT fk_post_parent_post_id;