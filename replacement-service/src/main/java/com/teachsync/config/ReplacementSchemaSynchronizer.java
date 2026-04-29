package com.teachsync.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ReplacementSchemaSynchronizer {

    @Bean
    ApplicationRunner synchronizeReplacementConstraints(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("""
                    update replacement_responses
                    set response_status = case
                        when response_status = 'WAITING' then 'PENDING'
                        when response_status = 'APPROVED' then 'ACCEPTED'
                        when response_status = 'REJECTED' then 'DECLINED'
                        else response_status
                    end
                    """);

            jdbcTemplate.execute("""
                    alter table replacement_responses
                    drop constraint if exists replacement_responses_response_status_check
                    """);

            jdbcTemplate.execute("""
                    alter table replacement_responses
                    add constraint replacement_responses_response_status_check
                    check (response_status in ('PENDING', 'ACCEPTED', 'DECLINED'))
                    """);

            jdbcTemplate.execute("""
                    update replacements
                    set status = case
                        when status = 'WAITING' then 'PENDING'
                        when status = 'COMPLETED' then 'APPROVED'
                        when status = 'REJECTED' then 'DECLINED'
                        when status = 'CLOSED' then 'AUTO_CLOSED'
                        else status
                    end
                    """);

            jdbcTemplate.execute("""
                    alter table replacements
                    drop constraint if exists replacements_status_check
                    """);

            jdbcTemplate.execute("""
                    alter table replacements
                    add constraint replacements_status_check
                    check (status in ('APPROVED', 'PENDING', 'DECLINED', 'EXPIRED', 'CANCELLED', 'AUTO_CLOSED'))
                    """);
        };
    }
}
