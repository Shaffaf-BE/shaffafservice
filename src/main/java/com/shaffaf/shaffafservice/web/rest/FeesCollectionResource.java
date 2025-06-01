package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.FeesCollectionRepository;
import com.shaffaf.shaffafservice.service.FeesCollectionService;
import com.shaffaf.shaffafservice.service.dto.FeesCollectionDTO;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.FeesCollection}.
 */
@RestController
@RequestMapping("/api/fees-collections")
public class FeesCollectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(FeesCollectionResource.class);

    private static final String ENTITY_NAME = "shaffafserviceFeesCollection";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FeesCollectionService feesCollectionService;

    private final FeesCollectionRepository feesCollectionRepository;

    public FeesCollectionResource(FeesCollectionService feesCollectionService, FeesCollectionRepository feesCollectionRepository) {
        this.feesCollectionService = feesCollectionService;
        this.feesCollectionRepository = feesCollectionRepository;
    }

    /**
     * {@code POST  /fees-collections} : Create a new feesCollection.
     *
     * @param feesCollectionDTO the feesCollectionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new feesCollectionDTO, or with status {@code 400 (Bad Request)} if the feesCollection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FeesCollectionDTO> createFeesCollection(@Valid @RequestBody FeesCollectionDTO feesCollectionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FeesCollection : {}", feesCollectionDTO);
        if (feesCollectionDTO.getId() != null) {
            throw new BadRequestAlertException("A new feesCollection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        feesCollectionDTO = feesCollectionService.save(feesCollectionDTO);
        return ResponseEntity.created(new URI("/api/fees-collections/" + feesCollectionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, feesCollectionDTO.getId().toString()))
            .body(feesCollectionDTO);
    }

    /**
     * {@code PUT  /fees-collections/:id} : Updates an existing feesCollection.
     *
     * @param id the id of the feesCollectionDTO to save.
     * @param feesCollectionDTO the feesCollectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feesCollectionDTO,
     * or with status {@code 400 (Bad Request)} if the feesCollectionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the feesCollectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FeesCollectionDTO> updateFeesCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FeesCollectionDTO feesCollectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FeesCollection : {}, {}", id, feesCollectionDTO);
        if (feesCollectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feesCollectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feesCollectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        feesCollectionDTO = feesCollectionService.update(feesCollectionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feesCollectionDTO.getId().toString()))
            .body(feesCollectionDTO);
    }

    /**
     * {@code PATCH  /fees-collections/:id} : Partial updates given fields of an existing feesCollection, field will ignore if it is null
     *
     * @param id the id of the feesCollectionDTO to save.
     * @param feesCollectionDTO the feesCollectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feesCollectionDTO,
     * or with status {@code 400 (Bad Request)} if the feesCollectionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the feesCollectionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the feesCollectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FeesCollectionDTO> partialUpdateFeesCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FeesCollectionDTO feesCollectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FeesCollection partially : {}, {}", id, feesCollectionDTO);
        if (feesCollectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feesCollectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feesCollectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FeesCollectionDTO> result = feesCollectionService.partialUpdate(feesCollectionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feesCollectionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /fees-collections} : get all the feesCollections.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of feesCollections in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FeesCollectionDTO>> getAllFeesCollections(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of FeesCollections");
        Page<FeesCollectionDTO> page = feesCollectionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /fees-collections/:id} : get the "id" feesCollection.
     *
     * @param id the id of the feesCollectionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the feesCollectionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeesCollectionDTO> getFeesCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FeesCollection : {}", id);
        Optional<FeesCollectionDTO> feesCollectionDTO = feesCollectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(feesCollectionDTO);
    }

    /**
     * {@code DELETE  /fees-collections/:id} : delete the "id" feesCollection.
     *
     * @param id the id of the feesCollectionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeesCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FeesCollection : {}", id);
        feesCollectionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
