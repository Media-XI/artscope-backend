
# 카테고리 이름과 부모에 유니크 부여
# 작성자 : gimdonghyeon (haroya01@naver.com)
# 작성 날짜  : 2024-03-23
# 현재 버전  : V202403051154 (이전 버전 : V202403051153__application.sql)

ALTER TABLE magazine_category ADD CONSTRAINT unique_name_parent_id UNIQUE (name, parent_id);

