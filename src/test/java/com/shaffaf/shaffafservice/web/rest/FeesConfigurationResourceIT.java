package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.FeesConfigurationAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.FeesConfiguration;
import com.shaffaf.shaffafservice.repository.FeesConfigurationRepository;
import com.shaffaf.shaffafservice.service.dto.FeesConfigurationDTO;
import com.shaffaf.shaffafservice.service.mapper.FeesConfigurationMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link FeesConfigurationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FeesConfigurationResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final Boolean DEFAULT_IS_RECURRING = false;
    private static final Boolean UPDATED_IS_RECURRING = true;

    private static final LocalDate DEFAULT_DUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_CONFIGURED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CONFIGURED_BY = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/fees-configurations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FeesConfigurationRepository feesConfigurationRepository;

    @Autowired
    private FeesConfigurationMapper feesConfigurationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFeesConfigurationMockMvc;

    private FeesConfiguration feesConfiguration;

    private FeesConfiguration insertedFeesConfiguration;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FeesConfiguration createEntity() {
        return new FeesConfiguration()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .amount(DEFAULT_AMOUNT)
            .isRecurring(DEFAULT_IS_RECURRING)
            .dueDate(DEFAULT_DUE_DATE)
            .configuredBy(DEFAULT_CONFIGURED_BY)
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
    public static FeesConfiguration createUpdatedEntity() {
        return new FeesConfiguration()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .isRecurring(UPDATED_IS_RECURRING)
            .dueDate(UPDATED_DUE_DATE)
            .configuredBy(UPDATED_CONFIGURED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        feesConfiguration = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFeesConfiguration != null) {
            feesConfigurationRepository.delete(insertedFeesConfiguration);
            insertedFeesConfiguration = null;
        }
    }

    @Test
    @Transactional
    void createFeesConfiguration() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FeesConfiguration
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(feesConfiguration);
        var returnedFeesConfigurationDTO = om.readValue(
            restFeesConfigurationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feesConfigurationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FeesConfigurationDTO.class
        );

        // Validate the FeesConfiguration in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFeesConfiguration = feesConfigurationMapper.toEntity(returnedFeesConfigurationDTO);
        assertFeesConfigurationUpdatableFieldsEquals(returnedFeesConfiguration, getPersistedFeesConfiguration(returnedFeesConfiguration));

        insertedFeesConfiguration = returnedFeesConfiguration;
    }

    @Test
    @Transactional
    void createFeesConfigurationWithExistingId() throws Exception {
        // Create the FeesConfiguration with an existing ID
        feesConfiguration.setId(1L);
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(feesConfiguration);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeesConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feesConfigurationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FeesConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        feesConfiguration.setTitle(null);

        // Create the FeesConfiguration, which fails.
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(feesConfiguration);

        restFeesConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feesConfigurationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        feesConfiguration.setAmount(null);

        // Create the FeesConfiguration, which fails.
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(feesConfiguration);

        restFeesConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feesConfigurationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsRecurringIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        feesConfiguration.setIsRecurring(null);

        // Create the FeesConfiguration, which fails.
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(feesConfiguration);

        restFeesConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feesConfigurationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFeesConfigurations() throws Exception {
        // Initialize the database
        insertedFeesConfiguration = feesConfigurationRepository.saveAndFlush(feesConfiguration);

        // Get all the feesConfigurationList
        restFeesConfigurationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feesConfiguration.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].isRecurring").value(hasItem(DEFAULT_IS_RECURRING)))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].configuredBy").value(hasItem(DEFAULT_CONFIGURED_BY)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getFeesConfiguration() throws Exception {
        // Initialize the database
        insertedFeesConfiguration = feesConfigurationRepository.saveAndFlush(feesConfiguration);

        // Get the feesConfiguration
        restFeesConfigurationMockMvc
            .perform(get(ENTITY_API_URL_ID, feesConfiguration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(feesConfiguration.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.isRecurring").value(DEFAULT_IS_RECURRING))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.configuredBy").value(DEFAULT_CONFIGURED_BY))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFeesConfiguration() throws Exception {
        // Get the feesConfiguration
        restFeesConfigurationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFeesConfiguration() throws Exception {
        // Initialize the database
        insertedFeesConfiguration = feesConfigurationRepository.saveAndFlush(feesConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feesConfiguration
        FeesConfiguration updatedFeesConfiguration = feesConfigurationRepository.findById(feesConfiguration.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFeesConfiguration are not directly saved in db
        em.detach(updatedFeesConfiguration);
        updatedFeesConfiguration
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .isRecurring(UPDATED_IS_RECURRING)
            .dueDate(UPDATED_DUE_DATE)
            .configuredBy(UPDATED_CONFIGURED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(updatedFeesConfiguration);

        restFeesConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, feesConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(feesConfigurationDTO))
            )
            .andExpect(status().isOk());

        // Validate the FeesConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFeesConfigurationToMatchAllProperties(updatedFeesConfiguration);
    }

    @Test
    @Transactional
    void putNonExistingFeesConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesConfiguration.setId(longCount.incrementAndGet());

        // Create the FeesConfiguration
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(feesConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeesConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, feesConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(feesConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeesConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFeesConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesConfiguration.setId(longCount.incrementAndGet());

        // Create the FeesConfiguration
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(feesConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeesConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(feesConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeesConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFeesConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesConfiguration.setId(longCount.incrementAndGet());

        // Create the FeesConfiguration
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(feesConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeesConfigurationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feesConfigurationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FeesConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFeesConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedFeesConfiguration = feesConfigurationRepository.saveAndFlush(feesConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feesConfiguration using partial update
        FeesConfiguration partialUpdatedFeesConfiguration = new FeesConfiguration();
        partialUpdatedFeesConfiguration.setId(feesConfiguration.getId());

        partialUpdatedFeesConfiguration
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .dueDate(UPDATED_DUE_DATE)
            .configuredBy(UPDATED_CONFIGURED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restFeesConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeesConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFeesConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the FeesConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFeesConfigurationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFeesConfiguration, feesConfiguration),
            getPersistedFeesConfiguration(feesConfiguration)
        );
    }

    @Test
    @Transactional
    void fullUpdateFeesConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedFeesConfiguration = feesConfigurationRepository.saveAndFlush(feesConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feesConfiguration using partial update
        FeesConfiguration partialUpdatedFeesConfiguration = new FeesConfiguration();
        partialUpdatedFeesConfiguration.setId(feesConfiguration.getId());

        partialUpdatedFeesConfiguration
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .isRecurring(UPDATED_IS_RECURRING)
            .dueDate(UPDATED_DUE_DATE)
            .configuredBy(UPDATED_CONFIGURED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restFeesConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeesConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFeesConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the FeesConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFeesConfigurationUpdatableFieldsEquals(
            partialUpdatedFeesConfiguration,
            getPersistedFeesConfiguration(partialUpdatedFeesConfiguration)
        );
    }

    @Test
    @Transactional
    void patchNonExistingFeesConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesConfiguration.setId(longCount.incrementAndGet());

        // Create the FeesConfiguration
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(feesConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeesConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, feesConfigurationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(feesConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeesConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFeesConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesConfiguration.setId(longCount.incrementAndGet());

        // Create the FeesConfiguration
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(feesConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeesConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(feesConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeesConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFeesConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feesConfiguration.setId(longCount.incrementAndGet());

        // Create the FeesConfiguration
        FeesConfigurationDTO feesConfigurationDTO = feesConfigurationMapper.toDto(feesConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeesConfigurationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(feesConfigurationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FeesConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFeesConfiguration() throws Exception {
        // Initialize the database
        insertedFeesConfiguration = feesConfigurationRepository.saveAndFlush(feesConfiguration);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the feesConfiguration
        restFeesConfigurationMockMvc
            .perform(delete(ENTITY_API_URL_ID, feesConfiguration.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return feesConfigurationRepository.count();
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

    protected FeesConfiguration getPersistedFeesConfiguration(FeesConfiguration feesConfiguration) {
        return feesConfigurationRepository.findById(feesConfiguration.getId()).orElseThrow();
    }

    protected void assertPersistedFeesConfigurationToMatchAllProperties(FeesConfiguration expectedFeesConfiguration) {
        assertFeesConfigurationAllPropertiesEquals(expectedFeesConfiguration, getPersistedFeesConfiguration(expectedFeesConfiguration));
    }

    protected void assertPersistedFeesConfigurationToMatchUpdatableProperties(FeesConfiguration expectedFeesConfiguration) {
        assertFeesConfigurationAllUpdatablePropertiesEquals(
            expectedFeesConfiguration,
            getPersistedFeesConfiguration(expectedFeesConfiguration)
        );
    }
}
