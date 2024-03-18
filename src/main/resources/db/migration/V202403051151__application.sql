
# 매거진 콘텐츠 맨 앞에 마크다운 형식의 미디어 URL 을 첨부합니다.
# 작성자 : Hoon9901 (shonn.dev@gmail.com)
# 작성 날짜  : 2024-03-18
# 현재 버전  : V202403051151 (이전 버전 : V202403051150__create_curation_table.sql)

DELIMITER $$
CREATE PROCEDURE add_first_media_to_content()
BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE p_magazine_id INT;
    DECLARE cur CURSOR FOR
        SELECT m.magazine_id
        FROM magazine m
                 INNER JOIN magazine_media mm ON (m.magazine_id = mm.magazine_id);
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; # NOT FOUND -> done true
    START TRANSACTION;
    OPEN cur;
    l:LOOP
        FETCH cur INTO p_magazine_id;
        IF done THEN
            LEAVE l;
        END IF;
        UPDATE magazine m
        SET content = CONCAT((SELECT CONCAT('![](https://cdn.artscope.kr/', mm.url, ')')
                              FROM magazine_media mm
                              WHERE mm.magazine_id = p_magazine_id
                              LIMIT 1), ' ', content)
        WHERE m.magazine_id = p_magazine_id;
    END LOOP;
    CLOSE cur;
    COMMIT;
END$$
DELIMITER ;

CALL add_first_media_to_content();
DROP PROCEDURE add_first_media_to_content;