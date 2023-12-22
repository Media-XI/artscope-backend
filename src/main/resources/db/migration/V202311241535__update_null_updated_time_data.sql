UPDATE post SET updated_time = created_time WHERE updated_time IS NULL;

UPDATE post_comment SET updated_time = created_time WHERE updated_time IS NULL;

UPDATE agora SET updated_time = created_time WHERE updated_time IS NULL;

UPDATE agora_opinion SET updated_time = created_time WHERE updated_time IS NULL;

UPDATE artwork SET updated_time = created_time WHERE updated_time IS NULL;

UPDATE artwork_comment SET updated_time = created_time WHERE updated_time IS NULL;

UPDATE event_schedule SET updated_time = created_time WHERE updated_time IS NULL;

UPDATE member SET updated_time = created_time WHERE updated_time IS NULL;