package com.teachsync.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserSchemaSynchronizer {

    private final JdbcTemplate jdbcTemplate;

    public UserSchemaSynchronizer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void syncProfilePictureColumn() {
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN profile_picture TYPE TEXT");
    }
}
