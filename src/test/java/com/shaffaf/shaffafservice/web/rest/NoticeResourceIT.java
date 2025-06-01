package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.NoticeAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.Notice;
import com.shaffaf.shaffafservice.repository.NoticeRepository;
import com.shaffaf.shaffafservice.service.dto.NoticeDTO;
import com.shaffaf.shaffafservice.service.mapper.NoticeMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link NoticeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NoticeResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DISPLAY_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DISPLAY_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DISPLAY_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DISPLAY_END_DATE = LocalDate.now(ZoneId.systemDefault());

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

    private static final String ENTITY_API_URL = "/api/notices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNoticeMockMvc;

    private Notice notice;

    private Notice insertedNotice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notice createEntity() {
        return new Notice()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .displayStartDate(DEFAULT_DISPLAY_START_DATE)
            .displayEndDate(DEFAULT_DISPLAY_END_DATE)
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
    public static Notice createUpdatedEntity() {
        return new Notice()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .displayStartDate(UPDATED_DISPLAY_START_DATE)
            .displayEndDate(UPDATED_DISPLAY_END_DATE)
            .addedBy(UPDATED_ADDED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        notice = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotice != null) {
            noticeRepository.delete(insertedNotice);
            insertedNotice = null;
        }
    }

    @Test
    @Transactional
    void createNotice() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Notice
        NoticeDTO noticeDTO = noticeMapper.toDto(notice);
        var returnedNoticeDTO = om.readValue(
            restNoticeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(noticeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NoticeDTO.class
        );

        // Validate the Notice in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotice = noticeMapper.toEntity(returnedNoticeDTO);
        assertNoticeUpdatableFieldsEquals(returnedNotice, getPersistedNotice(returnedNotice));

        insertedNotice = returnedNotice;
    }

    @Test
    @Transactional
    void createNoticeWithExistingId() throws Exception {
        // Create the Notice with an existing ID
        notice.setId(1L);
        NoticeDTO noticeDTO = noticeMapper.toDto(notice);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNoticeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(noticeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Notice in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notice.setTitle(null);

        // Create the Notice, which fails.
        NoticeDTO noticeDTO = noticeMapper.toDto(notice);

        restNoticeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(noticeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotices() throws Exception {
        // Initialize the database
        insertedNotice = noticeRepository.saveAndFlush(notice);

        // Get all the noticeList
        restNoticeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notice.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].displayStartDate").value(hasItem(DEFAULT_DISPLAY_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].displayEndDate").value(hasItem(DEFAULT_DISPLAY_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].addedBy").value(hasItem(DEFAULT_ADDED_BY)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getNotice() throws Exception {
        // Initialize the database
        insertedNotice = noticeRepository.saveAndFlush(notice);

        // Get the notice
        restNoticeMockMvc
            .perform(get(ENTITY_API_URL_ID, notice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notice.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.displayStartDate").value(DEFAULT_DISPLAY_START_DATE.toString()))
            .andExpect(jsonPath("$.displayEndDate").value(DEFAULT_DISPLAY_END_DATE.toString()))
            .andExpect(jsonPath("$.addedBy").value(DEFAULT_ADDED_BY))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNotice() throws Exception {
        // Get the notice
        restNoticeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotice() throws Exception {
        // Initialize the database
        insertedNotice = noticeRepository.saveAndFlush(notice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notice
        Notice updatedNotice = noticeRepository.findById(notice.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotice are not directly saved in db
        em.detach(updatedNotice);
        updatedNotice
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .displayStartDate(UPDATED_DISPLAY_START_DATE)
            .displayEndDate(UPDATED_DISPLAY_END_DATE)
            .addedBy(UPDATED_ADDED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        NoticeDTO noticeDTO = noticeMapper.toDto(updatedNotice);

        restNoticeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, noticeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(noticeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Notice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNoticeToMatchAllProperties(updatedNotice);
    }

    @Test
    @Transactional
    void putNonExistingNotice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notice.setId(longCount.incrementAndGet());

        // Create the Notice
        NoticeDTO noticeDTO = noticeMapper.toDto(notice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, noticeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(noticeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notice.setId(longCount.incrementAndGet());

        // Create the Notice
        NoticeDTO noticeDTO = noticeMapper.toDto(notice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(noticeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notice.setId(longCount.incrementAndGet());

        // Create the Notice
        NoticeDTO noticeDTO = noticeMapper.toDto(notice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(noticeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNoticeWithPatch() throws Exception {
        // Initialize the database
        insertedNotice = noticeRepository.saveAndFlush(notice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notice using partial update
        Notice partialUpdatedNotice = new Notice();
        partialUpdatedNotice.setId(notice.getId());

        partialUpdatedNotice
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .addedBy(UPDATED_ADDED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restNoticeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotice))
            )
            .andExpect(status().isOk());

        // Validate the Notice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNoticeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedNotice, notice), getPersistedNotice(notice));
    }

    @Test
    @Transactional
    void fullUpdateNoticeWithPatch() throws Exception {
        // Initialize the database
        insertedNotice = noticeRepository.saveAndFlush(notice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notice using partial update
        Notice partialUpdatedNotice = new Notice();
        partialUpdatedNotice.setId(notice.getId());

        partialUpdatedNotice
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .displayStartDate(UPDATED_DISPLAY_START_DATE)
            .displayEndDate(UPDATED_DISPLAY_END_DATE)
            .addedBy(UPDATED_ADDED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restNoticeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotice))
            )
            .andExpect(status().isOk());

        // Validate the Notice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNoticeUpdatableFieldsEquals(partialUpdatedNotice, getPersistedNotice(partialUpdatedNotice));
    }

    @Test
    @Transactional
    void patchNonExistingNotice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notice.setId(longCount.incrementAndGet());

        // Create the Notice
        NoticeDTO noticeDTO = noticeMapper.toDto(notice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, noticeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(noticeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notice.setId(longCount.incrementAndGet());

        // Create the Notice
        NoticeDTO noticeDTO = noticeMapper.toDto(notice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(noticeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notice.setId(longCount.incrementAndGet());

        // Create the Notice
        NoticeDTO noticeDTO = noticeMapper.toDto(notice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(noticeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotice() throws Exception {
        // Initialize the database
        insertedNotice = noticeRepository.saveAndFlush(notice);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notice
        restNoticeMockMvc
            .perform(delete(ENTITY_API_URL_ID, notice.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return noticeRepository.count();
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

    protected Notice getPersistedNotice(Notice notice) {
        return noticeRepository.findById(notice.getId()).orElseThrow();
    }

    protected void assertPersistedNoticeToMatchAllProperties(Notice expectedNotice) {
        assertNoticeAllPropertiesEquals(expectedNotice, getPersistedNotice(expectedNotice));
    }

    protected void assertPersistedNoticeToMatchUpdatableProperties(Notice expectedNotice) {
        assertNoticeAllUpdatablePropertiesEquals(expectedNotice, getPersistedNotice(expectedNotice));
    }
}
