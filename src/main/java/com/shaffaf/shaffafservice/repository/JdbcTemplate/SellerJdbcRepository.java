package com.shaffaf.shaffafservice.repository.JdbcTemplate;

import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.domain.enumeration.Status;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SellerJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public SellerJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean isSellerAssociatedWithProject(Long projectId, String sellerPhoneNumber) {
        String sql =
            """
            SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
            FROM  seller s
            INNER JOIN project p
            ON s.id = p.seller_id
            WHERE p.id = ?
            AND s.phone_number = ?
            AND s.deleted_on IS NULL
            AND s.status = 'ACTIVE'
            """;

        return jdbcTemplate.queryForObject(sql, Boolean.class, projectId, sellerPhoneNumber);
    }

    public Optional<Seller> findByPhoneNumber(String phoneNumber) {
        String sql =
            """
            SELECT s.id, s.first_name, s.last_name, s.email, s.status, s.phone_number FROM seller s
            WHERE s.phone_number = ?
            AND s.deleted_on IS NULL
            AND s.status = 'ACTIVE'
            """;

        return jdbcTemplate
            .query(
                sql,
                (rs, rowNum) -> {
                    Seller seller = new Seller();
                    seller.setId(rs.getLong("id"));
                    seller.setPhoneNumber(rs.getString("phone_number"));
                    seller.setFirstName(rs.getString("first_name"));
                    seller.setLastName(rs.getString("last_name"));
                    seller.setEmail(rs.getString("email"));
                    seller.setStatus(Status.valueOf(rs.getString("status")));
                    return seller;
                },
                phoneNumber
            )
            .stream()
            .findFirst();
    }
}
