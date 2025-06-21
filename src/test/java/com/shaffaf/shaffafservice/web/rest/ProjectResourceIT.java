package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.ProjectAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.domain.enumeration.Status;
import com.shaffaf.shaffafservice.repository.ProjectRepository;
import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import com.shaffaf.shaffafservice.service.mapper.ProjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProjectResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final BigDecimal DEFAULT_FEES_PER_UNIT_PER_MONTH = new BigDecimal(1);
    private static final BigDecimal UPDATED_FEES_PER_UNIT_PER_MONTH = new BigDecimal(2);

    private static final String DEFAULT_UNION_HEAD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_UNION_HEAD_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UNION_HEAD_MOBILE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_UNION_HEAD_MOBILE_NUMBER = "BBBBBBBBBB";
    private static final Integer DEFAULT_NUMBER_OF_UNITS = 1;
    private static final Integer UPDATED_NUMBER_OF_UNITS = 2;

    private static final String DEFAULT_CONSENT_PROVIDED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CONSENT_PROVIDED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CONSENT_PROVIDED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CONSENT_PROVIDED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DELETED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    // Seller constants for testing
    private static final String DEFAULT_FIRST_NAME = "John";
    private static final String DEFAULT_LAST_NAME = "Doe";
    private static final String DEFAULT_SELLER_EMAIL = "john.doe@example.com";
    private static final String DEFAULT_SELLER_PHONE = "+923001234567";

    private static final String ENTITY_API_URL = "/api/projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectMockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    private Project project;

    private Project insertedProject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createEntity() {
        return new Project()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .status(DEFAULT_STATUS)
            .feesPerUnitPerMonth(DEFAULT_FEES_PER_UNIT_PER_MONTH)
            .unionHeadName(DEFAULT_UNION_HEAD_NAME)
            .unionHeadMobileNumber(DEFAULT_UNION_HEAD_MOBILE_NUMBER)
            .numberOfUnits(DEFAULT_NUMBER_OF_UNITS)
            .consentProvidedBy(DEFAULT_CONSENT_PROVIDED_BY)
            .consentProvidedOn(DEFAULT_CONSENT_PROVIDED_ON)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .deletedDate(DEFAULT_DELETED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createUpdatedEntity() {
        return new Project()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .feesPerUnitPerMonth(UPDATED_FEES_PER_UNIT_PER_MONTH)
            .unionHeadName(UPDATED_UNION_HEAD_NAME)
            .unionHeadMobileNumber(UPDATED_UNION_HEAD_MOBILE_NUMBER)
            .numberOfUnits(UPDATED_NUMBER_OF_UNITS)
            .consentProvidedBy(UPDATED_CONSENT_PROVIDED_BY)
            .consentProvidedOn(UPDATED_CONSENT_PROVIDED_ON)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedDate(UPDATED_DELETED_DATE);
    }

    /**
     * Create a Seller entity for testing.
     */
    public static Seller createSellerEntity() {
        Seller seller = new Seller();
        seller.setFirstName(DEFAULT_FIRST_NAME);
        seller.setLastName(DEFAULT_LAST_NAME);
        seller.setEmail(DEFAULT_SELLER_EMAIL);
        seller.setPhoneNumber(DEFAULT_SELLER_PHONE);
        seller.setStatus(Status.ACTIVE);
        return seller;
    }

    /**
     * Create a Project entity with a seller for testing.
     */
    public Project createProjectEntity() {
        Project project = createEntity();

        // Create and save a seller first
        Seller seller = createSellerEntity();
        seller = sellerRepository.saveAndFlush(seller);

        // Set the seller in the project
        project.setSeller(seller);
        return project;
    }

    @BeforeEach
    void initTest() {
        project = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProject != null) {
            projectRepository.delete(insertedProject);
            insertedProject = null;
        }
    }

    @Test
    @Transactional
    void createProject() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);
        var returnedProjectDTO = om.readValue(
            restProjectMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProjectDTO.class
        );

        // Validate the Project in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProject = projectMapper.toEntity(returnedProjectDTO);
        assertProjectUpdatableFieldsEquals(returnedProject, getPersistedProject(returnedProject));

        insertedProject = returnedProject;
    }

    @Test
    @Transactional
    void createProjectWithExistingId() throws Exception {
        // Create the Project with an existing ID
        project.setId(1L);
        ProjectDTO projectDTO = projectMapper.toDto(project);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setName(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setStartDate(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setStatus(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnionHeadNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setUnionHeadName(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnionHeadMobileNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setUnionHeadMobileNumber(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumberOfUnitsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setNumberOfUnits(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjects() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].feesPerUnitPerMonth").value(hasItem(sameNumber(DEFAULT_FEES_PER_UNIT_PER_MONTH))))
            .andExpect(jsonPath("$.[*].unionHeadName").value(hasItem(DEFAULT_UNION_HEAD_NAME)))
            .andExpect(jsonPath("$.[*].unionHeadMobileNumber").value(hasItem(DEFAULT_UNION_HEAD_MOBILE_NUMBER)))
            .andExpect(jsonPath("$.[*].numberOfUnits").value(hasItem(DEFAULT_NUMBER_OF_UNITS)))
            .andExpect(jsonPath("$.[*].consentProvidedBy").value(hasItem(DEFAULT_CONSENT_PROVIDED_BY)))
            .andExpect(jsonPath("$.[*].consentProvidedOn").value(hasItem(DEFAULT_CONSENT_PROVIDED_ON.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedDate").value(hasItem(DEFAULT_DELETED_DATE.toString())));
    }

    @Test
    @Transactional
    void getProject() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get the project
        restProjectMockMvc
            .perform(get(ENTITY_API_URL_ID, project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.feesPerUnitPerMonth").value(sameNumber(DEFAULT_FEES_PER_UNIT_PER_MONTH)))
            .andExpect(jsonPath("$.unionHeadName").value(DEFAULT_UNION_HEAD_NAME))
            .andExpect(jsonPath("$.unionHeadMobileNumber").value(DEFAULT_UNION_HEAD_MOBILE_NUMBER))
            .andExpect(jsonPath("$.numberOfUnits").value(DEFAULT_NUMBER_OF_UNITS))
            .andExpect(jsonPath("$.consentProvidedBy").value(DEFAULT_CONSENT_PROVIDED_BY))
            .andExpect(jsonPath("$.consentProvidedOn").value(DEFAULT_CONSENT_PROVIDED_ON.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedDate").value(DEFAULT_DELETED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingProject() throws Exception {
        // Get the project
        restProjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProject() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the project
        Project updatedProject = projectRepository.findById(project.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProject are not directly saved in db
        em.detach(updatedProject);
        updatedProject
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .feesPerUnitPerMonth(UPDATED_FEES_PER_UNIT_PER_MONTH)
            .unionHeadName(UPDATED_UNION_HEAD_NAME)
            .unionHeadMobileNumber(UPDATED_UNION_HEAD_MOBILE_NUMBER)
            .numberOfUnits(UPDATED_NUMBER_OF_UNITS)
            .consentProvidedBy(UPDATED_CONSENT_PROVIDED_BY)
            .consentProvidedOn(UPDATED_CONSENT_PROVIDED_ON)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedDate(UPDATED_DELETED_DATE);
        ProjectDTO projectDTO = projectMapper.toDto(updatedProject);

        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProjectToMatchAllProperties(updatedProject);
    }

    @Test
    @Transactional
    void putNonExistingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .feesPerUnitPerMonth(UPDATED_FEES_PER_UNIT_PER_MONTH)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedDate(UPDATED_DELETED_DATE);

        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProject))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProject, project), getPersistedProject(project));
    }

    @Test
    @Transactional
    void fullUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .feesPerUnitPerMonth(UPDATED_FEES_PER_UNIT_PER_MONTH)
            .unionHeadName(UPDATED_UNION_HEAD_NAME)
            .unionHeadMobileNumber(UPDATED_UNION_HEAD_MOBILE_NUMBER)
            .numberOfUnits(UPDATED_NUMBER_OF_UNITS)
            .consentProvidedBy(UPDATED_CONSENT_PROVIDED_BY)
            .consentProvidedOn(UPDATED_CONSENT_PROVIDED_ON)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedDate(UPDATED_DELETED_DATE);

        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProject))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectUpdatableFieldsEquals(partialUpdatedProject, getPersistedProject(partialUpdatedProject));
    }

    @Test
    @Transactional
    void patchNonExistingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProject() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the project
        restProjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, project.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    /**
     * Test creating a project securely by a seller using the native query implementation.
     */
    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_SELLER")
    void createProjectSecureWithSellerRole() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().size();

        // Create a seller for this project
        com.shaffaf.shaffafservice.service.dto.SellerDTO sellerDTO = new com.shaffaf.shaffafservice.service.dto.SellerDTO();
        sellerDTO.setId(1L); // Using an existing seller

        // Create the Project with a seller
        ProjectDTO projectDTO = createProjectDto();
        projectDTO.setSeller(sellerDTO);

        // Simulate seller exists by mocking repository method
        // Note: In a real test, you'd create an actual seller in the database

        // Create project using the secure endpoint
        restProjectMockMvc
            .perform(
                post("/api/projects/secure/create-by-seller")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()));

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate + 1);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProject.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testProject.getSeller().getId()).isEqualTo(1L);
    }

    /**
     * Test authorization - only sellers and admins should be able to create projects.
     */
    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_USER")
    void createProjectSecureWithoutProperAuthority() throws Exception {
        // Create a seller for this project
        com.shaffaf.shaffafservice.service.dto.SellerDTO sellerDTO = new com.shaffaf.shaffafservice.service.dto.SellerDTO();
        sellerDTO.setId(1L);

        // Create the Project with a seller
        ProjectDTO projectDTO = createProjectDto();
        projectDTO.setSeller(sellerDTO);

        // Attempt to create project without proper role
        restProjectMockMvc
            .perform(
                post("/api/projects/secure/create-by-seller")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectDTO))
            )
            .andExpect(status().isForbidden());
    }

    /**
     * Test creating a project with admin role.
     */
    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
    void createProjectSecureWithAdminRole() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().size();

        // Create a seller for this project
        com.shaffaf.shaffafservice.service.dto.SellerDTO sellerDTO = new com.shaffaf.shaffafservice.service.dto.SellerDTO();
        sellerDTO.setId(1L);

        // Create the Project with a seller
        ProjectDTO projectDTO = createProjectDto();
        projectDTO.setSeller(sellerDTO);

        // Create project with admin role
        restProjectMockMvc
            .perform(
                post("/api/projects/secure/create-by-seller")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").isNotEmpty());
    }

    /**
     * Test validation - a project without a seller should fail.
     */
    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_SELLER")
    void createProjectSecureWithoutSeller() throws Exception {
        // Create the Project without a seller
        ProjectDTO projectDTO = createProjectDto();
        projectDTO.setSeller(null);

        // Create project without a seller should fail with bad request
        restProjectMockMvc
            .perform(
                post("/api/projects/secure/create-by-seller")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectDTO))
            )
            .andExpect(status().isBadRequest());
    }

    /**
     * Test creating a project with an existing ID should fail.
     */
    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_SELLER")
    void createProjectSecureWithExistingId() throws Exception {
        // Create a seller for this project
        com.shaffaf.shaffafservice.service.dto.SellerDTO sellerDTO = new com.shaffaf.shaffafservice.service.dto.SellerDTO();
        sellerDTO.setId(1L);

        // Create the Project with an existing ID
        ProjectDTO projectDTO = createProjectDto();
        projectDTO.setId(1L); // Existing ID
        projectDTO.setSeller(sellerDTO);

        // Create project with existing ID should fail
        restProjectMockMvc
            .perform(
                post("/api/projects/secure/create-by-seller")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectDTO))
            )
            .andExpect(status().isBadRequest());
    }

    /**
     * Helper method to create a basic ProjectDTO for testing.
     */
    private ProjectDTO createProjectDto() {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName(DEFAULT_NAME);
        projectDTO.setDescription(DEFAULT_DESCRIPTION);
        projectDTO.setStartDate(DEFAULT_START_DATE);
        projectDTO.setEndDate(DEFAULT_END_DATE);
        projectDTO.setStatus(DEFAULT_STATUS);
        projectDTO.setFeesPerUnitPerMonth(DEFAULT_FEES_PER_UNIT_PER_MONTH);
        projectDTO.setUnionHeadName(DEFAULT_UNION_HEAD_NAME);
        projectDTO.setUnionHeadMobileNumber(DEFAULT_UNION_HEAD_MOBILE_NUMBER);
        projectDTO.setNumberOfUnits(DEFAULT_NUMBER_OF_UNITS);
        return projectDTO;
    }

    protected long getRepositoryCount() {
        return projectRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Project getPersistedProject(Project project) {
        return projectRepository.findById(project.getId()).orElseThrow();
    }

    protected void assertPersistedProjectToMatchAllProperties(Project expectedProject) {
        assertProjectAllPropertiesEquals(expectedProject, getPersistedProject(expectedProject));
    }

    protected void assertPersistedProjectToMatchUpdatableProperties(Project expectedProject) {
        assertProjectAllUpdatablePropertiesEquals(expectedProject, getPersistedProject(expectedProject));
    }

    /**
     * Test secure get project endpoint with ADMIN role - should access any project.
     */
    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER })
    void getProjectSecureAsAdmin() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Admin should be able to access any project
        restProjectMockMvc
            .perform(get("/api/projects/v1/secure/{id}", project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    /**
     * Test secure get project endpoint with SELLER role - should only access own projects.
     */
    @Test
    @Transactional
    @WithMockUser(username = "+923001234567", authorities = { AuthoritiesConstants.SELLER, AuthoritiesConstants.USER })
    void getProjectSecureAsSeller() throws Exception {
        // Create a seller with matching phone number
        com.shaffaf.shaffafservice.domain.Seller seller = new com.shaffaf.shaffafservice.domain.Seller()
            .firstName("Test")
            .lastName("Seller")
            .email("test@example.com")
            .phoneNumber("+923001234567")
            .status(com.shaffaf.shaffafservice.domain.enumeration.Status.ACTIVE);

        com.shaffaf.shaffafservice.repository.SellerRepository sellerRepository = applicationContext.getBean(
            com.shaffaf.shaffafservice.repository.SellerRepository.class
        );
        seller = sellerRepository.saveAndFlush(seller);

        // Set the seller in the project
        project.setSeller(seller);
        project = projectRepository.saveAndFlush(project);

        // Seller should be able to access their own project
        restProjectMockMvc
            .perform(get("/api/projects/v1/secure/{id}", project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.seller.phoneNumber").value("+923001234567"));
    }

    /**
     * Test secure get project endpoint with SELLER role - should not access other's projects.
     */
    @Test
    @Transactional
    @WithMockUser(username = "+923001234567", authorities = { AuthoritiesConstants.SELLER, AuthoritiesConstants.USER })
    void getProjectSecureAsSellerNotOwner() throws Exception {
        // Create a different seller
        com.shaffaf.shaffafservice.domain.Seller differentSeller = new com.shaffaf.shaffafservice.domain.Seller()
            .firstName("Different")
            .lastName("Seller")
            .email("different@example.com")
            .phoneNumber("+923009876543")
            .status(com.shaffaf.shaffafservice.domain.enumeration.Status.ACTIVE);

        com.shaffaf.shaffafservice.repository.SellerRepository sellerRepository = applicationContext.getBean(
            com.shaffaf.shaffafservice.repository.SellerRepository.class
        );
        differentSeller = sellerRepository.saveAndFlush(differentSeller);

        // Set the different seller in the project
        project.setSeller(differentSeller);
        project = projectRepository.saveAndFlush(project);

        // Current seller should not be able to access other seller's project
        restProjectMockMvc.perform(get("/api/projects/v1/secure/{id}", project.getId())).andExpect(status().isNotFound());
    }

    /**
     * Test secure get project endpoint without proper authority - should be forbidden.
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    void getProjectSecureWithoutProperAuthority() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // User without ADMIN or SELLER authority should be forbidden
        restProjectMockMvc.perform(get("/api/projects/v1/secure/{id}", project.getId())).andExpect(status().isForbidden());
    }

    /**
     * Test secure get project endpoint with non-existent project - should return not found.
     */
    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER })
    void getProjectSecureNotFound() throws Exception {
        // Request a project that doesn't exist
        restProjectMockMvc.perform(get("/api/projects/v1/secure/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    /**
     * Test secure get project endpoint with invalid phone number format for seller.
     */
    @Test
    @Transactional
    @WithMockUser(username = "invalidphone", authorities = { AuthoritiesConstants.SELLER, AuthoritiesConstants.USER })
    void getProjectSecureWithInvalidPhoneNumber() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Seller with invalid phone number format should get bad request
        restProjectMockMvc.perform(get("/api/projects/v1/secure/{id}", project.getId())).andExpect(status().isBadRequest());
    }

    /**
     * Test secure get all projects endpoint - admin access.
     */
    @Test
    @Transactional
    @WithMockUser(username = "admin@example.com", authorities = { AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER })
    void getAllProjectsSecureAsAdmin() throws Exception {
        // Initialize the database with multiple projects
        projectRepository.saveAndFlush(project);

        // Create a second project with different seller
        Seller secondSeller = createSellerEntity();
        secondSeller.setPhoneNumber("+923001234568");
        secondSeller = sellerRepository.saveAndFlush(secondSeller);

        Project secondProject = createProjectEntity();
        secondProject.setName("Second Project");
        secondProject.setSeller(secondSeller);
        projectRepository.saveAndFlush(secondProject);

        // Admin should be able to see all projects
        restProjectMockMvc
            .perform(get("/api/projects/v1/secure").param("page", "0").param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1));
    }

    /**
     * Test secure get all projects endpoint - seller access (own projects only).
     */
    @Test
    @Transactional
    @WithMockUser(username = "+923001234567", authorities = { AuthoritiesConstants.SELLER, AuthoritiesConstants.USER })
    void getAllProjectsSecureAsSeller() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Create a second project with different seller
        Seller secondSeller = createSellerEntity();
        secondSeller.setPhoneNumber("+923001234568");
        secondSeller = sellerRepository.saveAndFlush(secondSeller);

        Project secondProject = createProjectEntity();
        secondProject.setName("Second Project");
        secondProject.setSeller(secondSeller);
        projectRepository.saveAndFlush(secondProject); // Seller should only see their own project
        restProjectMockMvc
            .perform(get("/api/projects/v1/secure").param("page", "0").param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].name").value(DEFAULT_NAME));
    }

    /**
     * Test secure get all projects endpoint with name filter.
     */
    @Test
    @Transactional
    @WithMockUser(username = "admin@example.com", authorities = { AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER })
    void getAllProjectsSecureWithNameFilter() throws Exception {
        // Initialize the database with projects having different names
        project.setName("Alpha Project");
        projectRepository.saveAndFlush(project);

        Seller secondSeller = createSellerEntity();
        secondSeller.setPhoneNumber("+923001234568");
        secondSeller = sellerRepository.saveAndFlush(secondSeller);

        Project secondProject = createProjectEntity();
        secondProject.setName("Beta Project");
        secondProject.setSeller(secondSeller);
        projectRepository.saveAndFlush(secondProject);

        // Filter by name containing "Alpha"
        restProjectMockMvc
            .perform(get("/api/projects/v1/secure").param("nameFilter", "Alpha").param("page", "0").param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Alpha Project"));
    }

    /**
     * Test secure get all projects endpoint with status filter.
     */
    @Test
    @Transactional
    @WithMockUser(username = "admin@example.com", authorities = { AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER })
    void getAllProjectsSecureWithStatusFilter() throws Exception {
        // Initialize projects with different statuses
        project.setStatus(Status.INACTIVE);
        projectRepository.saveAndFlush(project);

        Seller secondSeller = createSellerEntity();
        secondSeller.setPhoneNumber("+923001234568");
        secondSeller = sellerRepository.saveAndFlush(secondSeller);

        Project secondProject = createProjectEntity();
        secondProject.setStatus(Status.ACTIVE);
        secondProject.setSeller(secondSeller);
        projectRepository.saveAndFlush(secondProject);

        // Filter by INACTIVE status
        restProjectMockMvc
            .perform(get("/api/projects/v1/secure").param("statusFilter", "INACTIVE").param("page", "0").param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].status").value("INACTIVE"));
    }

    /**
     * Test secure get all projects endpoint with seller name filter (admin only).
     */
    @Test
    @Transactional
    @WithMockUser(username = "admin@example.com", authorities = { AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER })
    void getAllProjectsSecureWithSellerNameFilter() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Create a second seller with different name
        Seller secondSeller = createSellerEntity();
        secondSeller.setFirstName("Jane");
        secondSeller.setLastName("Smith");
        secondSeller.setPhoneNumber("+923001234568");
        secondSeller = sellerRepository.saveAndFlush(secondSeller);

        Project secondProject = createProjectEntity();
        secondProject.setSeller(secondSeller);
        projectRepository.saveAndFlush(secondProject);

        // Filter by seller name
        restProjectMockMvc
            .perform(get("/api/projects/v1/secure").param("sellerNameFilter", "Jane").param("page", "0").param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].seller.firstName").value("Jane"));
    }

    /**
     * Test secure get all projects endpoint with pagination.
     */
    @Test
    @Transactional
    @WithMockUser(username = "admin@example.com", authorities = { AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER })
    void getAllProjectsSecureWithPagination() throws Exception {
        // Create multiple projects
        for (int i = 0; i < 5; i++) {
            Project testProject = createProjectEntity();
            testProject.setName("Project " + i);
            projectRepository.saveAndFlush(testProject);
        }

        // Request first page with size 2
        restProjectMockMvc
            .perform(get("/api/projects/v1/secure").param("page", "0").param("size", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(5))
            .andExpect(jsonPath("$.totalPages").value(3))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(false));

        // Request second page
        restProjectMockMvc
            .perform(get("/api/projects/v1/secure").param("page", "1").param("size", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(false));
    }

    /**
     * Test secure get all projects endpoint without proper authority - should be forbidden.
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    void getAllProjectsSecureWithoutProperAuthority() throws Exception {
        // User without ADMIN or SELLER authority should be forbidden
        restProjectMockMvc.perform(get("/api/projects/v1/secure")).andExpect(status().isForbidden());
    }

    /**
     * Test secure get all projects endpoint with invalid pagination parameters.
     */
    @Test
    @Transactional
    @WithMockUser(username = "admin@example.com", authorities = { AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER })
    void getAllProjectsSecureWithInvalidPagination() throws Exception {
        // Test negative page number
        restProjectMockMvc
            .perform(get("/api/projects/v1/secure").param("page", "-1").param("size", "10"))
            .andExpect(status().isBadRequest());

        // Test invalid page size (too large)
        restProjectMockMvc
            .perform(get("/api/projects/v1/secure").param("page", "0").param("size", "101"))
            .andExpect(status().isBadRequest());

        // Test invalid page size (zero)
        restProjectMockMvc.perform(get("/api/projects/v1/secure").param("page", "0").param("size", "0")).andExpect(status().isBadRequest());
    }

    /**
     * Test secure get all projects endpoint with invalid phone number format for seller.
     */
    @Test
    @Transactional
    @WithMockUser(username = "invalidphone", authorities = { AuthoritiesConstants.SELLER, AuthoritiesConstants.USER })
    void getAllProjectsSecureWithInvalidPhoneNumber() throws Exception {
        // Seller with invalid phone number format should get bad request
        restProjectMockMvc.perform(get("/api/projects/v1/secure")).andExpect(status().isBadRequest());
    }

    /**
     * Test secure get all projects endpoint - seller ignores sellerNameFilter.
     */
    @Test
    @Transactional
    @WithMockUser(username = "+923001234567", authorities = { AuthoritiesConstants.SELLER, AuthoritiesConstants.USER })
    void getAllProjectsSecureSellerIgnoresSellerFilter() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Create another project with different seller
        Seller secondSeller = createSellerEntity();
        secondSeller.setFirstName("Jane");
        secondSeller.setPhoneNumber("+923001234568");
        secondSeller = sellerRepository.saveAndFlush(secondSeller);

        Project secondProject = createProjectEntity();
        secondProject.setSeller(secondSeller);
        projectRepository.saveAndFlush(secondProject); // Seller should only see their own project, regardless of sellerNameFilter
        restProjectMockMvc
            .perform(
                get("/api/projects/v1/secure")
                    .param("sellerNameFilter", "Jane") // This should be ignored for sellers
                    .param("page", "0")
                    .param("size", "10")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].seller.firstName").value(DEFAULT_FIRST_NAME)); // Should get own project
    }
}
