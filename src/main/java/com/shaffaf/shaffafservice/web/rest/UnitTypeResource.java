package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.UnitTypeRepository;
import com.shaffaf.shaffafservice.service.UnitTypeService;
import com.shaffaf.shaffafservice.service.dto.UnitTypeDTO;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.UnitType}.
 */
@RestController
@RequestMapping("/api/unit-types")
public class UnitTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(UnitTypeResource.class);

    private static final String ENTITY_NAME = "shaffafserviceUnitType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UnitTypeService unitTypeService;

    private final UnitTypeRepository unitTypeRepository;

    public UnitTypeResource(UnitTypeService unitTypeService, UnitTypeRepository unitTypeRepository) {
        this.unitTypeService = unitTypeService;
        this.unitTypeRepository = unitTypeRepository;
    }

    /**
     * {@code POST  /unit-types} : Create a new unitType.
     *
     * @param unitTypeDTO the unitTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new unitTypeDTO, or with status {@code 400 (Bad Request)} if the unitType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UnitTypeDTO> createUnitType(@Valid @RequestBody UnitTypeDTO unitTypeDTO) throws URISyntaxException {
        LOG.debug("REST request to save UnitType : {}", unitTypeDTO);
        if (unitTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new unitType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        unitTypeDTO = unitTypeService.save(unitTypeDTO);
        return ResponseEntity.created(new URI("/api/unit-types/" + unitTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, unitTypeDTO.getId().toString()))
            .body(unitTypeDTO);
    }

    /**
     * {@code PUT  /unit-types/:id} : Updates an existing unitType.
     *
     * @param id the id of the unitTypeDTO to save.
     * @param unitTypeDTO the unitTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated unitTypeDTO,
     * or with status {@code 400 (Bad Request)} if the unitTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the unitTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UnitTypeDTO> updateUnitType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UnitTypeDTO unitTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UnitType : {}, {}", id, unitTypeDTO);
        if (unitTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, unitTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!unitTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        unitTypeDTO = unitTypeService.update(unitTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, unitTypeDTO.getId().toString()))
            .body(unitTypeDTO);
    }

    /**
     * {@code PATCH  /unit-types/:id} : Partial updates given fields of an existing unitType, field will ignore if it is null
     *
     * @param id the id of the unitTypeDTO to save.
     * @param unitTypeDTO the unitTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated unitTypeDTO,
     * or with status {@code 400 (Bad Request)} if the unitTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the unitTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the unitTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UnitTypeDTO> partialUpdateUnitType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UnitTypeDTO unitTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UnitType partially : {}, {}", id, unitTypeDTO);
        if (unitTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, unitTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!unitTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UnitTypeDTO> result = unitTypeService.partialUpdate(unitTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, unitTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /unit-types} : get all the unitTypes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of unitTypes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UnitTypeDTO>> getAllUnitTypes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of UnitTypes");
        Page<UnitTypeDTO> page = unitTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /unit-types/:id} : get the "id" unitType.
     *
     * @param id the id of the unitTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the unitTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UnitTypeDTO> getUnitType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UnitType : {}", id);
        Optional<UnitTypeDTO> unitTypeDTO = unitTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(unitTypeDTO);
    }

    /**
     * {@code DELETE  /unit-types/:id} : delete the "id" unitType.
     *
     * @param id the id of the unitTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnitType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UnitType : {}", id);
        unitTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
