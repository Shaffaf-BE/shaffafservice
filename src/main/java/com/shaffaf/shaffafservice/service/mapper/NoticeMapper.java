package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Notice;
import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.service.dto.NoticeDTO;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notice} and its DTO {@link NoticeDTO}.
 */
@Mapper(componentModel = "spring")
public interface NoticeMapper extends EntityMapper<NoticeDTO, Notice> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    NoticeDTO toDto(Notice s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
