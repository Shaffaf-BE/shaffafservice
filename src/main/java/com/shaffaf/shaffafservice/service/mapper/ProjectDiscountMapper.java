package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.domain.ProjectDiscount;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import com.shaffaf.shaffafservice.service.dto.ProjectDiscountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectDiscount} and its DTO {@link ProjectDiscountDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectDiscountMapper extends EntityMapper<ProjectDiscountDTO, ProjectDiscount> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    ProjectDiscountDTO toDto(ProjectDiscount s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
