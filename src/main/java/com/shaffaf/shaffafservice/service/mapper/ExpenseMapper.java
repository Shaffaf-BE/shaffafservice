package com.shaffaf.shaffafservice.service.mapper;

import com.shaffaf.shaffafservice.domain.Expense;
import com.shaffaf.shaffafservice.domain.ExpenseType;
import com.shaffaf.shaffafservice.service.dto.ExpenseDTO;
import com.shaffaf.shaffafservice.service.dto.ExpenseTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Expense} and its DTO {@link ExpenseDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExpenseMapper extends EntityMapper<ExpenseDTO, Expense> {
    @Mapping(target = "expenseType", source = "expenseType", qualifiedByName = "expenseTypeId")
    ExpenseDTO toDto(Expense s);

    @Named("expenseTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ExpenseTypeDTO toDtoExpenseTypeId(ExpenseType expenseType);
}
