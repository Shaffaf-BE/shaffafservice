package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.domain.SellerCommission;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import com.shaffaf.shaffafservice.service.dto.SellerCommissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SellerCommission} and its DTO {@link SellerCommissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface SellerCommissionMapper extends EntityMapper<SellerCommissionDTO, SellerCommission> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    SellerCommissionDTO toDto(SellerCommission s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
