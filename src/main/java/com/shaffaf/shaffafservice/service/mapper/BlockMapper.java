package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Block;
import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.service.dto.BlockDTO;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Block} and its DTO {@link BlockDTO}.
 */
@Mapper(componentModel = "spring")
public interface BlockMapper extends EntityMapper<BlockDTO, Block> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    BlockDTO toDto(Block s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
