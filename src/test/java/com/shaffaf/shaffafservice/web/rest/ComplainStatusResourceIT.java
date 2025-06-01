package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.ComplainStatusAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.ComplainStatus;
import com.shaffaf.shaffafservice.repository.ComplainStatusRepository;
import com.shaffaf.shaffafservice.service.dto.ComplainStatusDTO;
import com.shaffaf.shaffafservice.service.mapper.ComplainStatusMapper;
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
 * Integration tests for the {@link ComplainStatusResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ComplainStatusResourceIT {

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/complain-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ComplainStatusRepository complainStatusRepository;

    @Autowired
    private ComplainStatusMapper complainStatusMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restComplainStatusMockMvc;

    private ComplainStatus complainStatus;

    private ComplainStatus insertedComplainStatus;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ComplainStatus createEntity() {
        return new ComplainStatus()
            .status(DEFAULT_STATUS)
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
    public static ComplainStatus createUpdatedEntity() {
        return new ComplainStatus()
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        complainStatus = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedComplainStatus != null) {
            complainStatusRepository.delete(insertedComplainStatus);
            insertedComplainStatus = null;
        }
    }

    @Test
    @Transactional
    void createComplainStatus() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ComplainStatus
        ComplainStatusDTO complainStatusDTO = complainStatusMapper.toDto(complainStatus);
        var returnedComplainStatusDTO = om.readValue(
            restComplainStatusMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainStatusDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ComplainStatusDTO.class
        );

        // Validate the ComplainStatus in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedComplainStatus = complainStatusMapper.toEntity(returnedComplainStatusDTO);
        assertComplainStatusUpdatableFieldsEquals(returnedComplainStatus, getPersistedComplainStatus(returnedComplainStatus));

        insertedComplainStatus = returnedComplainStatus;
    }

    @Test
    @Transactional
    void createComplainStatusWithExistingId() throws Exception {
        // Create the ComplainStatus with an existing ID
        complainStatus.setId(1L);
        ComplainStatusDTO complainStatusDTO = complainStatusMapper.toDto(complainStatus);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restComplainStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainStatusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ComplainStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        complainStatus.setStatus(null);

        // Create the ComplainStatus, which fails.
        ComplainStatusDTO complainStatusDTO = complainStatusMapper.toDto(complainStatus);

        restComplainStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainStatusDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllComplainStatuses() throws Exception {
        // Initialize the database
        insertedComplainStatus = complainStatusRepository.saveAndFlush(complainStatus);

        // Get all the complainStatusList
        restComplainStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(complainStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getComplainStatus() throws Exception {
        // Initialize the database
        insertedComplainStatus = complainStatusRepository.saveAndFlush(complainStatus);

        // Get the complainStatus
        restComplainStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, complainStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(complainStatus.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingComplainStatus() throws Exception {
        // Get the complainStatus
        restComplainStatusMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingComplainStatus() throws Exception {
        // Initialize the database
        insertedComplainStatus = complainStatusRepository.saveAndFlush(complainStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complainStatus
        ComplainStatus updatedComplainStatus = complainStatusRepository.findById(complainStatus.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedComplainStatus are not directly saved in db
        em.detach(updatedComplainStatus);
        updatedComplainStatus
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        ComplainStatusDTO complainStatusDTO = complainStatusMapper.toDto(updatedComplainStatus);

        restComplainStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, complainStatusDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainStatusDTO))
            )
            .andExpect(status().isOk());

        // Validate the ComplainStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedComplainStatusToMatchAllProperties(updatedComplainStatus);
    }

    @Test
    @Transactional
    void putNonExistingComplainStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainStatus.setId(longCount.incrementAndGet());

        // Create the ComplainStatus
        ComplainStatusDTO complainStatusDTO = complainStatusMapper.toDto(complainStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComplainStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, complainStatusDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchComplainStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainStatus.setId(longCount.incrementAndGet());

        // Create the ComplainStatus
        ComplainStatusDTO complainStatusDTO = complainStatusMapper.toDto(complainStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamComplainStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainStatus.setId(longCount.incrementAndGet());

        // Create the ComplainStatus
        ComplainStatusDTO complainStatusDTO = complainStatusMapper.toDto(complainStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainStatusMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ComplainStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateComplainStatusWithPatch() throws Exception {
        // Initialize the database
        insertedComplainStatus = complainStatusRepository.saveAndFlush(complainStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complainStatus using partial update
        ComplainStatus partialUpdatedComplainStatus = new ComplainStatus();
        partialUpdatedComplainStatus.setId(complainStatus.getId());

        partialUpdatedComplainStatus.createdBy(UPDATED_CREATED_BY).lastModifiedBy(UPDATED_LAST_MODIFIED_BY).deletedOn(UPDATED_DELETED_ON);

        restComplainStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComplainStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComplainStatus))
            )
            .andExpect(status().isOk());

        // Validate the ComplainStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComplainStatusUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedComplainStatus, complainStatus),
            getPersistedComplainStatus(complainStatus)
        );
    }

    @Test
    @Transactional
    void fullUpdateComplainStatusWithPatch() throws Exception {
        // Initialize the database
        insertedComplainStatus = complainStatusRepository.saveAndFlush(complainStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complainStatus using partial update
        ComplainStatus partialUpdatedComplainStatus = new ComplainStatus();
        partialUpdatedComplainStatus.setId(complainStatus.getId());

        partialUpdatedComplainStatus
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restComplainStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComplainStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComplainStatus))
            )
            .andExpect(status().isOk());

        // Validate the ComplainStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComplainStatusUpdatableFieldsEquals(partialUpdatedComplainStatus, getPersistedComplainStatus(partialUpdatedComplainStatus));
    }

    @Test
    @Transactional
    void patchNonExistingComplainStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainStatus.setId(longCount.incrementAndGet());

        // Create the ComplainStatus
        ComplainStatusDTO complainStatusDTO = complainStatusMapper.toDto(complainStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComplainStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, complainStatusDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(complainStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchComplainStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainStatus.setId(longCount.incrementAndGet());

        // Create the ComplainStatus
        ComplainStatusDTO complainStatusDTO = complainStatusMapper.toDto(complainStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(complainStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamComplainStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainStatus.setId(longCount.incrementAndGet());

        // Create the ComplainStatus
        ComplainStatusDTO complainStatusDTO = complainStatusMapper.toDto(complainStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainStatusMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(complainStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ComplainStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteComplainStatus() throws Exception {
        // Initialize the database
        insertedComplainStatus = complainStatusRepository.saveAndFlush(complainStatus);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the complainStatus
        restComplainStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, complainStatus.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return complainStatusRepository.count();
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

    protected ComplainStatus getPersistedComplainStatus(ComplainStatus complainStatus) {
        return complainStatusRepository.findById(complainStatus.getId()).orElseThrow();
    }

    protected void assertPersistedComplainStatusToMatchAllProperties(ComplainStatus expectedComplainStatus) {
        assertComplainStatusAllPropertiesEquals(expectedComplainStatus, getPersistedComplainStatus(expectedComplainStatus));
    }

    protected void assertPersistedComplainStatusToMatchUpdatableProperties(ComplainStatus expectedComplainStatus) {
        assertComplainStatusAllUpdatablePropertiesEquals(expectedComplainStatus, getPersistedComplainStatus(expectedComplainStatus));
    }
}
