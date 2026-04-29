package com.teachsync.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CourseSchemaSynchronizer {

    private final JdbcTemplate jdbcTemplate;

    public CourseSchemaSynchronizer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void syncCoursePhotoColumn() {
        jdbcTemplate.execute("ALTER TABLE courses ALTER COLUMN photo_url TYPE TEXT");
    }
}
