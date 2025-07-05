package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.UnionMember;
import com.shaffaf.shaffafservice.repository.UnionMemberRepository;
import com.shaffaf.shaffafservice.service.UnionMemberService;
import com.shaffaf.shaffafservice.service.dto.UnionMemberDTO;
import com.shaffaf.shaffafservice.service.mapper.UnionMemberMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.UnionMember}.
 */
@Service
@Transactional
public class UnionMemberServiceImpl implements UnionMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(UnionMemberServiceImpl.class);

    private final UnionMemberRepository unionMemberRepository;

    private final UnionMemberMapper unionMemberMapper;

    public UnionMemberServiceImpl(UnionMemberRepository unionMemberRepository, UnionMemberMapper unionMemberMapper) {
        this.unionMemberRepository = unionMemberRepository;
        this.unionMemberMapper = unionMemberMapper;
    }

    @Override
    public UnionMemberDTO save(UnionMemberDTO unionMemberDTO) {
        LOG.debug("Request to save UnionMember : {}", unionMemberDTO);

        // Check for uniqueness - throw exception if member already exists
        Long projectId = unionMemberDTO.getProject() != null ? unionMemberDTO.getProject().getId() : null;
        if (projectId != null && unionMemberRepository.existsUnionMemberForProject(projectId, unionMemberDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("Union member with this phone number already exists for this project");
        }

        UnionMember unionMember = unionMemberMapper.toEntity(unionMemberDTO);
        unionMember = unionMemberRepository.save(unionMember);
        return unionMemberMapper.toDto(unionMember);
    }

    @Override
    public UnionMemberDTO update(UnionMemberDTO unionMemberDTO) {
        LOG.debug("Request to update UnionMember : {}", unionMemberDTO);

        // Check for uniqueness - allow updating the same record but prevent duplicates
        Long projectId = unionMemberDTO.getProject() != null ? unionMemberDTO.getProject().getId() : null;
        if (
            projectId != null &&
            unionMemberDTO.getId() != null &&
            unionMemberRepository.existsUnionMemberForProjectExcludingId(projectId, unionMemberDTO.getPhoneNumber(), unionMemberDTO.getId())
        ) {
            throw new IllegalArgumentException("Another union member with this phone number already exists for this project");
        }

        UnionMember unionMember = unionMemberMapper.toEntity(unionMemberDTO);
        unionMember = unionMemberRepository.save(unionMember);
        return unionMemberMapper.toDto(unionMember);
    }

    @Override
    public Optional<UnionMemberDTO> partialUpdate(UnionMemberDTO unionMemberDTO) {
        LOG.debug("Request to partially update UnionMember : {}", unionMemberDTO);

        return unionMemberRepository
            .findById(unionMemberDTO.getId())
            .map(existingUnionMember -> {
                unionMemberMapper.partialUpdate(existingUnionMember, unionMemberDTO);

                return existingUnionMember;
            })
            .map(unionMemberRepository::save)
            .map(unionMemberMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnionMemberDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UnionMembers");
        return unionMemberRepository.findAll(pageable).map(unionMemberMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnionMemberDTO> findOne(Long id) {
        LOG.debug("Request to get UnionMember : {}", id);
        return unionMemberRepository.findById(id).map(unionMemberMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UnionMember : {}", id);
        unionMemberRepository.deleteById(id);
    }

    // Native SQL-based methods implementation    @Override
    @Transactional
    public UnionMemberDTO saveUnionMemberNative(UnionMemberDTO unionMemberDTO, String currentUserLogin) {
        LOG.debug("Request to save UnionMember using native SQL : {}", unionMemberDTO);

        // Validate input
        if (unionMemberDTO.getId() != null) {
            throw new IllegalArgumentException("A new union member cannot already have an ID");
        }
        // Check for uniqueness - throw exception if member already exists
        Long projectId = unionMemberDTO.getProject() != null ? unionMemberDTO.getProject().getId() : null;
        if (projectId != null && unionMemberRepository.existsUnionMemberForProject(projectId, unionMemberDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("Union member with this phone number already exists for this project");
        }

        // Set defaults
        unionMemberDTO.setIsUnionHead(false); // Regular member, not a head

        Instant now = Instant.now();
        unionMemberDTO.setCreatedBy(currentUserLogin);
        unionMemberDTO.setCreatedDate(now);

        // Use regular JPA save to avoid native SQL issues with sequence generation
        UnionMember unionMember = unionMemberMapper.toEntity(unionMemberDTO);
        unionMember = unionMemberRepository.save(unionMember);

        return unionMemberMapper.toDto(unionMember);
    }

    @Override
    @Transactional
    public UnionMemberDTO saveUnionHeadNative(UnionMemberDTO unionMemberDTO, String currentUserLogin) {
        LOG.debug("Request to save UnionHead using native SQL : {}", unionMemberDTO);
        // Validate input
        if (unionMemberDTO.getId() != null) {
            throw new IllegalArgumentException("A new union head cannot already have an ID");
        }

        if (unionMemberDTO.getProject() == null || unionMemberDTO.getProject().getId() == null) {
            throw new IllegalArgumentException("Project is required for union head");
        }
        // Check for uniqueness - throw exception if member already exists
        Long projectId = unionMemberDTO.getProject().getId();
        if (unionMemberRepository.existsUnionMemberForProject(projectId, unionMemberDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("Union member with this phone number already exists for this project");
        }

        // Check if union head already exists for this project
        if (unionMemberRepository.existsUnionHeadForProject(projectId)) {
            throw new IllegalStateException("A union head already exists for this project");
        }

        // Set as union head
        unionMemberDTO.setIsUnionHead(true);

        Instant now = Instant.now();
        unionMemberDTO.setCreatedBy(currentUserLogin);
        unionMemberDTO.setCreatedDate(now);

        // Use regular JPA save to avoid native SQL issues with sequence generation
        UnionMember unionMember = unionMemberMapper.toEntity(unionMemberDTO);
        unionMember = unionMemberRepository.save(unionMember);

        return unionMemberMapper.toDto(unionMember);
    }

    @Override
    @Transactional
    public Optional<UnionMemberDTO> updateUnionMemberNative(Long id, UnionMemberDTO unionMemberDTO, String currentUserLogin) {
        LOG.debug("Request to update UnionMember using native SQL : id={}, data={}", id, unionMemberDTO);

        // Validate input
        if (id == null || unionMemberDTO == null) {
            throw new IllegalArgumentException("ID and union member data are required");
        }
        // Check if the entity exists and get current values
        Optional<UnionMemberDTO> existing = findOneNative(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        // Preserve critical fields if not provided in the update
        UnionMemberDTO existingMember = existing.get(); // If isUnionHead is null in the update request, preserve the existing value
        Boolean isUnionHead = unionMemberDTO.getIsUnionHead();
        if (isUnionHead == null) {
            isUnionHead = existingMember.getIsUnionHead();
            LOG.debug("Preserving existing isUnionHead value: {} for union member ID: {}", isUnionHead, id);

            // If the existing record also has null (which shouldn't happen with NOT NULL constraint),
            // we need to default to false to avoid constraint violations
            if (isUnionHead == null) {
                LOG.warn("Existing record also has null isUnionHead for ID: {}. This indicates data corruption. Defaulting to false.", id);
                isUnionHead = Boolean.FALSE;
            }
        }

        // Final safety check - isUnionHead cannot be null due to database constraint
        if (isUnionHead == null) {
            LOG.warn("Both update request and existing record have null isUnionHead for ID: {}. Defaulting to false.", id);
            isUnionHead = Boolean.FALSE; // Ensure we have a proper Boolean object, not null
        }

        // Additional validation to ensure isUnionHead is never null
        if (isUnionHead == null) {
            LOG.error("Critical error: isUnionHead is still null after all safety checks for ID: {}", id);
            isUnionHead = Boolean.FALSE;
        }
        // Check for uniqueness - allow updating the same record but prevent duplicates
        Long projectId = unionMemberDTO.getProject() != null ? unionMemberDTO.getProject().getId() : null;
        if (
            projectId != null &&
            unionMemberRepository.existsUnionMemberForProjectExcludingId(projectId, unionMemberDTO.getPhoneNumber(), id)
        ) {
            throw new IllegalArgumentException("Another union member with this phone number already exists for this project");
        }
        Instant now = Instant.now();

        // Log all parameters before the update to debug null issues
        LOG.debug(
            "About to call updateUnionMemberNative with parameters: id={}, firstName={}, lastName={}, email={}, phoneNumber={}, isUnionHead={}, projectId={}, lastModifiedBy={}, lastModifiedDate={}",
            id,
            unionMemberDTO.getFirstName(),
            unionMemberDTO.getLastName(),
            unionMemberDTO.getEmail(),
            unionMemberDTO.getPhoneNumber(),
            isUnionHead,
            projectId,
            currentUserLogin,
            now
        );
        // Update using native SQL
        int result = unionMemberRepository.updateUnionMemberNative(
            id,
            unionMemberDTO.getFirstName(),
            unionMemberDTO.getLastName(),
            unionMemberDTO.getEmail(),
            unionMemberDTO.getPhoneNumber(),
            isUnionHead.booleanValue(), // Convert Boolean to primitive boolean to avoid null issues
            unionMemberDTO.getProject() != null ? unionMemberDTO.getProject().getId() : null,
            currentUserLogin,
            now
        );

        if (result == 0) {
            return Optional.empty();
        }

        // Return the updated entity
        return findOneNative(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnionMemberDTO> findOneNative(Long id) {
        LOG.debug("Request to get UnionMember using native SQL : {}", id);

        List<Object[]> results = unionMemberRepository.findUnionMemberByIdNativeList(id);

        if (results.isEmpty()) {
            LOG.debug("No UnionMember found for ID: {}", id);
            return Optional.empty();
        }

        if (results.size() > 1) {
            LOG.warn("Multiple results found for UnionMember ID: {}. Using first result.", id);
        }

        return Optional.of(mapObjectArrayToDTO(results.get(0)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnionMemberDTO> findUnionMembersByProjectNative(Long projectId, Pageable pageable) {
        LOG.debug("Request to get UnionMembers by project using native SQL : projectId={}, pageable={}", projectId, pageable);

        Page<Object[]> resultPage = unionMemberRepository.findUnionMembersByProjectIdNative(projectId, pageable);

        return resultPage.map(this::mapObjectArrayToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnionMemberDTO> findAllUnionMembersNative(Pageable pageable) {
        LOG.debug("Request to get all UnionMembers using native SQL : pageable={}", pageable);

        // Extract sort information
        String sortBy = "createdDate";
        String sortDirection = "DESC";

        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortBy = order.getProperty();
            sortDirection = order.getDirection().name();
        }

        Page<Object[]> resultPage = unionMemberRepository.findAllUnionMembersNative(sortBy, sortDirection, pageable);

        return resultPage.map(this::mapObjectArrayToDTO);
    }/**
     * Helper method to map Object[] from native query to UnionMemberDTO.
     */

    private UnionMemberDTO mapObjectArrayToDTO(Object[] row) {
        UnionMemberDTO dto = new UnionMemberDTO();

        try {
            // Log the row structure for debugging
            LOG.debug(
                "Mapping Object[] to UnionMemberDTO - row length: {}, content: {}",
                row != null ? row.length : "null",
                row != null ? java.util.Arrays.toString(row) : "null"
            );

            if (row == null) {
                LOG.error("Null row received in mapObjectArrayToDTO");
                throw new IllegalArgumentException("Null row cannot be mapped to UnionMemberDTO");
            }

            if (row.length < 11) {
                LOG.error(
                    "Insufficient columns in result set. Expected at least 11, got: {}. Row content: {}",
                    row.length,
                    java.util.Arrays.toString(row)
                );
                throw new IllegalArgumentException(
                    "Result set has insufficient columns for UnionMemberDTO mapping. Expected at least 11, got: " + row.length
                );
            }
            // Map core union member fields (indices 0-10) - these should always be present
            dto.setId(safeLongValue(row[0]));
            dto.setFirstName(safeStringValue(row[1]));
            dto.setLastName(safeStringValue(row[2]));
            dto.setEmail(safeStringValue(row[3]));
            dto.setPhoneNumber(safeStringValue(row[4]));
            dto.setCreatedBy(safeStringValue(row[5]));
            dto.setCreatedDate(safeInstantValue(row[6]));
            dto.setLastModifiedBy(safeStringValue(row[7]));
            dto.setLastModifiedDate(safeInstantValue(row[8]));
            dto.setDeletedOn(safeInstantValue(row[9]));
            dto.setIsUnionHead(safeBooleanValue(row[10]));

            // Handle project information if available (indices 11-13)
            if (row.length > 11 && row[11] != null) {
                com.shaffaf.shaffafservice.service.dto.ProjectDTO projectDTO = new com.shaffaf.shaffafservice.service.dto.ProjectDTO();
                projectDTO.setId(safeLongValue(row[11]));

                if (row.length > 12 && row[12] != null) {
                    projectDTO.setName(safeStringValue(row[12]));
                }
                if (row.length > 13 && row[13] != null) {
                    projectDTO.setDescription(safeStringValue(row[13]));
                }
                dto.setProject(projectDTO);
            }

            LOG.debug("Successfully mapped row to UnionMemberDTO with ID: {}", dto.getId());
        } catch (ArrayIndexOutOfBoundsException e) {
            LOG.error(
                "Array index out of bounds while mapping Object[] to UnionMemberDTO. Row length: {}, Error: {}",
                row != null ? row.length : "null",
                e.getMessage()
            );
            LOG.error("Row content: {}", row != null ? java.util.Arrays.toString(row) : "null");
            throw new RuntimeException("Array index out of bounds during DTO mapping: " + e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(
                "Error mapping Object[] to UnionMemberDTO. Row length: {}, Error: {}",
                row != null ? row.length : "null",
                e.getMessage(),
                e
            );
            LOG.error("Row content: {}", row != null ? java.util.Arrays.toString(row) : "null");
            throw new RuntimeException("Failed to map native query result to DTO: " + e.getMessage(), e);
        }

        return dto;
    }

    /**
     * Safely convert an Object to String, handling null values.
     */
    private String safeStringValue(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    /**
     * Safely convert an Object to Instant, handling various timestamp types.
     */
    private Instant safeInstantValue(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Instant) {
            return (Instant) obj;
        }
        if (obj instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) obj).toInstant();
        }
        if (obj instanceof java.util.Date) {
            return ((java.util.Date) obj).toInstant();
        }
        if (obj instanceof java.time.LocalDateTime) {
            return ((java.time.LocalDateTime) obj).atZone(java.time.ZoneOffset.UTC).toInstant();
        }
        LOG.warn("Unexpected type for timestamp value: {} ({})", obj, obj.getClass());
        return null;
    }

    /**
     * Safely convert an Object to Boolean, handling null values and providing defaults.
     */
    private Boolean safeBooleanValue(Object obj) {
        if (obj == null) {
            LOG.warn("Boolean field is null, defaulting to false");
            return false; // Default to false for non-nullable boolean fields
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj instanceof String) {
            String str = ((String) obj).toLowerCase().trim();
            return "true".equals(str) || "1".equals(str) || "yes".equals(str);
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue() != 0;
        }
        LOG.warn("Unexpected type for boolean value: {} ({}), defaulting to false", obj, obj.getClass());
        return false;
    }

    /**
     * Safely convert an Object to Long, handling various numeric types.
     */
    private Long safeLongValue(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        if (obj instanceof String) {
            try {
                return Long.valueOf((String) obj);
            } catch (NumberFormatException e) {
                LOG.warn("Could not parse numeric value: {}", obj);
                return null;
            }
        }
        LOG.warn("Unexpected type for numeric value: {} ({})", obj, obj.getClass());
        return null;
    }
}
