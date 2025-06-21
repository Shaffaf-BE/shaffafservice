package com.shaffaf.shaffafservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FeesConfiguration.
 */
@Entity
@Table(name = "fees_configuration")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FeesConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "fees_configuration_seq")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "configured_by")
    private String configuredBy;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "feesConfiguration")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "units", "feesConfiguration" }, allowSetters = true)
    private Set<FeesCollection> feesCollections = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "units", "feesConfigurations" }, allowSetters = true)
    private UnitType unitType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "units", "feesConfigurations", "project" }, allowSetters = true)
    private Block block;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FeesConfiguration id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public FeesConfiguration title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public FeesConfiguration description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public FeesConfiguration amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getIsRecurring() {
        return this.isRecurring;
    }

    public FeesConfiguration isRecurring(Boolean isRecurring) {
        this.setIsRecurring(isRecurring);
        return this;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public FeesConfiguration dueDate(LocalDate dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getConfiguredBy() {
        return this.configuredBy;
    }

    public FeesConfiguration configuredBy(String configuredBy) {
        this.setConfiguredBy(configuredBy);
        return this;
    }

    public void setConfiguredBy(String configuredBy) {
        this.configuredBy = configuredBy;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public FeesConfiguration createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public FeesConfiguration createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public FeesConfiguration lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public FeesConfiguration lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Instant getDeletedOn() {
        return this.deletedOn;
    }

    public FeesConfiguration deletedOn(Instant deletedOn) {
        this.setDeletedOn(deletedOn);
        return this;
    }

    public void setDeletedOn(Instant deletedOn) {
        this.deletedOn = deletedOn;
    }

    public Set<FeesCollection> getFeesCollections() {
        return this.feesCollections;
    }

    public void setFeesCollections(Set<FeesCollection> feesCollections) {
        if (this.feesCollections != null) {
            this.feesCollections.forEach(i -> i.setFeesConfiguration(null));
        }
        if (feesCollections != null) {
            feesCollections.forEach(i -> i.setFeesConfiguration(this));
        }
        this.feesCollections = feesCollections;
    }

    public FeesConfiguration feesCollections(Set<FeesCollection> feesCollections) {
        this.setFeesCollections(feesCollections);
        return this;
    }

    public FeesConfiguration addFeesCollection(FeesCollection feesCollection) {
        this.feesCollections.add(feesCollection);
        feesCollection.setFeesConfiguration(this);
        return this;
    }

    public FeesConfiguration removeFeesCollection(FeesCollection feesCollection) {
        this.feesCollections.remove(feesCollection);
        feesCollection.setFeesConfiguration(null);
        return this;
    }

    public UnitType getUnitType() {
        return this.unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public FeesConfiguration unitType(UnitType unitType) {
        this.setUnitType(unitType);
        return this;
    }

    public Block getBlock() {
        return this.block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public FeesConfiguration block(Block block) {
        this.setBlock(block);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeesConfiguration)) {
            return false;
        }
        return getId() != null && getId().equals(((FeesConfiguration) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FeesConfiguration{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", amount=" + getAmount() +
            ", isRecurring='" + getIsRecurring() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", configuredBy='" + getConfiguredBy() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", deletedOn='" + getDeletedOn() + "'" +
            "}";
    }
}
