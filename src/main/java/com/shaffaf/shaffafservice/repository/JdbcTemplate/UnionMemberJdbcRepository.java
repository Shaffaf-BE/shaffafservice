package com.shaffaf.shaffafservice.repository.JdbcTemplate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UnionMemberJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public UnionMemberJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean isUnionMemberAssociatedWithProject(Long projectId, String unionMemberPhoneNumber) {
        String sql =
            """
                SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
                FROM union_member um
                WHERE um.phone_number = ? AND um.project_id = ?
            """;

        return jdbcTemplate.queryForObject(sql, Boolean.class, unionMemberPhoneNumber, projectId);
    }
}
