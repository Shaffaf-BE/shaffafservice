package com.shaffaf.shaffafservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FeesCollection.
 */
@Entity
@Table(name = "fees_collection")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FeesCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "fees_collection_seq")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "amount_collected", precision = 21, scale = 2, nullable = false)
    private BigDecimal amountCollected;

    @Column(name = "amount_collected_by")
    private String amountCollectedBy;

    @Column(name = "amount_collected_on")
    private Instant amountCollectedOn;

    @Column(name = "paid_by")
    private String paidBy;

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

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "feesCollections")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "residents", "feesCollections", "unitType", "block" }, allowSetters = true)
    private Set<Unit> units = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "feesCollections", "unitType", "block" }, allowSetters = true)
    private FeesConfiguration feesConfiguration;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FeesCollection id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public FeesCollection title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public FeesCollection description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmountCollected() {
        return this.amountCollected;
    }

    public FeesCollection amountCollected(BigDecimal amountCollected) {
        this.setAmountCollected(amountCollected);
        return this;
    }

    public void setAmountCollected(BigDecimal amountCollected) {
        this.amountCollected = amountCollected;
    }

    public String getAmountCollectedBy() {
        return this.amountCollectedBy;
    }

    public FeesCollection amountCollectedBy(String amountCollectedBy) {
        this.setAmountCollectedBy(amountCollectedBy);
        return this;
    }

    public void setAmountCollectedBy(String amountCollectedBy) {
        this.amountCollectedBy = amountCollectedBy;
    }

    public Instant getAmountCollectedOn() {
        return this.amountCollectedOn;
    }

    public FeesCollection amountCollectedOn(Instant amountCollectedOn) {
        this.setAmountCollectedOn(amountCollectedOn);
        return this;
    }

    public void setAmountCollectedOn(Instant amountCollectedOn) {
        this.amountCollectedOn = amountCollectedOn;
    }

    public String getPaidBy() {
        return this.paidBy;
    }

    public FeesCollection paidBy(String paidBy) {
        this.setPaidBy(paidBy);
        return this;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public FeesCollection createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public FeesCollection createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public FeesCollection lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public FeesCollection lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Instant getDeletedOn() {
        return this.deletedOn;
    }

    public FeesCollection deletedOn(Instant deletedOn) {
        this.setDeletedOn(deletedOn);
        return this;
    }

    public void setDeletedOn(Instant deletedOn) {
        this.deletedOn = deletedOn;
    }

    public Set<Unit> getUnits() {
        return this.units;
    }

    public void setUnits(Set<Unit> units) {
        if (this.units != null) {
            this.units.forEach(i -> i.removeFeesCollections(this));
        }
        if (units != null) {
            units.forEach(i -> i.addFeesCollections(this));
        }
        this.units = units;
    }

    public FeesCollection units(Set<Unit> units) {
        this.setUnits(units);
        return this;
    }

    public FeesCollection addUnits(Unit unit) {
        this.units.add(unit);
        unit.getFeesCollections().add(this);
        return this;
    }

    public FeesCollection removeUnits(Unit unit) {
        this.units.remove(unit);
        unit.getFeesCollections().remove(this);
        return this;
    }

    public FeesConfiguration getFeesConfiguration() {
        return this.feesConfiguration;
    }

    public void setFeesConfiguration(FeesConfiguration feesConfiguration) {
        this.feesConfiguration = feesConfiguration;
    }

    public FeesCollection feesConfiguration(FeesConfiguration feesConfiguration) {
        this.setFeesConfiguration(feesConfiguration);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeesCollection)) {
            return false;
        }
        return getId() != null && getId().equals(((FeesCollection) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FeesCollection{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", amountCollected=" + getAmountCollected() +
            ", amountCollectedBy='" + getAmountCollectedBy() + "'" +
            ", amountCollectedOn='" + getAmountCollectedOn() + "'" +
            ", paidBy='" + getPaidBy() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", deletedOn='" + getDeletedOn() + "'" +
            "}";
    }
}
