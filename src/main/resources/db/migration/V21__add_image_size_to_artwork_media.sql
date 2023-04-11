ALTER TABLE `artwork_media`
    ADD COLUMN `image_width` INT(11) NULL DEFAULT NULL AFTER `media_url`;

ALTER TABLE `artwork_media`
    ADD COLUMN `image_height` INT(11) NULL DEFAULT NULL AFTER `image_width`;