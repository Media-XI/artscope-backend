CREATE TABLE `location`
(
    `location_id`  bigint       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `latitude`     double       NOT NULL,
    `longitude`    double       NOT NULL,
    `address`      varchar(255) NOT NULL,
    `name`         varchar(255) NOT NULL,
    `english_name` varchar(255) NULL,
    `link`         varchar(255) NOT NULL,
    `phone_number` varchar(255) NOT NULL,
    `web_site_url` varchar(255) NOT NULL,
    `sns_url`      varchar(255) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;