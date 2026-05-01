--liquibase formatted sql

--changeset teachsync:007-ensure-course-photo-url-text
ALTER TABLE courses
    ALTER COLUMN photo_url TYPE TEXT USING photo_url::TEXT;
