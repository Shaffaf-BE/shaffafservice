package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.enumeration.Status;
import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.service.SellerService;
import com.shaffaf.shaffafservice.service.dto.DashboardDataDTO;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
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

        // Validate and sanitize sort parameters
        String validatedSortBy = validateSortBy(sortBy);
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

    /**
     * Creates an empty dashboard data object for error cases.
     */
    private DashboardDataDTO createEmptyDashboard(Pageable pageable) {
        return new DashboardDataDTO(BigDecimal.ZERO, 0L, BigDecimal.ZERO, 0L, BigDecimal.ZERO, List.of(), 0L, pageable.getPageNumber(), 0);
    }

    /**
     * Validates and sanitizes the sort field parameter.
     */
    private String validateSortBy(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "transactionDate"; // default sort
        }

        // Allowed sort fields to prevent SQL injection
        switch (sortBy.toLowerCase().trim()) {
            case "sellername":
            case "seller_name":
                return "sellerName";
            case "amount":
                return "amount";
            case "transactiondate":
            case "transaction_date":
                return "transactionDate";
            default:
                LOG.warn("Invalid sort field '{}', using default 'transactionDate'", sortBy);
                return "transactionDate";
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
     * Sanitizes input to prevent XSS and injection attacks.
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove potentially dangerous characters and trim whitespace
        return input.trim().replaceAll("[<>\"']", "");
    }
}
