package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Seller} and its DTO {@link SellerDTO}.
 */
@Mapper(componentModel = "spring")
public interface SellerMapper extends EntityMapper<SellerDTO, Seller> {}
