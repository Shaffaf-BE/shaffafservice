package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.ComplainTypeAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.ComplainType;
import com.shaffaf.shaffafservice.repository.ComplainTypeRepository;
import com.shaffaf.shaffafservice.service.dto.ComplainTypeDTO;
import com.shaffaf.shaffafservice.service.mapper.ComplainTypeMapper;
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
 * Integration tests for the {@link ComplainTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ComplainTypeResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/complain-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ComplainTypeRepository complainTypeRepository;

    @Autowired
    private ComplainTypeMapper complainTypeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restComplainTypeMockMvc;

    private ComplainType complainType;

    private ComplainType insertedComplainType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ComplainType createEntity() {
        return new ComplainType()
            .title(DEFAULT_TITLE)
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
    public static ComplainType createUpdatedEntity() {
        return new ComplainType()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        complainType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedComplainType != null) {
            complainTypeRepository.delete(insertedComplainType);
            insertedComplainType = null;
        }
    }

    @Test
    @Transactional
    void createComplainType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ComplainType
        ComplainTypeDTO complainTypeDTO = complainTypeMapper.toDto(complainType);
        var returnedComplainTypeDTO = om.readValue(
            restComplainTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainTypeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ComplainTypeDTO.class
        );

        // Validate the ComplainType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedComplainType = complainTypeMapper.toEntity(returnedComplainTypeDTO);
        assertComplainTypeUpdatableFieldsEquals(returnedComplainType, getPersistedComplainType(returnedComplainType));

        insertedComplainType = returnedComplainType;
    }

    @Test
    @Transactional
    void createComplainTypeWithExistingId() throws Exception {
        // Create the ComplainType with an existing ID
        complainType.setId(1L);
        ComplainTypeDTO complainTypeDTO = complainTypeMapper.toDto(complainType);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restComplainTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ComplainType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        complainType.setTitle(null);

        // Create the ComplainType, which fails.
        ComplainTypeDTO complainTypeDTO = complainTypeMapper.toDto(complainType);

        restComplainTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllComplainTypes() throws Exception {
        // Initialize the database
        insertedComplainType = complainTypeRepository.saveAndFlush(complainType);

        // Get all the complainTypeList
        restComplainTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(complainType.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getComplainType() throws Exception {
        // Initialize the database
        insertedComplainType = complainTypeRepository.saveAndFlush(complainType);

        // Get the complainType
        restComplainTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, complainType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(complainType.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingComplainType() throws Exception {
        // Get the complainType
        restComplainTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingComplainType() throws Exception {
        // Initialize the database
        insertedComplainType = complainTypeRepository.saveAndFlush(complainType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complainType
        ComplainType updatedComplainType = complainTypeRepository.findById(complainType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedComplainType are not directly saved in db
        em.detach(updatedComplainType);
        updatedComplainType
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        ComplainTypeDTO complainTypeDTO = complainTypeMapper.toDto(updatedComplainType);

        restComplainTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, complainTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the ComplainType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedComplainTypeToMatchAllProperties(updatedComplainType);
    }

    @Test
    @Transactional
    void putNonExistingComplainType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainType.setId(longCount.incrementAndGet());

        // Create the ComplainType
        ComplainTypeDTO complainTypeDTO = complainTypeMapper.toDto(complainType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComplainTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, complainTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchComplainType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainType.setId(longCount.incrementAndGet());

        // Create the ComplainType
        ComplainTypeDTO complainTypeDTO = complainTypeMapper.toDto(complainType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamComplainType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainType.setId(longCount.incrementAndGet());

        // Create the ComplainType
        ComplainTypeDTO complainTypeDTO = complainTypeMapper.toDto(complainType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ComplainType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateComplainTypeWithPatch() throws Exception {
        // Initialize the database
        insertedComplainType = complainTypeRepository.saveAndFlush(complainType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complainType using partial update
        ComplainType partialUpdatedComplainType = new ComplainType();
        partialUpdatedComplainType.setId(complainType.getId());

        partialUpdatedComplainType.createdBy(UPDATED_CREATED_BY).lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restComplainTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComplainType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComplainType))
            )
            .andExpect(status().isOk());

        // Validate the ComplainType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComplainTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedComplainType, complainType),
            getPersistedComplainType(complainType)
        );
    }

    @Test
    @Transactional
    void fullUpdateComplainTypeWithPatch() throws Exception {
        // Initialize the database
        insertedComplainType = complainTypeRepository.saveAndFlush(complainType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complainType using partial update
        ComplainType partialUpdatedComplainType = new ComplainType();
        partialUpdatedComplainType.setId(complainType.getId());

        partialUpdatedComplainType
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restComplainTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComplainType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComplainType))
            )
            .andExpect(status().isOk());

        // Validate the ComplainType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComplainTypeUpdatableFieldsEquals(partialUpdatedComplainType, getPersistedComplainType(partialUpdatedComplainType));
    }

    @Test
    @Transactional
    void patchNonExistingComplainType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainType.setId(longCount.incrementAndGet());

        // Create the ComplainType
        ComplainTypeDTO complainTypeDTO = complainTypeMapper.toDto(complainType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComplainTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, complainTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(complainTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchComplainType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainType.setId(longCount.incrementAndGet());

        // Create the ComplainType
        ComplainTypeDTO complainTypeDTO = complainTypeMapper.toDto(complainType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(complainTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamComplainType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainType.setId(longCount.incrementAndGet());

        // Create the ComplainType
        ComplainTypeDTO complainTypeDTO = complainTypeMapper.toDto(complainType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(complainTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ComplainType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteComplainType() throws Exception {
        // Initialize the database
        insertedComplainType = complainTypeRepository.saveAndFlush(complainType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the complainType
        restComplainTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, complainType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return complainTypeRepository.count();
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

    protected ComplainType getPersistedComplainType(ComplainType complainType) {
        return complainTypeRepository.findById(complainType.getId()).orElseThrow();
    }

    protected void assertPersistedComplainTypeToMatchAllProperties(ComplainType expectedComplainType) {
        assertComplainTypeAllPropertiesEquals(expectedComplainType, getPersistedComplainType(expectedComplainType));
    }

    protected void assertPersistedComplainTypeToMatchUpdatableProperties(ComplainType expectedComplainType) {
        assertComplainTypeAllUpdatablePropertiesEquals(expectedComplainType, getPersistedComplainType(expectedComplainType));
    }
}
