
# Artwork 댓글 수 컬럼 추가
ALTER TABLE artwork
    ADD COLUMN comments INT NOT NULL DEFAULT 0;