package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.enumeration.Status;
import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.security.SqlSecurityUtil;
import com.shaffaf.shaffafservice.service.SellerService;
import com.shaffaf.shaffafservice.service.dto.DashboardDataDTO;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import com.shaffaf.shaffafservice.service.dto.SellerPersonalDashboardDTO;
import com.shaffaf.shaffafservice.service.dto.SellerProjectDTO;
import com.shaffaf.shaffafservice.service.dto.SellerSalesAggregateDTO;
import com.shaffaf.shaffafservice.service.dto.SellerSalesDashboardDTO;
import com.shaffaf.shaffafservice.service.dto.TransactionDetailDTO;
import com.shaffaf.shaffafservice.service.mapper.SellerMapper;
import com.shaffaf.shaffafservice.util.PhoneNumberUtil;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.Seller}.
 */
@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    private static final Logger LOG = LoggerFactory.getLogger(SellerServiceImpl.class);

    private final SellerRepository sellerRepository;

    private final SellerMapper sellerMapper;

    public SellerServiceImpl(SellerRepository sellerRepository, SellerMapper sellerMapper) {
        this.sellerRepository = sellerRepository;
        this.sellerMapper = sellerMapper;
    }

    @Override
    public Optional<SellerDTO> partialUpdate(SellerDTO sellerDTO) {
        LOG.debug("Request to partially update Seller : {}", sellerDTO);

        return sellerRepository
            .findById(sellerDTO.getId())
            .map(existingSeller -> {
                sellerMapper.partialUpdate(existingSeller, sellerDTO);

                return existingSeller;
            })
            .map(sellerRepository::save)
            .map(sellerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Seller : {}", id);
        sellerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SellerDTO> findAllOptimized(String searchTerm, Pageable pageable) {
        LOG.debug("Request to get all Sellers with optimization, search term: {}", searchTerm);
        return sellerRepository.findAllWithNativeQuery(searchTerm, pageable).map(sellerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SellerDTO> findOneOptimized(Long id) {
        LOG.debug("Request to get Seller with optimization : {}", id);

        // Input validation to prevent SQL injection
        if (id == null || id <= 0) {
            LOG.warn("Invalid seller ID: {}", id);
            return Optional.empty();
        }

        return sellerRepository.findByIdOptimized(id).map(sellerMapper::toDto);
    }

    @Override
    @Transactional
    public SellerDTO saveWithNativeQuery(SellerDTO sellerDTO) {
        LOG.debug("Request to save Seller with native query: {}", sellerDTO);

        // Sanitize inputs to prevent injection attacks
        String firstName = sanitizeInput(sellerDTO.getFirstName());
        String lastName = sanitizeInput(sellerDTO.getLastName());
        String email = sanitizeInput(sellerDTO.getEmail());
        String phoneNumber = sanitizeInput(sellerDTO.getPhoneNumber());

        // Always set status to ACTIVE when creating a new seller
        Status status = Status.ACTIVE;

        // Set audit fields
        sellerDTO.setCreatedDate(java.time.Instant.now());
        String createdBy = "system"; // This should be replaced with the current authenticated user

        // Execute native SQL query with PostgreSQL's RETURNING clause to get the ID in one step
        Long newId = sellerRepository.saveWithNativeQueryReturningId(
            firstName,
            lastName,
            email,
            phoneNumber,
            status.toString(),
            createdBy,
            sellerDTO.getCreatedDate()
        );

        if (newId == null) {
            LOG.error("Failed to create new seller, no ID returned");
            throw new RuntimeException("Failed to create seller - database did not return an ID");
        }

        // Fetch the complete seller entity to return
        return sellerMapper.toDto(
            sellerRepository
                .findByIdOptimized(newId)
                .orElseThrow(() -> {
                    LOG.error("Failed to retrieve newly created seller with ID: {}", newId);
                    return new RuntimeException("Failed to retrieve newly created seller");
                })
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDataDTO getDashboardData(Pageable pageable, String sortBy, String sortDirection) {
        LOG.debug("Request to get dashboard data with pagination and sorting");

        // Validate pagination parameters to prevent abuse
        if (!SqlSecurityUtil.isValidPagination(pageable.getPageNumber(), pageable.getPageSize(), 1000)) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        // Validate and sanitize sort parameters with enhanced security
        String validatedSortBy = validateSortBySecure(sortBy);
        String validatedSortDirection = validateSortDirection(sortDirection);
        try {
            // Get aggregated statistics using individual queries to avoid array parsing issues
            BigDecimal totalPayment = sellerRepository.getTotalPayment();
            Long totalSellers = sellerRepository.getTotalSellers();
            BigDecimal totalSales = totalPayment; // Total sales is same as total payment in this context
            Long newSellers = sellerRepository.getNewSellers();
            BigDecimal totalDues = sellerRepository.getTotalDues();
            // Debug logging
            LOG.debug(
                "Dashboard statistics - Payment: {}, Sellers: {}, Sales: {}, New: {}, Dues: {}",
                totalPayment,
                totalSellers,
                totalSales,
                newSellers,
                totalDues
            ); // Additional debugging - check if sellers and projects exist
            Long activeSellerCount = sellerRepository.countActiveSellers();
            Long activeProjectCount = sellerRepository.countActiveProjects();
            LOG.debug("Active sellers count: {}, Active projects count: {}", activeSellerCount, activeProjectCount);

            // Try to get some simple debug data to test basic query functionality
            List<Object[]> simpleDebugData = sellerRepository.getSimpleTransactionDetails();
            LOG.debug("Simple debug data size: {}", simpleDebugData.size());

            if (activeProjectCount > 0) {
                List<Object[]> sampleProjects = sellerRepository.getSampleProjectData();
                LOG.debug("Sample project data: {}", sampleProjects.size());
                sampleProjects.forEach(project -> LOG.debug("Project: {}", java.util.Arrays.toString(project)));
            }

            if (!simpleDebugData.isEmpty()) {
                Object[] firstRow = simpleDebugData.get(0);
                LOG.debug("First debug row: length={}, data={}", firstRow.length, java.util.Arrays.toString(firstRow));
            } // Get transaction details with pagination and sorting - prioritize real project data
            Page<Object[]> transactionPage;

            // Check if there are any actual projects first
            if (activeProjectCount > 0) {
                LOG.debug("Using actual project transactions query (found {} projects)", activeProjectCount);
                transactionPage = sellerRepository.getActualProjectTransactions(validatedSortBy, validatedSortDirection, pageable);
            } else {
                LOG.debug("No projects found, using combined query with sellers");
                transactionPage = sellerRepository.getTransactionDetailsWithRealProjects(validatedSortBy, validatedSortDirection, pageable);
            }

            List<TransactionDetailDTO> transactionDetails;

            LOG.debug(
                "Transaction page - isEmpty: {}, content size: {}, total elements: {}",
                transactionPage.isEmpty(),
                transactionPage.getContent().size(),
                transactionPage.getTotalElements()
            );
            // If no transaction details found, use simple debug data to verify sellers exist
            if (transactionPage.isEmpty()) {
                LOG.warn("No transaction details found, using simple debug query");
                List<Object[]> debugData = sellerRepository.getSimpleTransactionDetails();
                LOG.debug("Debug data size: {}", debugData.size());

                // Use only the debug data for now to test
                transactionDetails = debugData
                    .stream()
                    .map(row -> {
                        LOG.debug("Processing debug row: {}", java.util.Arrays.toString(row));
                        return mapToTransactionDetailDTO(row);
                    })
                    .collect(Collectors.toList());

                LOG.debug("Debug transaction details count: {}", transactionDetails.size());
            } else {
                // Use the actual transaction data from the main query
                LOG.info("Found {} transaction records from main query", transactionPage.getContent().size());

                transactionDetails = transactionPage
                    .getContent()
                    .stream()
                    .map(row -> {
                        LOG.debug("Processing real transaction row: {}", java.util.Arrays.toString(row));
                        return mapToTransactionDetailDTO(row);
                    })
                    .collect(Collectors.toList());

                LOG.debug("Real transaction details count: {}", transactionDetails.size());
            }

            // Log the first few transaction details for debugging
            transactionDetails.stream().limit(3).forEach(dto -> LOG.debug("Final DTO: {}", dto));

            return new DashboardDataDTO(
                totalPayment,
                totalSellers,
                totalSales,
                newSellers,
                totalDues,
                transactionDetails,
                transactionPage.getTotalElements(),
                pageable.getPageNumber(),
                transactionPage.getTotalPages()
            );
        } catch (Exception e) {
            LOG.error("Error retrieving dashboard data: {}", e.getMessage(), e);
            // Return empty dashboard data in case of error
            return createEmptyDashboard(pageable);
        }
    }

    @Override
    @Transactional
    public SellerDTO updateWithNativeQuery(SellerDTO sellerDTO) {
        LOG.debug("Request to update Seller with native query: {}", sellerDTO);

        // Validate input
        if (sellerDTO.getId() == null || sellerDTO.getId() <= 0) {
            throw new IllegalArgumentException("Valid seller ID is required for update");
        }

        // Sanitize inputs
        String firstName = sanitizeInput(sellerDTO.getFirstName());
        String lastName = sanitizeInput(sellerDTO.getLastName());
        String email = sanitizeInput(sellerDTO.getEmail());
        String phoneNumber = sanitizeInput(sellerDTO.getPhoneNumber());

        // Validate phone number format
        if (phoneNumber != null && !PhoneNumberUtil.isValidPakistaniMobile(phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        // Set audit fields
        sellerDTO.setLastModifiedDate(Instant.now());
        String lastModifiedBy = "system"; // Should be replaced with current authenticated user

        // Execute native update query
        int rowsAffected = sellerRepository.updateWithNativeQuery(
            sellerDTO.getId(),
            firstName,
            lastName,
            email,
            phoneNumber,
            sellerDTO.getStatus() != null ? sellerDTO.getStatus().toString() : Status.ACTIVE.toString(),
            lastModifiedBy,
            sellerDTO.getLastModifiedDate()
        );

        if (rowsAffected == 0) {
            throw new IllegalArgumentException("Seller not found or could not be updated");
        }

        // Retrieve and return the updated seller
        return sellerRepository
            .findByIdOptimized(sellerDTO.getId())
            .map(sellerMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Updated seller not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public SellerSalesDashboardDTO getSellerSalesDashboard(Pageable pageable, String sortBy, String sortDirection) {
        LOG.debug("Request to get seller sales dashboard with pagination and sorting");

        // Validate pagination parameters to prevent abuse (stricter for admin endpoints)
        if (!SqlSecurityUtil.isValidPagination(pageable.getPageNumber(), pageable.getPageSize(), 500)) {
            throw new IllegalArgumentException("Invalid pagination parameters for sales dashboard");
        }

        // Validate and sanitize sort parameters
        String validatedSortBy = validateSellerSalesSortBy(sortBy);
        String validatedSortDirection = validateSortDirection(sortDirection);

        try {
            // Get aggregated statistics using individual queries (reuse existing dashboard queries)
            BigDecimal totalPayment = sellerRepository.getTotalPayment();
            Long totalSellers = sellerRepository.getTotalSellers();
            BigDecimal totalSales = totalPayment; // Total sales is same as total payment in this context
            Long newSellers = sellerRepository.getNewSellers();
            BigDecimal totalDues = sellerRepository.getTotalDues();

            // Debug logging
            LOG.debug(
                "Dashboard statistics - Payment: {}, Sellers: {}, Sales: {}, New: {}, Dues: {}",
                totalPayment,
                totalSellers,
                totalSales,
                newSellers,
                totalDues
            );

            // Get seller sales aggregates with pagination and sorting
            Page<Object[]> sellerSalesPage = sellerRepository.getSellerSalesAggregates(validatedSortBy, validatedSortDirection, pageable);

            List<SellerSalesAggregateDTO> sellerSalesData = sellerSalesPage
                .getContent()
                .stream()
                .map(this::mapToSellerSalesAggregateDTO)
                .collect(Collectors.toList());

            LOG.debug("Seller sales data count: {}", sellerSalesData.size());

            // Log the first few seller sales for debugging
            sellerSalesData.stream().limit(3).forEach(dto -> LOG.debug("Seller Sales DTO: {}", dto));

            return new SellerSalesDashboardDTO(
                totalPayment,
                totalSellers,
                totalSales,
                newSellers,
                totalDues,
                sellerSalesData,
                sellerSalesPage.getTotalElements(),
                pageable.getPageNumber(),
                sellerSalesPage.getTotalPages()
            );
        } catch (Exception e) {
            LOG.error("Error retrieving seller sales dashboard: {}", e.getMessage(), e);
            // Return empty dashboard in case of error
            return createEmptySellerSalesDashboard(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SellerPersonalDashboardDTO getSellerPersonalDashboard(Long sellerId, Pageable pageable, String sortBy, String sortDirection) {
        LOG.debug("Request to get personal dashboard for seller ID: {}", sellerId);

        // Validate seller ID
        if (sellerId == null || sellerId <= 0) {
            throw new IllegalArgumentException("Valid seller ID is required");
        }

        // Validate pagination parameters
        if (!SqlSecurityUtil.isValidPagination(pageable.getPageNumber(), pageable.getPageSize(), 100)) {
            throw new IllegalArgumentException("Invalid pagination parameters for personal dashboard");
        }

        // Validate and sanitize sort parameters
        String validatedSortBy = validateProjectSortBy(sortBy);
        String validatedSortDirection = validateSortDirection(sortDirection);

        try {
            // First verify seller exists and is active
            Boolean isSellerActive = sellerRepository.isSellerActiveById(sellerId);
            if (isSellerActive == null || !isSellerActive) {
                throw new IllegalArgumentException("Seller not found or inactive");
            } // Get seller's personal statistics
            Optional<Object[]> sellerStatsOpt = sellerRepository.getSellerPersonalStatistics(sellerId);
            if (!sellerStatsOpt.isPresent()) {
                throw new IllegalArgumentException("Unable to retrieve seller statistics");
            }
            Object[] sellerStats = sellerStatsOpt.orElseThrow();

            // Check if the result is a nested array structure and extract the actual data
            if (sellerStats.length == 1 && sellerStats[0] instanceof Object[]) {
                // Handle nested array structure
                sellerStats = (Object[]) sellerStats[0];
                LOG.debug("Extracted nested array structure with {} elements", sellerStats.length);
            }

            LOG.debug("Seller statistics array length: {}, contents: {}", sellerStats.length, java.util.Arrays.toString(sellerStats));

            // Validate array length
            if (sellerStats.length < 8) {
                throw new IllegalArgumentException("Invalid seller statistics format - expected 8 fields, got " + sellerStats.length);
            }

            // Parse seller statistics with proper error handling
            String sellerName;
            String phoneNumber;
            String email;
            Long totalProjects;
            BigDecimal totalPayment;
            BigDecimal totalDues;
            Long newProjects;

            try {
                sellerName = sellerStats[1] != null ? sellerStats[1].toString().trim() : "Unknown Seller";
                phoneNumber = sellerStats[2] != null ? sellerStats[2].toString().trim() : "";
                email = sellerStats[3] != null ? sellerStats[3].toString().trim() : "";
                totalProjects = safeParseLong(sellerStats[4]);
                totalPayment = safeParseBigDecimal(sellerStats[5]);
                totalDues = safeParseBigDecimal(sellerStats[6]);
                newProjects = safeParseLong(sellerStats[7]);
                LOG.debug(
                    "Parsed statistics - Name: {}, Projects: {}, Payment: {}, Dues: {}, NewProjects: {}",
                    sellerName,
                    totalProjects,
                    totalPayment,
                    totalDues,
                    newProjects
                );
            } catch (ArrayIndexOutOfBoundsException e) {
                LOG.error("Array index error parsing seller statistics: {}", e.getMessage());
                throw new IllegalArgumentException("Failed to parse seller statistics - array structure mismatch: " + e.getMessage());
            }

            // Get count of sellers referred by this seller (for new sellers metric)
            Long newSellers = sellerRepository.getNewSellersReferredBy(sellerId);

            // Total sales is same as total payment in this context
            BigDecimal totalSales = totalPayment;

            LOG.debug(
                "Seller {} statistics - Projects: {}, Payment: {}, Sales: {}, New Sellers: {}, Dues: {}",
                sellerName,
                totalProjects,
                totalPayment,
                totalSales,
                newSellers,
                totalDues
            );

            // Get seller's projects with pagination
            Page<Object[]> projectsPage = sellerRepository.getSellerProjects(sellerId, validatedSortBy, validatedSortDirection, pageable);

            List<SellerProjectDTO> projects = projectsPage
                .getContent()
                .stream()
                .map(this::mapToSellerProjectDTO)
                .collect(Collectors.toList());

            LOG.debug("Retrieved {} projects for seller {}", projects.size(), sellerName);

            return new SellerPersonalDashboardDTO(
                sellerName,
                phoneNumber,
                email,
                totalPayment,
                totalProjects,
                totalSales,
                newSellers != null ? newSellers : 0L,
                totalDues,
                projects,
                projectsPage.getTotalElements(),
                pageable.getPageNumber(),
                projectsPage.getTotalPages()
            );
        } catch (Exception e) {
            LOG.error("Error retrieving personal dashboard for seller ID {}: {}", sellerId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve personal dashboard: " + e.getMessage());
        }
    }

    /**
     * Creates an empty dashboard data object for error cases.
     */
    private DashboardDataDTO createEmptyDashboard(Pageable pageable) {
        return new DashboardDataDTO(BigDecimal.ZERO, 0L, BigDecimal.ZERO, 0L, BigDecimal.ZERO, List.of(), 0L, pageable.getPageNumber(), 0);
    }

    /**
     * Creates an empty seller sales dashboard object for error cases.
     */
    private SellerSalesDashboardDTO createEmptySellerSalesDashboard(Pageable pageable) {
        return new SellerSalesDashboardDTO(
            BigDecimal.ZERO,
            0L,
            BigDecimal.ZERO,
            0L,
            BigDecimal.ZERO,
            List.of(),
            0L,
            pageable.getPageNumber(),
            0
        );
    }

    /**
     * Validates and sanitizes the sort field parameter for seller sales dashboard.
     */
    private String validateSellerSalesSortBy(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "totalSalesAmount"; // Default sort
        }

        String trimmed = sortBy.trim().toLowerCase();
        switch (trimmed) {
            case "sellername":
                return "sellerName";
            case "totalsalesamount":
                return "totalSalesAmount";
            case "totalprojects":
                return "totalProjects";
            case "totalunits":
                return "totalUnits";
            default:
                LOG.warn("Invalid sort field '{}', using default 'totalSalesAmount'", sortBy);
                return "totalSalesAmount";
        }
    }

    /**
     * Validates and sanitizes the sort direction parameter.
     */
    private String validateSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            return "DESC"; // default sort direction
        }

        String direction = sortDirection.toUpperCase().trim();
        return "ASC".equals(direction) ? "ASC" : "DESC";
    }

    /**
     * Validates sort parameters for project listing.
     */
    private String validateProjectSortBy(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "createdDate"; // Default sort by creation date
        }

        String sanitized = sortBy.trim().toLowerCase();

        // Check for SQL injection
        if (SqlSecurityUtil.containsSqlInjection(sanitized)) {
            LOG.warn("SQL injection attempt in project sort parameter: {}", sortBy);
            return "createdDate";
        }

        // Validate safe sort field
        if (!SqlSecurityUtil.isSafeSortField(sanitized)) {
            LOG.warn("Unsafe project sort field: {}", sortBy);
            return "createdDate";
        }

        // Map to allowed project sort fields
        switch (sanitized) {
            case "projectname":
            case "project_name":
                return "projectName";
            case "amount":
                return "amount";
            case "createddate":
            case "created_date":
                return "createdDate";
            case "status":
                return "status";
            case "numberofunits":
            case "number_of_units":
                return "numberOfUnits";
            default:
                LOG.warn("Invalid project sort field '{}', using default", sortBy);
                return "createdDate";
        }
    }

    /**
     * Maps Object array from native query to TransactionDetailDTO.
     */private TransactionDetailDTO mapToTransactionDetailDTO(Object[] row) {
        try {
            if (row == null) {
                LOG.warn("Row data is null");
                return createErrorTransactionDetail("Row is null");
            }

            LOG.debug("Mapping row with {} elements: {}", row.length, java.util.Arrays.toString(row));

            // For debugging, let's be very explicit about what we expect
            if (row.length < 4) {
                LOG.warn("Row has insufficient data ({} elements), expected at least 4", row.length);
                return createErrorTransactionDetail("Insufficient data in row: " + row.length + " elements");
            }
            // Start with basic mapping and add safety checks
            Long id = null;
            String sellerName = "Unknown Seller";
            String phoneNumber = "";
            String projectName = "No Project";
            BigDecimal amount = BigDecimal.ZERO; // Changed from 1000 to 0 for real data
            Integer numberOfUnits = 0; // Changed from 1 to 0 for real data
            BigDecimal feesPerUnit = BigDecimal.ZERO; // Changed from 1000 to 0 for real data
            Instant transactionDate = Instant.now();
            String status = "ACTIVE";
            String description = "Transaction detail";

            try {
                // Map each field carefully
                if (row.length > 0 && row[0] != null) {
                    id = safeParseLong(row[0]);
                }

                if (row.length > 1 && row[1] != null) {
                    sellerName = row[1].toString().trim();
                    if (sellerName.isEmpty()) sellerName = "Unknown Seller";
                }

                if (row.length > 2 && row[2] != null) {
                    phoneNumber = row[2].toString().trim();
                }

                if (row.length > 3 && row[3] != null) {
                    projectName = row[3].toString().trim();
                    if (projectName.isEmpty()) projectName = "No Project";
                }

                if (row.length > 4) {
                    amount = safeParseBigDecimal(row[4]);
                }

                if (row.length > 5) {
                    numberOfUnits = safeParseInteger(row[5]);
                }

                if (row.length > 6) {
                    feesPerUnit = safeParseBigDecimal(row[6]);
                }

                if (row.length > 7 && row[7] != null) {
                    transactionDate = parseTimestamp(row[7]);
                }

                if (row.length > 8 && row[8] != null) {
                    status = row[8].toString().trim();
                    if (status.isEmpty()) status = "ACTIVE";
                }

                if (row.length > 9 && row[9] != null) {
                    description = row[9].toString().trim();
                    if (description.isEmpty()) description = "Transaction detail";
                }
            } catch (Exception e) {
                LOG.warn("Error parsing individual fields from row: {}", e.getMessage());
                // Continue with default values
            }
            TransactionDetailDTO dto = new TransactionDetailDTO(
                id,
                sellerName,
                phoneNumber,
                projectName,
                amount,
                numberOfUnits,
                feesPerUnit,
                transactionDate,
                status,
                description
            );

            // Log whether this is real project data or just seller data
            boolean hasRealProjectData = amount.compareTo(BigDecimal.ZERO) > 0 && numberOfUnits > 0;
            LOG.debug("Successfully mapped DTO: {} [Real project data: {}]", dto, hasRealProjectData);
            return dto;
        } catch (Exception e) {
            LOG.error(
                "Error mapping transaction detail from row: {}, error: {}",
                row != null ? java.util.Arrays.toString(row) : "null",
                e.getMessage(),
                e
            );
            return createErrorTransactionDetail("Mapping error: " + e.getMessage());
        }
    }

    /**
     * Maps a database row to SellerSalesAggregateDTO.
     */
    private SellerSalesAggregateDTO mapToSellerSalesAggregateDTO(Object[] row) {
        try {
            if (row == null) {
                LOG.warn("Row data is null for seller sales aggregate");
                return createErrorSellerSalesAggregate("Row is null");
            }

            LOG.debug("Mapping seller sales row with {} elements: {}", row.length, java.util.Arrays.toString(row));

            if (row.length < 11) {
                LOG.warn("Row has insufficient data ({} elements), expected at least 11", row.length);
                return createErrorSellerSalesAggregate("Insufficient data in row: " + row.length + " elements");
            }

            // Extract fields with careful parsing
            Long sellerId = row[0] != null ? safeParseLong(row[0]) : null;
            String sellerName = row[1] != null ? row[1].toString().trim() : "Unknown Seller";
            String sellerPhoneNumber = row[2] != null ? row[2].toString().trim() : "";
            String sellerEmail = row[3] != null ? row[3].toString().trim() : "";
            Integer totalProjects = row[4] != null ? safeParseInteger(row[4]) : 0;
            Integer totalUnits = row[5] != null ? safeParseInteger(row[5]) : 0;
            BigDecimal totalSalesAmount = safeParseBigDecimal(row[6]);
            BigDecimal averageFeesPerUnit = safeParseBigDecimal(row[7]);
            BigDecimal highestProjectAmount = safeParseBigDecimal(row[8]);
            BigDecimal lowestProjectAmount = safeParseBigDecimal(row[9]);
            String mostRecentProjectName = row[10] != null ? row[10].toString().trim() : "No Projects";
            Instant lastProjectDate = row[11] != null ? parseTimestamp(row[11]) : Instant.now();
            String sellerStatus = row[12] != null ? row[12].toString().trim() : "ACTIVE";

            SellerSalesAggregateDTO dto = new SellerSalesAggregateDTO(
                sellerId,
                sellerName,
                sellerPhoneNumber,
                sellerEmail,
                totalProjects,
                totalUnits,
                totalSalesAmount,
                averageFeesPerUnit,
                highestProjectAmount,
                lowestProjectAmount,
                mostRecentProjectName,
                lastProjectDate,
                sellerStatus
            );

            LOG.debug("Successfully mapped seller sales DTO: {}", dto);
            return dto;
        } catch (Exception e) {
            LOG.error(
                "Error mapping seller sales aggregate from row: {}, error: {}",
                row != null ? java.util.Arrays.toString(row) : "null",
                e.getMessage(),
                e
            );
            return createErrorSellerSalesAggregate("Mapping error: " + e.getMessage());
        }
    }

    /**
     * Creates an error seller sales aggregate with specific error message.
     */
    private SellerSalesAggregateDTO createErrorSellerSalesAggregate(String errorMessage) {
        return new SellerSalesAggregateDTO(
            null,
            "Error Loading Seller",
            "",
            "",
            0,
            0,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            "Error",
            Instant.now(),
            "ERROR"
        );
    }

    /**
     * Safely parse timestamp from various date/time types.
     */
    private Instant parseTimestamp(Object dateObj) {
        try {
            if (dateObj instanceof Instant) {
                return (Instant) dateObj;
            } else if (dateObj instanceof java.sql.Timestamp) {
                return ((java.sql.Timestamp) dateObj).toInstant();
            } else if (dateObj instanceof java.time.LocalDateTime) {
                return ((java.time.LocalDateTime) dateObj).atZone(java.time.ZoneId.systemDefault()).toInstant();
            } else if (dateObj instanceof java.util.Date) {
                return ((java.util.Date) dateObj).toInstant();
            } else {
                LOG.debug("Unexpected date type: {}, using current time", dateObj.getClass().getSimpleName());
                return Instant.now();
            }
        } catch (Exception e) {
            LOG.warn("Error parsing timestamp from {}: {}", dateObj, e.getMessage());
            return Instant.now();
        }
    }

    /**
     * Creates an error transaction detail with specific error message.
     */
    private TransactionDetailDTO createErrorTransactionDetail(String errorMessage) {
        return new TransactionDetailDTO(
            null,
            "Error Loading Seller",
            "",
            "Error Loading Project",
            BigDecimal.ZERO,
            0,
            BigDecimal.ZERO,
            Instant.now(),
            "ERROR",
            errorMessage
        );
    }/**
     * Safely parse BigDecimal from database result, handling various numeric formats.
     */

    private BigDecimal safeParseBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        try {
            // Handle case where an array might be passed instead of a single value
            if (value.getClass().isArray()) {
                LOG.warn("Array passed to safeParseBigDecimal: {}, returning ZERO", value);
                return BigDecimal.ZERO;
            }

            // Handle different numeric types that might be returned from database
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            } else if (value instanceof Number) {
                return BigDecimal.valueOf(((Number) value).doubleValue());
            } else {
                String stringValue = value.toString().trim();
                if (stringValue.isEmpty()) {
                    return BigDecimal.ZERO;
                }
                // Handle scientific notation and other formats
                return new BigDecimal(stringValue);
            }
        } catch (NumberFormatException e) {
            LOG.warn("Failed to parse BigDecimal from value: {}, returning ZERO", value, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Safely parse Long from database result.
     */
    private Long safeParseLong(Object value) {
        if (value == null) {
            return 0L;
        }

        try {
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Number) {
                return ((Number) value).longValue();
            } else {
                String stringValue = value.toString().trim();
                if (stringValue.isEmpty()) {
                    return 0L;
                }
                return Long.valueOf(stringValue);
            }
        } catch (NumberFormatException e) {
            LOG.warn("Failed to parse Long from value: {}, returning 0", value, e);
            return 0L;
        }
    }

    /**
     * Safely parse Integer from database result.
     */
    private Integer safeParseInteger(Object value) {
        if (value == null) {
            return 0;
        }

        try {
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                String stringValue = value.toString().trim();
                if (stringValue.isEmpty()) {
                    return 0;
                }
                return Integer.valueOf(stringValue);
            }
        } catch (NumberFormatException e) {
            LOG.warn("Failed to parse Integer from value: {}, returning 0", value, e);
            return 0;
        }
    }

    /**
     * Enhanced input sanitization for SQL injection protection.
     * This method provides comprehensive protection against various SQL injection attacks.
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        // Remove potentially dangerous characters and trim whitespace
        String sanitized = input
            .trim()
            .replaceAll("[<>\"'&;]", "") // Remove script injection chars
            .replaceAll("(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute)", "") // Remove SQL keywords
            .replaceAll("--", "") // Remove SQL comments
            .replaceAll("/\\*.*?\\*/", ""); // Remove block comments

        // Limit length to prevent buffer overflow attempts
        if (sanitized.length() > 255) {
            LOG.warn("Input too long, truncating: original length {}", sanitized.length());
            sanitized = sanitized.substring(0, 255);
        }

        return sanitized;
    }/**
     * Enhanced validation for sort parameters with strict whitelist approach and SQL injection protection.
     */

    private String validateSortBySecure(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "transactionDate"; // default sort
        }

        String sanitized = sortBy.trim().toLowerCase();

        // Check for SQL injection attempts
        if (SqlSecurityUtil.containsSqlInjection(sanitized)) {
            LOG.warn("SQL injection attempt detected in sort parameter: {}", sortBy);
            return "transactionDate";
        }

        // Validate that it's a safe sort field
        if (!SqlSecurityUtil.isSafeSortField(sanitized)) {
            LOG.warn("Unsafe sort field: {}", sortBy);
            return "transactionDate";
        }

        // Map to allowed fields with strict whitelist
        switch (sanitized) {
            case "sellername":
            case "seller_name":
                return "sellerName";
            case "amount":
                return "amount";
            case "transactiondate":
            case "transaction_date":
                return "transactionDate";
            case "id":
                return "id";
            default:
                LOG.warn("Unmapped sort field '{}', using default", sanitized);
                return "transactionDate";
        }
    }

    /**
     * Maps database row to SellerProjectDTO.
     */
    private SellerProjectDTO mapToSellerProjectDTO(Object[] row) {
        try {
            if (row == null || row.length < 10) {
                LOG.warn("Invalid project row data: length {}", row != null ? row.length : 0);
                return createErrorSellerProject("Invalid row data");
            }

            LOG.debug("Mapping project row: {}", java.util.Arrays.toString(row));

            Long projectId = safeParseLong(row[0]);
            String projectName = row[1] != null ? row[1].toString().trim() : "Unknown Project";
            BigDecimal amount = safeParseBigDecimal(row[2]);
            Integer numberOfUnits = safeParseInteger(row[3]);
            BigDecimal feesPerUnit = safeParseBigDecimal(row[4]);
            Instant createdDate = parseTimestamp(row[5]);
            String status = row[6] != null ? row[6].toString().trim() : "ACTIVE";
            String description = row[7] != null ? row[7].toString().trim() : "Project description";
            BigDecimal totalRevenue = safeParseBigDecimal(row[8]);
            Integer daysActive = safeParseInteger(row[9]);

            SellerProjectDTO dto = new SellerProjectDTO(
                projectId,
                projectName,
                amount,
                numberOfUnits,
                feesPerUnit,
                createdDate,
                status,
                description,
                totalRevenue,
                daysActive
            );

            LOG.debug("Successfully mapped project DTO: {}", dto);
            return dto;
        } catch (Exception e) {
            LOG.error("Error mapping project from row: {}", row != null ? java.util.Arrays.toString(row) : "null", e);
            return createErrorSellerProject("Mapping error: " + e.getMessage());
        }
    }

    /**
     * Creates an error project DTO for mapping failures.
     */
    private SellerProjectDTO createErrorSellerProject(String errorMessage) {
        return new SellerProjectDTO(
            null,
            "Error Loading Project",
            BigDecimal.ZERO,
            0,
            BigDecimal.ZERO,
            Instant.now(),
            "ERROR",
            errorMessage,
            BigDecimal.ZERO,
            0
        );
    }
}
