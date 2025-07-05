package com.shaffaf.shaffafservice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
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
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationItemDTO;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationRequestDTO;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationResponseDTO;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BulkUnitCreationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BulkUnitCreationResourceIT {

    private static final String ENTITY_API_URL = "/api/bulk-unit-creation/v1/units";
    private static final String DEFAULT_PROJECT_NAME = "Test Project";
    private static final String DEFAULT_SELLER_PHONE = "1234567890";
    private static final String DEFAULT_SELLER_NAME = "Test Seller";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private UnitTypeRepository unitTypeRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBulkUnitCreationMockMvc;

    private Project project;
    private Seller seller;/**
     * Create a seller entity for testing.
     */

    public static Seller createSeller() {
        return new Seller()
            .firstName(DEFAULT_SELLER_NAME)
            .lastName("Test")
            .phoneNumber(DEFAULT_SELLER_PHONE)
            .status(Status.ACTIVE)
            .createdBy("system")
            .createdDate(Instant.now())
            .lastModifiedBy("system")
            .lastModifiedDate(Instant.now());
    }/**
     * Create a project entity for testing.
     */

    public static Project createProject(Seller seller) {
        return new Project()
            .name(DEFAULT_PROJECT_NAME)
            .description("Test Project Description")
            .numberOfUnits(100)
            .status(Status.ACTIVE)
            .seller(seller)
            .createdBy("system")
            .createdDate(Instant.now())
            .lastModifiedBy("system")
            .lastModifiedDate(Instant.now());
    }

    @BeforeEach
    void initTest() {
        // Create and save seller
        seller = createSeller();
        seller = sellerRepository.saveAndFlush(seller);

        // Create and save project
        project = createProject(seller);
        project = projectRepository.saveAndFlush(project);

        em.clear();
    }

    @AfterEach
    void cleanup() {
        // Clean up test data
        unitRepository.deleteAll();
        unitTypeRepository.deleteAll();
        blockRepository.deleteAll();
        if (project != null) {
            projectRepository.deleteById(project.getId());
        }
        if (seller != null) {
            sellerRepository.deleteById(seller.getId());
        }
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void createBulkUnitsAsAdmin() throws Exception { // Given
        BulkUnitCreationItemDTO item1 = new BulkUnitCreationItemDTO();
        item1.setBlock("Block A");
        item1.setUnitType("2BHK");
        item1.setUnitStart(101);
        item1.setUnitEnd(103);

        BulkUnitCreationItemDTO item2 = new BulkUnitCreationItemDTO();
        item2.setBlock("Block B");
        item2.setUnitType("3BHK");
        item2.setUnitStart(201);
        item2.setUnitEnd(202);

        BulkUnitCreationRequestDTO request = new BulkUnitCreationRequestDTO();
        request.setProjectId(project.getId());
        request.setItems(Arrays.asList(item1, item2));

        long initialBlockCount = blockRepository.count();
        long initialUnitTypeCount = unitTypeRepository.count();
        long initialUnitCount = unitRepository.count();

        // When & Then
        MvcResult result = restBulkUnitCreationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.createdBlocks").isArray())
            .andExpect(jsonPath("$.createdUnitTypes").isArray())
            .andExpect(jsonPath("$.createdUnits").isArray())
            .andReturn(); // Verify response
        String responseContent = result.getResponse().getContentAsString();
        BulkUnitCreationResponseDTO response = om.readValue(responseContent, BulkUnitCreationResponseDTO.class);

        assertThat(response.getMessage()).contains("Successfully created");
        assertThat(response.getCreatedBlocks()).hasSize(2);
        assertThat(response.getCreatedUnitTypes()).hasSize(2);
        assertThat(response.getTotalUnitsCreated()).isEqualTo(5); // 3 + 2 units

        // Verify database changes
        assertThat(blockRepository.count()).isEqualTo(initialBlockCount + 2);
        assertThat(unitTypeRepository.count()).isEqualTo(initialUnitTypeCount + 2);
        assertThat(unitRepository.count()).isEqualTo(initialUnitCount + 5);

        // Verify created entities exist in database
        assertThat(blockRepository.findByNameAndProjectId("Block A", project.getId())).isPresent();
        assertThat(blockRepository.findByNameAndProjectId("Block B", project.getId())).isPresent();
    }

    @Test
    @Transactional
    @WithMockUser(username = DEFAULT_SELLER_PHONE, authorities = AuthoritiesConstants.SELLER)
    void createBulkUnitsAsSellerWithOwnProject() throws Exception {
        // Given
        BulkUnitCreationItemDTO item = new BulkUnitCreationItemDTO();
        item.setBlock("Block A");
        item.setUnitType("2BHK");
        item.setUnitStart(101);
        item.setUnitEnd(102);

        BulkUnitCreationRequestDTO request = new BulkUnitCreationRequestDTO();
        request.setProjectId(project.getId());
        request.setItems(Arrays.asList(item));

        // When & Then
        restBulkUnitCreationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.createdBlocks").isArray());
    }

    @Test
    @Transactional
    @WithMockUser(username = "other_seller", authorities = AuthoritiesConstants.SELLER)
    void createBulkUnitsAsSellerWithOtherProject() throws Exception {
        // Given - create another seller
        Seller otherSeller = new Seller()
            .firstName("Other Seller")
            .lastName("Other")
            .phoneNumber("other_seller")
            .status(Status.ACTIVE)
            .createdBy("system")
            .createdDate(Instant.now());
        otherSeller = sellerRepository.saveAndFlush(otherSeller);

        BulkUnitCreationItemDTO item = new BulkUnitCreationItemDTO();
        item.setBlock("Block A");
        item.setUnitType("2BHK");
        item.setUnitStart(101);
        item.setUnitEnd(102);

        BulkUnitCreationRequestDTO request = new BulkUnitCreationRequestDTO();
        request.setProjectId(project.getId()); // This project belongs to different seller
        request.setItems(Arrays.asList(item));

        // When & Then
        restBulkUnitCreationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("Access denied: You can only create units for your own projects"));

        // Cleanup
        sellerRepository.deleteById(otherSeller.getId());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void createBulkUnitsWithInvalidProjectId() throws Exception {
        // Given
        BulkUnitCreationItemDTO item = new BulkUnitCreationItemDTO();
        item.setBlock("Block A");
        item.setUnitType("2BHK");
        item.setUnitStart(101);
        item.setUnitEnd(102);

        BulkUnitCreationRequestDTO request = new BulkUnitCreationRequestDTO();
        request.setProjectId(99999L); // Non-existent project
        request.setItems(Arrays.asList(item));

        // When & Then
        restBulkUnitCreationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Project not found with ID: 99999"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void createBulkUnitsWithInvalidUnitRange() throws Exception {
        // Given
        BulkUnitCreationItemDTO item = new BulkUnitCreationItemDTO();
        item.setBlock("Block A");
        item.setUnitType("2BHK");
        item.setUnitStart(105);
        item.setUnitEnd(102); // Invalid range: from > to

        BulkUnitCreationRequestDTO request = new BulkUnitCreationRequestDTO();
        request.setProjectId(project.getId());
        request.setItems(Arrays.asList(item));

        // When & Then
        restBulkUnitCreationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void createBulkUnitsWithEmptyItems() throws Exception {
        // Given
        BulkUnitCreationRequestDTO request = new BulkUnitCreationRequestDTO();
        request.setProjectId(project.getId());
        request.setItems(Arrays.asList()); // Empty items list

        // When & Then
        restBulkUnitCreationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Items list cannot be empty"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void createBulkUnitsWithDuplicateUnits() throws Exception {
        // Given - Create an existing unit
        Block existingBlock = new Block().name("Block A").project(project).createdBy("system").createdDate(Instant.now());
        existingBlock = blockRepository.saveAndFlush(existingBlock);
        UnitType existingUnitType = new UnitType().name("2BHK").createdBy("system").createdDate(Instant.now());
        existingUnitType = unitTypeRepository.saveAndFlush(existingUnitType);

        Unit existingUnit = new Unit()
            .unitNumber("101")
            .block(existingBlock)
            .unitType(existingUnitType)
            .createdBy("system")
            .createdDate(Instant.now());
        existingUnit = unitRepository.saveAndFlush(existingUnit); // Try to create the same unit again
        BulkUnitCreationItemDTO item = new BulkUnitCreationItemDTO();
        item.setBlock("Block A");
        item.setUnitType("2BHK");
        item.setUnitStart(101);
        item.setUnitEnd(101);

        BulkUnitCreationRequestDTO request = new BulkUnitCreationRequestDTO();
        request.setProjectId(project.getId());
        request.setItems(Arrays.asList(item));

        // When & Then
        MvcResult result = restBulkUnitCreationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.warnings").isArray())
            .andReturn();

        // Verify that warnings are present for duplicate units
        String responseContent = result.getResponse().getContentAsString();
        BulkUnitCreationResponseDTO response = om.readValue(responseContent, BulkUnitCreationResponseDTO.class);

        assertThat(response.getWarnings()).isNotEmpty();
        assertThat(response.getWarnings().get(0)).contains("already exists");
    }

    @Test
    @Transactional
    void createBulkUnitsWithoutAuthentication() throws Exception {
        // Given
        BulkUnitCreationItemDTO item = new BulkUnitCreationItemDTO();
        item.setBlock("Block A");
        item.setUnitType("2BHK");
        item.setUnitStart(101);
        item.setUnitEnd(102);

        BulkUnitCreationRequestDTO request = new BulkUnitCreationRequestDTO();
        request.setProjectId(project.getId());
        request.setItems(Arrays.asList(item));

        // When & Then
        restBulkUnitCreationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_USER") // User without admin or seller role
    void createBulkUnitsWithInsufficientPermissions() throws Exception {
        // Given
        BulkUnitCreationItemDTO item = new BulkUnitCreationItemDTO();
        item.setBlock("Block A");
        item.setUnitType("2BHK");
        item.setUnitStart(101);
        item.setUnitEnd(102);

        BulkUnitCreationRequestDTO request = new BulkUnitCreationRequestDTO();
        request.setProjectId(project.getId());
        request.setItems(Arrays.asList(item));

        // When & Then
        restBulkUnitCreationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isForbidden());
    }
}
