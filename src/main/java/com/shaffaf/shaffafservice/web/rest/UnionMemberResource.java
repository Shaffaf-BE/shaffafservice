package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.UnionMemberRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.security.SecurityUtils;
import com.shaffaf.shaffafservice.service.UnionMemberService;
import com.shaffaf.shaffafservice.service.dto.UnionMemberDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.UnionMember}.
 */
@RestController
@RequestMapping("/api/union-members/v1")
public class UnionMemberResource {

    private static final Logger LOG = LoggerFactory.getLogger(UnionMemberResource.class);

    private static final String ENTITY_NAME = "shaffafserviceUnionMember";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UnionMemberService unionMemberService;

    private final UnionMemberRepository unionMemberRepository;

    public UnionMemberResource(UnionMemberService unionMemberService, UnionMemberRepository unionMemberRepository) {
        this.unionMemberService = unionMemberService;
        this.unionMemberRepository = unionMemberRepository;
    }

    /**
     * {@code POST  /union-members} : Create a new unionMember.
     *
     * @param unionMemberDTO the unionMemberDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new unionMemberDTO, or with status {@code 400 (Bad Request)} if the unionMember has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */@PostMapping("")
    public ResponseEntity<UnionMemberDTO> createUnionMember(@Valid @RequestBody UnionMemberDTO unionMemberDTO) throws URISyntaxException {
        LOG.debug("REST request to save UnionMember : {}", unionMemberDTO);
        if (unionMemberDTO.getId() != null) {
            throw new BadRequestAlertException("A new unionMember cannot already have an ID", ENTITY_NAME, "idexists");
        }
        try {
            unionMemberDTO = unionMemberService.save(unionMemberDTO);
            return ResponseEntity.created(new URI("/api/union-members/" + unionMemberDTO.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, unionMemberDTO.getId().toString()))
                .body(unionMemberDTO);
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(
                "Union member with this phone number already exists for this project",
                ENTITY_NAME,
                "duplicatemember"
            );
        }
    }

