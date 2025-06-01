package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.UnitTypeAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.UnitType;
import com.shaffaf.shaffafservice.repository.UnitTypeRepository;
import com.shaffaf.shaffafservice.service.dto.UnitTypeDTO;
import com.shaffaf.shaffafservice.service.mapper.UnitTypeMapper;
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
 * Integration tests for the {@link UnitTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UnitTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/unit-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UnitTypeRepository unitTypeRepository;

    @Autowired
    private UnitTypeMapper unitTypeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUnitTypeMockMvc;

    private UnitType unitType;

    private UnitType insertedUnitType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UnitType createEntity() {
        return new UnitType()
            .name(DEFAULT_NAME)
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
    public static UnitType createUpdatedEntity() {
        return new UnitType()
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        unitType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUnitType != null) {
            unitTypeRepository.delete(insertedUnitType);
            insertedUnitType = null;
        }
    }

    @Test
    @Transactional
    void createUnitType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UnitType
        UnitTypeDTO unitTypeDTO = unitTypeMapper.toDto(unitType);
        var returnedUnitTypeDTO = om.readValue(
            restUnitTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(unitTypeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UnitTypeDTO.class
        );

        // Validate the UnitType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUnitType = unitTypeMapper.toEntity(returnedUnitTypeDTO);
        assertUnitTypeUpdatableFieldsEquals(returnedUnitType, getPersistedUnitType(returnedUnitType));

        insertedUnitType = returnedUnitType;
    }

    @Test
    @Transactional
    void createUnitTypeWithExistingId() throws Exception {
        // Create the UnitType with an existing ID
        unitType.setId(1L);
        UnitTypeDTO unitTypeDTO = unitTypeMapper.toDto(unitType);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUnitTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(unitTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UnitType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        unitType.setName(null);

        // Create the UnitType, which fails.
        UnitTypeDTO unitTypeDTO = unitTypeMapper.toDto(unitType);

        restUnitTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(unitTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUnitTypes() throws Exception {
        // Initialize the database
        insertedUnitType = unitTypeRepository.saveAndFlush(unitType);

        // Get all the unitTypeList
        restUnitTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(unitType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getUnitType() throws Exception {
        // Initialize the database
        insertedUnitType = unitTypeRepository.saveAndFlush(unitType);

        // Get the unitType
        restUnitTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, unitType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(unitType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUnitType() throws Exception {
        // Get the unitType
        restUnitTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUnitType() throws Exception {
        // Initialize the database
        insertedUnitType = unitTypeRepository.saveAndFlush(unitType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the unitType
        UnitType updatedUnitType = unitTypeRepository.findById(unitType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUnitType are not directly saved in db
        em.detach(updatedUnitType);
        updatedUnitType
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        UnitTypeDTO unitTypeDTO = unitTypeMapper.toDto(updatedUnitType);

        restUnitTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, unitTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(unitTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the UnitType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUnitTypeToMatchAllProperties(updatedUnitType);
    }

    @Test
    @Transactional
    void putNonExistingUnitType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unitType.setId(longCount.incrementAndGet());

        // Create the UnitType
        UnitTypeDTO unitTypeDTO = unitTypeMapper.toDto(unitType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUnitTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, unitTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(unitTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UnitType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUnitType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unitType.setId(longCount.incrementAndGet());

        // Create the UnitType
        UnitTypeDTO unitTypeDTO = unitTypeMapper.toDto(unitType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUnitTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(unitTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UnitType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUnitType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unitType.setId(longCount.incrementAndGet());

        // Create the UnitType
        UnitTypeDTO unitTypeDTO = unitTypeMapper.toDto(unitType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUnitTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(unitTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UnitType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUnitTypeWithPatch() throws Exception {
        // Initialize the database
        insertedUnitType = unitTypeRepository.saveAndFlush(unitType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the unitType using partial update
        UnitType partialUpdatedUnitType = new UnitType();
        partialUpdatedUnitType.setId(unitType.getId());

        partialUpdatedUnitType
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .deletedOn(UPDATED_DELETED_ON);

        restUnitTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUnitType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUnitType))
            )
            .andExpect(status().isOk());

        // Validate the UnitType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUnitTypeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUnitType, unitType), getPersistedUnitType(unitType));
    }

    @Test
    @Transactional
    void fullUpdateUnitTypeWithPatch() throws Exception {
        // Initialize the database
        insertedUnitType = unitTypeRepository.saveAndFlush(unitType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the unitType using partial update
        UnitType partialUpdatedUnitType = new UnitType();
        partialUpdatedUnitType.setId(unitType.getId());

        partialUpdatedUnitType
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restUnitTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUnitType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUnitType))
            )
            .andExpect(status().isOk());

        // Validate the UnitType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUnitTypeUpdatableFieldsEquals(partialUpdatedUnitType, getPersistedUnitType(partialUpdatedUnitType));
    }

    @Test
    @Transactional
    void patchNonExistingUnitType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unitType.setId(longCount.incrementAndGet());

        // Create the UnitType
        UnitTypeDTO unitTypeDTO = unitTypeMapper.toDto(unitType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUnitTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, unitTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(unitTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UnitType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUnitType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unitType.setId(longCount.incrementAndGet());

        // Create the UnitType
        UnitTypeDTO unitTypeDTO = unitTypeMapper.toDto(unitType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUnitTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(unitTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UnitType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUnitType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unitType.setId(longCount.incrementAndGet());

        // Create the UnitType
        UnitTypeDTO unitTypeDTO = unitTypeMapper.toDto(unitType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUnitTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(unitTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UnitType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUnitType() throws Exception {
        // Initialize the database
        insertedUnitType = unitTypeRepository.saveAndFlush(unitType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the unitType
        restUnitTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, unitType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return unitTypeRepository.count();
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

    protected UnitType getPersistedUnitType(UnitType unitType) {
        return unitTypeRepository.findById(unitType.getId()).orElseThrow();
    }

    protected void assertPersistedUnitTypeToMatchAllProperties(UnitType expectedUnitType) {
        assertUnitTypeAllPropertiesEquals(expectedUnitType, getPersistedUnitType(expectedUnitType));
    }

    protected void assertPersistedUnitTypeToMatchUpdatableProperties(UnitType expectedUnitType) {
        assertUnitTypeAllUpdatablePropertiesEquals(expectedUnitType, getPersistedUnitType(expectedUnitType));
    }
}
