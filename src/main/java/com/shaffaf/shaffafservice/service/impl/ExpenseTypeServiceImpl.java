package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.ExpenseType;
import com.shaffaf.shaffafservice.repository.ExpenseTypeRepository;
import com.shaffaf.shaffafservice.service.ExpenseTypeService;
import com.shaffaf.shaffafservice.service.dto.ExpenseTypeDTO;
import com.shaffaf.shaffafservice.service.mapper.ExpenseTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.ExpenseType}.
 */
@Service
@Transactional
public class ExpenseTypeServiceImpl implements ExpenseTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(ExpenseTypeServiceImpl.class);

    private final ExpenseTypeRepository expenseTypeRepository;

    private final ExpenseTypeMapper expenseTypeMapper;

    public ExpenseTypeServiceImpl(ExpenseTypeRepository expenseTypeRepository, ExpenseTypeMapper expenseTypeMapper) {
        this.expenseTypeRepository = expenseTypeRepository;
        this.expenseTypeMapper = expenseTypeMapper;
    }

    @Override
    public ExpenseTypeDTO save(ExpenseTypeDTO expenseTypeDTO) {
        LOG.debug("Request to save ExpenseType : {}", expenseTypeDTO);
        ExpenseType expenseType = expenseTypeMapper.toEntity(expenseTypeDTO);
        expenseType = expenseTypeRepository.save(expenseType);
        return expenseTypeMapper.toDto(expenseType);
    }

    @Override
    public ExpenseTypeDTO update(ExpenseTypeDTO expenseTypeDTO) {
        LOG.debug("Request to update ExpenseType : {}", expenseTypeDTO);
        ExpenseType expenseType = expenseTypeMapper.toEntity(expenseTypeDTO);
        expenseType = expenseTypeRepository.save(expenseType);
        return expenseTypeMapper.toDto(expenseType);
    }

    @Override
    public Optional<ExpenseTypeDTO> partialUpdate(ExpenseTypeDTO expenseTypeDTO) {
        LOG.debug("Request to partially update ExpenseType : {}", expenseTypeDTO);

        return expenseTypeRepository
            .findById(expenseTypeDTO.getId())
            .map(existingExpenseType -> {
                expenseTypeMapper.partialUpdate(existingExpenseType, expenseTypeDTO);

                return existingExpenseType;
            })
            .map(expenseTypeRepository::save)
            .map(expenseTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseTypeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ExpenseTypes");
        return expenseTypeRepository.findAll(pageable).map(expenseTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExpenseTypeDTO> findOne(Long id) {
        LOG.debug("Request to get ExpenseType : {}", id);
        return expenseTypeRepository.findById(id).map(expenseTypeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ExpenseType : {}", id);
        expenseTypeRepository.deleteById(id);
    }
}
