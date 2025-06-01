package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.ExpenseType;
import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.service.dto.ExpenseTypeDTO;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExpenseType} and its DTO {@link ExpenseTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExpenseTypeMapper extends EntityMapper<ExpenseTypeDTO, ExpenseType> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    ExpenseTypeDTO toDto(ExpenseType s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
