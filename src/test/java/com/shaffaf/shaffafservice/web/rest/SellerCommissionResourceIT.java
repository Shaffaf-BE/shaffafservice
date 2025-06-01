package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.SellerCommissionAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.SellerCommission;
import com.shaffaf.shaffafservice.repository.SellerCommissionRepository;
import com.shaffaf.shaffafservice.service.dto.SellerCommissionDTO;
import com.shaffaf.shaffafservice.service.mapper.SellerCommissionMapper;
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
 * Integration tests for the {@link SellerCommissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SellerCommissionResourceIT {

    private static final Integer DEFAULT_COMMISSION_MONTH = 1;
    private static final Integer UPDATED_COMMISSION_MONTH = 2;

    private static final Integer DEFAULT_COMMISSION_YEAR = 1;
    private static final Integer UPDATED_COMMISSION_YEAR = 2;

    private static final BigDecimal DEFAULT_COMMISSION_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_COMMISSION_AMOUNT = new BigDecimal(2);

    private static final Instant DEFAULT_COMMISSION_PAID_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMMISSION_PAID_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_COMMISSION_PAID_BY = "AAAAAAAAAA";
    private static final String UPDATED_COMMISSION_PAID_BY = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/seller-commissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SellerCommissionRepository sellerCommissionRepository;

    @Autowired
    private SellerCommissionMapper sellerCommissionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSellerCommissionMockMvc;

    private SellerCommission sellerCommission;

    private SellerCommission insertedSellerCommission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SellerCommission createEntity() {
        return new SellerCommission()
            .commissionMonth(DEFAULT_COMMISSION_MONTH)
            .commissionYear(DEFAULT_COMMISSION_YEAR)
            .commissionAmount(DEFAULT_COMMISSION_AMOUNT)
            .commissionPaidOn(DEFAULT_COMMISSION_PAID_ON)
            .commissionPaidBy(DEFAULT_COMMISSION_PAID_BY)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
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
    public static SellerCommission createUpdatedEntity() {
        return new SellerCommission()
            .commissionMonth(UPDATED_COMMISSION_MONTH)
            .commissionYear(UPDATED_COMMISSION_YEAR)
            .commissionAmount(UPDATED_COMMISSION_AMOUNT)
            .commissionPaidOn(UPDATED_COMMISSION_PAID_ON)
            .commissionPaidBy(UPDATED_COMMISSION_PAID_BY)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        sellerCommission = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSellerCommission != null) {
            sellerCommissionRepository.delete(insertedSellerCommission);
            insertedSellerCommission = null;
        }
    }

    @Test
    @Transactional
    void createSellerCommission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SellerCommission
        SellerCommissionDTO sellerCommissionDTO = sellerCommissionMapper.toDto(sellerCommission);
        var returnedSellerCommissionDTO = om.readValue(
            restSellerCommissionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerCommissionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SellerCommissionDTO.class
        );

        // Validate the SellerCommission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSellerCommission = sellerCommissionMapper.toEntity(returnedSellerCommissionDTO);
        assertSellerCommissionUpdatableFieldsEquals(returnedSellerCommission, getPersistedSellerCommission(returnedSellerCommission));

        insertedSellerCommission = returnedSellerCommission;
    }

    @Test
    @Transactional
    void createSellerCommissionWithExistingId() throws Exception {
        // Create the SellerCommission with an existing ID
        sellerCommission.setId(1L);
        SellerCommissionDTO sellerCommissionDTO = sellerCommissionMapper.toDto(sellerCommission);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSellerCommissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerCommissionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SellerCommission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sellerCommission.setPhoneNumber(null);

        // Create the SellerCommission, which fails.
        SellerCommissionDTO sellerCommissionDTO = sellerCommissionMapper.toDto(sellerCommission);

        restSellerCommissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerCommissionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSellerCommissions() throws Exception {
        // Initialize the database
        insertedSellerCommission = sellerCommissionRepository.saveAndFlush(sellerCommission);

        // Get all the sellerCommissionList
        restSellerCommissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sellerCommission.getId().intValue())))
            .andExpect(jsonPath("$.[*].commissionMonth").value(hasItem(DEFAULT_COMMISSION_MONTH)))
            .andExpect(jsonPath("$.[*].commissionYear").value(hasItem(DEFAULT_COMMISSION_YEAR)))
            .andExpect(jsonPath("$.[*].commissionAmount").value(hasItem(sameNumber(DEFAULT_COMMISSION_AMOUNT))))
            .andExpect(jsonPath("$.[*].commissionPaidOn").value(hasItem(DEFAULT_COMMISSION_PAID_ON.toString())))
            .andExpect(jsonPath("$.[*].commissionPaidBy").value(hasItem(DEFAULT_COMMISSION_PAID_BY)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getSellerCommission() throws Exception {
        // Initialize the database
        insertedSellerCommission = sellerCommissionRepository.saveAndFlush(sellerCommission);

        // Get the sellerCommission
        restSellerCommissionMockMvc
            .perform(get(ENTITY_API_URL_ID, sellerCommission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sellerCommission.getId().intValue()))
            .andExpect(jsonPath("$.commissionMonth").value(DEFAULT_COMMISSION_MONTH))
            .andExpect(jsonPath("$.commissionYear").value(DEFAULT_COMMISSION_YEAR))
            .andExpect(jsonPath("$.commissionAmount").value(sameNumber(DEFAULT_COMMISSION_AMOUNT)))
            .andExpect(jsonPath("$.commissionPaidOn").value(DEFAULT_COMMISSION_PAID_ON.toString()))
            .andExpect(jsonPath("$.commissionPaidBy").value(DEFAULT_COMMISSION_PAID_BY))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSellerCommission() throws Exception {
        // Get the sellerCommission
        restSellerCommissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSellerCommission() throws Exception {
        // Initialize the database
        insertedSellerCommission = sellerCommissionRepository.saveAndFlush(sellerCommission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sellerCommission
        SellerCommission updatedSellerCommission = sellerCommissionRepository.findById(sellerCommission.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSellerCommission are not directly saved in db
        em.detach(updatedSellerCommission);
        updatedSellerCommission
            .commissionMonth(UPDATED_COMMISSION_MONTH)
            .commissionYear(UPDATED_COMMISSION_YEAR)
            .commissionAmount(UPDATED_COMMISSION_AMOUNT)
            .commissionPaidOn(UPDATED_COMMISSION_PAID_ON)
            .commissionPaidBy(UPDATED_COMMISSION_PAID_BY)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        SellerCommissionDTO sellerCommissionDTO = sellerCommissionMapper.toDto(updatedSellerCommission);

        restSellerCommissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sellerCommissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sellerCommissionDTO))
            )
            .andExpect(status().isOk());

        // Validate the SellerCommission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSellerCommissionToMatchAllProperties(updatedSellerCommission);
    }

    @Test
    @Transactional
    void putNonExistingSellerCommission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sellerCommission.setId(longCount.incrementAndGet());

        // Create the SellerCommission
        SellerCommissionDTO sellerCommissionDTO = sellerCommissionMapper.toDto(sellerCommission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSellerCommissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sellerCommissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sellerCommissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SellerCommission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSellerCommission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sellerCommission.setId(longCount.incrementAndGet());

        // Create the SellerCommission
        SellerCommissionDTO sellerCommissionDTO = sellerCommissionMapper.toDto(sellerCommission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerCommissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sellerCommissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SellerCommission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSellerCommission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sellerCommission.setId(longCount.incrementAndGet());

        // Create the SellerCommission
        SellerCommissionDTO sellerCommissionDTO = sellerCommissionMapper.toDto(sellerCommission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerCommissionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerCommissionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SellerCommission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSellerCommissionWithPatch() throws Exception {
        // Initialize the database
        insertedSellerCommission = sellerCommissionRepository.saveAndFlush(sellerCommission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sellerCommission using partial update
        SellerCommission partialUpdatedSellerCommission = new SellerCommission();
        partialUpdatedSellerCommission.setId(sellerCommission.getId());

        partialUpdatedSellerCommission
            .commissionYear(UPDATED_COMMISSION_YEAR)
            .commissionAmount(UPDATED_COMMISSION_AMOUNT)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restSellerCommissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSellerCommission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSellerCommission))
            )
            .andExpect(status().isOk());

        // Validate the SellerCommission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSellerCommissionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSellerCommission, sellerCommission),
            getPersistedSellerCommission(sellerCommission)
        );
    }

    @Test
    @Transactional
    void fullUpdateSellerCommissionWithPatch() throws Exception {
        // Initialize the database
        insertedSellerCommission = sellerCommissionRepository.saveAndFlush(sellerCommission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sellerCommission using partial update
        SellerCommission partialUpdatedSellerCommission = new SellerCommission();
        partialUpdatedSellerCommission.setId(sellerCommission.getId());

        partialUpdatedSellerCommission
            .commissionMonth(UPDATED_COMMISSION_MONTH)
            .commissionYear(UPDATED_COMMISSION_YEAR)
            .commissionAmount(UPDATED_COMMISSION_AMOUNT)
            .commissionPaidOn(UPDATED_COMMISSION_PAID_ON)
            .commissionPaidBy(UPDATED_COMMISSION_PAID_BY)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restSellerCommissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSellerCommission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSellerCommission))
            )
            .andExpect(status().isOk());

        // Validate the SellerCommission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSellerCommissionUpdatableFieldsEquals(
            partialUpdatedSellerCommission,
            getPersistedSellerCommission(partialUpdatedSellerCommission)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSellerCommission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sellerCommission.setId(longCount.incrementAndGet());

        // Create the SellerCommission
        SellerCommissionDTO sellerCommissionDTO = sellerCommissionMapper.toDto(sellerCommission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSellerCommissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sellerCommissionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sellerCommissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SellerCommission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSellerCommission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sellerCommission.setId(longCount.incrementAndGet());

        // Create the SellerCommission
        SellerCommissionDTO sellerCommissionDTO = sellerCommissionMapper.toDto(sellerCommission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerCommissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sellerCommissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SellerCommission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSellerCommission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sellerCommission.setId(longCount.incrementAndGet());

        // Create the SellerCommission
        SellerCommissionDTO sellerCommissionDTO = sellerCommissionMapper.toDto(sellerCommission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerCommissionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sellerCommissionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SellerCommission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSellerCommission() throws Exception {
        // Initialize the database
        insertedSellerCommission = sellerCommissionRepository.saveAndFlush(sellerCommission);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sellerCommission
        restSellerCommissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, sellerCommission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sellerCommissionRepository.count();
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

    protected SellerCommission getPersistedSellerCommission(SellerCommission sellerCommission) {
        return sellerCommissionRepository.findById(sellerCommission.getId()).orElseThrow();
    }

    protected void assertPersistedSellerCommissionToMatchAllProperties(SellerCommission expectedSellerCommission) {
        assertSellerCommissionAllPropertiesEquals(expectedSellerCommission, getPersistedSellerCommission(expectedSellerCommission));
    }

    protected void assertPersistedSellerCommissionToMatchUpdatableProperties(SellerCommission expectedSellerCommission) {
        assertSellerCommissionAllUpdatablePropertiesEquals(
            expectedSellerCommission,
            getPersistedSellerCommission(expectedSellerCommission)
        );
    }
}
