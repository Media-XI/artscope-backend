ALTER TABLE exhibition_media
    ADD CONSTRAINT exhibition_media_exhibition_id_fk foreign key (exhibition_id) REFERENCES exhibition (exhibition_id);