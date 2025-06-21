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
 * A Unit.
 */
@Entity
@Table(name = "unit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Unit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "unit_seq")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "unit_number", nullable = false)
    private String unitNumber;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "unit")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "unit", "residentType" }, allowSetters = true)
    private Set<Resident> residents = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_unit__fees_collections",
        joinColumns = @JoinColumn(name = "unit_id"),
        inverseJoinColumns = @JoinColumn(name = "fees_collections_id")
    )
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

    public Unit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnitNumber() {
        return this.unitNumber;
    }

    public Unit unitNumber(String unitNumber) {
        this.setUnitNumber(unitNumber);
        return this;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Unit createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Unit createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public Unit lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Unit lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Instant getDeletedOn() {
        return this.deletedOn;
    }

    public Unit deletedOn(Instant deletedOn) {
        this.setDeletedOn(deletedOn);
        return this;
    }

    public void setDeletedOn(Instant deletedOn) {
        this.deletedOn = deletedOn;
    }

    public Set<Resident> getResidents() {
        return this.residents;
    }

    public void setResidents(Set<Resident> residents) {
        if (this.residents != null) {
            this.residents.forEach(i -> i.setUnit(null));
        }
        if (residents != null) {
            residents.forEach(i -> i.setUnit(this));
        }
        this.residents = residents;
    }

    public Unit residents(Set<Resident> residents) {
        this.setResidents(residents);
        return this;
    }

    public Unit addResidents(Resident resident) {
        this.residents.add(resident);
        resident.setUnit(this);
        return this;
    }

    public Unit removeResidents(Resident resident) {
        this.residents.remove(resident);
        resident.setUnit(null);
        return this;
    }

    public Set<FeesCollection> getFeesCollections() {
        return this.feesCollections;
    }

    public void setFeesCollections(Set<FeesCollection> feesCollections) {
        this.feesCollections = feesCollections;
    }

    public Unit feesCollections(Set<FeesCollection> feesCollections) {
        this.setFeesCollections(feesCollections);
        return this;
    }

    public Unit addFeesCollections(FeesCollection feesCollection) {
        this.feesCollections.add(feesCollection);
        return this;
    }

    public Unit removeFeesCollections(FeesCollection feesCollection) {
        this.feesCollections.remove(feesCollection);
        return this;
    }

    public UnitType getUnitType() {
        return this.unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public Unit unitType(UnitType unitType) {
        this.setUnitType(unitType);
        return this;
    }

    public Block getBlock() {
        return this.block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Unit block(Block block) {
        this.setBlock(block);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Unit)) {
            return false;
        }
        return getId() != null && getId().equals(((Unit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Unit{" +
            "id=" + getId() +
            ", unitNumber='" + getUnitNumber() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", deletedOn='" + getDeletedOn() + "'" +
            "}";
    }
}
