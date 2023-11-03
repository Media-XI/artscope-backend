CREATE TABLE artwork_comment
(
    artwork_comment_id bigint AUTO_INCREMENT PRIMARY KEY,
    content            text       NOT NULL,
    author_id          binary(16) NOT NULL, # 작성자
    artwork_id         bigint     NOT NULL, # 댓글 단 작품
    parent_id          bigint,              # 부모 댓글
    created_time       DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time       DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_author_id FOREIGN KEY (author_id) REFERENCES member (member_id),
    CONSTRAINT fk_comment_parent_id FOREIGN KEY (parent_id) REFERENCES artwork_comment (artwork_comment_id),
    CONSTRAINT fk_comment_artwork_id FOREIGN KEY (artwork_id) REFERENCES artwork (artwork_id)
);