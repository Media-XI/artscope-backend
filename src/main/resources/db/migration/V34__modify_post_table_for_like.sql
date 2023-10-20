ALTER TABLE post
    DROP COLUMN title;

ALTER table post
    change view views int default 0 not null;

ALTER TABLE post
    ADD COLUMN likes INT NOT NULL DEFAULT 0;