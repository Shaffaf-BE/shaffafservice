package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.ComplainStatus;
import com.shaffaf.shaffafservice.service.dto.ComplainStatusDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ComplainStatus} and its DTO {@link ComplainStatusDTO}.
 */
@Mapper(componentModel = "spring")
public interface ComplainStatusMapper extends EntityMapper<ComplainStatusDTO, ComplainStatus> {}
