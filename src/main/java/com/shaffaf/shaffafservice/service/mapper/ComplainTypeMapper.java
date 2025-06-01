package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.ComplainType;
import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.service.dto.ComplainTypeDTO;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ComplainType} and its DTO {@link ComplainTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface ComplainTypeMapper extends EntityMapper<ComplainTypeDTO, ComplainType> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    ComplainTypeDTO toDto(ComplainType s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
