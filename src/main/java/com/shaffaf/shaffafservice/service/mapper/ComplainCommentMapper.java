package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Complain;
import com.shaffaf.shaffafservice.domain.ComplainComment;
import com.shaffaf.shaffafservice.service.dto.ComplainCommentDTO;
import com.shaffaf.shaffafservice.service.dto.ComplainDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ComplainComment} and its DTO {@link ComplainCommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ComplainCommentMapper extends EntityMapper<ComplainCommentDTO, ComplainComment> {
    @Mapping(target = "complain", source = "complain", qualifiedByName = "complainId")
    ComplainCommentDTO toDto(ComplainComment s);

    @Named("complainId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ComplainDTO toDtoComplainId(Complain complain);
}
