package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.ResidentType;
import com.shaffaf.shaffafservice.service.dto.ResidentTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ResidentType} and its DTO {@link ResidentTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface ResidentTypeMapper extends EntityMapper<ResidentTypeDTO, ResidentType> {}
