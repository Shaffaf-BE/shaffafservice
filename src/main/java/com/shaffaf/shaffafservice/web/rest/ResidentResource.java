package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.ResidentRepository;
import com.shaffaf.shaffafservice.service.ResidentService;
import com.shaffaf.shaffafservice.service.dto.ResidentDTO;
import com.shaffaf.shaffafservice.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.Resident}.
 */
@RestController
@RequestMapping("/api/residents")
public class ResidentResource {

    private static final Logger LOG = LoggerFactory.getLogger(ResidentResource.class);

    private static final String ENTITY_NAME = "shaffafserviceResident";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResidentService residentService;

    private final ResidentRepository residentRepository;

    public ResidentResource(ResidentService residentService, ResidentRepository residentRepository) {
        this.residentService = residentService;
        this.residentRepository = residentRepository;
    }

    /**
     * {@code POST  /residents} : Create a new resident.
     *
     * @param residentDTO the residentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new residentDTO, or with status {@code 400 (Bad Request)} if the resident has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ResidentDTO> createResident(@RequestBody ResidentDTO residentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Resident : {}", residentDTO);
        if (residentDTO.getId() != null) {
            throw new BadRequestAlertException("A new resident cannot already have an ID", ENTITY_NAME, "idexists");
        }
        residentDTO = residentService.save(residentDTO);
        return ResponseEntity.created(new URI("/api/residents/" + residentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, residentDTO.getId().toString()))
            .body(residentDTO);
    }

    /**
     * {@code PUT  /residents/:id} : Updates an existing resident.
     *
     * @param id the id of the residentDTO to save.
     * @param residentDTO the residentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated residentDTO,
     * or with status {@code 400 (Bad Request)} if the residentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the residentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResidentDTO> updateResident(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResidentDTO residentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Resident : {}, {}", id, residentDTO);
        if (residentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, residentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!residentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        residentDTO = residentService.update(residentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, residentDTO.getId().toString()))
            .body(residentDTO);
    }

    /**
     * {@code PATCH  /residents/:id} : Partial updates given fields of an existing resident, field will ignore if it is null
     *
     * @param id the id of the residentDTO to save.
     * @param residentDTO the residentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated residentDTO,
     * or with status {@code 400 (Bad Request)} if the residentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the residentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the residentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ResidentDTO> partialUpdateResident(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResidentDTO residentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Resident partially : {}, {}", id, residentDTO);
        if (residentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, residentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!residentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ResidentDTO> result = residentService.partialUpdate(residentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, residentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /residents} : get all the residents.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of residents in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ResidentDTO>> getAllResidents(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Residents");
        Page<ResidentDTO> page = residentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /residents/:id} : get the "id" resident.
     *
     * @param id the id of the residentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the residentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResidentDTO> getResident(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Resident : {}", id);
        Optional<ResidentDTO> residentDTO = residentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(residentDTO);
    }

    /**
     * {@code DELETE  /residents/:id} : delete the "id" resident.
     *
     * @param id the id of the residentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResident(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Resident : {}", id);
        residentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
