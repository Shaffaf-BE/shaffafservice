package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.ExpenseTypeRepository;
import com.shaffaf.shaffafservice.service.ExpenseTypeService;
import com.shaffaf.shaffafservice.service.dto.ExpenseTypeDTO;
import com.shaffaf.shaffafservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.ExpenseType}.
 */
@RestController
@RequestMapping("/api/expense-types")
public class ExpenseTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExpenseTypeResource.class);

    private static final String ENTITY_NAME = "shaffafserviceExpenseType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExpenseTypeService expenseTypeService;

    private final ExpenseTypeRepository expenseTypeRepository;

    public ExpenseTypeResource(ExpenseTypeService expenseTypeService, ExpenseTypeRepository expenseTypeRepository) {
        this.expenseTypeService = expenseTypeService;
        this.expenseTypeRepository = expenseTypeRepository;
    }

    /**
     * {@code POST  /expense-types} : Create a new expenseType.
     *
     * @param expenseTypeDTO the expenseTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new expenseTypeDTO, or with status {@code 400 (Bad Request)} if the expenseType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ExpenseTypeDTO> createExpenseType(@Valid @RequestBody ExpenseTypeDTO expenseTypeDTO) throws URISyntaxException {
        LOG.debug("REST request to save ExpenseType : {}", expenseTypeDTO);
        if (expenseTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new expenseType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        expenseTypeDTO = expenseTypeService.save(expenseTypeDTO);
        return ResponseEntity.created(new URI("/api/expense-types/" + expenseTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, expenseTypeDTO.getId().toString()))
            .body(expenseTypeDTO);
    }

    /**
     * {@code PUT  /expense-types/:id} : Updates an existing expenseType.
     *
     * @param id the id of the expenseTypeDTO to save.
     * @param expenseTypeDTO the expenseTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated expenseTypeDTO,
     * or with status {@code 400 (Bad Request)} if the expenseTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the expenseTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseTypeDTO> updateExpenseType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ExpenseTypeDTO expenseTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ExpenseType : {}, {}", id, expenseTypeDTO);
        if (expenseTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, expenseTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!expenseTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        expenseTypeDTO = expenseTypeService.update(expenseTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, expenseTypeDTO.getId().toString()))
            .body(expenseTypeDTO);
    }

    /**
     * {@code PATCH  /expense-types/:id} : Partial updates given fields of an existing expenseType, field will ignore if it is null
     *
     * @param id the id of the expenseTypeDTO to save.
     * @param expenseTypeDTO the expenseTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated expenseTypeDTO,
     * or with status {@code 400 (Bad Request)} if the expenseTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the expenseTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the expenseTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ExpenseTypeDTO> partialUpdateExpenseType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ExpenseTypeDTO expenseTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ExpenseType partially : {}, {}", id, expenseTypeDTO);
        if (expenseTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, expenseTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!expenseTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ExpenseTypeDTO> result = expenseTypeService.partialUpdate(expenseTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, expenseTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /expense-types} : get all the expenseTypes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of expenseTypes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ExpenseTypeDTO>> getAllExpenseTypes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ExpenseTypes");
        Page<ExpenseTypeDTO> page = expenseTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /expense-types/:id} : get the "id" expenseType.
     *
     * @param id the id of the expenseTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the expenseTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseTypeDTO> getExpenseType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ExpenseType : {}", id);
        Optional<ExpenseTypeDTO> expenseTypeDTO = expenseTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(expenseTypeDTO);
    }

    /**
     * {@code DELETE  /expense-types/:id} : delete the "id" expenseType.
     *
     * @param id the id of the expenseTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ExpenseType : {}", id);
        expenseTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
