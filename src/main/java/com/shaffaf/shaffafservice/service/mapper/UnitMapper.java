package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Block;
import com.shaffaf.shaffafservice.domain.FeesCollection;
import com.shaffaf.shaffafservice.domain.Unit;
import com.shaffaf.shaffafservice.domain.UnitType;
import com.shaffaf.shaffafservice.service.dto.BlockDTO;
import com.shaffaf.shaffafservice.service.dto.FeesCollectionDTO;
import com.shaffaf.shaffafservice.service.dto.UnitDTO;
import com.shaffaf.shaffafservice.service.dto.UnitTypeDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Unit} and its DTO {@link UnitDTO}.
 */
@Mapper(componentModel = "spring")
public interface UnitMapper extends EntityMapper<UnitDTO, Unit> {
    @Mapping(target = "feesCollections", source = "feesCollections", qualifiedByName = "feesCollectionIdSet")
    @Mapping(target = "unitType", source = "unitType", qualifiedByName = "unitTypeId")
    @Mapping(target = "block", source = "block", qualifiedByName = "blockId")
    UnitDTO toDto(Unit s);

    @Mapping(target = "removeFeesCollections", ignore = true)
    Unit toEntity(UnitDTO unitDTO);

    @Named("feesCollectionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FeesCollectionDTO toDtoFeesCollectionId(FeesCollection feesCollection);

    @Named("feesCollectionIdSet")
    default Set<FeesCollectionDTO> toDtoFeesCollectionIdSet(Set<FeesCollection> feesCollection) {
        return feesCollection.stream().map(this::toDtoFeesCollectionId).collect(Collectors.toSet());
    }

    @Named("unitTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UnitTypeDTO toDtoUnitTypeId(UnitType unitType);

    @Named("blockId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BlockDTO toDtoBlockId(Block block);
}
