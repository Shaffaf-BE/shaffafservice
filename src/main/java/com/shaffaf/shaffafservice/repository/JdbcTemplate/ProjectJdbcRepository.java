package com.shaffaf.shaffafservice.repository.JdbcTemplate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean isSellerAssociatedWithProject(Long projectId, Long sellerId) {
        String sql =
            """
                SELECT CASE WHEN count(*) > 0 THEN TRUE ELSE FALSE END AS EXISTS
                FROM project p
                WHERE p.id = ?
                AND seller_id = ?
            """;

        return jdbcTemplate.queryForObject(sql, Boolean.class, projectId, sellerId);
    }
}
