package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.repository.SellerCommissionRepository;
import com.shaffaf.shaffafservice.service.SellerCommissionService;
import com.shaffaf.shaffafservice.service.dto.SellerCommissionDTO;
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
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.SellerCommission}.
 */
@RestController
@RequestMapping("/api/seller-commissions")
public class SellerCommissionResource {

    private static final Logger LOG = LoggerFactory.getLogger(SellerCommissionResource.class);

    private static final String ENTITY_NAME = "shaffafserviceSellerCommission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SellerCommissionService sellerCommissionService;

    private final SellerCommissionRepository sellerCommissionRepository;

    public SellerCommissionResource(
        SellerCommissionService sellerCommissionService,
        SellerCommissionRepository sellerCommissionRepository
    ) {
        this.sellerCommissionService = sellerCommissionService;
        this.sellerCommissionRepository = sellerCommissionRepository;
    }

    /**
     * {@code POST  /seller-commissions} : Create a new sellerCommission.
     *
     * @param sellerCommissionDTO the sellerCommissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sellerCommissionDTO, or with status {@code 400 (Bad Request)} if the sellerCommission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SellerCommissionDTO> createSellerCommission(@Valid @RequestBody SellerCommissionDTO sellerCommissionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SellerCommission : {}", sellerCommissionDTO);
        if (sellerCommissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new sellerCommission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sellerCommissionDTO = sellerCommissionService.save(sellerCommissionDTO);
        return ResponseEntity.created(new URI("/api/seller-commissions/" + sellerCommissionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, sellerCommissionDTO.getId().toString()))
            .body(sellerCommissionDTO);
    }

    /**
     * {@code PUT  /seller-commissions/:id} : Updates an existing sellerCommission.
     *
     * @param id the id of the sellerCommissionDTO to save.
     * @param sellerCommissionDTO the sellerCommissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sellerCommissionDTO,
     * or with status {@code 400 (Bad Request)} if the sellerCommissionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sellerCommissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SellerCommissionDTO> updateSellerCommission(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SellerCommissionDTO sellerCommissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SellerCommission : {}, {}", id, sellerCommissionDTO);
        if (sellerCommissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sellerCommissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sellerCommissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sellerCommissionDTO = sellerCommissionService.update(sellerCommissionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sellerCommissionDTO.getId().toString()))
            .body(sellerCommissionDTO);
    }

    /**
     * {@code PATCH  /seller-commissions/:id} : Partial updates given fields of an existing sellerCommission, field will ignore if it is null
     *
     * @param id the id of the sellerCommissionDTO to save.
     * @param sellerCommissionDTO the sellerCommissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sellerCommissionDTO,
     * or with status {@code 400 (Bad Request)} if the sellerCommissionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the sellerCommissionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sellerCommissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SellerCommissionDTO> partialUpdateSellerCommission(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SellerCommissionDTO sellerCommissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SellerCommission partially : {}, {}", id, sellerCommissionDTO);
        if (sellerCommissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sellerCommissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sellerCommissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SellerCommissionDTO> result = sellerCommissionService.partialUpdate(sellerCommissionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sellerCommissionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /seller-commissions} : get all the sellerCommissions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sellerCommissions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SellerCommissionDTO>> getAllSellerCommissions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of SellerCommissions");
        Page<SellerCommissionDTO> page = sellerCommissionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /seller-commissions/:id} : get the "id" sellerCommission.
     *
     * @param id the id of the sellerCommissionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sellerCommissionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SellerCommissionDTO> getSellerCommission(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SellerCommission : {}", id);
        Optional<SellerCommissionDTO> sellerCommissionDTO = sellerCommissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sellerCommissionDTO);
    }

    /**
     * {@code DELETE  /seller-commissions/:id} : delete the "id" sellerCommission.
     *
     * @param id the id of the sellerCommissionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSellerCommission(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SellerCommission : {}", id);
        sellerCommissionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
