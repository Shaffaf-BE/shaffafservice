package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.ProjectDiscountRepository;
import com.shaffaf.shaffafservice.service.ProjectDiscountService;
import com.shaffaf.shaffafservice.service.dto.ProjectDiscountDTO;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.ProjectDiscount}.
 */
@RestController
@RequestMapping("/api/project-discounts")
public class ProjectDiscountResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectDiscountResource.class);

    private static final String ENTITY_NAME = "shaffafserviceProjectDiscount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectDiscountService projectDiscountService;

    private final ProjectDiscountRepository projectDiscountRepository;

    public ProjectDiscountResource(ProjectDiscountService projectDiscountService, ProjectDiscountRepository projectDiscountRepository) {
        this.projectDiscountService = projectDiscountService;
        this.projectDiscountRepository = projectDiscountRepository;
    }

    /**
     * {@code POST  /project-discounts} : Create a new projectDiscount.
     *
     * @param projectDiscountDTO the projectDiscountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectDiscountDTO, or with status {@code 400 (Bad Request)} if the projectDiscount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProjectDiscountDTO> createProjectDiscount(@Valid @RequestBody ProjectDiscountDTO projectDiscountDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProjectDiscount : {}", projectDiscountDTO);
        if (projectDiscountDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectDiscount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        projectDiscountDTO = projectDiscountService.save(projectDiscountDTO);
        return ResponseEntity.created(new URI("/api/project-discounts/" + projectDiscountDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, projectDiscountDTO.getId().toString()))
            .body(projectDiscountDTO);
    }

    /**
     * {@code PUT  /project-discounts/:id} : Updates an existing projectDiscount.
     *
     * @param id the id of the projectDiscountDTO to save.
     * @param projectDiscountDTO the projectDiscountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectDiscountDTO,
     * or with status {@code 400 (Bad Request)} if the projectDiscountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectDiscountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDiscountDTO> updateProjectDiscount(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectDiscountDTO projectDiscountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProjectDiscount : {}, {}", id, projectDiscountDTO);
        if (projectDiscountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectDiscountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectDiscountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        projectDiscountDTO = projectDiscountService.update(projectDiscountDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectDiscountDTO.getId().toString()))
            .body(projectDiscountDTO);
    }

    /**
     * {@code PATCH  /project-discounts/:id} : Partial updates given fields of an existing projectDiscount, field will ignore if it is null
     *
     * @param id the id of the projectDiscountDTO to save.
     * @param projectDiscountDTO the projectDiscountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectDiscountDTO,
     * or with status {@code 400 (Bad Request)} if the projectDiscountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectDiscountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectDiscountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectDiscountDTO> partialUpdateProjectDiscount(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectDiscountDTO projectDiscountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProjectDiscount partially : {}, {}", id, projectDiscountDTO);
        if (projectDiscountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectDiscountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectDiscountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectDiscountDTO> result = projectDiscountService.partialUpdate(projectDiscountDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectDiscountDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /project-discounts} : get all the projectDiscounts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectDiscounts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProjectDiscountDTO>> getAllProjectDiscounts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ProjectDiscounts");
        Page<ProjectDiscountDTO> page = projectDiscountService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /project-discounts/:id} : get the "id" projectDiscount.
     *
     * @param id the id of the projectDiscountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectDiscountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDiscountDTO> getProjectDiscount(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProjectDiscount : {}", id);
        Optional<ProjectDiscountDTO> projectDiscountDTO = projectDiscountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectDiscountDTO);
    }

    /**
     * {@code DELETE  /project-discounts/:id} : delete the "id" projectDiscount.
     *
     * @param id the id of the projectDiscountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectDiscount(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProjectDiscount : {}", id);
        projectDiscountService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
