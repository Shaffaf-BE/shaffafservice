package com.shaffaf.shaffafservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Complain.
 */
@Entity
@Table(name = "complain")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Complain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "complain_seq")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "complain_date")
    private Instant complainDate;

    @Column(name = "added_by")
    private String addedBy;

    @Column(name = "assignee")
    private String assignee;

    @Column(name = "resolution_comments")
    private String resolutionComments;

    @Column(name = "resolved_on")
    private Instant resolvedOn;

    @Column(name = "resolved_by")
    private String resolvedBy;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @Column(name = "deleted_on")
    private Instant deletedOn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "complain")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "complain" }, allowSetters = true)
    private Set<ComplainComment> complainComments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "complains", "project" }, allowSetters = true)
    private ComplainType complainType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "complains" }, allowSetters = true)
    private ComplainStatus complainStatus;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Complain id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Complain title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Complain description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getComplainDate() {
        return this.complainDate;
    }

    public Complain complainDate(Instant complainDate) {
        this.setComplainDate(complainDate);
        return this;
    }

    public void setComplainDate(Instant complainDate) {
        this.complainDate = complainDate;
    }

    public String getAddedBy() {
        return this.addedBy;
    }

    public Complain addedBy(String addedBy) {
        this.setAddedBy(addedBy);
        return this;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getAssignee() {
        return this.assignee;
    }

    public Complain assignee(String assignee) {
        this.setAssignee(assignee);
        return this;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getResolutionComments() {
        return this.resolutionComments;
    }

    public Complain resolutionComments(String resolutionComments) {
        this.setResolutionComments(resolutionComments);
        return this;
    }

    public void setResolutionComments(String resolutionComments) {
        this.resolutionComments = resolutionComments;
    }

    public Instant getResolvedOn() {
        return this.resolvedOn;
    }

    public Complain resolvedOn(Instant resolvedOn) {
        this.setResolvedOn(resolvedOn);
        return this;
    }

    public void setResolvedOn(Instant resolvedOn) {
        this.resolvedOn = resolvedOn;
    }

    public String getResolvedBy() {
        return this.resolvedBy;
    }

    public Complain resolvedBy(String resolvedBy) {
        this.setResolvedBy(resolvedBy);
        return this;
    }

    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Complain createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Complain createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public Complain lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Complain lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Instant getDeletedOn() {
        return this.deletedOn;
    }

    public Complain deletedOn(Instant deletedOn) {
        this.setDeletedOn(deletedOn);
        return this;
    }

    public void setDeletedOn(Instant deletedOn) {
        this.deletedOn = deletedOn;
    }

    public Set<ComplainComment> getComplainComments() {
        return this.complainComments;
    }

    public void setComplainComments(Set<ComplainComment> complainComments) {
        if (this.complainComments != null) {
            this.complainComments.forEach(i -> i.setComplain(null));
        }
        if (complainComments != null) {
            complainComments.forEach(i -> i.setComplain(this));
        }
        this.complainComments = complainComments;
    }

    public Complain complainComments(Set<ComplainComment> complainComments) {
        this.setComplainComments(complainComments);
        return this;
    }

    public Complain addComplainComments(ComplainComment complainComment) {
        this.complainComments.add(complainComment);
        complainComment.setComplain(this);
        return this;
    }

    public Complain removeComplainComments(ComplainComment complainComment) {
        this.complainComments.remove(complainComment);
        complainComment.setComplain(null);
        return this;
    }

    public ComplainType getComplainType() {
        return this.complainType;
    }

    public void setComplainType(ComplainType complainType) {
        this.complainType = complainType;
    }

    public Complain complainType(ComplainType complainType) {
        this.setComplainType(complainType);
        return this;
    }

    public ComplainStatus getComplainStatus() {
        return this.complainStatus;
    }

    public void setComplainStatus(ComplainStatus complainStatus) {
        this.complainStatus = complainStatus;
    }

    public Complain complainStatus(ComplainStatus complainStatus) {
        this.setComplainStatus(complainStatus);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Complain)) {
            return false;
        }
        return getId() != null && getId().equals(((Complain) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Complain{" +
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
            "}";
    }
}
