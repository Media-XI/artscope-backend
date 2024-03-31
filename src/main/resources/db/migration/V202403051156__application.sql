
# 팀 팔로잉 기능 구현을 위한 테이블 변경 및 컬럼 추가
# 작성자 : Hoon9901 (shonn.dev@gmail.com)
# 작성 날짜  : 2024-03-31
# 현재 버전  : V202403051156 (이전 버전 : V202403051155__application.sql)

# 복합키를 제거하기 전 FK 겸 PK로 사용되므로 PK 삭제 전, FK 제거
ALTER TABLE `follow`
    DROP FOREIGN KEY `fk_follow_follow_id`,
    DROP FOREIGN KEY `fk_follow_follower_id`;

# 대리키 생성 및 기존 복합키(PK) 제거
ALTER TABLE `follow`
    ADD `follow_id` BIGINT AUTO_INCREMENT PRIMARY KEY FIRST,
    DROP PRIMARY KEY;

# 기존 팔로잉 컬럼 명을 팔로잉 멤버로 변경 / 새로운 TEAM 팔로잉 컬럼 생성
ALTER TABLE `follow`
    CHANGE `following_id` `following_member_id` BINARY(16) COMMENT 'member -> member follow',
    ADD `following_team_id` BIGINT COMMENT 'member -> team follow' AFTER `following_member_id`;

# 기존 변경된 컬럼으로 FK 생성 및 팀 컬럼 FK 생성 그리고
# 도메인 제약 조건 생성
ALTER TABLE `follow`
    ADD CONSTRAINT `fk_follow_follower_id` FOREIGN KEY (`follower_id`) REFERENCES `member` (`member_id`),
    ADD CONSTRAINT `fk_follow_following_member_id` FOREIGN KEY (`following_member_id`) REFERENCES `member` (`member_id`),
    ADD CONSTRAINT `fk_follow_following_team_id` FOREIGN KEY (`following_team_id`) REFERENCES `team` (`team_id`),
    ADD CONSTRAINT UNIQUE (`follower_id`, `following_member_id`, `following_team_id`);
