package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.service.SellerService;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.Seller}.
 */
@RestController
@RequestMapping("/api/sellers/v1")
public class SellerResource {

    private static final Logger LOG = LoggerFactory.getLogger(SellerResource.class);

    private static final String ENTITY_NAME = "shaffafserviceSeller";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SellerService sellerService;

    private final SellerRepository sellerRepository;

    public SellerResource(SellerService sellerService, SellerRepository sellerRepository) {
        this.sellerService = sellerService;
        this.sellerRepository = sellerRepository;
    }

    /**
     * {@code POST  /sellers/optimized} : Create a new seller using optimized native processing with enhanced security.
     * Only accessible to administrators.
     *
     * @param sellerDTO the sellerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sellerDTO, or with status {@code 400 (Bad Request)} if the seller has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/optimized")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SellerDTO> createSellerOptimized(@Valid @RequestBody SellerDTO sellerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Seller with optimization: {}", sellerDTO);
        if (sellerDTO.getId() != null) {
            throw new BadRequestAlertException("A new seller cannot already have an ID", ENTITY_NAME, "idexists");
        }

        // Apply rate limiting check
        if (isRateLimitExceeded()) {
            LOG.warn("Rate limit exceeded for seller creation");
            return ResponseEntity.status(429).build(); // Too Many Requests
        }

        SellerDTO result = sellerService.saveOptimized(sellerDTO);

        return ResponseEntity.created(new URI("/api/sellers/v1/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * Simple rate limiting check implementation.
     * In a production environment, replace with a proper rate limiting solution.
     *
     * @return true if rate limit is exceeded, false otherwise
     */
    private boolean isRateLimitExceeded() {
        // For demonstration - in production, use a proper rate limiter
        // This could check a counter in Redis or similar
        return false;
    }

    /**
     * {@code PUT  /sellers/secure-optimized/:id} : Updates a seller with optimized and secure processing.
     * Only accessible to administrators.
     *
     * @param id the id of the sellerDTO to update
     * @param sellerDTO the sellerDTO to update
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sellerDTO,
     * or with status {@code 400 (Bad Request)} if the sellerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the seller is not found,
     * or with status {@code 500 (Internal Server Error)} if the sellerDTO couldn't be updated.
     */
    @PutMapping("/secure-optimized/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SellerDTO> updateSellerSecureOptimized(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SellerDTO sellerDTO
    ) {
        LOG.debug("REST request to update Seller with secure optimized processing: {}, {}", id, sellerDTO);

        if (sellerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sellerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!sellerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        // Validate phone number format
        if (sellerDTO.getPhoneNumber() != null && !sellerDTO.getPhoneNumber().matches("^\\+[0-9]{11,12}$")) {
            throw new BadRequestAlertException("Phone number must be in format +923311234569", ENTITY_NAME, "invalidphone");
        }

        // Apply rate limiting check to prevent abuse
        if (isRateLimitExceeded()) {
            LOG.warn("Rate limit exceeded for seller update");
            return ResponseEntity.status(429).build(); // Too Many Requests
        }

        SellerDTO result = sellerService.updateSecureOptimized(sellerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sellers/:id} : Partial updates given fields of an existing seller, field will ignore if it is null
     *
     * @param id the id of the sellerDTO to save.
     * @param sellerDTO the sellerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sellerDTO,
     * or with status {@code 400 (Bad Request)} if the sellerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the sellerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sellerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SellerDTO> partialUpdateSeller(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SellerDTO sellerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Seller partially : {}, {}", id, sellerDTO);
        if (sellerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sellerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sellerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SellerDTO> result = sellerService.partialUpdate(sellerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sellerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sellers/optimized} : get all the sellers using an optimized native SQL query.
     * Only accessible to administrators.
     *
     * @param searchTerm the optional search term to filter results
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sellers in body.
     */
    @GetMapping("/optimized")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<SellerDTO>> getAllSellersOptimized(
        @RequestParam(required = false) String searchTerm,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of Sellers with optimization, search term: {}", searchTerm);
        Page<SellerDTO> page = sellerService.findAllOptimized(searchTerm, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sellers/:id} : get the "id" seller.
     *
     * @param id the id of the sellerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sellerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SellerDTO> getSeller(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Seller : {}", id);
        Optional<SellerDTO> sellerDTO = sellerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sellerDTO);
    }

    /**
     * {@code GET  /sellers/secure-optimized/:id} : Get a seller by ID using optimized and secure native query.
     *
     * @param id the id of the sellerDTO to retrieve
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sellerDTO,
     *         or with status {@code 404 (Not Found)}, or with status {@code 400 (Bad Request)} if the ID is invalid
     */
    @GetMapping("/optimized/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SellerDTO> getSellerSecureOptimized(@PathVariable Long id) {
        LOG.debug("REST request to get Seller with secure optimization : {}", id);

        // Validate ID to prevent potential attacks
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        // Apply rate limiting check to prevent abuse
        if (isRateLimitExceeded()) {
            LOG.warn("Rate limit exceeded for seller retrieval");
            return ResponseEntity.status(429).build(); // Too Many Requests
        }

        Optional<SellerDTO> sellerDTO = sellerService.findOneOptimized(id);
        return ResponseUtil.wrapOrNotFound(sellerDTO);
    }
}
