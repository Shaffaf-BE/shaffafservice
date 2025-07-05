package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.repository.UnionMemberRepository;
import com.shaffaf.shaffafservice.repository.UnitTypeRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.security.SecurityUtils;
import com.shaffaf.shaffafservice.service.UnitTypeService;
import com.shaffaf.shaffafservice.service.dto.UnitTypeDTO;
import com.shaffaf.shaffafservice.util.PhoneNumberUtil;
import com.shaffaf.shaffafservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.UnitType}.
 */
@RestController
@RequestMapping("/api/unit-types/v1")
public class UnitTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(UnitTypeResource.class);

    private static final String ENTITY_NAME = "shaffafserviceUnitType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UnitTypeService unitTypeService;

    private final UnitTypeRepository unitTypeRepository;

    private final SellerRepository sellerRepository;

    private final UnionMemberRepository unionMemberRepository;

    public UnitTypeResource(
        UnitTypeService unitTypeService,
        UnitTypeRepository unitTypeRepository,
        SellerRepository sellerRepository,
        UnionMemberRepository unionMemberRepository
    ) {
        this.unitTypeService = unitTypeService;
        this.unitTypeRepository = unitTypeRepository;
        this.sellerRepository = sellerRepository;
        this.unionMemberRepository = unionMemberRepository;
    }/**
     * {@code POST  /unit-types} : Create a new unitType.
     *
     * @param unitTypeDTO the unitTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new unitTypeDTO, or with status {@code 400 (Bad Request)} if the unitType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */

    @PostMapping("")
    @PreAuthorize(
        "hasAnyAuthority(" +
        "\"" +
        AuthoritiesConstants.ADMIN +
        "\", " +
        "\"" +
        AuthoritiesConstants.SELLER +
        "\", " +
        "\"" +
        AuthoritiesConstants.UNION_HEAD +
        "\"" +
        ")"
    )
    public ResponseEntity<UnitTypeDTO> createUnitType(@Valid @RequestBody UnitTypeDTO unitTypeDTO) throws URISyntaxException {
        LOG.debug("REST request to save UnitType : {}", unitTypeDTO);
        if (unitTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new unitType cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (unitTypeDTO.getProject() == null || unitTypeDTO.getProject().getId() == null) {
            throw new BadRequestAlertException("Project ID must be provided", ENTITY_NAME, "projectidnull");
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Current user not found. Please ensure you are properly authenticated."
                )
            );

        String phoneNumber = PhoneNumberUtil.normalize(currentUserLogin);

        if (!PhoneNumberUtil.isValidPakistaniMobile(phoneNumber)) {
            throw new BadRequestAlertException("Invalid phone number format", ENTITY_NAME, "invalidphonenumber");
        }

        // Check if the current user is authorized to create a unit type
        // Admins can create unit types for any project, no additional checks needed
        // If the user is a union head, they can create unit types for their own projects
        if (
            SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.UNION_HEAD) &&
            !SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        ) {
            if (!unionMemberRepository.isUnionMemberAssociatedWithProject(unitTypeDTO.getProject().getId(), phoneNumber)) {
                throw new BadRequestAlertException(
                    "Union heads can only create unit types for their own projects",
                    ENTITY_NAME,
                    "unauthorized"
                );
            }
        }
        // If the Seller is the current user, they can only create unit types for their associate project
        if (
            SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER) &&
            !SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        ) {
            if (!sellerRepository.isSellerAssociatedWithProject(unitTypeDTO.getProject().getId(), phoneNumber)) {
                throw new BadRequestAlertException(
                    "Sellers can only create unit types for their own projects",
                    ENTITY_NAME,
                    "unauthorized"
                );
            }
        }

        unitTypeDTO = unitTypeService.save(unitTypeDTO);
        return ResponseEntity.created(new URI("/api/unit-types/v1/" + unitTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, unitTypeDTO.getId().toString()))
            .body(unitTypeDTO);
    }

    /**
     * {@code PUT  /unit-types/:id} : Updates an existing unitType.
     *
     * @param id the id of the unitTypeDTO to save.
     * @param unitTypeDTO the unitTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated unitTypeDTO,
     * or with status {@code 400 (Bad Request)} if the unitTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the unitTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize(
        "hasAnyAuthority(" +
        "\"" +
        AuthoritiesConstants.ADMIN +
        "\", " +
        "\"" +
        AuthoritiesConstants.SELLER +
        "\", " +
        "\"" +
        AuthoritiesConstants.UNION_HEAD +
        "\"" +
        ")"
    )
    public ResponseEntity<UnitTypeDTO> updateUnitType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UnitTypeDTO unitTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UnitType : {}, {}", id, unitTypeDTO);
        if (unitTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, unitTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!unitTypeRepository.existsByIdNative(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Current user not found. Please ensure you are properly authenticated."
                )
            );

        String phoneNumber = PhoneNumberUtil.normalize(currentUserLogin);

        if (!PhoneNumberUtil.isValidPakistaniMobile(phoneNumber)) {
            throw new BadRequestAlertException("Invalid phone number format", ENTITY_NAME, "invalidphonenumber");
        }

        // Check if the current user is authorized to update a unit type
        // Admins can update unit types for any project, no additional checks needed
        // If the user is a union head, they can update unit types for their own projects
        if (
            SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.UNION_HEAD) &&
            !SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        ) {
            if (!unionMemberRepository.isUnionMemberAssociatedWithProject(unitTypeDTO.getProject().getId(), phoneNumber)) {
                throw new BadRequestAlertException(
                    "Union heads can only update unit types for their own projects",
                    ENTITY_NAME,
                    "unauthorized"
                );
            }
        }
        // If the Seller is the current user, they can only update unit types for their associate project
        if (
            SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER) &&
            !SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        ) {
            if (!sellerRepository.isSellerAssociatedWithProject(unitTypeDTO.getProject().getId(), phoneNumber)) {
                throw new BadRequestAlertException(
                    "Sellers can only update unit types for their own projects",
                    ENTITY_NAME,
                    "unauthorized"
                );
            }
        }

        unitTypeDTO = unitTypeService.update(unitTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, unitTypeDTO.getId().toString()))
            .body(unitTypeDTO);
    }

    /**
     * {@code GET  /unit-types/projects/{projectId}} : get all the unit types by ProjectId.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of unit types in body.
     */
    @GetMapping("/projects/{projectId}")
    @PreAuthorize(
        "hasAnyAuthority(" +
        "\"" +
        AuthoritiesConstants.ADMIN +
        "\", " +
        "\"" +
        AuthoritiesConstants.SELLER +
        "\", " +
        "\"" +
        AuthoritiesConstants.UNION_HEAD +
        "\"" +
        ")"
    )
    public ResponseEntity<Page<UnitTypeDTO>> getAllUnitTypes(@PathVariable("projectId") Long projectId, Pageable pageable) {
        LOG.debug("REST request to get a page of UnitTypes By ProjectId");
        Page<UnitTypeDTO> page = unitTypeService.findAllByProjectId(projectId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page);
    }

    /**
     * {@code GET  /unit-types/:id} : get the "id" unitType.
     *
     * @param id the id of the unitTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the unitTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize(
        "hasAnyAuthority(" +
        "\"" +
        AuthoritiesConstants.ADMIN +
        "\", " +
        "\"" +
        AuthoritiesConstants.SELLER +
        "\", " +
        "\"" +
        AuthoritiesConstants.UNION_HEAD +
        "\"" +
        ")"
    )
    public ResponseEntity<UnitTypeDTO> getUnitType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UnitType : {}", id);
        Optional<UnitTypeDTO> unitTypeDTO = unitTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(unitTypeDTO);
    }
}
