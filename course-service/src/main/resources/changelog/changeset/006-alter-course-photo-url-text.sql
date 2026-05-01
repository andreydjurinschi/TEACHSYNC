--liquibase formatted sql

--changeset teachsync:006-alter-course-photo-url-text
ALTER TABLE courses
    ALTER COLUMN photo_url TYPE TEXT;
