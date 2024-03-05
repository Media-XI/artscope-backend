CREATE TABLE `curation`
(
    id           BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    magazine_id  BIGINT   NULL,
    created_time DATETIME NOT NULL,
    updated_time DATETIME NOT NULL,
    is_deleted   tinyint  NOT NULL DEFAULT 0,
    FOREIGN KEY (`magazine_id`) REFERENCES `magazine` (`magazine_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
