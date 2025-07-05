package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.BlockRepository;
import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.repository.UnionMemberRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.security.SecurityUtils;
import com.shaffaf.shaffafservice.service.BlockService;
import com.shaffaf.shaffafservice.service.dto.BlockDTO;
import com.shaffaf.shaffafservice.util.PhoneNumberUtil;
import com.shaffaf.shaffafservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.Block}.
 */
@RestController
@RequestMapping("/api/blocks/v1")
public class BlockResource {

    private static final Logger LOG = LoggerFactory.getLogger(BlockResource.class);

    private static final String ENTITY_NAME = "shaffafserviceBlock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BlockService blockService;

    private final BlockRepository blockRepository;

    private final SellerRepository sellerRepository;

    private final UnionMemberRepository unionMemberRepository;

    public BlockResource(
        BlockService blockService,
        BlockRepository blockRepository,
        SellerRepository sellerRepository,
        UnionMemberRepository unionMemberRepository
    ) {
        this.blockService = blockService;
        this.blockRepository = blockRepository;
        this.sellerRepository = sellerRepository;
        this.unionMemberRepository = unionMemberRepository;
    }

    /**
     * {@code POST  /blocks} : Create a new block.
     *
     * @param blockDTO the blockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new blockDTO, or with status {@code 400 (Bad Request)} if the block has already an ID.
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
    public ResponseEntity<BlockDTO> createBlock(@Valid @RequestBody BlockDTO blockDTO) throws URISyntaxException {
        LOG.debug("REST request to save Block : {}", blockDTO);
        if (blockDTO.getId() != null) {
            throw new BadRequestAlertException("A new block cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (blockDTO.getProject() == null || blockDTO.getProject().getId() == null) {
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

        // Check if the current user is authorized to create a block
        // Admins can create blocks for any project, no additional checks needed
        // If the user is a union head, they can create blocks for their own projects
        if (
            SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.UNION_HEAD) &&
            !SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        ) {
            if (!unionMemberRepository.isUnionMemberAssociatedWithProject(blockDTO.getProject().getId(), phoneNumber)) {
                throw new BadRequestAlertException(
                    "Union heads can only create blocks for their own projects",
                    ENTITY_NAME,
                    "unauthorized"
                );
            }
        }
        // If the Seller is the current user, they can only create blocks for their associate project
        if (
            SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER) &&
            !SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        ) {
            if (!sellerRepository.isSellerAssociatedWithProject(blockDTO.getProject().getId(), phoneNumber)) {
                throw new BadRequestAlertException("Sellers can only create blocks for their own projects", ENTITY_NAME, "unauthorized");
            }
        }

        blockDTO = blockService.save(blockDTO);
        return ResponseEntity.created(new URI("/api/blocks/v1" + blockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, blockDTO.getId().toString()))
            .body(blockDTO);
    }

    /**
     * {@code PUT  /blocks/:id} : Updates an existing block.
     *
     * @param id the id of the blockDTO to save.
     * @param blockDTO the blockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated blockDTO,
     * or with status {@code 400 (Bad Request)} if the blockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the blockDTO couldn't be updated.
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
    public ResponseEntity<BlockDTO> updateBlock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BlockDTO blockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Block : {}, {}", id, blockDTO);
        if (blockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, blockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!blockRepository.existsByIdNative(id)) {
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

        // Check if the current user is authorized to update a block
        // Admins can update blocks for any project, no additional checks needed
        // If the user is a union head, they can update blocks for their own projects
        if (
            SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.UNION_HEAD) &&
            !SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        ) {
            if (!unionMemberRepository.isUnionMemberAssociatedWithProject(blockDTO.getProject().getId(), phoneNumber)) {
                throw new BadRequestAlertException(
                    "Union heads can only update blocks for their own projects",
                    ENTITY_NAME,
                    "unauthorized"
                );
            }
        }
        // If the Seller is the current user, they can only update blocks for their associate project
        if (
            SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER) &&
            !SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        ) {
            if (!sellerRepository.isSellerAssociatedWithProject(blockDTO.getProject().getId(), phoneNumber)) {
                throw new BadRequestAlertException("Sellers can only update blocks for their own projects", ENTITY_NAME, "unauthorized");
            }
        }

        blockDTO = blockService.update(blockDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, blockDTO.getId().toString()))
            .body(blockDTO);
    }

    /**
     * {@code GET  /blocks/projects/{projectId}} : get all the blocks by ProjectId.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of blocks in body.
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
    public ResponseEntity<Page<BlockDTO>> getAllBlocks(@PathVariable("projectId") Long projectId, Pageable pageable) {
        LOG.debug("REST request to get a page of Blocks By ProjectId");
        Page<BlockDTO> page = blockService.findAllByProjectId(projectId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page);
    }

    /**
     * {@code GET  /blocks/:id} : get the "id" block.
     *
     * @param id the id of the blockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the blockDTO, or with status {@code 404 (Not Found)}.
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
    public ResponseEntity<BlockDTO> getBlock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Block : {}", id);
        Optional<BlockDTO> blockDTO = blockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(blockDTO);
    }
}
