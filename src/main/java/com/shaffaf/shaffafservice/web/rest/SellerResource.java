package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.service.SellerService;
import com.shaffaf.shaffafservice.service.dto.DashboardDataDTO;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import com.shaffaf.shaffafservice.util.PhoneNumberUtil;
import com.shaffaf.shaffafservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    @GetMapping("/get-many-sellers")
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
     * {@code GET  /sellers/secure-optimized/:id} : Get a seller by ID using optimized and secure native query.
     *
     * @param id the id of the sellerDTO to retrieve
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sellerDTO,
     *         or with status {@code 404 (Not Found)}, or with status {@code 400 (Bad Request)} if the ID is invalid
     */
    @GetMapping("/get-seller/{id}")
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

    /**
     * {@code POST  /native} : Create a new seller using native SQL query.
     *
     * @param sellerDTO the sellerDTO to create
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sellerDTO,
     *         or with status {@code 400 (Bad Request)} if the seller has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/create-seller")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SellerDTO> createSellerNative(@Valid @RequestBody SellerDTO sellerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Seller with native query: {}", sellerDTO);
        if (sellerDTO.getId() != null) {
            throw new BadRequestAlertException("A new seller cannot already have an ID", ENTITY_NAME, "idexists");
        }

        // Validate phone number format
        if (sellerDTO.getPhoneNumber() != null && !PhoneNumberUtil.isValidPakistaniMobile(sellerDTO.getPhoneNumber())) {
            throw new BadRequestAlertException(PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE, ENTITY_NAME, "invalidphone");
        }

        // Apply rate limiting check
        if (isRateLimitExceeded()) {
            LOG.warn("Rate limit exceeded for seller creation");
            return ResponseEntity.status(429).build(); // Too Many Requests
        }

        SellerDTO result = sellerService.saveWithNativeQuery(sellerDTO);

        return ResponseEntity.created(new URI("/api/sellers/v1/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /native/:id} : Updates an existing seller using native SQL query.
     *
     * @param sellerDTO the sellerDTO to update
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sellerDTO,
     *         or with status {@code 400 (Bad Request)} if the sellerDTO is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the sellerDTO couldn't be updated
     */
    @PutMapping("/update-seller")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SellerDTO> updateSellerNative(@Valid @RequestBody SellerDTO sellerDTO) {
        LOG.debug("REST request to update Seller with native query: {}, {}", sellerDTO.getId(), sellerDTO);
        Long id = sellerDTO.getId();
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
        if (sellerDTO.getPhoneNumber() != null && !PhoneNumberUtil.isValidPakistaniMobile(sellerDTO.getPhoneNumber())) {
            throw new BadRequestAlertException(PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE, ENTITY_NAME, "invalidphone");
        }

        // Apply rate limiting check to prevent abuse
        if (isRateLimitExceeded()) {
            LOG.warn("Rate limit exceeded for seller update");
            return ResponseEntity.status(429).build(); // Too Many Requests
        }

        SellerDTO result = sellerService.updateWithNativeQuery(sellerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /sellers/dashboard} : Get dashboard data with aggregated seller statistics and transactions.
     *
     * @param pageable the pagination information for transaction details.
     * @param sortBy the field to sort by.
     * @param sortDirection the sort direction (asc or desc).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the dashboard data in body.
     */
    @GetMapping("/sellers/dashboard")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<DashboardDataDTO> getDashboardData(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
        @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection
    ) {
        LOG.debug("REST request to get dashboard data");

        DashboardDataDTO dashboardData = sellerService.getDashboardData(pageable, sortBy, sortDirection);

        return ResponseEntity.ok()
            .headers(
                PaginationUtil.generatePaginationHttpHeaders(
                    ServletUriComponentsBuilder.fromCurrentRequest(),
                    new PageImpl<>(dashboardData.getTransactionDetails(), pageable, dashboardData.getTransactionDetails().size())
                )
            )
            .body(dashboardData);
    }

    /**
     * GET /sellers/debug-transaction-data : Debug transaction data mapping.
     *
     * @return the ResponseEntity with status 200 (OK) and debug data
     */
    @GetMapping("/debug-transaction-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDebugTransactionData() {
        LOG.debug("REST request to get debug transaction data");
        try {
            // Test the simple query first
            List<Object[]> simpleData = sellerRepository.getSimpleTransactionDetails();
            Long activeCount = sellerRepository.countActiveSellers();
            Long projectCount = sellerRepository.countActiveProjects();

            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("activeSellerCount", activeCount);
            debugInfo.put("activeProjectCount", projectCount);
            debugInfo.put("simpleDataCount", simpleData.size());
            if (projectCount > 0) {
                List<Object[]> sampleProjects = sellerRepository.getSampleProjectData();
                debugInfo.put("sampleProjectCount", sampleProjects.size());

                if (!sampleProjects.isEmpty()) {
                    Object[] firstProject = sampleProjects.get(0);
                    debugInfo.put("firstProjectData", Arrays.toString(firstProject));
                }

                // Test the actual project transactions query
                try {
                    Page<Object[]> actualProjectPage = sellerRepository.getActualProjectTransactions(
                        "amount",
                        "DESC",
                        org.springframework.data.domain.PageRequest.of(0, 3)
                    );
                    debugInfo.put("actualProjectTransactionsCount", actualProjectPage.getContent().size());

                    if (!actualProjectPage.isEmpty()) {
                        Object[] firstTransaction = actualProjectPage.getContent().get(0);
                        debugInfo.put("firstActualTransaction", Arrays.toString(firstTransaction));
                    }
                } catch (Exception e) {
                    debugInfo.put("actualProjectTransactionsError", e.getMessage());
                }
            }

            if (!simpleData.isEmpty()) {
                Object[] firstRow = simpleData.get(0);
                debugInfo.put("firstRowLength", firstRow.length);
                debugInfo.put("firstRowData", Arrays.toString(firstRow));

                List<String> fieldTypes = new ArrayList<>();
                for (Object field : firstRow) {
                    fieldTypes.add(field != null ? field.getClass().getSimpleName() : "null");
                }
                debugInfo.put("fieldTypes", fieldTypes);
            }

            return ResponseEntity.ok(debugInfo);
        } catch (Exception e) {
            LOG.error("Error getting debug transaction data: {}", e.getMessage(), e);
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
        }
    }
}
