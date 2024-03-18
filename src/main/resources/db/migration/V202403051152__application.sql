# 매거진 미디어 전체를 매거진 콘텐츠에 추가합니다.
# 작성자 : Hoon9901 (shonn.dev@gmail.com)
# 작성 날짜  : 2024-03-18
# 현재 버전  : V202403051152 (이전 버전 : V202403051151__application.sql)

DELIMITER $$
CREATE PROCEDURE add_all_media_to_content()
BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE p_magazine_id INT;
    DECLARE p_url VARCHAR(512);
    DECLARE cur CURSOR FOR
        SELECT m.magazine_id, mm.url
        FROM magazine m
                 INNER JOIN magazine_media mm ON (m.magazine_id = mm.magazine_id);
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; # NOT FOUND -> done true
    START TRANSACTION;
    OPEN cur;
    l:
    LOOP
        FETCH cur INTO p_magazine_id, p_url;
        IF done THEN
            LEAVE l;
        END IF;
        UPDATE magazine m
        SET content = CONCAT('![](https://cdn.artscope.kr/', p_url, ')', ' ', content)
        WHERE m.magazine_id = p_magazine_id;
    END LOOP;
    CLOSE cur;
    COMMIT;
END$$
DELIMITER ;

CALL add_all_media_to_content();
DROP PROCEDURE add_all_media_to_content;