package com.shaffaf.shaffafservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shaffaf.shaffafservice.domain.ComplainComment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ComplainCommentDTO implements Serializable {

    private Long id;

    @NotNull
    private String comment;

    private String addedBy;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Instant deletedOn;

    private ComplainDTO complain;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public ComplainDTO getComplain() {
        return complain;
    }

    public void setComplain(ComplainDTO complain) {
        this.complain = complain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ComplainCommentDTO)) {
            return false;
        }

        ComplainCommentDTO complainCommentDTO = (ComplainCommentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, complainCommentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ComplainCommentDTO{" +
            "id=" + getId() +
            ", comment='" + getComment() + "'" +
            ", addedBy='" + getAddedBy() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", deletedOn='" + getDeletedOn() + "'" +
            ", complain=" + getComplain() +
            "}";
    }
}
