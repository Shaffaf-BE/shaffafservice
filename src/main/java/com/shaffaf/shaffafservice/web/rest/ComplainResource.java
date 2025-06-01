package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.ComplainRepository;
import com.shaffaf.shaffafservice.service.ComplainService;
import com.shaffaf.shaffafservice.service.dto.ComplainDTO;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.Complain}.
 */
@RestController
@RequestMapping("/api/complains")
public class ComplainResource {

    private static final Logger LOG = LoggerFactory.getLogger(ComplainResource.class);

    private static final String ENTITY_NAME = "shaffafserviceComplain";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ComplainService complainService;

    private final ComplainRepository complainRepository;

    public ComplainResource(ComplainService complainService, ComplainRepository complainRepository) {
        this.complainService = complainService;
        this.complainRepository = complainRepository;
    }

    /**
     * {@code POST  /complains} : Create a new complain.
     *
     * @param complainDTO the complainDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new complainDTO, or with status {@code 400 (Bad Request)} if the complain has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ComplainDTO> createComplain(@Valid @RequestBody ComplainDTO complainDTO) throws URISyntaxException {
        LOG.debug("REST request to save Complain : {}", complainDTO);
        if (complainDTO.getId() != null) {
            throw new BadRequestAlertException("A new complain cannot already have an ID", ENTITY_NAME, "idexists");
        }
        complainDTO = complainService.save(complainDTO);
        return ResponseEntity.created(new URI("/api/complains/" + complainDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, complainDTO.getId().toString()))
            .body(complainDTO);
    }

    /**
     * {@code PUT  /complains/:id} : Updates an existing complain.
     *
     * @param id the id of the complainDTO to save.
     * @param complainDTO the complainDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated complainDTO,
     * or with status {@code 400 (Bad Request)} if the complainDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the complainDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ComplainDTO> updateComplain(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ComplainDTO complainDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Complain : {}, {}", id, complainDTO);
        if (complainDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, complainDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!complainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        complainDTO = complainService.update(complainDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, complainDTO.getId().toString()))
            .body(complainDTO);
    }

    /**
     * {@code PATCH  /complains/:id} : Partial updates given fields of an existing complain, field will ignore if it is null
     *
     * @param id the id of the complainDTO to save.
     * @param complainDTO the complainDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated complainDTO,
     * or with status {@code 400 (Bad Request)} if the complainDTO is not valid,
     * or with status {@code 404 (Not Found)} if the complainDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the complainDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ComplainDTO> partialUpdateComplain(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ComplainDTO complainDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Complain partially : {}, {}", id, complainDTO);
        if (complainDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, complainDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!complainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ComplainDTO> result = complainService.partialUpdate(complainDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, complainDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /complains} : get all the complains.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of complains in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ComplainDTO>> getAllComplains(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Complains");
        Page<ComplainDTO> page = complainService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /complains/:id} : get the "id" complain.
     *
     * @param id the id of the complainDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the complainDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComplainDTO> getComplain(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Complain : {}", id);
        Optional<ComplainDTO> complainDTO = complainService.findOne(id);
        return ResponseUtil.wrapOrNotFound(complainDTO);
    }

    /**
     * {@code DELETE  /complains/:id} : delete the "id" complain.
     *
     * @param id the id of the complainDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplain(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Complain : {}", id);
        complainService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
