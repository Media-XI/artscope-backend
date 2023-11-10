ALTER TABLE `location`
    MODIFY `web_site_url` varchar(255) NULL,
    MODIFY `sns_url` varchar(255) NULL,
    DROP COLUMN `link`;
