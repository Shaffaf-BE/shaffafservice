package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {
    @Mapping(target = "seller", source = "seller", qualifiedByName = "sellerId")
    ProjectDTO toDto(Project s);

    @Named("sellerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SellerDTO toDtoSellerId(Seller seller);
}
