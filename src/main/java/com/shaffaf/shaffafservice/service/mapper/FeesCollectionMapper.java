package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.FeesCollection;
import com.shaffaf.shaffafservice.domain.FeesConfiguration;
import com.shaffaf.shaffafservice.domain.Unit;
import com.shaffaf.shaffafservice.service.dto.FeesCollectionDTO;
import com.shaffaf.shaffafservice.service.dto.FeesConfigurationDTO;
import com.shaffaf.shaffafservice.service.dto.UnitDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FeesCollection} and its DTO {@link FeesCollectionDTO}.
 */
@Mapper(componentModel = "spring")
public interface FeesCollectionMapper extends EntityMapper<FeesCollectionDTO, FeesCollection> {
    @Mapping(target = "units", source = "units", qualifiedByName = "unitIdSet")
    @Mapping(target = "feesConfiguration", source = "feesConfiguration", qualifiedByName = "feesConfigurationId")
    FeesCollectionDTO toDto(FeesCollection s);

    @Mapping(target = "units", ignore = true)
    @Mapping(target = "removeUnits", ignore = true)
    FeesCollection toEntity(FeesCollectionDTO feesCollectionDTO);

    @Named("unitId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UnitDTO toDtoUnitId(Unit unit);

    @Named("unitIdSet")
    default Set<UnitDTO> toDtoUnitIdSet(Set<Unit> unit) {
        return unit.stream().map(this::toDtoUnitId).collect(Collectors.toSet());
    }

    @Named("feesConfigurationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FeesConfigurationDTO toDtoFeesConfigurationId(FeesConfiguration feesConfiguration);
}
