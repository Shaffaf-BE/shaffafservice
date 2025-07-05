package com.shaffaf.shaffafservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shaffaf.shaffafservice.domain.Block;
import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.domain.Unit;
import com.shaffaf.shaffafservice.domain.UnitType;
import com.shaffaf.shaffafservice.domain.enumeration.Status;
import com.shaffaf.shaffafservice.repository.BlockRepository;
import com.shaffaf.shaffafservice.repository.ProjectRepository;
import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.repository.UnitRepository;
import com.shaffaf.shaffafservice.repository.UnitTypeRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.security.SecurityUtils;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationItemDTO;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationRequestDTO;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationResponseDTO;
import com.shaffaf.shaffafservice.service.dto.BulkUnitInfoDTO;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BulkUnitCreationServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private UnitTypeRepository unitTypeRepository;

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private BulkUnitCreationServiceImpl bulkUnitCreationService;

    private Project testProject;
    private Seller testSeller;
    private BulkUnitCreationRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        // Create test seller
        testSeller = new Seller();
        testSeller.setId(1L);
        testSeller.setFirstName("John");
        testSeller.setLastName("Doe");
        testSeller.setEmail("john.doe@example.com");
        testSeller.setPhoneNumber("+923001234567");
        testSeller.setStatus(Status.ACTIVE);

        // Create test project
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setSeller(testSeller);

        // Create test request
        BulkUnitCreationItemDTO item1 = new BulkUnitCreationItemDTO("A", "5 bed DD", 1, 20);
        BulkUnitCreationItemDTO item2 = new BulkUnitCreationItemDTO("B", "4 bed DD", 1, 30);

        testRequest = new BulkUnitCreationRequestDTO(1L, Arrays.asList(item1, item2));
    }

    @Test
    void createUnitsInBulk_AdminUser_Success() {
        // Arrange
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER)).thenReturn(false); // Admin user

            when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

            // Mock block creation
            Block blockA = new Block();
            blockA.setId(1L);
            blockA.setName("A");
            when(blockRepository.findByNameAndProjectId("A", 1L)).thenReturn(Optional.empty());

            Block blockB = new Block();
            blockB.setId(2L);
            blockB.setName("B");
            when(blockRepository.findByNameAndProjectId("B", 1L)).thenReturn(Optional.empty());

            // Configure save to return specific blocks based on input
            when(blockRepository.save(any(Block.class))).thenAnswer(invocation -> {
                Block block = invocation.getArgument(0);
                if ("A".equals(block.getName())) {
                    return blockA;
                } else if ("B".equals(block.getName())) {
                    return blockB;
                }
                return block;
            });

            // Mock unit type creation
            UnitType unitType5Bed = new UnitType();
            unitType5Bed.setId(1L);
            unitType5Bed.setName("5 bed DD");
            when(unitTypeRepository.findByName("5 bed DD")).thenReturn(Optional.empty());

            UnitType unitType4Bed = new UnitType();
            unitType4Bed.setId(2L);
            unitType4Bed.setName("4 bed DD");
            when(unitTypeRepository.findByName("4 bed DD")).thenReturn(Optional.empty());

            // Configure save to return specific unit types based on input
            when(unitTypeRepository.save(any(UnitType.class))).thenAnswer(invocation -> {
                UnitType unitType = invocation.getArgument(0);
                if ("5 bed DD".equals(unitType.getName())) {
                    return unitType5Bed;
                } else if ("4 bed DD".equals(unitType.getName())) {
                    return unitType4Bed;
                }
                return unitType;
            });

            // Mock unit creation
            when(unitRepository.existsByUnitNumberAndBlockId(anyString(), any())).thenReturn(false);
            when(unitRepository.save(any(Unit.class))).thenReturn(new Unit());

            // Act
            BulkUnitCreationResponseDTO response = bulkUnitCreationService.createUnitsInBulk(testRequest, "admin");

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getTotalUnitsCreated()).isEqualTo(50); // 20 + 30
            assertThat(response.getTotalBlocksCreated()).isEqualTo(2);
            assertThat(response.getTotalUnitTypesCreated()).isEqualTo(2);
            assertThat(response.getCreatedBlocks()).containsExactlyInAnyOrder("A", "B");
            assertThat(response.getCreatedUnitTypes()).containsExactlyInAnyOrder("5 bed DD", "4 bed DD");

            // Verify interactions
            verify(projectRepository, times(1)).findById(1L);
            verify(blockRepository, times(2)).save(any(Block.class));
            verify(unitTypeRepository, times(2)).save(any(UnitType.class));
            verify(unitRepository, times(50)).save(any(Unit.class));
        }
    }

    @Test
    void createUnitsInBulk_SellerUser_Success() {
        // Arrange
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER)).thenReturn(true); // Seller user

            when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
            when(sellerRepository.findByPhoneNumber("+923001234567")).thenReturn(Optional.of(testSeller));

            // Mock existing entities to reduce saves
            Block existingBlock = new Block();
            existingBlock.setId(1L);
            existingBlock.setName("A");
            when(blockRepository.findByNameAndProjectId("A", 1L)).thenReturn(Optional.of(existingBlock));
            when(blockRepository.findByNameAndProjectId("B", 1L)).thenReturn(Optional.empty());
            when(blockRepository.save(any(Block.class))).thenReturn(new Block());

            UnitType existingUnitType = new UnitType();
            existingUnitType.setId(1L);
            existingUnitType.setName("5 bed DD");
            when(unitTypeRepository.findByName("5 bed DD")).thenReturn(Optional.of(existingUnitType));
            when(unitTypeRepository.findByName("4 bed DD")).thenReturn(Optional.empty());
            when(unitTypeRepository.save(any(UnitType.class))).thenReturn(new UnitType());

            when(unitRepository.existsByUnitNumberAndBlockId(anyString(), any())).thenReturn(false);
            when(unitRepository.save(any(Unit.class))).thenReturn(new Unit());

            // Act
            BulkUnitCreationResponseDTO response = bulkUnitCreationService.createUnitsInBulk(testRequest, "+923001234567");

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getTotalUnitsCreated()).isEqualTo(50);

            // Verify seller access validation
            verify(sellerRepository, times(1)).findByPhoneNumber("+923001234567");
        }
    }

    @Test
    void createUnitsInBulk_ProjectNotFound_ThrowsException() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bulkUnitCreationService.createUnitsInBulk(testRequest, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Project not found with ID: 1");
    }

    @Test
    void createUnitsInBulk_SellerNotFound_ThrowsException() {
        // Arrange
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER)).thenReturn(true);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
            when(sellerRepository.findByPhoneNumber("unknown")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> bulkUnitCreationService.createUnitsInBulk(testRequest, "unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seller not found for login: unknown");
        }
    }

    @Test
    void createUnitsInBulk_SellerAccessDenied_ThrowsException() {
        // Arrange
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER)).thenReturn(true);

            Seller differentSeller = new Seller();
            differentSeller.setId(2L);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
            when(sellerRepository.findByPhoneNumber("+923009999999")).thenReturn(Optional.of(differentSeller));

            // Act & Assert
            assertThatThrownBy(() -> bulkUnitCreationService.createUnitsInBulk(testRequest, "+923009999999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Access denied: You don't have permission to modify this project");
        }
    }

    @Test
    void createUnitsInBulk_InvalidUnitRange_ThrowsException() {
        // Arrange
        BulkUnitCreationItemDTO invalidItem = new BulkUnitCreationItemDTO("A", "5 bed DD", 20, 10); // start > end
        BulkUnitCreationRequestDTO invalidRequest = new BulkUnitCreationRequestDTO(1L, Arrays.asList(invalidItem));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        // Act & Assert
        assertThatThrownBy(() -> bulkUnitCreationService.createUnitsInBulk(invalidRequest, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid unit range for block 'A': start (20) cannot be greater than end (10)");
    }

    @Test
    void createUnitsInBulk_DuplicateItems_ThrowsException() {
        // Arrange
        BulkUnitCreationItemDTO item1 = new BulkUnitCreationItemDTO("A", "5 bed DD", 1, 10);
        BulkUnitCreationItemDTO item2 = new BulkUnitCreationItemDTO("A", "5 bed DD", 11, 20); // Duplicate block-unitType
        BulkUnitCreationRequestDTO duplicateRequest = new BulkUnitCreationRequestDTO(1L, Arrays.asList(item1, item2));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        // Act & Assert
        assertThatThrownBy(() -> bulkUnitCreationService.createUnitsInBulk(duplicateRequest, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Duplicate entry found for block 'A' and unit type '5 bed DD'");
    }

    @Test
    void createUnitsInBulk_TooManyUnits_ThrowsException() {
        // Arrange
        BulkUnitCreationItemDTO hugeItem = new BulkUnitCreationItemDTO("A", "5 bed DD", 1, 1001); // 1001 units
        BulkUnitCreationRequestDTO hugeRequest = new BulkUnitCreationRequestDTO(1L, Arrays.asList(hugeItem));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        // Act & Assert
        assertThatThrownBy(() -> bulkUnitCreationService.createUnitsInBulk(hugeRequest, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unit range too large for block 'A': 1001 units. Maximum allowed is 1000.");
    }

    // GET Units Tests

    @Test
    void getAllUnitsForProject_AsAdmin_ShouldReturnAllUnits() {
        // Arrange
        Long projectId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        // Mock data
        Object[] unitData = {
            BigInteger.valueOf(1L), // unit_id
            "101", // unit_number
            BigInteger.valueOf(1L), // block_id
            "Block A", // block_name
            BigInteger.valueOf(1L), // unit_type_id
            "2BHK", // unit_type_name
            BigInteger.valueOf(1L), // project_id
            "Test Project", // project_name
            "admin", // created_by
            Timestamp.from(Instant.now()), // created_date
        };

        List<Object[]> unitDataList = Arrays.asList(new Object[][] { unitData });
        Page<Object[]> unitPage = new PageImpl<>(unitDataList, pageable, 1);

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(unitRepository.findAllUnitsWithDetailsNativeByProject(projectId, pageable)).thenReturn(unitPage);

        // Act
        Page<BulkUnitInfoDTO> result = bulkUnitCreationService.getAllUnitsForProject(projectId, pageable, "admin");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        BulkUnitInfoDTO unitInfo = result.getContent().get(0);
        assertThat(unitInfo.getUnitId()).isEqualTo(1L);
        assertThat(unitInfo.getUnitNumber()).isEqualTo("101");
        assertThat(unitInfo.getBlockName()).isEqualTo("Block A");
        assertThat(unitInfo.getUnitTypeName()).isEqualTo("2BHK");
        assertThat(unitInfo.getProjectName()).isEqualTo("Test Project");
    }

    @Test
    void getAllUnitsForProject_AsSellerOwnProject_ShouldReturnUnits() {
        // Arrange
        Long projectId = 1L;
        String sellerPhone = "+923317898915";
        Pageable pageable = PageRequest.of(0, 10);

        // Mock seller data
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setPhoneNumber(sellerPhone);

        // Mock unit data
        Object[] unitData = {
            BigInteger.valueOf(1L), // unit_id
            "101", // unit_number
            BigInteger.valueOf(1L), // block_id
            "Block A", // block_name
            BigInteger.valueOf(1L), // unit_type_id
            "2BHK", // unit_type_name
            BigInteger.valueOf(1L), // project_id
            "Test Project", // project_name
            sellerPhone, // created_by
            Timestamp.from(Instant.now()), // created_date
        };

        List<Object[]> unitDataList = Arrays.asList(new Object[][] { unitData });
        Page<Object[]> unitPage = new PageImpl<>(unitDataList, pageable, 1);

        try (MockedStatic<SecurityUtils> securityUtilsMocked = mockStatic(SecurityUtils.class)) {
            securityUtilsMocked.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER)).thenReturn(true);

            when(projectRepository.existsById(projectId)).thenReturn(true);
            when(sellerRepository.findByPhoneNumber(sellerPhone)).thenReturn(Optional.of(seller));
            when(unitRepository.isProjectOwnedBySellerNative(projectId, seller.getId())).thenReturn(true);
            when(unitRepository.findAllUnitsWithDetailsNativeBySellerProject(projectId, seller.getId(), pageable)).thenReturn(unitPage);

            // Act
            Page<BulkUnitInfoDTO> result = bulkUnitCreationService.getAllUnitsForProject(projectId, pageable, sellerPhone);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            BulkUnitInfoDTO unitInfo = result.getContent().get(0);
            assertThat(unitInfo.getUnitId()).isEqualTo(1L);
            assertThat(unitInfo.getUnitNumber()).isEqualTo("101");
            assertThat(unitInfo.getProjectName()).isEqualTo("Test Project");
        }
    }

    @Test
    void getAllUnitsForProject_AsSellerOtherProject_ShouldThrowSecurityException() {
        // Arrange
        Long projectId = 1L;
        String sellerPhone = "+923317898915";
        Pageable pageable = PageRequest.of(0, 10);

        Seller seller = new Seller();
        seller.setId(1L);
        seller.setPhoneNumber(sellerPhone);

        try (MockedStatic<SecurityUtils> securityUtilsMocked = mockStatic(SecurityUtils.class)) {
            securityUtilsMocked.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER)).thenReturn(true);

            when(projectRepository.existsById(projectId)).thenReturn(true);
            when(sellerRepository.findByPhoneNumber(sellerPhone)).thenReturn(Optional.of(seller));
            when(unitRepository.isProjectOwnedBySellerNative(projectId, seller.getId())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> bulkUnitCreationService.getAllUnitsForProject(projectId, pageable, sellerPhone))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Access denied: You can only view units for your own projects");
        }
    }

    @Test
    void getAllUnitsForProject_ProjectNotFound_ShouldThrowException() {
        // Arrange
        Long projectId = 999L;
        Pageable pageable = PageRequest.of(0, 10);

        when(projectRepository.existsById(projectId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> bulkUnitCreationService.getAllUnitsForProject(projectId, pageable, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Project not found with ID: 999");
    }

    @Test
    void getAllUnitsForProject_SellerNotFound_ShouldThrowException() {
        // Arrange
        Long projectId = 1L;
        String sellerPhone = "+923317898915";
        Pageable pageable = PageRequest.of(0, 10);

        try (MockedStatic<SecurityUtils> securityUtilsMocked = mockStatic(SecurityUtils.class)) {
            securityUtilsMocked.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER)).thenReturn(true);

            when(projectRepository.existsById(projectId)).thenReturn(true);
            when(sellerRepository.findByPhoneNumber(sellerPhone)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> bulkUnitCreationService.getAllUnitsForProject(projectId, pageable, sellerPhone))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seller not found with phone number: " + sellerPhone);
        }
    }
}
