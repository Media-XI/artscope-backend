ALTER TABLE `artwork`
    CHANGE COLUMN `view` `views` INTEGER NOT NULL DEFAULT 0;

ALTER TABLE `artwork`
    CHANGE COLUMN `like` `likes` INTEGER NOT NULL DEFAULT 0;