# 매거진 테이블에 teami� coulum 추가
# 작성자 : gimdonghyeon (haroya01@naver.com)
# 작성 날짜  : 2024-04-04
# 현재 버전  : V202403051158 (이전 버전 : V202403051157__application.sql)

ALTER TABLE `magazine`
    ADD `team_id` BIGINT NULL,
    ADD CONSTRAINT `fk_magazine_team_id` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`);
