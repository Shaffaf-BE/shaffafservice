package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.ComplainAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.Complain;
import com.shaffaf.shaffafservice.repository.ComplainRepository;
import com.shaffaf.shaffafservice.service.dto.ComplainDTO;
import com.shaffaf.shaffafservice.service.mapper.ComplainMapper;
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
 * Integration tests for the {@link ComplainResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ComplainResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_COMPLAIN_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLAIN_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ADDED_BY = "AAAAAAAAAA";
    private static final String UPDATED_ADDED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_ASSIGNEE = "AAAAAAAAAA";
    private static final String UPDATED_ASSIGNEE = "BBBBBBBBBB";

    private static final String DEFAULT_RESOLUTION_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_RESOLUTION_COMMENTS = "BBBBBBBBBB";

    private static final Instant DEFAULT_RESOLVED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESOLVED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_RESOLVED_BY = "AAAAAAAAAA";
    private static final String UPDATED_RESOLVED_BY = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/complains";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ComplainRepository complainRepository;

    @Autowired
    private ComplainMapper complainMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restComplainMockMvc;

    private Complain complain;

    private Complain insertedComplain;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Complain createEntity() {
        return new Complain()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .complainDate(DEFAULT_COMPLAIN_DATE)
            .addedBy(DEFAULT_ADDED_BY)
            .assignee(DEFAULT_ASSIGNEE)
            .resolutionComments(DEFAULT_RESOLUTION_COMMENTS)
            .resolvedOn(DEFAULT_RESOLVED_ON)
            .resolvedBy(DEFAULT_RESOLVED_BY)
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
    public static Complain createUpdatedEntity() {
        return new Complain()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .complainDate(UPDATED_COMPLAIN_DATE)
            .addedBy(UPDATED_ADDED_BY)
            .assignee(UPDATED_ASSIGNEE)
            .resolutionComments(UPDATED_RESOLUTION_COMMENTS)
            .resolvedOn(UPDATED_RESOLVED_ON)
            .resolvedBy(UPDATED_RESOLVED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        complain = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedComplain != null) {
            complainRepository.delete(insertedComplain);
            insertedComplain = null;
        }
    }

    @Test
    @Transactional
    void createComplain() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Complain
        ComplainDTO complainDTO = complainMapper.toDto(complain);
        var returnedComplainDTO = om.readValue(
            restComplainMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ComplainDTO.class
        );

        // Validate the Complain in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedComplain = complainMapper.toEntity(returnedComplainDTO);
        assertComplainUpdatableFieldsEquals(returnedComplain, getPersistedComplain(returnedComplain));

        insertedComplain = returnedComplain;
    }

    @Test
    @Transactional
    void createComplainWithExistingId() throws Exception {
        // Create the Complain with an existing ID
        complain.setId(1L);
        ComplainDTO complainDTO = complainMapper.toDto(complain);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restComplainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Complain in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        complain.setTitle(null);

        // Create the Complain, which fails.
        ComplainDTO complainDTO = complainMapper.toDto(complain);

        restComplainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllComplains() throws Exception {
        // Initialize the database
        insertedComplain = complainRepository.saveAndFlush(complain);

        // Get all the complainList
        restComplainMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(complain.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].complainDate").value(hasItem(DEFAULT_COMPLAIN_DATE.toString())))
            .andExpect(jsonPath("$.[*].addedBy").value(hasItem(DEFAULT_ADDED_BY)))
            .andExpect(jsonPath("$.[*].assignee").value(hasItem(DEFAULT_ASSIGNEE)))
            .andExpect(jsonPath("$.[*].resolutionComments").value(hasItem(DEFAULT_RESOLUTION_COMMENTS)))
            .andExpect(jsonPath("$.[*].resolvedOn").value(hasItem(DEFAULT_RESOLVED_ON.toString())))
            .andExpect(jsonPath("$.[*].resolvedBy").value(hasItem(DEFAULT_RESOLVED_BY)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getComplain() throws Exception {
        // Initialize the database
        insertedComplain = complainRepository.saveAndFlush(complain);

        // Get the complain
        restComplainMockMvc
            .perform(get(ENTITY_API_URL_ID, complain.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(complain.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.complainDate").value(DEFAULT_COMPLAIN_DATE.toString()))
            .andExpect(jsonPath("$.addedBy").value(DEFAULT_ADDED_BY))
            .andExpect(jsonPath("$.assignee").value(DEFAULT_ASSIGNEE))
            .andExpect(jsonPath("$.resolutionComments").value(DEFAULT_RESOLUTION_COMMENTS))
            .andExpect(jsonPath("$.resolvedOn").value(DEFAULT_RESOLVED_ON.toString()))
            .andExpect(jsonPath("$.resolvedBy").value(DEFAULT_RESOLVED_BY))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingComplain() throws Exception {
        // Get the complain
        restComplainMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingComplain() throws Exception {
        // Initialize the database
        insertedComplain = complainRepository.saveAndFlush(complain);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complain
        Complain updatedComplain = complainRepository.findById(complain.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedComplain are not directly saved in db
        em.detach(updatedComplain);
        updatedComplain
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .complainDate(UPDATED_COMPLAIN_DATE)
            .addedBy(UPDATED_ADDED_BY)
            .assignee(UPDATED_ASSIGNEE)
            .resolutionComments(UPDATED_RESOLUTION_COMMENTS)
            .resolvedOn(UPDATED_RESOLVED_ON)
            .resolvedBy(UPDATED_RESOLVED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        ComplainDTO complainDTO = complainMapper.toDto(updatedComplain);

        restComplainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, complainDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainDTO))
            )
            .andExpect(status().isOk());

        // Validate the Complain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedComplainToMatchAllProperties(updatedComplain);
    }

    @Test
    @Transactional
    void putNonExistingComplain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complain.setId(longCount.incrementAndGet());

        // Create the Complain
        ComplainDTO complainDTO = complainMapper.toDto(complain);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComplainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, complainDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Complain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchComplain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complain.setId(longCount.incrementAndGet());

        // Create the Complain
        ComplainDTO complainDTO = complainMapper.toDto(complain);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Complain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamComplain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complain.setId(longCount.incrementAndGet());

        // Create the Complain
        ComplainDTO complainDTO = complainMapper.toDto(complain);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Complain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateComplainWithPatch() throws Exception {
        // Initialize the database
        insertedComplain = complainRepository.saveAndFlush(complain);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complain using partial update
        Complain partialUpdatedComplain = new Complain();
        partialUpdatedComplain.setId(complain.getId());

        partialUpdatedComplain
            .title(UPDATED_TITLE)
            .addedBy(UPDATED_ADDED_BY)
            .assignee(UPDATED_ASSIGNEE)
            .resolvedBy(UPDATED_RESOLVED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restComplainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComplain.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComplain))
            )
            .andExpect(status().isOk());

        // Validate the Complain in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComplainUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedComplain, complain), getPersistedComplain(complain));
    }

    @Test
    @Transactional
    void fullUpdateComplainWithPatch() throws Exception {
        // Initialize the database
        insertedComplain = complainRepository.saveAndFlush(complain);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complain using partial update
        Complain partialUpdatedComplain = new Complain();
        partialUpdatedComplain.setId(complain.getId());

        partialUpdatedComplain
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .complainDate(UPDATED_COMPLAIN_DATE)
            .addedBy(UPDATED_ADDED_BY)
            .assignee(UPDATED_ASSIGNEE)
            .resolutionComments(UPDATED_RESOLUTION_COMMENTS)
            .resolvedOn(UPDATED_RESOLVED_ON)
            .resolvedBy(UPDATED_RESOLVED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restComplainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComplain.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComplain))
            )
            .andExpect(status().isOk());

        // Validate the Complain in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComplainUpdatableFieldsEquals(partialUpdatedComplain, getPersistedComplain(partialUpdatedComplain));
    }

    @Test
    @Transactional
    void patchNonExistingComplain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complain.setId(longCount.incrementAndGet());

        // Create the Complain
        ComplainDTO complainDTO = complainMapper.toDto(complain);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComplainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, complainDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(complainDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Complain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchComplain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complain.setId(longCount.incrementAndGet());

        // Create the Complain
        ComplainDTO complainDTO = complainMapper.toDto(complain);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(complainDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Complain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamComplain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complain.setId(longCount.incrementAndGet());

        // Create the Complain
        ComplainDTO complainDTO = complainMapper.toDto(complain);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(complainDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Complain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteComplain() throws Exception {
        // Initialize the database
        insertedComplain = complainRepository.saveAndFlush(complain);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the complain
        restComplainMockMvc
            .perform(delete(ENTITY_API_URL_ID, complain.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return complainRepository.count();
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

    protected Complain getPersistedComplain(Complain complain) {
        return complainRepository.findById(complain.getId()).orElseThrow();
    }

    protected void assertPersistedComplainToMatchAllProperties(Complain expectedComplain) {
        assertComplainAllPropertiesEquals(expectedComplain, getPersistedComplain(expectedComplain));
    }

    protected void assertPersistedComplainToMatchUpdatableProperties(Complain expectedComplain) {
        assertComplainAllUpdatablePropertiesEquals(expectedComplain, getPersistedComplain(expectedComplain));
    }
}
