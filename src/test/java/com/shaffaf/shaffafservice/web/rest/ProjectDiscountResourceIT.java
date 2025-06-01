package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.ProjectDiscountAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.ProjectDiscount;
import com.shaffaf.shaffafservice.repository.ProjectDiscountRepository;
import com.shaffaf.shaffafservice.service.dto.ProjectDiscountDTO;
import com.shaffaf.shaffafservice.service.mapper.ProjectDiscountMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProjectDiscountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProjectDiscountResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DISCOUNT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DISCOUNT_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DISCOUNT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DISCOUNT_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_DISCOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT = new BigDecimal(2);

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

    private static final String ENTITY_API_URL = "/api/project-discounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectDiscountRepository projectDiscountRepository;

    @Autowired
    private ProjectDiscountMapper projectDiscountMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectDiscountMockMvc;

    private ProjectDiscount projectDiscount;

    private ProjectDiscount insertedProjectDiscount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectDiscount createEntity() {
        return new ProjectDiscount()
            .title(DEFAULT_TITLE)
            .discountStartDate(DEFAULT_DISCOUNT_START_DATE)
            .discountEndDate(DEFAULT_DISCOUNT_END_DATE)
            .discount(DEFAULT_DISCOUNT)
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
    public static ProjectDiscount createUpdatedEntity() {
        return new ProjectDiscount()
            .title(UPDATED_TITLE)
            .discountStartDate(UPDATED_DISCOUNT_START_DATE)
            .discountEndDate(UPDATED_DISCOUNT_END_DATE)
            .discount(UPDATED_DISCOUNT)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedDate(UPDATED_DELETED_DATE);
    }

    @BeforeEach
    void initTest() {
        projectDiscount = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProjectDiscount != null) {
            projectDiscountRepository.delete(insertedProjectDiscount);
            insertedProjectDiscount = null;
        }
    }

    @Test
    @Transactional
    void createProjectDiscount() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProjectDiscount
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);
        var returnedProjectDiscountDTO = om.readValue(
            restProjectDiscountMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDiscountDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProjectDiscountDTO.class
        );

        // Validate the ProjectDiscount in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProjectDiscount = projectDiscountMapper.toEntity(returnedProjectDiscountDTO);
        assertProjectDiscountUpdatableFieldsEquals(returnedProjectDiscount, getPersistedProjectDiscount(returnedProjectDiscount));

        insertedProjectDiscount = returnedProjectDiscount;
    }

    @Test
    @Transactional
    void createProjectDiscountWithExistingId() throws Exception {
        // Create the ProjectDiscount with an existing ID
        projectDiscount.setId(1L);
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDiscountDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProjectDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectDiscount.setTitle(null);

        // Create the ProjectDiscount, which fails.
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);

        restProjectDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDiscountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDiscountStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectDiscount.setDiscountStartDate(null);

        // Create the ProjectDiscount, which fails.
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);

        restProjectDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDiscountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDiscountEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectDiscount.setDiscountEndDate(null);

        // Create the ProjectDiscount, which fails.
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);

        restProjectDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDiscountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDiscountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectDiscount.setDiscount(null);

        // Create the ProjectDiscount, which fails.
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);

        restProjectDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDiscountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjectDiscounts() throws Exception {
        // Initialize the database
        insertedProjectDiscount = projectDiscountRepository.saveAndFlush(projectDiscount);

        // Get all the projectDiscountList
        restProjectDiscountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectDiscount.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].discountStartDate").value(hasItem(DEFAULT_DISCOUNT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].discountEndDate").value(hasItem(DEFAULT_DISCOUNT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(sameNumber(DEFAULT_DISCOUNT))))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedDate").value(hasItem(DEFAULT_DELETED_DATE.toString())));
    }

    @Test
    @Transactional
    void getProjectDiscount() throws Exception {
        // Initialize the database
        insertedProjectDiscount = projectDiscountRepository.saveAndFlush(projectDiscount);

        // Get the projectDiscount
        restProjectDiscountMockMvc
            .perform(get(ENTITY_API_URL_ID, projectDiscount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectDiscount.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.discountStartDate").value(DEFAULT_DISCOUNT_START_DATE.toString()))
            .andExpect(jsonPath("$.discountEndDate").value(DEFAULT_DISCOUNT_END_DATE.toString()))
            .andExpect(jsonPath("$.discount").value(sameNumber(DEFAULT_DISCOUNT)))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedDate").value(DEFAULT_DELETED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingProjectDiscount() throws Exception {
        // Get the projectDiscount
        restProjectDiscountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProjectDiscount() throws Exception {
        // Initialize the database
        insertedProjectDiscount = projectDiscountRepository.saveAndFlush(projectDiscount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectDiscount
        ProjectDiscount updatedProjectDiscount = projectDiscountRepository.findById(projectDiscount.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProjectDiscount are not directly saved in db
        em.detach(updatedProjectDiscount);
        updatedProjectDiscount
            .title(UPDATED_TITLE)
            .discountStartDate(UPDATED_DISCOUNT_START_DATE)
            .discountEndDate(UPDATED_DISCOUNT_END_DATE)
            .discount(UPDATED_DISCOUNT)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedDate(UPDATED_DELETED_DATE);
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(updatedProjectDiscount);

        restProjectDiscountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectDiscountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectDiscountDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProjectDiscountToMatchAllProperties(updatedProjectDiscount);
    }

    @Test
    @Transactional
    void putNonExistingProjectDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectDiscount.setId(longCount.incrementAndGet());

        // Create the ProjectDiscount
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectDiscountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectDiscountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectDiscountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjectDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectDiscount.setId(longCount.incrementAndGet());

        // Create the ProjectDiscount
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectDiscountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectDiscountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjectDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectDiscount.setId(longCount.incrementAndGet());

        // Create the ProjectDiscount
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectDiscountMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDiscountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProjectDiscountWithPatch() throws Exception {
        // Initialize the database
        insertedProjectDiscount = projectDiscountRepository.saveAndFlush(projectDiscount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectDiscount using partial update
        ProjectDiscount partialUpdatedProjectDiscount = new ProjectDiscount();
        partialUpdatedProjectDiscount.setId(projectDiscount.getId());

        partialUpdatedProjectDiscount
            .discount(UPDATED_DISCOUNT)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .deletedDate(UPDATED_DELETED_DATE);

        restProjectDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectDiscount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProjectDiscount))
            )
            .andExpect(status().isOk());

        // Validate the ProjectDiscount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectDiscountUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProjectDiscount, projectDiscount),
            getPersistedProjectDiscount(projectDiscount)
        );
    }

    @Test
    @Transactional
    void fullUpdateProjectDiscountWithPatch() throws Exception {
        // Initialize the database
        insertedProjectDiscount = projectDiscountRepository.saveAndFlush(projectDiscount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectDiscount using partial update
        ProjectDiscount partialUpdatedProjectDiscount = new ProjectDiscount();
        partialUpdatedProjectDiscount.setId(projectDiscount.getId());

        partialUpdatedProjectDiscount
            .title(UPDATED_TITLE)
            .discountStartDate(UPDATED_DISCOUNT_START_DATE)
            .discountEndDate(UPDATED_DISCOUNT_END_DATE)
            .discount(UPDATED_DISCOUNT)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedDate(UPDATED_DELETED_DATE);

        restProjectDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectDiscount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProjectDiscount))
            )
            .andExpect(status().isOk());

        // Validate the ProjectDiscount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectDiscountUpdatableFieldsEquals(
            partialUpdatedProjectDiscount,
            getPersistedProjectDiscount(partialUpdatedProjectDiscount)
        );
    }

    @Test
    @Transactional
    void patchNonExistingProjectDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectDiscount.setId(longCount.incrementAndGet());

        // Create the ProjectDiscount
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectDiscountDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectDiscountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjectDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectDiscount.setId(longCount.incrementAndGet());

        // Create the ProjectDiscount
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectDiscountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjectDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectDiscount.setId(longCount.incrementAndGet());

        // Create the ProjectDiscount
        ProjectDiscountDTO projectDiscountDTO = projectDiscountMapper.toDto(projectDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectDiscountMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(projectDiscountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProjectDiscount() throws Exception {
        // Initialize the database
        insertedProjectDiscount = projectDiscountRepository.saveAndFlush(projectDiscount);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the projectDiscount
        restProjectDiscountMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectDiscount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return projectDiscountRepository.count();
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

    protected ProjectDiscount getPersistedProjectDiscount(ProjectDiscount projectDiscount) {
        return projectDiscountRepository.findById(projectDiscount.getId()).orElseThrow();
    }

    protected void assertPersistedProjectDiscountToMatchAllProperties(ProjectDiscount expectedProjectDiscount) {
        assertProjectDiscountAllPropertiesEquals(expectedProjectDiscount, getPersistedProjectDiscount(expectedProjectDiscount));
    }

    protected void assertPersistedProjectDiscountToMatchUpdatableProperties(ProjectDiscount expectedProjectDiscount) {
        assertProjectDiscountAllUpdatablePropertiesEquals(expectedProjectDiscount, getPersistedProjectDiscount(expectedProjectDiscount));
    }
}
