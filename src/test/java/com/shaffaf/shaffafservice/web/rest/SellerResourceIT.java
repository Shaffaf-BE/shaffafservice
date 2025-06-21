package com.shaffaf.shaffafservice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.domain.enumeration.Status;
import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.service.SellerService;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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
class SellerResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "test@example.com";
    private static final String UPDATED_EMAIL = "updated@example.com";

    private static final String DEFAULT_PHONE_NUMBER = "+923311234567";
    private static final String UPDATED_PHONE_NUMBER = "+923311234568";
    private static final String INVALID_PHONE_NUMBER = "12345678";

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final String ENTITY_API_URL = "/api/sellers/v1";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_API_URL_MANY = ENTITY_API_URL + "/get-many-sellers";
    private static final String ENTITY_API_URL_GET_ONE = ENTITY_API_URL + "/get-seller/{id}";
    private static final String ENTITY_API_URL_CREATE = ENTITY_API_URL + "/create-seller";
    private static final String ENTITY_API_URL_UPDATE = ENTITY_API_URL + "/update-seller";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt());

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSellerMockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Seller seller;
    private SellerDTO sellerDTO;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Seller createEntity(EntityManager em) {
        Seller seller = new Seller()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .status(DEFAULT_STATUS);
        return seller;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Seller createUpdatedEntity(EntityManager em) {
        Seller seller = new Seller()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .status(UPDATED_STATUS);
        return seller;
    }

    /**
     * Convert entity to DTO
     */
    private SellerDTO convertToDto(Seller seller) {
        SellerDTO dto = new SellerDTO();
        dto.setId(seller.getId());
        dto.setFirstName(seller.getFirstName());
        dto.setLastName(seller.getLastName());
        dto.setEmail(seller.getEmail());
        dto.setPhoneNumber(seller.getPhoneNumber());
        dto.setStatus(seller.getStatus());
        return dto;
    }

    @BeforeEach
    public void initTest() {
        seller = createEntity(em);
        sellerDTO = convertToDto(seller);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void createSellerNative() throws Exception {
        int databaseSizeBeforeCreate = sellerRepository.findAll().size();
        // Create the Seller
        restSellerMockMvc
            .perform(
                post(ENTITY_API_URL_CREATE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sellerDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Seller in the database
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeCreate + 1);
        Seller testSeller = sellerList.get(sellerList.size() - 1);
        assertThat(testSeller.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testSeller.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testSeller.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testSeller.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testSeller.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void createSellerWithExistingId() throws Exception {
        // Create the Seller with an existing ID
        seller.setId(1L);
        SellerDTO sellerDTO = convertToDto(seller);

        int databaseSizeBeforeCreate = sellerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSellerMockMvc
            .perform(
                post(ENTITY_API_URL_CREATE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void createSellerWithInvalidPhone() throws Exception {
        // Set invalid phone number
        sellerDTO.setPhoneNumber(INVALID_PHONE_NUMBER);

        int databaseSizeBeforeCreate = sellerRepository.findAll().size();

        // Phone number validation should fail
        restSellerMockMvc
            .perform(
                post(ENTITY_API_URL_CREATE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database - nothing should change
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void updateSellerNative() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        int databaseSizeBeforeUpdate = sellerRepository.findAll().size();

        // Update the seller
        Seller updatedSeller = sellerRepository.findById(seller.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSeller are not directly saved in db
        em.detach(updatedSeller);
        updatedSeller
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .status(UPDATED_STATUS);
        SellerDTO updatedSellerDTO = convertToDto(updatedSeller);

        restSellerMockMvc
            .perform(
                put(ENTITY_API_URL_UPDATE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSellerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Seller in the database
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeUpdate);
        Seller testSeller = sellerList.get(sellerList.size() - 1);
        assertThat(testSeller.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testSeller.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testSeller.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testSeller.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testSeller.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void updateSellerWithInvalidPhone() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        int databaseSizeBeforeUpdate = sellerRepository.findAll().size();

        // Update the seller with invalid phone
        Seller updatedSeller = sellerRepository.findById(seller.getId()).orElseThrow();
        em.detach(updatedSeller);

        updatedSeller
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(INVALID_PHONE_NUMBER)
            .status(UPDATED_STATUS);
        SellerDTO updatedSellerDTO = convertToDto(updatedSeller);

        restSellerMockMvc
            .perform(
                put(ENTITY_API_URL_UPDATE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database - should remain unchanged
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void updateNonExistingSellerNative() throws Exception {
        int databaseSizeBeforeUpdate = sellerRepository.findAll().size();
        seller.setId(count.incrementAndGet());
        SellerDTO sellerDTO = convertToDto(seller);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(
                put(ENTITY_API_URL_UPDATE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void updateSellerWithoutAdminRole() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Update the seller
        Seller updatedSeller = sellerRepository.findById(seller.getId()).orElseThrow();
        em.detach(updatedSeller);
        SellerDTO updatedSellerDTO = convertToDto(updatedSeller);

        // Without admin role should be forbidden
        restSellerMockMvc
            .perform(
                put(ENTITY_API_URL_UPDATE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSellerDTO))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void partialUpdateSeller() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        int databaseSizeBeforeUpdate = sellerRepository.findAll().size();

        // Update the seller using partial update
        Seller partialUpdatedSeller = new Seller();
        partialUpdatedSeller.setId(seller.getId());
        partialUpdatedSeller.setFirstName(UPDATED_FIRST_NAME);
        partialUpdatedSeller.setPhoneNumber(UPDATED_PHONE_NUMBER);

        SellerDTO partialUpdatedSellerDTO = convertToDto(partialUpdatedSeller);

        restSellerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSellerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSellerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Seller in the database
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeUpdate);
        Seller testSeller = sellerList.get(sellerList.size() - 1);
        assertThat(testSeller.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testSeller.getEmail()).isEqualTo(DEFAULT_EMAIL); // Email unchanged
        assertThat(testSeller.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testSeller.getStatus()).isEqualTo(DEFAULT_STATUS); // Status unchanged
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void partialUpdateSellerWithInvalidId() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        Long invalidId = Long.MAX_VALUE;

        // Update with mismatch ID
        SellerDTO partialUpdatedSellerDTO = convertToDto(seller);
        partialUpdatedSellerDTO.setId(invalidId);

        // Patch with mismatched IDs should throw BadRequest
        restSellerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, seller.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSellerDTO))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void getAllSellersOptimized() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellers
        restSellerMockMvc
            .perform(get(ENTITY_API_URL_MANY))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seller.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getAllSellersOptimizedWithoutAdminRole() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellers - should be forbidden without admin role
        restSellerMockMvc.perform(get(ENTITY_API_URL_MANY)).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void getSellerSecureOptimized() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get the seller
        restSellerMockMvc
            .perform(get(ENTITY_API_URL_GET_ONE, seller.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(seller.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void getNonExistingSellerSecureOptimized() throws Exception {
        // Get the seller
        restSellerMockMvc.perform(get(ENTITY_API_URL_GET_ONE, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void getSellerWithInvalidIdSecureOptimized() throws Exception {
        // Get with invalid ID should return bad request
        restSellerMockMvc.perform(get(ENTITY_API_URL_GET_ONE, -1)).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void getSellerSecureOptimizedWithoutAdminRole() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get the seller - should be forbidden without admin role
        restSellerMockMvc.perform(get(ENTITY_API_URL_GET_ONE, seller.getId())).andExpect(status().isForbidden());
    }
}
