package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.UnitType;
import com.shaffaf.shaffafservice.service.dto.UnitTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UnitType} and its DTO {@link UnitTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface UnitTypeMapper extends EntityMapper<UnitTypeDTO, UnitType> {}
