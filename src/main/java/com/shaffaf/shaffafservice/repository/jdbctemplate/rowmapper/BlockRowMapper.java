package com.shaffaf.shaffafservice.repository.jdbctemplate.rowmapper;

import com.shaffaf.shaffafservice.domain.Block;
import com.shaffaf.shaffafservice.domain.Project;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;

public class BlockRowMapper implements RowMapper<Block> {

    @Override
    public Block mapRow(ResultSet rs, int rowNum) throws SQLException {
        Block block = new Block();

        block.setId(rs.getLong("id"));
        block.setName(rs.getString("name"));
        block.setCreatedBy(rs.getString("created_by"));
        block.setLastModifiedBy(rs.getString("last_modified_by"));

        Timestamp createdOn = rs.getTimestamp("created_date");
        Timestamp lastModifiedOn = rs.getTimestamp("last_modified_date");

        block.setCreatedDate(createdOn != null ? createdOn.toInstant() : null);
        block.setLastModifiedDate(lastModifiedOn != null ? lastModifiedOn.toInstant() : null);

        Project project = new Project();
        project.setId(rs.getLong("project_id"));

        block.setProject(project);
        return block;
    }
}
