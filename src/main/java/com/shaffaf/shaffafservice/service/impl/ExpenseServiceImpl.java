package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.Expense;
import com.shaffaf.shaffafservice.repository.ExpenseRepository;
import com.shaffaf.shaffafservice.service.ExpenseService;
import com.shaffaf.shaffafservice.service.dto.ExpenseDTO;
import com.shaffaf.shaffafservice.service.mapper.ExpenseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.Expense}.
 */
@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private static final Logger LOG = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    private final ExpenseRepository expenseRepository;

    private final ExpenseMapper expenseMapper;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    public ExpenseDTO save(ExpenseDTO expenseDTO) {
        LOG.debug("Request to save Expense : {}", expenseDTO);
        Expense expense = expenseMapper.toEntity(expenseDTO);
        expense = expenseRepository.save(expense);
        return expenseMapper.toDto(expense);
    }

    @Override
    public ExpenseDTO update(ExpenseDTO expenseDTO) {
        LOG.debug("Request to update Expense : {}", expenseDTO);
        Expense expense = expenseMapper.toEntity(expenseDTO);
        expense = expenseRepository.save(expense);
        return expenseMapper.toDto(expense);
    }

    @Override
    public Optional<ExpenseDTO> partialUpdate(ExpenseDTO expenseDTO) {
        LOG.debug("Request to partially update Expense : {}", expenseDTO);

        return expenseRepository
            .findById(expenseDTO.getId())
            .map(existingExpense -> {
                expenseMapper.partialUpdate(existingExpense, expenseDTO);

                return existingExpense;
            })
            .map(expenseRepository::save)
            .map(expenseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Expenses");
        return expenseRepository.findAll(pageable).map(expenseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExpenseDTO> findOne(Long id) {
        LOG.debug("Request to get Expense : {}", id);
        return expenseRepository.findById(id).map(expenseMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Expense : {}", id);
        expenseRepository.deleteById(id);
    }
}
