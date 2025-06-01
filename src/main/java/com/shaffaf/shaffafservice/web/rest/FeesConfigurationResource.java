package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.FeesConfigurationRepository;
import com.shaffaf.shaffafservice.service.FeesConfigurationService;
import com.shaffaf.shaffafservice.service.dto.FeesConfigurationDTO;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.FeesConfiguration}.
 */
@RestController
@RequestMapping("/api/fees-configurations")
public class FeesConfigurationResource {

    private static final Logger LOG = LoggerFactory.getLogger(FeesConfigurationResource.class);

    private static final String ENTITY_NAME = "shaffafserviceFeesConfiguration";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FeesConfigurationService feesConfigurationService;

    private final FeesConfigurationRepository feesConfigurationRepository;

    public FeesConfigurationResource(
        FeesConfigurationService feesConfigurationService,
        FeesConfigurationRepository feesConfigurationRepository
    ) {
        this.feesConfigurationService = feesConfigurationService;
        this.feesConfigurationRepository = feesConfigurationRepository;
    }

    /**
     * {@code POST  /fees-configurations} : Create a new feesConfiguration.
     *
     * @param feesConfigurationDTO the feesConfigurationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new feesConfigurationDTO, or with status {@code 400 (Bad Request)} if the feesConfiguration has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FeesConfigurationDTO> createFeesConfiguration(@Valid @RequestBody FeesConfigurationDTO feesConfigurationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FeesConfiguration : {}", feesConfigurationDTO);
        if (feesConfigurationDTO.getId() != null) {
            throw new BadRequestAlertException("A new feesConfiguration cannot already have an ID", ENTITY_NAME, "idexists");
        }
        feesConfigurationDTO = feesConfigurationService.save(feesConfigurationDTO);
        return ResponseEntity.created(new URI("/api/fees-configurations/" + feesConfigurationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, feesConfigurationDTO.getId().toString()))
            .body(feesConfigurationDTO);
    }

    /**
     * {@code PUT  /fees-configurations/:id} : Updates an existing feesConfiguration.
     *
     * @param id the id of the feesConfigurationDTO to save.
     * @param feesConfigurationDTO the feesConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feesConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the feesConfigurationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the feesConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FeesConfigurationDTO> updateFeesConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FeesConfigurationDTO feesConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FeesConfiguration : {}, {}", id, feesConfigurationDTO);
        if (feesConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feesConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feesConfigurationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        feesConfigurationDTO = feesConfigurationService.update(feesConfigurationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feesConfigurationDTO.getId().toString()))
            .body(feesConfigurationDTO);
    }

    /**
     * {@code PATCH  /fees-configurations/:id} : Partial updates given fields of an existing feesConfiguration, field will ignore if it is null
     *
     * @param id the id of the feesConfigurationDTO to save.
     * @param feesConfigurationDTO the feesConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feesConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the feesConfigurationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the feesConfigurationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the feesConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FeesConfigurationDTO> partialUpdateFeesConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FeesConfigurationDTO feesConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FeesConfiguration partially : {}, {}", id, feesConfigurationDTO);
        if (feesConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feesConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feesConfigurationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FeesConfigurationDTO> result = feesConfigurationService.partialUpdate(feesConfigurationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feesConfigurationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /fees-configurations} : get all the feesConfigurations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of feesConfigurations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FeesConfigurationDTO>> getAllFeesConfigurations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of FeesConfigurations");
        Page<FeesConfigurationDTO> page = feesConfigurationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /fees-configurations/:id} : get the "id" feesConfiguration.
     *
     * @param id the id of the feesConfigurationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the feesConfigurationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeesConfigurationDTO> getFeesConfiguration(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FeesConfiguration : {}", id);
        Optional<FeesConfigurationDTO> feesConfigurationDTO = feesConfigurationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(feesConfigurationDTO);
    }

    /**
     * {@code DELETE  /fees-configurations/:id} : delete the "id" feesConfiguration.
     *
     * @param id the id of the feesConfigurationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeesConfiguration(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FeesConfiguration : {}", id);
        feesConfigurationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
