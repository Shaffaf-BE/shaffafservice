package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.NoticeRepository;
import com.shaffaf.shaffafservice.service.NoticeService;
import com.shaffaf.shaffafservice.service.dto.NoticeDTO;
import com.shaffaf.shaffafservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.Notice}.
 */
@RestController
@RequestMapping("/api/notices")
public class NoticeResource {

    private static final Logger LOG = LoggerFactory.getLogger(NoticeResource.class);

    private static final String ENTITY_NAME = "shaffafserviceNotice";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NoticeService noticeService;

    private final NoticeRepository noticeRepository;

    public NoticeResource(NoticeService noticeService, NoticeRepository noticeRepository) {
        this.noticeService = noticeService;
        this.noticeRepository = noticeRepository;
    }

    /**
     * {@code POST  /notices} : Create a new notice.
     *
     * @param noticeDTO the noticeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new noticeDTO, or with status {@code 400 (Bad Request)} if the notice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NoticeDTO> createNotice(@Valid @RequestBody NoticeDTO noticeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Notice : {}", noticeDTO);
        if (noticeDTO.getId() != null) {
            throw new BadRequestAlertException("A new notice cannot already have an ID", ENTITY_NAME, "idexists");
        }
        noticeDTO = noticeService.save(noticeDTO);
        return ResponseEntity.created(new URI("/api/notices/" + noticeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, noticeDTO.getId().toString()))
            .body(noticeDTO);
    }

    /**
     * {@code PUT  /notices/:id} : Updates an existing notice.
     *
     * @param id the id of the noticeDTO to save.
     * @param noticeDTO the noticeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated noticeDTO,
     * or with status {@code 400 (Bad Request)} if the noticeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the noticeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NoticeDTO> updateNotice(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NoticeDTO noticeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Notice : {}, {}", id, noticeDTO);
        if (noticeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, noticeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!noticeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        noticeDTO = noticeService.update(noticeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, noticeDTO.getId().toString()))
            .body(noticeDTO);
    }

    /**
     * {@code PATCH  /notices/:id} : Partial updates given fields of an existing notice, field will ignore if it is null
     *
     * @param id the id of the noticeDTO to save.
     * @param noticeDTO the noticeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated noticeDTO,
     * or with status {@code 400 (Bad Request)} if the noticeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the noticeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the noticeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NoticeDTO> partialUpdateNotice(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NoticeDTO noticeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Notice partially : {}, {}", id, noticeDTO);
        if (noticeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, noticeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!noticeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NoticeDTO> result = noticeService.partialUpdate(noticeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, noticeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notices} : get all the notices.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notices in body.
     */
    @GetMapping("")
    public ResponseEntity<List<NoticeDTO>> getAllNotices(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Notices");
        Page<NoticeDTO> page = noticeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notices/:id} : get the "id" notice.
     *
     * @param id the id of the noticeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the noticeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NoticeDTO> getNotice(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Notice : {}", id);
        Optional<NoticeDTO> noticeDTO = noticeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(noticeDTO);
    }

    /**
     * {@code DELETE  /notices/:id} : delete the "id" notice.
     *
     * @param id the id of the noticeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Notice : {}", id);
        noticeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
