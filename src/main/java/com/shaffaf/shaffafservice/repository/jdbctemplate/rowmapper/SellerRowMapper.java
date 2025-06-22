package com.shaffaf.shaffafservice.repository.jdbctemplate.rowmapper;

import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.domain.enumeration.Status;
import org.springframework.jdbc.core.RowMapper;

public class SellerRowMapper implements RowMapper<Seller> {

    @Override
    public Seller mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Seller seller = new Seller();

        seller.setId(rs.getLong("id"));
        seller.setPhoneNumber(rs.getString("phone_number"));
        seller.setFirstName(rs.getString("first_name"));
        seller.setLastName(rs.getString("last_name"));
        seller.setEmail(rs.getString("email"));
        seller.setStatus(Status.valueOf(rs.getString("status")));

        return seller;
    }
}
