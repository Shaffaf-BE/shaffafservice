package com.shaffaf.shaffafservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.shaffaf.shaffafservice.domain.Notice} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NoticeDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    private String description;

    private LocalDate displayStartDate;

    private LocalDate displayEndDate;

    private String addedBy;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Instant deletedOn;

    private ProjectDTO project;

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

    public LocalDate getDisplayStartDate() {
        return displayStartDate;
    }

    public void setDisplayStartDate(LocalDate displayStartDate) {
        this.displayStartDate = displayStartDate;
    }

    public LocalDate getDisplayEndDate() {
        return displayEndDate;
    }

    public void setDisplayEndDate(LocalDate displayEndDate) {
        this.displayEndDate = displayEndDate;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
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

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NoticeDTO)) {
            return false;
        }

        NoticeDTO noticeDTO = (NoticeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, noticeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NoticeDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", displayStartDate='" + getDisplayStartDate() + "'" +
            ", displayEndDate='" + getDisplayEndDate() + "'" +
            ", addedBy='" + getAddedBy() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", deletedOn='" + getDeletedOn() + "'" +
            ", project=" + getProject() +
            "}";
    }
}
