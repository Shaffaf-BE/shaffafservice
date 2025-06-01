package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Complain;
import com.shaffaf.shaffafservice.domain.ComplainStatus;
import com.shaffaf.shaffafservice.domain.ComplainType;
import com.shaffaf.shaffafservice.service.dto.ComplainDTO;
import com.shaffaf.shaffafservice.service.dto.ComplainStatusDTO;
import com.shaffaf.shaffafservice.service.dto.ComplainTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Complain} and its DTO {@link ComplainDTO}.
 */
@Mapper(componentModel = "spring")
public interface ComplainMapper extends EntityMapper<ComplainDTO, Complain> {
    @Mapping(target = "complainType", source = "complainType", qualifiedByName = "complainTypeId")
    @Mapping(target = "complainStatus", source = "complainStatus", qualifiedByName = "complainStatusId")
    ComplainDTO toDto(Complain s);

    @Named("complainTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ComplainTypeDTO toDtoComplainTypeId(ComplainType complainType);

    @Named("complainStatusId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ComplainStatusDTO toDtoComplainStatusId(ComplainStatus complainStatus);
}
