package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.ComplainCommentAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.ComplainComment;
import com.shaffaf.shaffafservice.repository.ComplainCommentRepository;
import com.shaffaf.shaffafservice.service.dto.ComplainCommentDTO;
import com.shaffaf.shaffafservice.service.mapper.ComplainCommentMapper;
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
 * Integration tests for the {@link ComplainCommentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ComplainCommentResourceIT {

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String DEFAULT_ADDED_BY = "AAAAAAAAAA";
    private static final String UPDATED_ADDED_BY = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/complain-comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ComplainCommentRepository complainCommentRepository;

    @Autowired
    private ComplainCommentMapper complainCommentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restComplainCommentMockMvc;

    private ComplainComment complainComment;

    private ComplainComment insertedComplainComment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ComplainComment createEntity() {
        return new ComplainComment()
            .comment(DEFAULT_COMMENT)
            .addedBy(DEFAULT_ADDED_BY)
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
    public static ComplainComment createUpdatedEntity() {
        return new ComplainComment()
            .comment(UPDATED_COMMENT)
            .addedBy(UPDATED_ADDED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        complainComment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedComplainComment != null) {
            complainCommentRepository.delete(insertedComplainComment);
            insertedComplainComment = null;
        }
    }

    @Test
    @Transactional
    void createComplainComment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ComplainComment
        ComplainCommentDTO complainCommentDTO = complainCommentMapper.toDto(complainComment);
        var returnedComplainCommentDTO = om.readValue(
            restComplainCommentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainCommentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ComplainCommentDTO.class
        );

        // Validate the ComplainComment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedComplainComment = complainCommentMapper.toEntity(returnedComplainCommentDTO);
        assertComplainCommentUpdatableFieldsEquals(returnedComplainComment, getPersistedComplainComment(returnedComplainComment));

        insertedComplainComment = returnedComplainComment;
    }

    @Test
    @Transactional
    void createComplainCommentWithExistingId() throws Exception {
        // Create the ComplainComment with an existing ID
        complainComment.setId(1L);
        ComplainCommentDTO complainCommentDTO = complainCommentMapper.toDto(complainComment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restComplainCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainCommentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ComplainComment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCommentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        complainComment.setComment(null);

        // Create the ComplainComment, which fails.
        ComplainCommentDTO complainCommentDTO = complainCommentMapper.toDto(complainComment);

        restComplainCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainCommentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllComplainComments() throws Exception {
        // Initialize the database
        insertedComplainComment = complainCommentRepository.saveAndFlush(complainComment);

        // Get all the complainCommentList
        restComplainCommentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(complainComment.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].addedBy").value(hasItem(DEFAULT_ADDED_BY)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getComplainComment() throws Exception {
        // Initialize the database
        insertedComplainComment = complainCommentRepository.saveAndFlush(complainComment);

        // Get the complainComment
        restComplainCommentMockMvc
            .perform(get(ENTITY_API_URL_ID, complainComment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(complainComment.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.addedBy").value(DEFAULT_ADDED_BY))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingComplainComment() throws Exception {
        // Get the complainComment
        restComplainCommentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingComplainComment() throws Exception {
        // Initialize the database
        insertedComplainComment = complainCommentRepository.saveAndFlush(complainComment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complainComment
        ComplainComment updatedComplainComment = complainCommentRepository.findById(complainComment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedComplainComment are not directly saved in db
        em.detach(updatedComplainComment);
        updatedComplainComment
            .comment(UPDATED_COMMENT)
            .addedBy(UPDATED_ADDED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        ComplainCommentDTO complainCommentDTO = complainCommentMapper.toDto(updatedComplainComment);

        restComplainCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, complainCommentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainCommentDTO))
            )
            .andExpect(status().isOk());

        // Validate the ComplainComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedComplainCommentToMatchAllProperties(updatedComplainComment);
    }

    @Test
    @Transactional
    void putNonExistingComplainComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainComment.setId(longCount.incrementAndGet());

        // Create the ComplainComment
        ComplainCommentDTO complainCommentDTO = complainCommentMapper.toDto(complainComment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComplainCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, complainCommentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchComplainComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainComment.setId(longCount.incrementAndGet());

        // Create the ComplainComment
        ComplainCommentDTO complainCommentDTO = complainCommentMapper.toDto(complainComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(complainCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamComplainComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainComment.setId(longCount.incrementAndGet());

        // Create the ComplainComment
        ComplainCommentDTO complainCommentDTO = complainCommentMapper.toDto(complainComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainCommentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(complainCommentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ComplainComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateComplainCommentWithPatch() throws Exception {
        // Initialize the database
        insertedComplainComment = complainCommentRepository.saveAndFlush(complainComment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complainComment using partial update
        ComplainComment partialUpdatedComplainComment = new ComplainComment();
        partialUpdatedComplainComment.setId(complainComment.getId());

        partialUpdatedComplainComment
            .comment(UPDATED_COMMENT)
            .addedBy(UPDATED_ADDED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restComplainCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComplainComment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComplainComment))
            )
            .andExpect(status().isOk());

        // Validate the ComplainComment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComplainCommentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedComplainComment, complainComment),
            getPersistedComplainComment(complainComment)
        );
    }

    @Test
    @Transactional
    void fullUpdateComplainCommentWithPatch() throws Exception {
        // Initialize the database
        insertedComplainComment = complainCommentRepository.saveAndFlush(complainComment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the complainComment using partial update
        ComplainComment partialUpdatedComplainComment = new ComplainComment();
        partialUpdatedComplainComment.setId(complainComment.getId());

        partialUpdatedComplainComment
            .comment(UPDATED_COMMENT)
            .addedBy(UPDATED_ADDED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restComplainCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComplainComment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComplainComment))
            )
            .andExpect(status().isOk());

        // Validate the ComplainComment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComplainCommentUpdatableFieldsEquals(
            partialUpdatedComplainComment,
            getPersistedComplainComment(partialUpdatedComplainComment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingComplainComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainComment.setId(longCount.incrementAndGet());

        // Create the ComplainComment
        ComplainCommentDTO complainCommentDTO = complainCommentMapper.toDto(complainComment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComplainCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, complainCommentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(complainCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchComplainComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainComment.setId(longCount.incrementAndGet());

        // Create the ComplainComment
        ComplainCommentDTO complainCommentDTO = complainCommentMapper.toDto(complainComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(complainCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComplainComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamComplainComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        complainComment.setId(longCount.incrementAndGet());

        // Create the ComplainComment
        ComplainCommentDTO complainCommentDTO = complainCommentMapper.toDto(complainComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplainCommentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(complainCommentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ComplainComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteComplainComment() throws Exception {
        // Initialize the database
        insertedComplainComment = complainCommentRepository.saveAndFlush(complainComment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the complainComment
        restComplainCommentMockMvc
            .perform(delete(ENTITY_API_URL_ID, complainComment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return complainCommentRepository.count();
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

    protected ComplainComment getPersistedComplainComment(ComplainComment complainComment) {
        return complainCommentRepository.findById(complainComment.getId()).orElseThrow();
    }

    protected void assertPersistedComplainCommentToMatchAllProperties(ComplainComment expectedComplainComment) {
        assertComplainCommentAllPropertiesEquals(expectedComplainComment, getPersistedComplainComment(expectedComplainComment));
    }

    protected void assertPersistedComplainCommentToMatchUpdatableProperties(ComplainComment expectedComplainComment) {
        assertComplainCommentAllUpdatablePropertiesEquals(expectedComplainComment, getPersistedComplainComment(expectedComplainComment));
    }
}
