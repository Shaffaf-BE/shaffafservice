package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.Block;
import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.domain.Unit;
import com.shaffaf.shaffafservice.domain.UnitType;
import com.shaffaf.shaffafservice.repository.BlockRepository;
import com.shaffaf.shaffafservice.repository.ProjectRepository;
import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.repository.UnitRepository;
import com.shaffaf.shaffafservice.repository.UnitTypeRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.security.SecurityUtils;
import com.shaffaf.shaffafservice.service.BulkUnitCreationService;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationItemDTO;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationRequestDTO;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationResponseDTO;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing bulk unit creation.
 */
@Service
@Transactional
public class BulkUnitCreationServiceImpl implements BulkUnitCreationService {

    private static final Logger LOG = LoggerFactory.getLogger(BulkUnitCreationServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final SellerRepository sellerRepository;
    private final BlockRepository blockRepository;
    private final UnitTypeRepository unitTypeRepository;
    private final UnitRepository unitRepository;

    public BulkUnitCreationServiceImpl(
        ProjectRepository projectRepository,
        SellerRepository sellerRepository,
        BlockRepository blockRepository,
        UnitTypeRepository unitTypeRepository,
        UnitRepository unitRepository
    ) {
        this.projectRepository = projectRepository;
        this.sellerRepository = sellerRepository;
        this.blockRepository = blockRepository;
        this.unitTypeRepository = unitTypeRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    @Transactional
    public BulkUnitCreationResponseDTO createUnitsInBulk(BulkUnitCreationRequestDTO request, String currentUserLogin) {
        LOG.debug("Request to create units in bulk for project: {}", request.getProjectId());

        // Validate project existence and access
        Project project = validateProjectAccess(request.getProjectId(), currentUserLogin);

        // Validate bulk creation items
        validateBulkCreationItems(request.getItems());

        // Process creation
        List<String> createdBlocks = new ArrayList<>();
        List<String> createdUnitTypes = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        int totalUnitsCreated = 0;

        Instant now = Instant.now();

        for (BulkUnitCreationItemDTO item : request.getItems()) {
            try {
                // Get or create Block
                Block block = getOrCreateBlock(item.getBlock(), project, currentUserLogin, now);
                if (!createdBlocks.contains(block.getName())) {
                    createdBlocks.add(block.getName());
                }

                // Get or create UnitType
                UnitType unitType = getOrCreateUnitType(item.getUnitType(), currentUserLogin, now);
                if (!createdUnitTypes.contains(unitType.getName())) {
                    createdUnitTypes.add(unitType.getName());
                }

                // Create Units in range
                int unitsCreatedForItem = createUnitsInRange(item, block, unitType, currentUserLogin, now, warnings);
                totalUnitsCreated += unitsCreatedForItem;

                LOG.debug("Created {} units for block '{}' with unit type '{}'", unitsCreatedForItem, block.getName(), unitType.getName());
            } catch (Exception e) {
                LOG.error("Error processing bulk creation item: {}", item, e);
                warnings.add(
                    "Failed to process item for block '" +
                    item.getBlock() +
                    "' and unit type '" +
                    item.getUnitType() +
                    "': " +
                    e.getMessage()
                );
            }
        }

        String message = String.format(
            "Successfully created %d units across %d blocks and %d unit types",
            totalUnitsCreated,
            createdBlocks.size(),
            createdUnitTypes.size()
        );

        return new BulkUnitCreationResponseDTO(
            message,
            totalUnitsCreated,
            createdBlocks.size(),
            createdUnitTypes.size(),
            createdBlocks,
            createdUnitTypes,
            warnings
        );
    }

    private Project validateProjectAccess(Long projectId, String currentUserLogin) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            throw new IllegalArgumentException("Project not found with ID: " + projectId);
        }

        Project project = projectOpt.get();

        // Check if current user is a seller and validate project ownership
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER)) {
            Optional<Seller> seller = sellerRepository.findByPhoneNumber(currentUserLogin);
            if (seller.isEmpty()) {
                throw new IllegalArgumentException("Seller not found for login: " + currentUserLogin);
            }

            if (!project.getSeller().getId().equals(seller.get().getId())) {
                throw new IllegalArgumentException("Access denied: You don't have permission to modify this project");
            }
        }

        return project;
    }

    private void validateBulkCreationItems(List<BulkUnitCreationItemDTO> items) {
        Set<String> processedItems = new HashSet<>();

        for (BulkUnitCreationItemDTO item : items) {
            // Validate unit range
            if (item.getUnitStart() > item.getUnitEnd()) {
                throw new IllegalArgumentException(
                    String.format(
                        "Invalid unit range for block '%s': start (%d) cannot be greater than end (%d)",
                        item.getBlock(),
                        item.getUnitStart(),
                        item.getUnitEnd()
                    )
                );
            }

            // Check for duplicate block-unitType combinations
            String itemKey = item.getBlock() + "-" + item.getUnitType();
            if (processedItems.contains(itemKey)) {
                throw new IllegalArgumentException(
                    String.format("Duplicate entry found for block '%s' and unit type '%s'", item.getBlock(), item.getUnitType())
                );
            }
            processedItems.add(itemKey);

            // Validate reasonable range (prevent creating too many units accidentally)
            int unitCount = item.getUnitEnd() - item.getUnitStart() + 1;
            if (unitCount > 1000) {
                throw new IllegalArgumentException(
                    String.format("Unit range too large for block '%s': %d units. Maximum allowed is 1000.", item.getBlock(), unitCount)
                );
            }
        }
    }

    private Block getOrCreateBlock(String blockName, Project project, String currentUserLogin, Instant now) {
        Optional<Block> existingBlock = blockRepository.findByNameAndProjectId(blockName, project.getId());

        if (existingBlock.isPresent()) {
            return existingBlock.get();
        }

        // Create new block using JPA save for sequence generation
        Block newBlock = new Block();
        newBlock.setName(blockName);
        newBlock.setProject(project);
        newBlock.setCreatedBy(currentUserLogin);
        newBlock.setCreatedDate(now);

        return blockRepository.save(newBlock);
    }

    private UnitType getOrCreateUnitType(String unitTypeName, String currentUserLogin, Instant now) {
        Optional<UnitType> existingUnitType = unitTypeRepository.findByName(unitTypeName);

        if (existingUnitType.isPresent()) {
            return existingUnitType.get();
        }

        // Create new unit type using JPA save for sequence generation
        UnitType newUnitType = new UnitType();
        newUnitType.setName(unitTypeName);
        newUnitType.setCreatedBy(currentUserLogin);
        newUnitType.setCreatedDate(now);

        return unitTypeRepository.save(newUnitType);
    }

    private int createUnitsInRange(
        BulkUnitCreationItemDTO item,
        Block block,
        UnitType unitType,
        String currentUserLogin,
        Instant now,
        List<String> warnings
    ) {
        int unitsCreated = 0;

        for (int unitNumber = item.getUnitStart(); unitNumber <= item.getUnitEnd(); unitNumber++) {
            String unitNumberStr = String.valueOf(unitNumber);

            // Check if unit already exists
            if (unitRepository.existsByUnitNumberAndBlockId(unitNumberStr, block.getId())) {
                warnings.add(String.format("Unit %s already exists in block '%s', skipping", unitNumberStr, block.getName()));
                continue;
            }

            // Create new unit using JPA save for sequence generation
            Unit newUnit = new Unit();
            newUnit.setUnitNumber(unitNumberStr);
            newUnit.setBlock(block);
            newUnit.setUnitType(unitType);
            newUnit.setCreatedBy(currentUserLogin);
            newUnit.setCreatedDate(now);

            unitRepository.save(newUnit);
            unitsCreated++;
        }

        return unitsCreated;
    }
}
