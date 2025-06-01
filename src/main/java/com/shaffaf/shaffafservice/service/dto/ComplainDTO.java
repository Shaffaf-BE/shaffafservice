package com.shaffaf.shaffafservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shaffaf.shaffafservice.domain.Complain} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ComplainDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    private String description;

    private Instant complainDate;

    private String addedBy;

    private String assignee;

    private String resolutionComments;

    private Instant resolvedOn;

    private String resolvedBy;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Instant deletedOn;

    private ComplainTypeDTO complainType;

    private ComplainStatusDTO complainStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getComplainDate() {
        return complainDate;
    }

    public void setComplainDate(Instant complainDate) {
        this.complainDate = complainDate;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getResolutionComments() {
        return resolutionComments;
    }

    public void setResolutionComments(String resolutionComments) {
        this.resolutionComments = resolutionComments;
    }

    public Instant getResolvedOn() {
        return resolvedOn;
    }

    public void setResolvedOn(Instant resolvedOn) {
        this.resolvedOn = resolvedOn;
    }

    public String getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Instant getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Instant deletedOn) {
        this.deletedOn = deletedOn;
    }

    public ComplainTypeDTO getComplainType() {
        return complainType;
    }

    public void setComplainType(ComplainTypeDTO complainType) {
        this.complainType = complainType;
    }

    public ComplainStatusDTO getComplainStatus() {
        return complainStatus;
    }

    public void setComplainStatus(ComplainStatusDTO complainStatus) {
        this.complainStatus = complainStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ComplainDTO)) {
            return false;
        }

        ComplainDTO complainDTO = (ComplainDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, complainDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ComplainDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", complainDate='" + getComplainDate() + "'" +
            ", addedBy='" + getAddedBy() + "'" +
            ", assignee='" + getAssignee() + "'" +
            ", resolutionComments='" + getResolutionComments() + "'" +
            ", resolvedOn='" + getResolvedOn() + "'" +
            ", resolvedBy='" + getResolvedBy() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", deletedOn='" + getDeletedOn() + "'" +
            ", complainType=" + getComplainType() +
            ", complainStatus=" + getComplainStatus() +
            "}";
    }
}