    /**
     * {@code PUT  /union-members/:id} : Updates an existing unionMember.
     *
     * @param id the id of the unionMemberDTO to save.
     * @param unionMemberDTO the unionMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated unionMemberDTO,
     * or with status {@code 400 (Bad Request)} if the unionMemberDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the unionMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UnionMemberDTO> updateUnionMember(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UnionMemberDTO unionMemberDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UnionMember : {}, {}", id, unionMemberDTO);
        if (unionMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, unionMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!unionMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        try {
            unionMemberDTO = unionMemberService.update(unionMemberDTO);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, unionMemberDTO.getId().toString()))
                .body(unionMemberDTO);
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(
                "Another union member with this phone number already exists for this project",
                ENTITY_NAME,
                "duplicatemember"
            );
        }
    }

    /**
     * {@code PATCH  /union-members/:id} : Partial updates given fields of an existing unionMember, field will ignore if it is null
     *
     * @param id the id of the unionMemberDTO to save.
     * @param unionMemberDTO the unionMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated unionMemberDTO,
     * or with status {@code 400 (Bad Request)} if the unionMemberDTO is not valid,
     * or with status {@code 404 (Not Found)} if the unionMemberDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the unionMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UnionMemberDTO> partialUpdateUnionMember(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UnionMemberDTO unionMemberDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UnionMember partially : {}, {}", id, unionMemberDTO);
        if (unionMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, unionMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!unionMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UnionMemberDTO> result = unionMemberService.partialUpdate(unionMemberDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, unionMemberDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /union-members} : get all the unionMembers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of unionMembers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UnionMemberDTO>> getAllUnionMembers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of UnionMembers");
        Page<UnionMemberDTO> page = unionMemberService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /union-members/:id} : get the "id" unionMember.
     *
     * @param id the id of the unionMemberDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the unionMemberDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UnionMemberDTO> getUnionMember(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UnionMember : {}", id);
        Optional<UnionMemberDTO> unionMemberDTO = unionMemberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(unionMemberDTO);
    }

    /**
     * {@code DELETE  /union-members/:id} : delete the "id" unionMember.
     *
     * @param id the id of the unionMemberDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnionMember(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UnionMember : {}", id);
        unionMemberService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST  /union-members/member} : Create a new union member (non-head) using native SQL.
     * Restricted to ADMIN and SELLER roles only.
     *
     * @param unionMemberDTO the unionMemberDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new unionMemberDTO, or with status {@code 400 (Bad Request)} if the unionMember has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */@PostMapping("/member")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.SELLER + "\")")
    public ResponseEntity<UnionMemberDTO> createUnionMemberNative(@Valid @RequestBody UnionMemberDTO unionMemberDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save UnionMember using native SQL : {}", unionMemberDTO);

        if (unionMemberDTO.getId() != null) {
            throw new BadRequestAlertException("A new union member cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (unionMemberDTO.getProject() == null || unionMemberDTO.getProject().getId() == null) {
            throw new BadRequestAlertException("Project is required for union member", ENTITY_NAME, "projectrequired");
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Current user login not found", ENTITY_NAME, "usernotfound"));
        try {
            unionMemberDTO = unionMemberService.saveUnionMemberNative(unionMemberDTO, currentUserLogin);
            return ResponseEntity.created(new URI("/api/union-members/member/" + unionMemberDTO.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, unionMemberDTO.getId().toString()))
                .body(unionMemberDTO);
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(
                "Union member with this phone number already exists for this project",
                ENTITY_NAME,
                "duplicatemember"
            );
        }
    }

    /**
     * {@code POST  /union-members/head} : Create a new union head using native SQL.
     * Ensures only one head per project. Restricted to ADMIN and SELLER roles only.
     *
     * @param unionMemberDTO the unionMemberDTO to create as union head.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new unionMemberDTO, or with status {@code 400 (Bad Request)} if validation fails.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/head")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.SELLER + "\")")
    public ResponseEntity<UnionMemberDTO> createUnionHeadNative(@Valid @RequestBody UnionMemberDTO unionMemberDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save UnionHead using native SQL : {}", unionMemberDTO);

        if (unionMemberDTO.getId() != null) {
            throw new BadRequestAlertException("A new union head cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (unionMemberDTO.getProject() == null || unionMemberDTO.getProject().getId() == null) {
            throw new BadRequestAlertException("Project is required for union head", ENTITY_NAME, "projectrequired");
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Current user login not found", ENTITY_NAME, "usernotfound"));
        try {
            unionMemberDTO = unionMemberService.saveUnionHeadNative(unionMemberDTO, currentUserLogin);
        } catch (IllegalStateException e) {
            throw new BadRequestAlertException("A union head already exists for this project", ENTITY_NAME, "unionheadexists");
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(
                "Union member with this phone number already exists for this project",
                ENTITY_NAME,
                "duplicatemember"
            );
        }

        return ResponseEntity.created(new URI("/api/union-members/head/" + unionMemberDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, unionMemberDTO.getId().toString()))
            .body(unionMemberDTO);
    }

    /**
     * {@code GET  /union-members/native/{id}} : get the union member by ID using native SQL.
     * Restricted to ADMIN and SELLER roles only.
     *
     * @param id the id of the unionMemberDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the unionMemberDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/native/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.SELLER + "\")")
    public ResponseEntity<UnionMemberDTO> getUnionMemberNative(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UnionMember using native SQL : {}", id);
        Optional<UnionMemberDTO> unionMemberDTO = unionMemberService.findOneNative(id);
        return ResponseUtil.wrapOrNotFound(unionMemberDTO);
    }

    /**
     * {@code PUT  /union-members/native/{id}} : Updates an existing union member using native SQL.
     * Restricted to ADMIN and SELLER roles only.
     *
     * @param id the id of the unionMemberDTO to update.
     * @param unionMemberDTO the unionMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated unionMemberDTO,
     * or with status {@code 400 (Bad Request)} if the unionMemberDTO is not valid,
     * or with status {@code 404 (Not Found)} if the unionMemberDTO is not found.
     */
    @PutMapping("/native/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.SELLER + "\")")
    public ResponseEntity<UnionMemberDTO> updateUnionMemberNative(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UnionMemberDTO unionMemberDTO
    ) {
        LOG.debug("REST request to update UnionMember using native SQL : {}, {}", id, unionMemberDTO);

        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        if (unionMemberDTO.getProject() == null || unionMemberDTO.getProject().getId() == null) {
            throw new BadRequestAlertException("Project is required for union member", ENTITY_NAME, "projectrequired");
        }
        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Current user login not found", ENTITY_NAME, "usernotfound"));

        try {
            Optional<UnionMemberDTO> result = unionMemberService.updateUnionMemberNative(id, unionMemberDTO, currentUserLogin);

            return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString())
            );
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(
                "Another union member with this phone number already exists for this project",
                ENTITY_NAME,
                "duplicatemember"
            );
        }
    }

    /**
     * {@code GET  /union-members/project/{projectId}} : get all union members for a specific project using native SQL with pagination.
     * Restricted to ADMIN and SELLER roles only.
     *
     * @param projectId the project ID to filter by.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of union members in body.
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.SELLER + "\")")
    public ResponseEntity<List<UnionMemberDTO>> getUnionMembersByProject(
        @PathVariable("projectId") Long projectId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get UnionMembers by project using native SQL : projectId={}", projectId);

        if (projectId == null) {
            throw new BadRequestAlertException("Project ID is required", ENTITY_NAME, "projectidrequired");
        }

        Page<UnionMemberDTO> page = unionMemberService.findUnionMembersByProjectNative(projectId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /union-members/native} : get all union members using native SQL with pagination and sorting.
     * Restricted to ADMIN and SELLER roles only.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of union members in body.
     */
    @GetMapping("/native")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.SELLER + "\")")
    public ResponseEntity<List<UnionMemberDTO>> getAllUnionMembersNative(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get all UnionMembers using native SQL");
        Page<UnionMemberDTO> page = unionMemberService.findAllUnionMembersNative(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
