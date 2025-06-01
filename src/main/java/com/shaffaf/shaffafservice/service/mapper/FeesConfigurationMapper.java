package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Block;
import com.shaffaf.shaffafservice.domain.FeesConfiguration;
import com.shaffaf.shaffafservice.domain.UnitType;
import com.shaffaf.shaffafservice.service.dto.BlockDTO;
import com.shaffaf.shaffafservice.service.dto.FeesConfigurationDTO;
import com.shaffaf.shaffafservice.service.dto.UnitTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FeesConfiguration} and its DTO {@link FeesConfigurationDTO}.
 */
@Mapper(componentModel = "spring")
public interface FeesConfigurationMapper extends EntityMapper<FeesConfigurationDTO, FeesConfiguration> {
    @Mapping(target = "unitType", source = "unitType", qualifiedByName = "unitTypeId")
    @Mapping(target = "block", source = "block", qualifiedByName = "blockId")
    FeesConfigurationDTO toDto(FeesConfiguration s);

    @Named("unitTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UnitTypeDTO toDtoUnitTypeId(UnitType unitType);

    @Named("blockId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BlockDTO toDtoBlockId(Block block);
}
