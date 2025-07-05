package com.shaffaf.shaffafservice.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * DTO for bulk unit creation request.
 */
public class BulkUnitCreationRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotEmpty(message = "Bulk creation items cannot be empty")
    @Valid
    private List<BulkUnitCreationItemDTO> items;

    public BulkUnitCreationRequestDTO() {}

    public BulkUnitCreationRequestDTO(Long projectId, List<BulkUnitCreationItemDTO> items) {
        this.projectId = projectId;
        this.items = items;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<BulkUnitCreationItemDTO> getItems() {
        return items;
    }

    public void setItems(List<BulkUnitCreationItemDTO> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "BulkUnitCreationRequestDTO{" + "projectId=" + projectId + ", items=" + items + '}';
    }
}
