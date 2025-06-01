package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.domain.UnionMember;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import com.shaffaf.shaffafservice.service.dto.UnionMemberDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UnionMember} and its DTO {@link UnionMemberDTO}.
 */
@Mapper(componentModel = "spring")
public interface UnionMemberMapper extends EntityMapper<UnionMemberDTO, UnionMember> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    UnionMemberDTO toDto(UnionMember s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
