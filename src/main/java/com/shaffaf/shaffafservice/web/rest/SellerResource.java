package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.service.SellerService;
import com.shaffaf.shaffafservice.service.dto.DashboardDataDTO;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import com.shaffaf.shaffafservice.service.dto.SellerPersonalDashboardDTO;
import com.shaffaf.shaffafservice.service.dto.SellerSalesDashboardDTO;
import com.shaffaf.shaffafservice.util.PhoneNumberUtil;
import com.shaffaf.shaffafservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // Rate limiting maps - in production, use Redis or dedicated rate limiting service
    private static final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private static final Map<String, Long> lastResetTime = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final long RATE_LIMIT_WINDOW_MS = 60000; // 1 minute

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SellerService sellerService;

    private final SellerRepository sellerRepository;

    public SellerResource(SellerService sellerService, SellerRepository sellerRepository) {
        this.sellerService = sellerService;
        this.sellerRepository = sellerRepository;
    }

    /**
     * Enhanced rate limiting implementation with user-based tracking.
     * In production, replace with Redis-based rate limiter like Spring Cloud Gateway or Bucket4j.
     *
     * @return true if rate limit is exceeded, false otherwise
     */
    private boolean isRateLimitExceeded() {
        String userKey = getCurrentUserLogin();
        if (userKey == null) {
            userKey = "anonymous";
        }

        long currentTime = System.currentTimeMillis();

        // Reset counter if window has expired
        Long lastReset = lastResetTime.get(userKey);
        if (lastReset == null || (currentTime - lastReset) > RATE_LIMIT_WINDOW_MS) {
            requestCounts.put(userKey, new AtomicInteger(0));
            lastResetTime.put(userKey, currentTime);
        }

        AtomicInteger count = requestCounts.computeIfAbsent(userKey, k -> new AtomicInteger(0));
        int currentCount = count.incrementAndGet();

        if (currentCount > MAX_REQUESTS_PER_MINUTE) {
            LOG.warn("Rate limit exceeded for user: {}, requests: {}", userKey, currentCount);
            return true;
        }

        return false;
    }

    /**
     * Get the current user's login from Spring Security context.
     */
    private String getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            return authentication.getName();
        }
        return null;
    }/**
     * Extract seller ID from the current authentication context.
     * This method tries multiple strategies to identify the seller.
     */

    private Long getSellerIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOG.warn("No authentication context found");
            return null;
        }

        LOG.debug(
            "Authentication type: {}, Principal type: {}, Name: {}",
            authentication.getClass().getSimpleName(),
            authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getSimpleName() : "null",
            authentication.getName()
        );

        // Strategy 1: Check if the principal is a JWT and extract seller ID from claims
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt) {
            org.springframework.security.oauth2.jwt.Jwt jwt = (org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal();
            LOG.debug("Processing JWT token with claims: {}", jwt.getClaims().keySet());

            // Check for explicit seller ID in JWT claims
            if (jwt.getClaims().containsKey("sellerId")) {
                Object sellerIdClaim = jwt.getClaims().get("sellerId");
                if (sellerIdClaim instanceof Number) {
                    Long sellerId = ((Number) sellerIdClaim).longValue();
                    LOG.debug("Found seller ID in JWT claims: {}", sellerId);
                    return sellerId;
                } else if (sellerIdClaim instanceof String) {
                    try {
                        Long sellerId = Long.parseLong((String) sellerIdClaim);
                        LOG.debug("Parsed seller ID from JWT claims: {}", sellerId);
                        return sellerId;
                    } catch (NumberFormatException e) {
                        LOG.warn("Invalid seller ID format in JWT: {}", sellerIdClaim);
                    }
                }
            }

            // Strategy 2: Look up seller by phone number from JWT claims (common in mobile apps)
            String phoneNumber = jwt.getClaimAsString("phone_number");
            if (phoneNumber != null) {
                LOG.debug("Found phone number in JWT claims: {}", phoneNumber);
                Long sellerId = lookupSellerByPhoneNumber(phoneNumber);
                if (sellerId != null) {
                    return sellerId;
                }
            }

            // Strategy 3: Try to parse the subject as seller ID
            String subject = jwt.getSubject();
            if (subject != null) {
                LOG.debug("Checking JWT subject: {}", subject);
                // First try phone number lookup if subject looks like a phone
                if (subject.matches("^[+]?[0-9]{10,15}$")) {
                    Long sellerId = lookupSellerByPhoneNumber(subject);
                    if (sellerId != null) {
                        return sellerId;
                    }
                }

                // Then try parsing as numeric seller ID
                try {
                    Long sellerId = Long.parseLong(subject);
                    LOG.debug("Parsed seller ID from JWT subject: {}", sellerId);
                    return sellerId;
                } catch (NumberFormatException e) {
                    LOG.debug("Subject '{}' is not a numeric seller ID", subject);
                }
            }
        }

        // Strategy 4: Fallback - use authentication name
        String name = authentication.getName();
        if (name != null) {
            LOG.debug("Processing authentication name: {}", name);

            // Strategy 4a: Try to look up by phone number if name looks like a phone number
            if (name.matches("^[+]?[0-9]{10,15}$")) {
                LOG.debug("Authentication name looks like a phone number: {}", name);
                Long sellerId = lookupSellerByPhoneNumber(name);
                if (sellerId != null) {
                    return sellerId;
                }

                // Only try parsing as numeric seller ID if phone lookup failed AND it's not a phone number format
                LOG.debug("Phone number lookup failed for: {}, not attempting numeric parsing for phone-like string", name);
            } else {
                // Strategy 4b: Try parsing as numeric seller ID only if it doesn't look like a phone
                try {
                    Long sellerId = Long.parseLong(name);
                    LOG.debug("Parsed seller ID from authentication name: {}", sellerId);
                    return sellerId;
                } catch (NumberFormatException e) {
                    LOG.debug("Authentication name '{}' is not a numeric seller ID", name);
                }
            }
        }

        LOG.warn("Unable to extract seller ID from authentication context");
        return null;
    }

    /**
     * Helper method to look up seller by phone number with various format attempts.
     */
    private Long lookupSellerByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        try {
            LOG.debug("Looking up seller by phone number: {}", phoneNumber);
            // Try with the original number first
            Optional<com.shaffaf.shaffafservice.domain.Seller> seller = sellerRepository.findByPhoneNumber(phoneNumber);
            if (seller.isPresent()) {
                Long sellerId = seller.orElseThrow().getId();
                LOG.debug("Found seller by phone number '{}': ID = {}", phoneNumber, sellerId);
                return sellerId;
            }

            // If not found and starts with +, try without the + prefix
            if (phoneNumber.startsWith("+")) {
                String nameWithoutPlus = phoneNumber.substring(1);
                LOG.debug("Trying phone lookup without +: {}", nameWithoutPlus);
                seller = sellerRepository.findByPhoneNumber(nameWithoutPlus);
                if (seller.isPresent()) {
                    Long sellerId = seller.orElseThrow().getId();
                    LOG.debug("Found seller by phone number without + '{}': ID = {}", nameWithoutPlus, sellerId);
                    return sellerId;
                }
            }

            // If not found and doesn't start with +, try with + prefix
            if (!phoneNumber.startsWith("+")) {
                String nameWithPlus = "+" + phoneNumber;
                LOG.debug("Trying phone lookup with +: {}", nameWithPlus);
                seller = sellerRepository.findByPhoneNumber(nameWithPlus);
                if (seller.isPresent()) {
                    Long sellerId = seller.orElseThrow().getId();
                    LOG.debug("Found seller by phone number with + '{}': ID = {}", nameWithPlus, sellerId);
                    return sellerId;
                }
            }

            LOG.debug("No seller found for phone number: {}", phoneNumber);
            return null;
        } catch (Exception ex) {
            LOG.warn("Error looking up seller by phone number '{}': {}", phoneNumber, ex.getMessage());
            return null;
        }
    }

    /**
     * Temporary method for testing - extracts seller ID with fallback to a test seller.
     * Remove this in production and use proper JWT claims.
     *//**
     * Validates and sanitizes sort parameters to prevent SQL injection.
     */
    private String validateSortParameter(String sortBy, Set<String> allowedFields, String defaultValue) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return defaultValue;
        }

        String sanitized = sortBy.trim().toLowerCase().replaceAll("[^a-z_]", "");
        if (allowedFields.contains(sanitized)) {
            return sanitized;
        }

        LOG.warn("Invalid sort parameter '{}', using default '{}'", sortBy, defaultValue);
        return defaultValue;
    }

    /**
     * Validates sort direction parameter.
     */
    private String validateSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            return "DESC";
        }

        String direction = sortDirection.trim().toUpperCase();
        return "ASC".equals(direction) ? "ASC" : "DESC";
    }

    /**
     * Logs audit information for sensitive operations.
     */
    private void logAuditEvent(String operation, String details) {
        String userLogin = getCurrentUserLogin();
        LOG.info("AUDIT: User '{}' performed '{}' - Details: {}", userLogin, operation, details);
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
     * This endpoint is secured and includes comprehensive input validation and rate limiting.
     *
     * @param pageable the pagination information for transaction details.
     * @param sortBy the field to sort by.
     * @param sortDirection the sort direction (asc or desc).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the dashboard data in body.
     */@GetMapping("/dashboard")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.SELLER + "\")")
    public ResponseEntity<DashboardDataDTO> getDashboardData(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
        @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection
    ) {
        LOG.debug("REST request to get dashboard data - sortBy: {}, sortDirection: {}", sortBy, sortDirection);

        // Apply rate limiting
        if (isRateLimitExceeded()) {
            LOG.warn("Rate limit exceeded for dashboard data request");
            return ResponseEntity.status(429).header("X-RateLimit-Retry-After", "60").build();
        }

        // Validate and sanitize input parameters
        Set<String> allowedSortFields = new HashSet<>(Arrays.asList("id", "sellername", "totalsalesamount", "totalprojects", "totalunits"));
        sortBy = validateSortParameter(sortBy, allowedSortFields, "id");
        sortDirection = validateSortDirection(sortDirection);

        // Validate pagination parameters
        if (pageable.getPageSize() > 1000) {
            throw new BadRequestAlertException("Page size too large (max 1000)", ENTITY_NAME, "pagesize.toolarge");
        }

        // Log audit event for sensitive data access
        logAuditEvent(
            "DASHBOARD_ACCESS",
            String.format("Page: %d, Size: %d, Sort: %s %s", pageable.getPageNumber(), pageable.getPageSize(), sortBy, sortDirection)
        );

        try {
            DashboardDataDTO dashboardData = sellerService.getDashboardData(pageable, sortBy, sortDirection);

            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                new PageImpl<>(dashboardData.getTransactionDetails(), pageable, dashboardData.getTransactionDetails().size())
            );

            // Add security headers
            headers.add("X-Content-Type-Options", "nosniff");
            headers.add("X-Frame-Options", "DENY");
            headers.add("Cache-Control", "private, no-cache, no-store, must-revalidate");

            return ResponseEntity.ok().headers(headers).body(dashboardData);
        } catch (Exception e) {
            LOG.error("Error retrieving dashboard data", e);
            throw new BadRequestAlertException("Failed to retrieve dashboard data", ENTITY_NAME, "dashboard.error");
        }
    }

    /**
     * {@code GET  /sellers/sales-dashboard} : Get seller sales dashboard with aggregated sales data per seller.
     * This endpoint is admin-only and includes comprehensive security measures.
     *
     * @param pageable the pagination information.
     * @param sortBy the field to sort by (sellerName, totalSalesAmount, totalProjects, totalUnits).
     * @param sortDirection the sort direction (ASC or DESC).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the seller sales dashboard data in body.
     */@GetMapping("/sales-dashboard")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SellerSalesDashboardDTO> getSellerSalesDashboard(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(value = "sortBy", defaultValue = "totalSalesAmount") String sortBy,
        @RequestParam(value = "sortDirection", defaultValue = "DESC") String sortDirection
    ) {
        LOG.debug("REST request to get seller sales dashboard data - sortBy: {}, sortDirection: {}", sortBy, sortDirection);

        // Apply rate limiting (stricter for admin endpoints)
        if (isRateLimitExceeded()) {
            LOG.warn("Rate limit exceeded for sales dashboard request");
            return ResponseEntity.status(429).header("X-RateLimit-Retry-After", "60").build();
        }

        // Validate and sanitize input parameters
        Set<String> allowedSortFields = new HashSet<>(Arrays.asList("sellername", "totalsalesamount", "totalprojects", "totalunits"));
        sortBy = validateSortParameter(sortBy, allowedSortFields, "totalsalesamount");
        sortDirection = validateSortDirection(sortDirection);

        // Validate pagination parameters
        if (pageable.getPageSize() > 500) {
            throw new BadRequestAlertException("Page size too large for sales dashboard (max 500)", ENTITY_NAME, "pagesize.toolarge.sales");
        }

        // Log audit event for sensitive admin data access
        logAuditEvent(
            "SALES_DASHBOARD_ACCESS",
            String.format("Page: %d, Size: %d, Sort: %s %s", pageable.getPageNumber(), pageable.getPageSize(), sortBy, sortDirection)
        );

        try {
            SellerSalesDashboardDTO salesDashboard = sellerService.getSellerSalesDashboard(pageable, sortBy, sortDirection);

            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                new PageImpl<>(salesDashboard.getSellerSalesData(), pageable, salesDashboard.getSellerSalesData().size())
            );

            // Add security headers
            headers.add("X-Content-Type-Options", "nosniff");
            headers.add("X-Frame-Options", "DENY");
            headers.add("Cache-Control", "private, no-cache, no-store, must-revalidate");
            headers.add("X-Robots-Tag", "noindex, nofollow, nosnippet, noarchive");

            return ResponseEntity.ok().headers(headers).body(salesDashboard);
        } catch (Exception e) {
            LOG.error("Error retrieving sales dashboard data", e);
            throw new BadRequestAlertException("Failed to retrieve sales dashboard data", ENTITY_NAME, "salesdashboard.error");
        }
    }

    /**
     * {@code GET  /sellers/my-dashboard} : Get personal dashboard for the currently logged-in seller.
     * This endpoint returns aggregated sales data and projects for the authenticated seller.
     *
     * @param pageable the pagination information for projects.
     * @param sortBy the field to sort projects by (projectName, amount, createdDate, status).
     * @param sortDirection the sort direction (ASC or DESC).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the personal dashboard data in body.
     */@GetMapping("/my-dashboard")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.SELLER + "\") or hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SellerPersonalDashboardDTO> getMyPersonalDashboard(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
        @RequestParam(value = "sortDirection", defaultValue = "DESC") String sortDirection
    ) {
        LOG.debug("REST request to get personal dashboard - sortBy: {}, sortDirection: {}", sortBy, sortDirection);

        // Apply rate limiting
        if (isRateLimitExceeded()) {
            LOG.warn("Rate limit exceeded for personal dashboard request");
            return ResponseEntity.status(429).header("X-RateLimit-Retry-After", "60").build();
        } // Get current authenticated user (seller)
        String currentUserLogin = getCurrentUserLogin();
        if (currentUserLogin == null) {
            LOG.warn("No authenticated user found for personal dashboard request");
            return ResponseEntity.status(401).build(); // Unauthorized
        } // Get seller ID from authentication context
        Long sellerId = getSellerIdFromAuthentication();
        if (sellerId == null) {
            LOG.warn("Unable to determine seller ID for user: {}", currentUserLogin);
            return ResponseEntity.status(403).header("X-Error", "Unable to determine seller identity").build();
        }

        // Validate and sanitize input parameters
        Set<String> allowedSortFields = new HashSet<>(Arrays.asList("projectname", "amount", "createddate", "status", "numberofunits"));
        sortBy = validateSortParameter(sortBy, allowedSortFields, "createddate");
        sortDirection = validateSortDirection(sortDirection);

        // Log audit event for personal data access
        logAuditEvent(
            "PERSONAL_DASHBOARD_ACCESS",
            String.format(
                "Seller ID: %d, Page: %d, Size: %d, Sort: %s %s",
                sellerId,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortBy,
                sortDirection
            )
        );

        try {
            SellerPersonalDashboardDTO personalDashboard = sellerService.getSellerPersonalDashboard(
                sellerId,
                pageable,
                sortBy,
                sortDirection
            );

            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                new PageImpl<>(personalDashboard.getProjects(), pageable, personalDashboard.getTotalElements())
            );

            // Add security headers
            headers.add("X-Content-Type-Options", "nosniff");
            headers.add("X-Frame-Options", "DENY");
            headers.add("Cache-Control", "private, no-cache, no-store, must-revalidate");

            return ResponseEntity.ok().headers(headers).body(personalDashboard);
        } catch (IllegalArgumentException e) {
            LOG.warn("Invalid request for personal dashboard: {}", e.getMessage());
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "personaldashboard.invalid");
        } catch (RuntimeException e) {
            LOG.error("Runtime error retrieving personal dashboard for seller {}: {}", sellerId, e.getMessage(), e);
            // For debugging, include the actual error message
            throw new BadRequestAlertException("Runtime error: " + e.getMessage(), ENTITY_NAME, "personaldashboard.runtime");
        } catch (Exception e) {
            LOG.error("Error retrieving personal dashboard for seller {}: {}", sellerId, e.getMessage(), e);
            // For debugging, include the actual error message
            throw new BadRequestAlertException("Service error: " + e.getMessage(), ENTITY_NAME, "personaldashboard.error");
        }
    }

    /**
     * {@code GET  /sellers/{sellerId}/dashboard} : Get personal dashboard for a specific seller (admin only).
     * This endpoint allows administrators to view any seller's personal dashboard.
     *
     * @param sellerId the ID of the seller to get dashboard for.
     * @param pageable the pagination information for projects.
     * @param sortBy the field to sort projects by.
     * @param sortDirection the sort direction (ASC or DESC).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the personal dashboard data in body.
     */@GetMapping("/{sellerId}/dashboard")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SellerPersonalDashboardDTO> getSellerPersonalDashboard(
        @PathVariable Long sellerId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
        @RequestParam(value = "sortDirection", defaultValue = "DESC") String sortDirection
    ) {
        LOG.debug("REST request to get personal dashboard for seller {} - sortBy: {}, sortDirection: {}", sellerId, sortBy, sortDirection);

        // Apply rate limiting (stricter for admin endpoints)
        if (isRateLimitExceeded()) {
            LOG.warn("Rate limit exceeded for admin personal dashboard request");
            return ResponseEntity.status(429).header("X-RateLimit-Retry-After", "60").build();
        }

        // Validate seller ID
        if (sellerId == null || sellerId <= 0) {
            throw new BadRequestAlertException("Valid seller ID is required", ENTITY_NAME, "sellerid.invalid");
        }

        // Validate and sanitize input parameters
        Set<String> allowedSortFields = new HashSet<>(Arrays.asList("projectname", "amount", "createddate", "status", "numberofunits"));
        sortBy = validateSortParameter(sortBy, allowedSortFields, "createddate");
        sortDirection = validateSortDirection(sortDirection);

        // Validate pagination parameters
        if (pageable.getPageSize() > 100) {
            throw new BadRequestAlertException(
                "Page size too large for personal dashboard (max 100)",
                ENTITY_NAME,
                "pagesize.toolarge.personal"
            );
        }

        // Log audit event for admin access to seller data
        logAuditEvent(
            "ADMIN_SELLER_DASHBOARD_ACCESS",
            String.format(
                "Target Seller ID: %d, Page: %d, Size: %d, Sort: %s %s",
                sellerId,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortBy,
                sortDirection
            )
        );

        try {
            SellerPersonalDashboardDTO personalDashboard = sellerService.getSellerPersonalDashboard(
                sellerId,
                pageable,
                sortBy,
                sortDirection
            );

            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                new PageImpl<>(personalDashboard.getProjects(), pageable, personalDashboard.getTotalElements())
            );

            // Add security headers
            headers.add("X-Content-Type-Options", "nosniff");
            headers.add("X-Frame-Options", "DENY");
            headers.add("Cache-Control", "private, no-cache, no-store, must-revalidate");
            headers.add("X-Robots-Tag", "noindex, nofollow, nosnippet, noarchive");

            return ResponseEntity.ok().headers(headers).body(personalDashboard);
        } catch (IllegalArgumentException e) {
            LOG.warn("Invalid request for seller {} personal dashboard: {}", sellerId, e.getMessage());
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "personaldashboard.invalid");
        } catch (Exception e) {
            LOG.error("Error retrieving personal dashboard for seller {}: {}", sellerId, e.getMessage(), e);
            throw new BadRequestAlertException("Failed to retrieve personal dashboard", ENTITY_NAME, "personaldashboard.error");
        }
    }
}
