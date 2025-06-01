package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.FeesCollectionAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.FeesCollection;
import com.shaffaf.shaffafservice.repository.FeesCollectionRepository;
import com.shaffaf.shaffafservice.service.dto.FeesCollectionDTO;
import com.shaffaf.shaffafservice.service.mapper.FeesCollectionMapper;
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
 * Integration tests for the {@link FeesCollectionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FeesCollectionResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT_COLLECTED = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_COLLECTED = new BigDecimal(2);

    private static final String DEFAULT_AMOUNT_COLLECTED_BY = "AAAAAAAAAA";
    private static final String UPDATED_AMOUNT_COLLECTED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_AMOUNT_COLLECTED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AMOUNT_COLLECTED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_PAID_BY = "AAAAAAAAAA";
    private static final String UPDATED_PAID_BY = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/fees-collections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FeesCollectionRepository feesCollectionRepository;

    @Autowired
    private FeesCollectionMapper feesCollectionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFeesCollectionMockMvc;

    private FeesCollection feesCollection;

    private FeesCollection insertedFeesCollection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FeesCollection createEntity() {
        return new FeesCollection()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .amountCollected(DEFAULT_AMOUNT_COLLECTED)
            .amountCollectedBy(DEFAULT_AMOUNT_COLLECTED_BY)
            .amountCollectedOn(DEFAULT_AMOUNT_COLLECTED_ON)
            .paidBy(DEFAULT_PAID_BY)
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
    public static FeesCollection createUpdatedEntity() {
        return new FeesCollection()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .amountCollected(UPDATED_AMOUNT_COLLECTED)
            .amountCollectedBy(UPDATED_AMOUNT_COLLECTED_BY)
            .amountCollectedOn(UPDATED_AMOUNT_COLLECTED_ON)
            .paidBy(UPDATED_PAID_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        feesCollection = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFeesCollection != null) {
            feesCollectionRepository.delete(insertedFeesCollection);
            insertedFeesCollection = null;
        }
    }

    @Test
    @Transactional
    void createFeesCollection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FeesCollection
        FeesCollectionDTO feesCollectionDTO = feesCollectionMapper.toDto(feesCollection);
        var returnedFeesCollectionDTO = om.readValue(
            restFeesCollectionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feesCollectionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FeesCollectionDTO.class
        );

        // Validate the FeesCollection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFeesCollection = feesCollectionMapper.toEntity(returnedFeesCollectionDTO);
        assertFeesCollectionUpdatableFieldsEquals(returnedFeesCollection, getPersistedFeesCollection(returnedFeesCollection));

        insertedFeesCollection = returnedFeesCollection;
    }

    @Test
    @Transactional
    void createFeesCollectionWithExistingId() throws Exception {
        // Create the FeesCollection with an existing ID
        feesCollection.setId(1L);
        FeesCollectionDTO feesCollectionDTO = feesCollectionMapper.toDto(feesCollection);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeesCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feesCollectionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FeesCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        feesCollection.setTitle(null);

        // Create the FeesCollection, which fails.
        FeesCollectionDTO feesCollectionDTO = feesCollectionMapper.toDto(feesCollection);

        restFeesCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feesCollectionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountCollectedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        feesCollection.setAmountCollected(null);

        // Create the FeesCollection, which fails.
        FeesCollectionDTO feesCollectionDTO = feesCollectionMapper.toDto(feesCollection);

        restFeesCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feesCollectionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFeesCollections() throws Exception {
        // Initialize the database
        insertedFeesCollection = feesCollectionRepository.saveAndFlush(feesCollection);

        // Get all the feesCollectionList
        restFeesCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feesCollection.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amountCollected").value(hasItem(sameNumber(DEFAULT_AMOUNT_COLLECTED))))
            .andExpect(jsonPath("$.[*].amountCollectedBy").value(hasItem(DEFAULT_AMOUNT_COLLECTED_BY)))
            .andExpect(jsonPath("$.[*].amountCollectedOn").value(hasItem(DEFAULT_AMOUNT_COLLECTED_ON.toString())))
            .andExpect(jsonPath("$.[*].paidBy").value(hasItem(DEFAULT_PAID_BY)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getFeesCollection() throws Exception {
        // Initialize the database
        insertedFeesCollection = feesCollectionRepository.saveAndFlush(feesCollection);

        // Get the feesCollection
        restFeesCollectionMockMvc
            .perform(get(ENTITY_API_URL_ID, feesCollection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(feesCollection.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.amountCollected").value(sameNumber(DEFAULT_AMOUNT_COLLECTED)))
            .andExpect(jsonPath("$.amountCollectedBy").value(DEFAULT_AMOUNT_COLLECTED_BY))
            .andExpect(jsonPath("$.amountCollectedOn").value(DEFAULT_AMOUNT_COLLECTED_ON.toString()))
            .andExpect(jsonPath("$.paidBy").value(DEFAULT_PAID_BY))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFeesCollection() throws Exception {
        // Get the feesCollection
        restFeesCollectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFeesCollection() throws Exception {
        // Initialize the database
        insertedFeesCollection = feesCollectionRepository.saveAndFlush(feesCollection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feesCollection
        FeesCollection updatedFeesCollection = feesCollectionRepository.findById(feesCollection.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFeesCollection are not directly saved in db
        em.detach(updatedFeesCollection);
        updatedFeesCollection
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .amountCollected(UPDATED_AMOUNT_COLLECTED)
            .amountCollectedBy(UPDATED_AMOUNT_COLLECTED_BY)
            .amountCollectedOn(UPDATED_AMOUNT_COLLECTED_ON)
            .paidBy(UPDATED_PAID_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        FeesCollectionDTO feesCollectionDTO = feesCollectionMapper.toDto(updatedFeesCollection);

        restFeesCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, feesCollectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(feesCollectionDTO))
            )
            .andExpect(status().isOk());

        // Validate the FeesCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFeesCollectionToMatchAllProperties(updatedFeesCollection);
    }

    @Test
    @Transactional
    void putNonExistingFeesCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesCollection.setId(longCount.incrementAndGet());

        // Create the FeesCollection
        FeesCollectionDTO feesCollectionDTO = feesCollectionMapper.toDto(feesCollection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeesCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, feesCollectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(feesCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeesCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFeesCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesCollection.setId(longCount.incrementAndGet());

        // Create the FeesCollection
        FeesCollectionDTO feesCollectionDTO = feesCollectionMapper.toDto(feesCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeesCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(feesCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeesCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFeesCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesCollection.setId(longCount.incrementAndGet());

        // Create the FeesCollection
        FeesCollectionDTO feesCollectionDTO = feesCollectionMapper.toDto(feesCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeesCollectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feesCollectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FeesCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFeesCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedFeesCollection = feesCollectionRepository.saveAndFlush(feesCollection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feesCollection using partial update
        FeesCollection partialUpdatedFeesCollection = new FeesCollection();
        partialUpdatedFeesCollection.setId(feesCollection.getId());

        partialUpdatedFeesCollection
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .amountCollected(UPDATED_AMOUNT_COLLECTED)
            .amountCollectedOn(UPDATED_AMOUNT_COLLECTED_ON)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restFeesCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeesCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFeesCollection))
            )
            .andExpect(status().isOk());

        // Validate the FeesCollection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFeesCollectionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFeesCollection, feesCollection),
            getPersistedFeesCollection(feesCollection)
        );
    }

    @Test
    @Transactional
    void fullUpdateFeesCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedFeesCollection = feesCollectionRepository.saveAndFlush(feesCollection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feesCollection using partial update
        FeesCollection partialUpdatedFeesCollection = new FeesCollection();
        partialUpdatedFeesCollection.setId(feesCollection.getId());

        partialUpdatedFeesCollection
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .amountCollected(UPDATED_AMOUNT_COLLECTED)
            .amountCollectedBy(UPDATED_AMOUNT_COLLECTED_BY)
            .amountCollectedOn(UPDATED_AMOUNT_COLLECTED_ON)
            .paidBy(UPDATED_PAID_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restFeesCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeesCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFeesCollection))
            )
            .andExpect(status().isOk());

        // Validate the FeesCollection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFeesCollectionUpdatableFieldsEquals(partialUpdatedFeesCollection, getPersistedFeesCollection(partialUpdatedFeesCollection));
    }

    @Test
    @Transactional
    void patchNonExistingFeesCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesCollection.setId(longCount.incrementAndGet());

        // Create the FeesCollection
        FeesCollectionDTO feesCollectionDTO = feesCollectionMapper.toDto(feesCollection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeesCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, feesCollectionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(feesCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeesCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFeesCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesCollection.setId(longCount.incrementAndGet());

        // Create the FeesCollection
        FeesCollectionDTO feesCollectionDTO = feesCollectionMapper.toDto(feesCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeesCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(feesCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeesCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFeesCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesCollection.setId(longCount.incrementAndGet());

        // Create the FeesCollection
        FeesCollectionDTO feesCollectionDTO = feesCollectionMapper.toDto(feesCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeesCollectionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(feesCollectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FeesCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFeesCollection() throws Exception {
        // Initialize the database
        insertedFeesCollection = feesCollectionRepository.saveAndFlush(feesCollection);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the feesCollection
        restFeesCollectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, feesCollection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return feesCollectionRepository.count();
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

    protected FeesCollection getPersistedFeesCollection(FeesCollection feesCollection) {
        return feesCollectionRepository.findById(feesCollection.getId()).orElseThrow();
    }

    protected void assertPersistedFeesCollectionToMatchAllProperties(FeesCollection expectedFeesCollection) {
        assertFeesCollectionAllPropertiesEquals(expectedFeesCollection, getPersistedFeesCollection(expectedFeesCollection));
    }

    protected void assertPersistedFeesCollectionToMatchUpdatableProperties(FeesCollection expectedFeesCollection) {
        assertFeesCollectionAllUpdatablePropertiesEquals(expectedFeesCollection, getPersistedFeesCollection(expectedFeesCollection));
    }
}
