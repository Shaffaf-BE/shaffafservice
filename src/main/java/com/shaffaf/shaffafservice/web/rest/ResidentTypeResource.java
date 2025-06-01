package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.ResidentTypeRepository;
import com.shaffaf.shaffafservice.service.ResidentTypeService;
import com.shaffaf.shaffafservice.service.dto.ResidentTypeDTO;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.ResidentType}.
 */
@RestController
@RequestMapping("/api/resident-types")
public class ResidentTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(ResidentTypeResource.class);

    private static final String ENTITY_NAME = "shaffafserviceResidentType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResidentTypeService residentTypeService;

    private final ResidentTypeRepository residentTypeRepository;

    public ResidentTypeResource(ResidentTypeService residentTypeService, ResidentTypeRepository residentTypeRepository) {
        this.residentTypeService = residentTypeService;
        this.residentTypeRepository = residentTypeRepository;
    }

    /**
     * {@code POST  /resident-types} : Create a new residentType.
     *
     * @param residentTypeDTO the residentTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new residentTypeDTO, or with status {@code 400 (Bad Request)} if the residentType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ResidentTypeDTO> createResidentType(@Valid @RequestBody ResidentTypeDTO residentTypeDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ResidentType : {}", residentTypeDTO);
        if (residentTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new residentType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        residentTypeDTO = residentTypeService.save(residentTypeDTO);
        return ResponseEntity.created(new URI("/api/resident-types/" + residentTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, residentTypeDTO.getId().toString()))
            .body(residentTypeDTO);
    }

    /**
     * {@code PUT  /resident-types/:id} : Updates an existing residentType.
     *
     * @param id the id of the residentTypeDTO to save.
     * @param residentTypeDTO the residentTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated residentTypeDTO,
     * or with status {@code 400 (Bad Request)} if the residentTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the residentTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResidentTypeDTO> updateResidentType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ResidentTypeDTO residentTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ResidentType : {}, {}", id, residentTypeDTO);
        if (residentTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, residentTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!residentTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        residentTypeDTO = residentTypeService.update(residentTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, residentTypeDTO.getId().toString()))
            .body(residentTypeDTO);
    }

    /**
     * {@code PATCH  /resident-types/:id} : Partial updates given fields of an existing residentType, field will ignore if it is null
     *
     * @param id the id of the residentTypeDTO to save.
     * @param residentTypeDTO the residentTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated residentTypeDTO,
     * or with status {@code 400 (Bad Request)} if the residentTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the residentTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the residentTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ResidentTypeDTO> partialUpdateResidentType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ResidentTypeDTO residentTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ResidentType partially : {}, {}", id, residentTypeDTO);
        if (residentTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, residentTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!residentTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ResidentTypeDTO> result = residentTypeService.partialUpdate(residentTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, residentTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /resident-types} : get all the residentTypes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of residentTypes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ResidentTypeDTO>> getAllResidentTypes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ResidentTypes");
        Page<ResidentTypeDTO> page = residentTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /resident-types/:id} : get the "id" residentType.
     *
     * @param id the id of the residentTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the residentTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResidentTypeDTO> getResidentType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ResidentType : {}", id);
        Optional<ResidentTypeDTO> residentTypeDTO = residentTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(residentTypeDTO);
    }

    /**
     * {@code DELETE  /resident-types/:id} : delete the "id" residentType.
     *
     * @param id the id of the residentTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResidentType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ResidentType : {}", id);
        residentTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
