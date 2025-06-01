package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.UnionMemberAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.UnionMember;
import com.shaffaf.shaffafservice.repository.UnionMemberRepository;
import com.shaffaf.shaffafservice.service.dto.UnionMemberDTO;
import com.shaffaf.shaffafservice.service.mapper.UnionMemberMapper;
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
 * Integration tests for the {@link UnionMemberResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UnionMemberResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/union-members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UnionMemberRepository unionMemberRepository;

    @Autowired
    private UnionMemberMapper unionMemberMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUnionMemberMockMvc;

    private UnionMember unionMember;

    private UnionMember insertedUnionMember;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UnionMember createEntity() {
        return new UnionMember()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
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
    public static UnionMember createUpdatedEntity() {
        return new UnionMember()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        unionMember = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUnionMember != null) {
            unionMemberRepository.delete(insertedUnionMember);
            insertedUnionMember = null;
        }
    }

    @Test
    @Transactional
    void createUnionMember() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UnionMember
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);
        var returnedUnionMemberDTO = om.readValue(
            restUnionMemberMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(unionMemberDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UnionMemberDTO.class
        );

        // Validate the UnionMember in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUnionMember = unionMemberMapper.toEntity(returnedUnionMemberDTO);
        assertUnionMemberUpdatableFieldsEquals(returnedUnionMember, getPersistedUnionMember(returnedUnionMember));

        insertedUnionMember = returnedUnionMember;
    }

    @Test
    @Transactional
    void createUnionMemberWithExistingId() throws Exception {
        // Create the UnionMember with an existing ID
        unionMember.setId(1L);
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUnionMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(unionMemberDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UnionMember in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        unionMember.setFirstName(null);

        // Create the UnionMember, which fails.
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);

        restUnionMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(unionMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        unionMember.setLastName(null);

        // Create the UnionMember, which fails.
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);

        restUnionMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(unionMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        unionMember.setEmail(null);

        // Create the UnionMember, which fails.
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);

        restUnionMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(unionMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        unionMember.setPhoneNumber(null);

        // Create the UnionMember, which fails.
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);

        restUnionMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(unionMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUnionMembers() throws Exception {
        // Initialize the database
        insertedUnionMember = unionMemberRepository.saveAndFlush(unionMember);

        // Get all the unionMemberList
        restUnionMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(unionMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getUnionMember() throws Exception {
        // Initialize the database
        insertedUnionMember = unionMemberRepository.saveAndFlush(unionMember);

        // Get the unionMember
        restUnionMemberMockMvc
            .perform(get(ENTITY_API_URL_ID, unionMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(unionMember.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUnionMember() throws Exception {
        // Get the unionMember
        restUnionMemberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUnionMember() throws Exception {
        // Initialize the database
        insertedUnionMember = unionMemberRepository.saveAndFlush(unionMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the unionMember
        UnionMember updatedUnionMember = unionMemberRepository.findById(unionMember.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUnionMember are not directly saved in db
        em.detach(updatedUnionMember);
        updatedUnionMember
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(updatedUnionMember);

        restUnionMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, unionMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(unionMemberDTO))
            )
            .andExpect(status().isOk());

        // Validate the UnionMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUnionMemberToMatchAllProperties(updatedUnionMember);
    }

    @Test
    @Transactional
    void putNonExistingUnionMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unionMember.setId(longCount.incrementAndGet());

        // Create the UnionMember
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUnionMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, unionMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(unionMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UnionMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUnionMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unionMember.setId(longCount.incrementAndGet());

        // Create the UnionMember
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUnionMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(unionMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UnionMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUnionMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unionMember.setId(longCount.incrementAndGet());

        // Create the UnionMember
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUnionMemberMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(unionMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UnionMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUnionMemberWithPatch() throws Exception {
        // Initialize the database
        insertedUnionMember = unionMemberRepository.saveAndFlush(unionMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the unionMember using partial update
        UnionMember partialUpdatedUnionMember = new UnionMember();
        partialUpdatedUnionMember.setId(unionMember.getId());

        partialUpdatedUnionMember
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restUnionMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUnionMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUnionMember))
            )
            .andExpect(status().isOk());

        // Validate the UnionMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUnionMemberUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUnionMember, unionMember),
            getPersistedUnionMember(unionMember)
        );
    }

    @Test
    @Transactional
    void fullUpdateUnionMemberWithPatch() throws Exception {
        // Initialize the database
        insertedUnionMember = unionMemberRepository.saveAndFlush(unionMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the unionMember using partial update
        UnionMember partialUpdatedUnionMember = new UnionMember();
        partialUpdatedUnionMember.setId(unionMember.getId());

        partialUpdatedUnionMember
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restUnionMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUnionMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUnionMember))
            )
            .andExpect(status().isOk());

        // Validate the UnionMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUnionMemberUpdatableFieldsEquals(partialUpdatedUnionMember, getPersistedUnionMember(partialUpdatedUnionMember));
    }

    @Test
    @Transactional
    void patchNonExistingUnionMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unionMember.setId(longCount.incrementAndGet());

        // Create the UnionMember
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUnionMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, unionMemberDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(unionMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UnionMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUnionMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unionMember.setId(longCount.incrementAndGet());

        // Create the UnionMember
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUnionMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(unionMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UnionMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUnionMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        unionMember.setId(longCount.incrementAndGet());

        // Create the UnionMember
        UnionMemberDTO unionMemberDTO = unionMemberMapper.toDto(unionMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUnionMemberMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(unionMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UnionMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUnionMember() throws Exception {
        // Initialize the database
        insertedUnionMember = unionMemberRepository.saveAndFlush(unionMember);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the unionMember
        restUnionMemberMockMvc
            .perform(delete(ENTITY_API_URL_ID, unionMember.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return unionMemberRepository.count();
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

    protected UnionMember getPersistedUnionMember(UnionMember unionMember) {
        return unionMemberRepository.findById(unionMember.getId()).orElseThrow();
    }

    protected void assertPersistedUnionMemberToMatchAllProperties(UnionMember expectedUnionMember) {
        assertUnionMemberAllPropertiesEquals(expectedUnionMember, getPersistedUnionMember(expectedUnionMember));
    }

    protected void assertPersistedUnionMemberToMatchUpdatableProperties(UnionMember expectedUnionMember) {
        assertUnionMemberAllUpdatablePropertiesEquals(expectedUnionMember, getPersistedUnionMember(expectedUnionMember));
    }
}
