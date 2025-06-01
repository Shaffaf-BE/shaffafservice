package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.ResidentTypeAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.ResidentType;
import com.shaffaf.shaffafservice.repository.ResidentTypeRepository;
import com.shaffaf.shaffafservice.service.dto.ResidentTypeDTO;
import com.shaffaf.shaffafservice.service.mapper.ResidentTypeMapper;
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
 * Integration tests for the {@link ResidentTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ResidentTypeResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/resident-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ResidentTypeRepository residentTypeRepository;

    @Autowired
    private ResidentTypeMapper residentTypeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResidentTypeMockMvc;

    private ResidentType residentType;

    private ResidentType insertedResidentType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResidentType createEntity() {
        return new ResidentType()
            .type(DEFAULT_TYPE)
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
    public static ResidentType createUpdatedEntity() {
        return new ResidentType()
            .type(UPDATED_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        residentType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedResidentType != null) {
            residentTypeRepository.delete(insertedResidentType);
            insertedResidentType = null;
        }
    }

    @Test
    @Transactional
    void createResidentType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ResidentType
        ResidentTypeDTO residentTypeDTO = residentTypeMapper.toDto(residentType);
        var returnedResidentTypeDTO = om.readValue(
            restResidentTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(residentTypeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ResidentTypeDTO.class
        );

        // Validate the ResidentType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedResidentType = residentTypeMapper.toEntity(returnedResidentTypeDTO);
        assertResidentTypeUpdatableFieldsEquals(returnedResidentType, getPersistedResidentType(returnedResidentType));

        insertedResidentType = returnedResidentType;
    }

    @Test
    @Transactional
    void createResidentTypeWithExistingId() throws Exception {
        // Create the ResidentType with an existing ID
        residentType.setId(1L);
        ResidentTypeDTO residentTypeDTO = residentTypeMapper.toDto(residentType);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restResidentTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(residentTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ResidentType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        residentType.setType(null);

        // Create the ResidentType, which fails.
        ResidentTypeDTO residentTypeDTO = residentTypeMapper.toDto(residentType);

        restResidentTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(residentTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllResidentTypes() throws Exception {
        // Initialize the database
        insertedResidentType = residentTypeRepository.saveAndFlush(residentType);

        // Get all the residentTypeList
        restResidentTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(residentType.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getResidentType() throws Exception {
        // Initialize the database
        insertedResidentType = residentTypeRepository.saveAndFlush(residentType);

        // Get the residentType
        restResidentTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, residentType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(residentType.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingResidentType() throws Exception {
        // Get the residentType
        restResidentTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingResidentType() throws Exception {
        // Initialize the database
        insertedResidentType = residentTypeRepository.saveAndFlush(residentType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the residentType
        ResidentType updatedResidentType = residentTypeRepository.findById(residentType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedResidentType are not directly saved in db
        em.detach(updatedResidentType);
        updatedResidentType
            .type(UPDATED_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        ResidentTypeDTO residentTypeDTO = residentTypeMapper.toDto(updatedResidentType);

        restResidentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, residentTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(residentTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the ResidentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedResidentTypeToMatchAllProperties(updatedResidentType);
    }

    @Test
    @Transactional
    void putNonExistingResidentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        residentType.setId(longCount.incrementAndGet());

        // Create the ResidentType
        ResidentTypeDTO residentTypeDTO = residentTypeMapper.toDto(residentType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResidentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, residentTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(residentTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResidentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchResidentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        residentType.setId(longCount.incrementAndGet());

        // Create the ResidentType
        ResidentTypeDTO residentTypeDTO = residentTypeMapper.toDto(residentType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(residentTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResidentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamResidentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        residentType.setId(longCount.incrementAndGet());

        // Create the ResidentType
        ResidentTypeDTO residentTypeDTO = residentTypeMapper.toDto(residentType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(residentTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResidentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateResidentTypeWithPatch() throws Exception {
        // Initialize the database
        insertedResidentType = residentTypeRepository.saveAndFlush(residentType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the residentType using partial update
        ResidentType partialUpdatedResidentType = new ResidentType();
        partialUpdatedResidentType.setId(residentType.getId());

        partialUpdatedResidentType.type(UPDATED_TYPE).lastModifiedBy(UPDATED_LAST_MODIFIED_BY).deletedOn(UPDATED_DELETED_ON);

        restResidentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResidentType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedResidentType))
            )
            .andExpect(status().isOk());

        // Validate the ResidentType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertResidentTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedResidentType, residentType),
            getPersistedResidentType(residentType)
        );
    }

    @Test
    @Transactional
    void fullUpdateResidentTypeWithPatch() throws Exception {
        // Initialize the database
        insertedResidentType = residentTypeRepository.saveAndFlush(residentType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the residentType using partial update
        ResidentType partialUpdatedResidentType = new ResidentType();
        partialUpdatedResidentType.setId(residentType.getId());

        partialUpdatedResidentType
            .type(UPDATED_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restResidentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResidentType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedResidentType))
            )
            .andExpect(status().isOk());

        // Validate the ResidentType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertResidentTypeUpdatableFieldsEquals(partialUpdatedResidentType, getPersistedResidentType(partialUpdatedResidentType));
    }

    @Test
    @Transactional
    void patchNonExistingResidentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        residentType.setId(longCount.incrementAndGet());

        // Create the ResidentType
        ResidentTypeDTO residentTypeDTO = residentTypeMapper.toDto(residentType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResidentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, residentTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(residentTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResidentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchResidentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        residentType.setId(longCount.incrementAndGet());

        // Create the ResidentType
        ResidentTypeDTO residentTypeDTO = residentTypeMapper.toDto(residentType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(residentTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResidentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamResidentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        residentType.setId(longCount.incrementAndGet());

        // Create the ResidentType
        ResidentTypeDTO residentTypeDTO = residentTypeMapper.toDto(residentType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(residentTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResidentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteResidentType() throws Exception {
        // Initialize the database
        insertedResidentType = residentTypeRepository.saveAndFlush(residentType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the residentType
        restResidentTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, residentType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return residentTypeRepository.count();
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

    protected ResidentType getPersistedResidentType(ResidentType residentType) {
        return residentTypeRepository.findById(residentType.getId()).orElseThrow();
    }

    protected void assertPersistedResidentTypeToMatchAllProperties(ResidentType expectedResidentType) {
        assertResidentTypeAllPropertiesEquals(expectedResidentType, getPersistedResidentType(expectedResidentType));
    }

    protected void assertPersistedResidentTypeToMatchUpdatableProperties(ResidentType expectedResidentType) {
        assertResidentTypeAllUpdatablePropertiesEquals(expectedResidentType, getPersistedResidentType(expectedResidentType));
    }
}
