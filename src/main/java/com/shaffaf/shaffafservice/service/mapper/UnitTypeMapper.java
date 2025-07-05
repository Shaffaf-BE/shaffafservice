package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.domain.UnitType;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import com.shaffaf.shaffafservice.service.dto.UnitTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UnitType} and its DTO {@link UnitTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface UnitTypeMapper extends EntityMapper<UnitTypeDTO, UnitType> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    UnitTypeDTO toDto(UnitType s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
