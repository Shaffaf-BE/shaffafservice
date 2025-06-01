package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.ComplainStatusRepository;
import com.shaffaf.shaffafservice.service.ComplainStatusService;
import com.shaffaf.shaffafservice.service.dto.ComplainStatusDTO;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.ComplainStatus}.
 */
@RestController
@RequestMapping("/api/complain-statuses")
public class ComplainStatusResource {

    private static final Logger LOG = LoggerFactory.getLogger(ComplainStatusResource.class);

    private static final String ENTITY_NAME = "shaffafserviceComplainStatus";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ComplainStatusService complainStatusService;

    private final ComplainStatusRepository complainStatusRepository;

    public ComplainStatusResource(ComplainStatusService complainStatusService, ComplainStatusRepository complainStatusRepository) {
        this.complainStatusService = complainStatusService;
        this.complainStatusRepository = complainStatusRepository;
    }

    /**
     * {@code POST  /complain-statuses} : Create a new complainStatus.
     *
     * @param complainStatusDTO the complainStatusDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new complainStatusDTO, or with status {@code 400 (Bad Request)} if the complainStatus has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ComplainStatusDTO> createComplainStatus(@Valid @RequestBody ComplainStatusDTO complainStatusDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ComplainStatus : {}", complainStatusDTO);
        if (complainStatusDTO.getId() != null) {
            throw new BadRequestAlertException("A new complainStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        complainStatusDTO = complainStatusService.save(complainStatusDTO);
        return ResponseEntity.created(new URI("/api/complain-statuses/" + complainStatusDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, complainStatusDTO.getId().toString()))
            .body(complainStatusDTO);
    }

    /**
     * {@code PUT  /complain-statuses/:id} : Updates an existing complainStatus.
     *
     * @param id the id of the complainStatusDTO to save.
     * @param complainStatusDTO the complainStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated complainStatusDTO,
     * or with status {@code 400 (Bad Request)} if the complainStatusDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the complainStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ComplainStatusDTO> updateComplainStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ComplainStatusDTO complainStatusDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ComplainStatus : {}, {}", id, complainStatusDTO);
        if (complainStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, complainStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!complainStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        complainStatusDTO = complainStatusService.update(complainStatusDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, complainStatusDTO.getId().toString()))
            .body(complainStatusDTO);
    }

    /**
     * {@code PATCH  /complain-statuses/:id} : Partial updates given fields of an existing complainStatus, field will ignore if it is null
     *
     * @param id the id of the complainStatusDTO to save.
     * @param complainStatusDTO the complainStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated complainStatusDTO,
     * or with status {@code 400 (Bad Request)} if the complainStatusDTO is not valid,
     * or with status {@code 404 (Not Found)} if the complainStatusDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the complainStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ComplainStatusDTO> partialUpdateComplainStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ComplainStatusDTO complainStatusDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ComplainStatus partially : {}, {}", id, complainStatusDTO);
        if (complainStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, complainStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!complainStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ComplainStatusDTO> result = complainStatusService.partialUpdate(complainStatusDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, complainStatusDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /complain-statuses} : get all the complainStatuses.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of complainStatuses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ComplainStatusDTO>> getAllComplainStatuses(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ComplainStatuses");
        Page<ComplainStatusDTO> page = complainStatusService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /complain-statuses/:id} : get the "id" complainStatus.
     *
     * @param id the id of the complainStatusDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the complainStatusDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComplainStatusDTO> getComplainStatus(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ComplainStatus : {}", id);
        Optional<ComplainStatusDTO> complainStatusDTO = complainStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(complainStatusDTO);
    }

    /**
     * {@code DELETE  /complain-statuses/:id} : delete the "id" complainStatus.
     *
     * @param id the id of the complainStatusDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplainStatus(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ComplainStatus : {}", id);
        complainStatusService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
