package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.ExpenseTypeAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.ExpenseType;
import com.shaffaf.shaffafservice.repository.ExpenseTypeRepository;
import com.shaffaf.shaffafservice.service.dto.ExpenseTypeDTO;
import com.shaffaf.shaffafservice.service.mapper.ExpenseTypeMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ExpenseTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExpenseTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DELETED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/expense-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExpenseTypeRepository expenseTypeRepository;

    @Autowired
    private ExpenseTypeMapper expenseTypeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExpenseTypeMockMvc;

    private ExpenseType expenseType;

    private ExpenseType insertedExpenseType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExpenseType createEntity() {
        return new ExpenseType()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .deletedOn(DEFAULT_DELETED_ON);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExpenseType createUpdatedEntity() {
        return new ExpenseType()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        expenseType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedExpenseType != null) {
            expenseTypeRepository.delete(insertedExpenseType);
            insertedExpenseType = null;
        }
    }

    @Test
    @Transactional
    void createExpenseType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ExpenseType
        ExpenseTypeDTO expenseTypeDTO = expenseTypeMapper.toDto(expenseType);
        var returnedExpenseTypeDTO = om.readValue(
            restExpenseTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expenseTypeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExpenseTypeDTO.class
        );

        // Validate the ExpenseType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExpenseType = expenseTypeMapper.toEntity(returnedExpenseTypeDTO);
        assertExpenseTypeUpdatableFieldsEquals(returnedExpenseType, getPersistedExpenseType(returnedExpenseType));

        insertedExpenseType = returnedExpenseType;
    }

    @Test
    @Transactional
    void createExpenseTypeWithExistingId() throws Exception {
        // Create the ExpenseType with an existing ID
        expenseType.setId(1L);
        ExpenseTypeDTO expenseTypeDTO = expenseTypeMapper.toDto(expenseType);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExpenseTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expenseTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ExpenseType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        expenseType.setName(null);

        // Create the ExpenseType, which fails.
        ExpenseTypeDTO expenseTypeDTO = expenseTypeMapper.toDto(expenseType);

        restExpenseTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expenseTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExpenseTypes() throws Exception {
        // Initialize the database
        insertedExpenseType = expenseTypeRepository.saveAndFlush(expenseType);

        // Get all the expenseTypeList
        restExpenseTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(expenseType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getExpenseType() throws Exception {
        // Initialize the database
        insertedExpenseType = expenseTypeRepository.saveAndFlush(expenseType);

        // Get the expenseType
        restExpenseTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, expenseType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(expenseType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingExpenseType() throws Exception {
        // Get the expenseType
        restExpenseTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExpenseType() throws Exception {
        // Initialize the database
        insertedExpenseType = expenseTypeRepository.saveAndFlush(expenseType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the expenseType
        ExpenseType updatedExpenseType = expenseTypeRepository.findById(expenseType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExpenseType are not directly saved in db
        em.detach(updatedExpenseType);
        updatedExpenseType
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        ExpenseTypeDTO expenseTypeDTO = expenseTypeMapper.toDto(updatedExpenseType);

        restExpenseTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, expenseTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(expenseTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the ExpenseType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExpenseTypeToMatchAllProperties(updatedExpenseType);
    }

    @Test
    @Transactional
    void putNonExistingExpenseType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expenseType.setId(longCount.incrementAndGet());

        // Create the ExpenseType
        ExpenseTypeDTO expenseTypeDTO = expenseTypeMapper.toDto(expenseType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExpenseTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, expenseTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(expenseTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExpenseType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExpenseType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expenseType.setId(longCount.incrementAndGet());

        // Create the ExpenseType
        ExpenseTypeDTO expenseTypeDTO = expenseTypeMapper.toDto(expenseType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpenseTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(expenseTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExpenseType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExpenseType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expenseType.setId(longCount.incrementAndGet());

        // Create the ExpenseType
        ExpenseTypeDTO expenseTypeDTO = expenseTypeMapper.toDto(expenseType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpenseTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expenseTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExpenseType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExpenseTypeWithPatch() throws Exception {
        // Initialize the database
        insertedExpenseType = expenseTypeRepository.saveAndFlush(expenseType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the expenseType using partial update
        ExpenseType partialUpdatedExpenseType = new ExpenseType();
        partialUpdatedExpenseType.setId(expenseType.getId());

        partialUpdatedExpenseType.name(UPDATED_NAME).createdBy(UPDATED_CREATED_BY).lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restExpenseTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExpenseType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExpenseType))
            )
            .andExpect(status().isOk());

        // Validate the ExpenseType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExpenseTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExpenseType, expenseType),
            getPersistedExpenseType(expenseType)
        );
    }

    @Test
    @Transactional
    void fullUpdateExpenseTypeWithPatch() throws Exception {
        // Initialize the database
        insertedExpenseType = expenseTypeRepository.saveAndFlush(expenseType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the expenseType using partial update
        ExpenseType partialUpdatedExpenseType = new ExpenseType();
        partialUpdatedExpenseType.setId(expenseType.getId());

        partialUpdatedExpenseType
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restExpenseTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExpenseType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExpenseType))
            )
            .andExpect(status().isOk());

        // Validate the ExpenseType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExpenseTypeUpdatableFieldsEquals(partialUpdatedExpenseType, getPersistedExpenseType(partialUpdatedExpenseType));
    }

    @Test
    @Transactional
    void patchNonExistingExpenseType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expenseType.setId(longCount.incrementAndGet());

        // Create the ExpenseType
        ExpenseTypeDTO expenseTypeDTO = expenseTypeMapper.toDto(expenseType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExpenseTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, expenseTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(expenseTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExpenseType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExpenseType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expenseType.setId(longCount.incrementAndGet());

        // Create the ExpenseType
        ExpenseTypeDTO expenseTypeDTO = expenseTypeMapper.toDto(expenseType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpenseTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(expenseTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExpenseType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExpenseType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expenseType.setId(longCount.incrementAndGet());

        // Create the ExpenseType
        ExpenseTypeDTO expenseTypeDTO = expenseTypeMapper.toDto(expenseType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpenseTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(expenseTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExpenseType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExpenseType() throws Exception {
        // Initialize the database
        insertedExpenseType = expenseTypeRepository.saveAndFlush(expenseType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the expenseType
        restExpenseTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, expenseType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return expenseTypeRepository.count();
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

    protected ExpenseType getPersistedExpenseType(ExpenseType expenseType) {
        return expenseTypeRepository.findById(expenseType.getId()).orElseThrow();
    }

    protected void assertPersistedExpenseTypeToMatchAllProperties(ExpenseType expectedExpenseType) {
        assertExpenseTypeAllPropertiesEquals(expectedExpenseType, getPersistedExpenseType(expectedExpenseType));
    }

    protected void assertPersistedExpenseTypeToMatchUpdatableProperties(ExpenseType expectedExpenseType) {
        assertExpenseTypeAllUpdatablePropertiesEquals(expectedExpenseType, getPersistedExpenseType(expectedExpenseType));
    }
}
