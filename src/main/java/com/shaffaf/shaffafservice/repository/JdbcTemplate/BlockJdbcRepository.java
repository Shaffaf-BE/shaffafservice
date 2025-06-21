package com.shaffaf.shaffafservice.repository.JdbcTemplate;

import com.shaffaf.shaffafservice.domain.Block;
import com.shaffaf.shaffafservice.domain.Project;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BlockJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public BlockJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long saveBlock(String blockName, Long projectId, String username) {
        String sql =
            "INSERT INTO block(name, project_id, created_by, created_date, last_modified_by, last_modified_date) " +
            "VALUES (?, ?, ?, now(), ?, now()) RETURNING id";
        return jdbcTemplate.queryForObject(sql, Long.class, blockName, projectId, username, username);
    }

    public Optional<Block> findById(Long id) {
        String sql = "SELECT * FROM block WHERE id = ? AND deleted_on IS NULL";

        return jdbcTemplate
            .query(
                sql,
                (rs, rowNum) -> {
                    Block block = new Block();
                    block.setId(rs.getLong("id"));
                    block.setName(rs.getString("name"));

                    Project project = new Project();
                    project.setId(rs.getLong("project_id"));

                    block.setProject(project);
                    return block;
                },
                id
            )
            .stream()
            .findFirst();
    }

    public Page<Block> findAllByProjectId(Long projectId, Pageable pageable) {
        // First, get total count for pagination metadata
        String countSql = "SELECT COUNT(*) FROM block WHERE project_id = ? AND deleted_on IS NULL";
        Integer totalItems = jdbcTemplate.queryForObject(countSql, Integer.class, projectId);

        // Build the SQL query with sorting
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM block WHERE project_id = ? AND deleted_on IS NULL");

        // Handle sorting
        if (pageable.getSort().isSorted()) {
            List<String> sortClauses = new ArrayList<>();

            for (Sort.Order order : pageable.getSort()) {
                // Prevent SQL injection by validating column names
                String column = validateAndGetColumn(order.getProperty());
                if (column != null) {
                    sortClauses.add(column + " " + order.getDirection().name());
                }
            }

            if (!sortClauses.isEmpty()) {
                sqlBuilder.append(" ORDER BY ").append(String.join(", ", sortClauses));
            }
        } else {
            // Default sort by id if no sort is specified
            sqlBuilder.append(" ORDER BY id ASC");
        }

        // Add pagination
        sqlBuilder.append(" LIMIT ? OFFSET ?");

        List<Block> blocks = jdbcTemplate.query(
            sqlBuilder.toString(),
            (rs, rowNum) -> {
                Block block = new Block();
                block.setId(rs.getLong("id"));
                block.setName(rs.getString("name"));

                Project project = new Project();
                project.setId(rs.getLong("project_id"));

                block.setProject(project);
                return block;
            },
            projectId,
            pageable.getPageSize(),
            pageable.getOffset()
        );

        return new PageImpl<>(blocks, pageable, totalItems != null ? totalItems : 0);
    }

    /**
     * Validates the column name to prevent SQL injection
     * @param property the property from Sort.Order
     * @return safe column name or null if invalid
     */
    private String validateAndGetColumn(String property) {
        // Whitelist approach - only allow specific column names
        switch (property.toLowerCase()) {
            case "id":
                return "id";
            case "name":
                return "name";
            case "created_on":
                return "created_on";
            case "updated_on":
                return "updated_on";
            default:
                return null;
        }
    }
}
