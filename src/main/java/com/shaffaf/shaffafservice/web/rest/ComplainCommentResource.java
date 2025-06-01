package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.ComplainCommentRepository;
import com.shaffaf.shaffafservice.service.ComplainCommentService;
import com.shaffaf.shaffafservice.service.dto.ComplainCommentDTO;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.ComplainComment}.
 */
@RestController
@RequestMapping("/api/complain-comments")
public class ComplainCommentResource {

    private static final Logger LOG = LoggerFactory.getLogger(ComplainCommentResource.class);

    private static final String ENTITY_NAME = "shaffafserviceComplainComment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ComplainCommentService complainCommentService;

    private final ComplainCommentRepository complainCommentRepository;

    public ComplainCommentResource(ComplainCommentService complainCommentService, ComplainCommentRepository complainCommentRepository) {
        this.complainCommentService = complainCommentService;
        this.complainCommentRepository = complainCommentRepository;
    }

    /**
     * {@code POST  /complain-comments} : Create a new complainComment.
     *
     * @param complainCommentDTO the complainCommentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new complainCommentDTO, or with status {@code 400 (Bad Request)} if the complainComment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ComplainCommentDTO> createComplainComment(@Valid @RequestBody ComplainCommentDTO complainCommentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ComplainComment : {}", complainCommentDTO);
        if (complainCommentDTO.getId() != null) {
            throw new BadRequestAlertException("A new complainComment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        complainCommentDTO = complainCommentService.save(complainCommentDTO);
        return ResponseEntity.created(new URI("/api/complain-comments/" + complainCommentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, complainCommentDTO.getId().toString()))
            .body(complainCommentDTO);
    }

    /**
     * {@code PUT  /complain-comments/:id} : Updates an existing complainComment.
     *
     * @param id the id of the complainCommentDTO to save.
     * @param complainCommentDTO the complainCommentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated complainCommentDTO,
     * or with status {@code 400 (Bad Request)} if the complainCommentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the complainCommentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ComplainCommentDTO> updateComplainComment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ComplainCommentDTO complainCommentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ComplainComment : {}, {}", id, complainCommentDTO);
        if (complainCommentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, complainCommentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!complainCommentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        complainCommentDTO = complainCommentService.update(complainCommentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, complainCommentDTO.getId().toString()))
            .body(complainCommentDTO);
    }

    /**
     * {@code PATCH  /complain-comments/:id} : Partial updates given fields of an existing complainComment, field will ignore if it is null
     *
     * @param id the id of the complainCommentDTO to save.
     * @param complainCommentDTO the complainCommentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated complainCommentDTO,
     * or with status {@code 400 (Bad Request)} if the complainCommentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the complainCommentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the complainCommentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ComplainCommentDTO> partialUpdateComplainComment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ComplainCommentDTO complainCommentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ComplainComment partially : {}, {}", id, complainCommentDTO);
        if (complainCommentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, complainCommentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!complainCommentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ComplainCommentDTO> result = complainCommentService.partialUpdate(complainCommentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, complainCommentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /complain-comments} : get all the complainComments.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of complainComments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ComplainCommentDTO>> getAllComplainComments(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ComplainComments");
        Page<ComplainCommentDTO> page = complainCommentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /complain-comments/:id} : get the "id" complainComment.
     *
     * @param id the id of the complainCommentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the complainCommentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComplainCommentDTO> getComplainComment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ComplainComment : {}", id);
        Optional<ComplainCommentDTO> complainCommentDTO = complainCommentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(complainCommentDTO);
    }

    /**
     * {@code DELETE  /complain-comments/:id} : delete the "id" complainComment.
     *
     * @param id the id of the complainCommentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplainComment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ComplainComment : {}", id);
        complainCommentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
