START TRANSACTION;

# 매거진 카테고리에 포스트 생성
INSERT INTO magazine_category (name, created_time, updated_time)
VALUES ('post', now(), now());

# 포스트 -> 매거진 이관
INSERT INTO magazine (title, content, views, likes, comments, created_time, updated_time, member_id,
                      category_id)
SELECT CONCAT(SUBSTR(content, 1, 30),'...'),
       content,
       views,
       likes,
       comments,
       post.created_time,
       post.updated_time,
       author_id,
       magazine_category.category_id
FROM post
         LEFT JOIN magazine_category ON magazine_category.name = 'post';

# 포스트 미디어 -> 매거진 미디어 이관
INSERT INTO magazine_media (url, type, created_time, updated_time, magazine_id)
SELECT media_url, media_type, pm.created_time, now(), magazine_id
FROM post_media pm
         INNER JOIN post ON pm.post_id = post.post_id
         INNER JOIN magazine ON post.created_time = magazine.created_time;

# 포스트 댓글 -> 매거진 댓글 이관
INSERT INTO magazine_comment (comment, member_id, magazine_id, created_time, updated_time)
SELECT post_comment.content, post_comment.author_id, magazine_id, post_comment.created_time, post_comment.updated_time FROM post_comment
                                                                                                                                INNER JOIN post ON post_comment.post_id = post.post_id
                                                                                                                                INNER JOIN magazine ON post.created_time = magazine.created_time;

# 포스트 좋아요 -> 매거진 좋아요 이관
INSERT INTO magazine_like (magazine_id, member_id, liked_time)
SELECT magazine_id, post_like_member.member_id, liked_time FROM post_like_member
                                                                    INNER JOIN post ON post_like_member.post_id = post.post_id
                                                                    INNER JOIN magazine ON post.created_time = magazine.created_time;

COMMIT;