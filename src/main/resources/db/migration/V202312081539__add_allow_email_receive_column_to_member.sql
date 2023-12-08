ALTER TABLE member
    ADD COLUMN allow_email_receive BOOLEAN NOT NULL DEFAULT FALSE
    ADD COLUMN allow_email_receive_datetime TIMESTAMP;