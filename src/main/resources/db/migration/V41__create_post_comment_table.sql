CREATE TABLE post_comment (
                              post_comment_id bigint auto_increment
                                  primary key,
                              content text not null,
                              mention_username varchar(50) null,
                              author_id binary(16) not null,
                              post_id bigint not null,
                              parent_id bigint null,
                              likes int default 0 not null,
                              comments int default 0 not null,
                              created_time datetime default CURRENT_TIMESTAMP not null,
                              updated_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
                              constraint fk_author_id
                                  foreign key (author_id) references member(member_id),
                              constraint fk_post_id
                                  foreign key (post_id) references post(post_id),
                              constraint fk_parent_id
                                  foreign key (parent_id) references post_comment(post_comment_id)
);