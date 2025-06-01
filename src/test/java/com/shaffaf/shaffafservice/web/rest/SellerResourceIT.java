package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.SellerAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.domain.enumeration.Status;
import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import com.shaffaf.shaffafservice.service.mapper.SellerMapper;
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
 * Integration tests for the {@link SellerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SellerResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

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

    private static final String ENTITY_API_URL = "/api/sellers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSellerMockMvc;

    private Seller seller;

    private Seller insertedSeller;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Seller createEntity() {
        return new Seller()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
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
    public static Seller createUpdatedEntity() {
        return new Seller()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        seller = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSeller != null) {
            sellerRepository.delete(insertedSeller);
            insertedSeller = null;
        }
    }

    @Test
    @Transactional
    void createSeller() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);
        var returnedSellerDTO = om.readValue(
            restSellerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SellerDTO.class
        );

        // Validate the Seller in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSeller = sellerMapper.toEntity(returnedSellerDTO);
        assertSellerUpdatableFieldsEquals(returnedSeller, getPersistedSeller(returnedSeller));

        insertedSeller = returnedSeller;
    }

    @Test
    @Transactional
    void createSellerWithExistingId() throws Exception {
        // Create the Seller with an existing ID
        seller.setId(1L);
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSellerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seller.setFirstName(null);

        // Create the Seller, which fails.
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        restSellerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seller.setLastName(null);

        // Create the Seller, which fails.
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        restSellerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seller.setEmail(null);

        // Create the Seller, which fails.
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        restSellerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seller.setPhoneNumber(null);

        // Create the Seller, which fails.
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        restSellerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seller.setStatus(null);

        // Create the Seller, which fails.
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        restSellerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSellers() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList
        restSellerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seller.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getSeller() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get the seller
        restSellerMockMvc
            .perform(get(ENTITY_API_URL_ID, seller.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(seller.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSeller() throws Exception {
        // Get the seller
        restSellerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSeller() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seller
        Seller updatedSeller = sellerRepository.findById(seller.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSeller are not directly saved in db
        em.detach(updatedSeller);
        updatedSeller
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        SellerDTO sellerDTO = sellerMapper.toDto(updatedSeller);

        restSellerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sellerDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSellerToMatchAllProperties(updatedSeller);
    }

    @Test
    @Transactional
    void putNonExistingSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sellerDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSellerWithPatch() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seller using partial update
        Seller partialUpdatedSeller = new Seller();
        partialUpdatedSeller.setId(seller.getId());

        partialUpdatedSeller
            .lastName(UPDATED_LAST_NAME)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restSellerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeller.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSeller))
            )
            .andExpect(status().isOk());

        // Validate the Seller in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSellerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSeller, seller), getPersistedSeller(seller));
    }

    @Test
    @Transactional
    void fullUpdateSellerWithPatch() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seller using partial update
        Seller partialUpdatedSeller = new Seller();
        partialUpdatedSeller.setId(seller.getId());

        partialUpdatedSeller
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restSellerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeller.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSeller))
            )
            .andExpect(status().isOk());

        // Validate the Seller in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSellerUpdatableFieldsEquals(partialUpdatedSeller, getPersistedSeller(partialUpdatedSeller));
    }

    @Test
    @Transactional
    void patchNonExistingSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sellerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSeller() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the seller
        restSellerMockMvc
            .perform(delete(ENTITY_API_URL_ID, seller.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sellerRepository.count();
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

    protected Seller getPersistedSeller(Seller seller) {
        return sellerRepository.findById(seller.getId()).orElseThrow();
    }

    protected void assertPersistedSellerToMatchAllProperties(Seller expectedSeller) {
        assertSellerAllPropertiesEquals(expectedSeller, getPersistedSeller(expectedSeller));
    }

    protected void assertPersistedSellerToMatchUpdatableProperties(Seller expectedSeller) {
        assertSellerAllUpdatablePropertiesEquals(expectedSeller, getPersistedSeller(expectedSeller));
    }
}
