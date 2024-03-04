START TRANSACTION;

# 매거진 카테고리에 아트워크 생성
INSERT INTO magazine_category (name, created_time, updated_time)
VALUES ('artwork', now(), now());

# 아트워크 -> 매거진 이관
INSERT INTO magazine (title, content, views, likes, comments, visibled, created_time, updated_time, member_id,
                      category_id)
SELECT title,
       description,
       views,
       likes,
       comments,
       visible,
       artwork.created_time,
       artwork.updated_time,
       member_id,
       magazine_category.category_id
FROM artwork
         LEFT JOIN magazine_category ON magazine_category.name = 'artwork';

# 아트워크 미디어 -> 매거진 미디어 이관
INSERT INTO magazine_media (url, type, created_time, updated_time, magazine_id)
SELECT media_url, media_type, am.created_time, now(), magazine_id
FROM artwork_media am
         INNER JOIN artwork ON am.artwork_id = artwork.artwork_id
         INNER JOIN magazine ON artwork.created_time = magazine.created_time;

# 아트워크 댓글 -> 매거진 댓글 이관
INSERT INTO magazine_comment (comment, member_id, magazine_id, created_time, updated_time)
SELECT artwork_comment.content, author_id, magazine_id, artwork_comment.created_time, artwork_comment.updated_time
FROM artwork_comment
         INNER JOIN artwork ON artwork_comment.artwork_id = artwork.artwork_id
         INNER JOIN magazine ON artwork.created_time = magazine.created_time;

# 아트워크 좋아요 -> 매거진 좋아요 이관
INSERT INTO magazine_like (magazine_id, member_id, liked_time)
SELECT magazine_id, artwork_like_member.member_id, liked_time
FROM artwork_like_member
         INNER JOIN artwork ON artwork_like_member.artwork_id = artwork.artwork_id
         INNER JOIN magazine ON artwork.created_time = magazine.created_time;

COMMIT;