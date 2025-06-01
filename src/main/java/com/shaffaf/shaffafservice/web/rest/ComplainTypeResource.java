package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.ComplainTypeRepository;
import com.shaffaf.shaffafservice.service.ComplainTypeService;
import com.shaffaf.shaffafservice.service.dto.ComplainTypeDTO;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.ComplainType}.
 */
@RestController
@RequestMapping("/api/complain-types")
public class ComplainTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(ComplainTypeResource.class);

    private static final String ENTITY_NAME = "shaffafserviceComplainType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ComplainTypeService complainTypeService;

    private final ComplainTypeRepository complainTypeRepository;

    public ComplainTypeResource(ComplainTypeService complainTypeService, ComplainTypeRepository complainTypeRepository) {
        this.complainTypeService = complainTypeService;
        this.complainTypeRepository = complainTypeRepository;
    }

    /**
     * {@code POST  /complain-types} : Create a new complainType.
     *
     * @param complainTypeDTO the complainTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new complainTypeDTO, or with status {@code 400 (Bad Request)} if the complainType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ComplainTypeDTO> createComplainType(@Valid @RequestBody ComplainTypeDTO complainTypeDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ComplainType : {}", complainTypeDTO);
        if (complainTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new complainType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        complainTypeDTO = complainTypeService.save(complainTypeDTO);
        return ResponseEntity.created(new URI("/api/complain-types/" + complainTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, complainTypeDTO.getId().toString()))
            .body(complainTypeDTO);
    }

    /**
     * {@code PUT  /complain-types/:id} : Updates an existing complainType.
     *
     * @param id the id of the complainTypeDTO to save.
     * @param complainTypeDTO the complainTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated complainTypeDTO,
     * or with status {@code 400 (Bad Request)} if the complainTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the complainTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ComplainTypeDTO> updateComplainType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ComplainTypeDTO complainTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ComplainType : {}, {}", id, complainTypeDTO);
        if (complainTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, complainTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!complainTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        complainTypeDTO = complainTypeService.update(complainTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, complainTypeDTO.getId().toString()))
            .body(complainTypeDTO);
    }

    /**
     * {@code PATCH  /complain-types/:id} : Partial updates given fields of an existing complainType, field will ignore if it is null
     *
     * @param id the id of the complainTypeDTO to save.
     * @param complainTypeDTO the complainTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated complainTypeDTO,
     * or with status {@code 400 (Bad Request)} if the complainTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the complainTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the complainTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ComplainTypeDTO> partialUpdateComplainType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ComplainTypeDTO complainTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ComplainType partially : {}, {}", id, complainTypeDTO);
        if (complainTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, complainTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!complainTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ComplainTypeDTO> result = complainTypeService.partialUpdate(complainTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, complainTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /complain-types} : get all the complainTypes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of complainTypes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ComplainTypeDTO>> getAllComplainTypes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ComplainTypes");
        Page<ComplainTypeDTO> page = complainTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /complain-types/:id} : get the "id" complainType.
     *
     * @param id the id of the complainTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the complainTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComplainTypeDTO> getComplainType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ComplainType : {}", id);
        Optional<ComplainTypeDTO> complainTypeDTO = complainTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(complainTypeDTO);
    }

    /**
     * {@code DELETE  /complain-types/:id} : delete the "id" complainType.
     *
     * @param id the id of the complainTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplainType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ComplainType : {}", id);
        complainTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
