#
# 카테고리를 계층화 합니다
# 작성자 : gimdonghyeon (haroya01@naver.com)
# 작성 날짜  : 2024-03-22
# 현재 버전  : V202403051153 (이전 버전 : V202403051152__application.sql)

ALTER TABLE `magazine_category`
    ADD `parent_id` BIGINT NULL,
    ADD `slug` VARCHAR(30) NULL UNIQUE,
    ADD CONSTRAINT `fk_magazine_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `magazine_category` (`category_id`);


