package com.shaffaf.shaffafservice.web.rest;

import static com.shaffaf.shaffafservice.domain.BlockAsserts.*;
import static com.shaffaf.shaffafservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaffaf.shaffafservice.IntegrationTest;
import com.shaffaf.shaffafservice.domain.Block;
import com.shaffaf.shaffafservice.repository.BlockRepository;
import com.shaffaf.shaffafservice.service.dto.BlockDTO;
import com.shaffaf.shaffafservice.service.mapper.BlockMapper;
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
 * Integration tests for the {@link BlockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BlockResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/blocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockMapper blockMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBlockMockMvc;

    private Block block;

    private Block insertedBlock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Block createEntity() {
        return new Block()
            .name(DEFAULT_NAME)
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
    public static Block createUpdatedEntity() {
        return new Block()
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
    }

    @BeforeEach
    void initTest() {
        block = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBlock != null) {
            blockRepository.delete(insertedBlock);
            insertedBlock = null;
        }
    }

    @Test
    @Transactional
    void createBlock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(block);
        var returnedBlockDTO = om.readValue(
            restBlockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blockDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BlockDTO.class
        );

        // Validate the Block in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBlock = blockMapper.toEntity(returnedBlockDTO);
        assertBlockUpdatableFieldsEquals(returnedBlock, getPersistedBlock(returnedBlock));

        insertedBlock = returnedBlock;
    }

    @Test
    @Transactional
    void createBlockWithExistingId() throws Exception {
        // Create the Block with an existing ID
        block.setId(1L);
        BlockDTO blockDTO = blockMapper.toDto(block);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Block in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        block.setName(null);

        // Create the Block, which fails.
        BlockDTO blockDTO = blockMapper.toDto(block);

        restBlockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBlocks() throws Exception {
        // Initialize the database
        insertedBlock = blockRepository.saveAndFlush(block);

        // Get all the blockList
        restBlockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(block.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deletedOn").value(hasItem(DEFAULT_DELETED_ON.toString())));
    }

    @Test
    @Transactional
    void getBlock() throws Exception {
        // Initialize the database
        insertedBlock = blockRepository.saveAndFlush(block);

        // Get the block
        restBlockMockMvc
            .perform(get(ENTITY_API_URL_ID, block.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(block.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.deletedOn").value(DEFAULT_DELETED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBlock() throws Exception {
        // Get the block
        restBlockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBlock() throws Exception {
        // Initialize the database
        insertedBlock = blockRepository.saveAndFlush(block);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the block
        Block updatedBlock = blockRepository.findById(block.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBlock are not directly saved in db
        em.detach(updatedBlock);
        updatedBlock
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);
        BlockDTO blockDTO = blockMapper.toDto(updatedBlock);

        restBlockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blockDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blockDTO))
            )
            .andExpect(status().isOk());

        // Validate the Block in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBlockToMatchAllProperties(updatedBlock);
    }

    @Test
    @Transactional
    void putNonExistingBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        block.setId(longCount.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(block);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blockDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Block in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        block.setId(longCount.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(block);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Block in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        block.setId(longCount.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(block);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Block in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBlockWithPatch() throws Exception {
        // Initialize the database
        insertedBlock = blockRepository.saveAndFlush(block);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the block using partial update
        Block partialUpdatedBlock = new Block();
        partialUpdatedBlock.setId(block.getId());

        partialUpdatedBlock.name(UPDATED_NAME).createdBy(UPDATED_CREATED_BY).deletedOn(UPDATED_DELETED_ON);

        restBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBlock))
            )
            .andExpect(status().isOk());

        // Validate the Block in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBlockUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBlock, block), getPersistedBlock(block));
    }

    @Test
    @Transactional
    void fullUpdateBlockWithPatch() throws Exception {
        // Initialize the database
        insertedBlock = blockRepository.saveAndFlush(block);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the block using partial update
        Block partialUpdatedBlock = new Block();
        partialUpdatedBlock.setId(block.getId());

        partialUpdatedBlock
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .deletedOn(UPDATED_DELETED_ON);

        restBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBlock))
            )
            .andExpect(status().isOk());

        // Validate the Block in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBlockUpdatableFieldsEquals(partialUpdatedBlock, getPersistedBlock(partialUpdatedBlock));
    }

    @Test
    @Transactional
    void patchNonExistingBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        block.setId(longCount.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(block);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, blockDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Block in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        block.setId(longCount.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(block);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Block in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBlock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        block.setId(longCount.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(block);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(blockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Block in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBlock() throws Exception {
        // Initialize the database
        insertedBlock = blockRepository.saveAndFlush(block);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the block
        restBlockMockMvc
            .perform(delete(ENTITY_API_URL_ID, block.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return blockRepository.count();
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

    protected Block getPersistedBlock(Block block) {
        return blockRepository.findById(block.getId()).orElseThrow();
    }

    protected void assertPersistedBlockToMatchAllProperties(Block expectedBlock) {
        assertBlockAllPropertiesEquals(expectedBlock, getPersistedBlock(expectedBlock));
    }

    protected void assertPersistedBlockToMatchUpdatableProperties(Block expectedBlock) {
        assertBlockAllUpdatablePropertiesEquals(expectedBlock, getPersistedBlock(expectedBlock));
    }
}
