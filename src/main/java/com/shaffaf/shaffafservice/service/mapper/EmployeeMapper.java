package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Employee;
import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.service.dto.EmployeeDTO;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Employee} and its DTO {@link EmployeeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    EmployeeDTO toDto(Employee s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
