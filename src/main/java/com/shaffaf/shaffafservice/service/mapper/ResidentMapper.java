package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Resident;
import com.shaffaf.shaffafservice.domain.ResidentType;
import com.shaffaf.shaffafservice.domain.Unit;
import com.shaffaf.shaffafservice.service.dto.ResidentDTO;
import com.shaffaf.shaffafservice.service.dto.ResidentTypeDTO;
import com.shaffaf.shaffafservice.service.dto.UnitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Resident} and its DTO {@link ResidentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ResidentMapper extends EntityMapper<ResidentDTO, Resident> {
    @Mapping(target = "unit", source = "unit", qualifiedByName = "unitId")
    @Mapping(target = "residentType", source = "residentType", qualifiedByName = "residentTypeId")
    ResidentDTO toDto(Resident s);

    @Named("unitId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UnitDTO toDtoUnitId(Unit unit);

    @Named("residentTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ResidentTypeDTO toDtoResidentTypeId(ResidentType residentType);
}
