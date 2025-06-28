package com.shaffaf.shaffafservice.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for Seller Sales Aggregate information.
 */
public class SellerSalesAggregateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long sellerId;
    private String sellerName;
    private String sellerPhoneNumber;
    private String sellerEmail;
    private Integer totalProjects;
    private Integer totalUnits;
    private BigDecimal totalSalesAmount;
    private BigDecimal averageFeesPerUnit;
    private BigDecimal highestProjectAmount;
    private BigDecimal lowestProjectAmount;
    private String mostRecentProjectName;
    private Instant lastProjectDate;
    private String sellerStatus;

    public SellerSalesAggregateDTO() {}

    public SellerSalesAggregateDTO(
        Long sellerId,
        String sellerName,
        String sellerPhoneNumber,
        String sellerEmail,
        Integer totalProjects,
        Integer totalUnits,
        BigDecimal totalSalesAmount,
        BigDecimal averageFeesPerUnit,
        BigDecimal highestProjectAmount,
        BigDecimal lowestProjectAmount,
        String mostRecentProjectName,
        Instant lastProjectDate,
        String sellerStatus
    ) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.sellerPhoneNumber = sellerPhoneNumber;
        this.sellerEmail = sellerEmail;
        this.totalProjects = totalProjects;
        this.totalUnits = totalUnits;
        this.totalSalesAmount = totalSalesAmount;
        this.averageFeesPerUnit = averageFeesPerUnit;
        this.highestProjectAmount = highestProjectAmount;
        this.lowestProjectAmount = lowestProjectAmount;
        this.mostRecentProjectName = mostRecentProjectName;
        this.lastProjectDate = lastProjectDate;
        this.sellerStatus = sellerStatus;
    }

    // Getters and Setters
    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerPhoneNumber() {
        return sellerPhoneNumber;
    }

    public void setSellerPhoneNumber(String sellerPhoneNumber) {
        this.sellerPhoneNumber = sellerPhoneNumber;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public Integer getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(Integer totalProjects) {
        this.totalProjects = totalProjects;
    }

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits;
    }

    public BigDecimal getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(BigDecimal totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    public BigDecimal getAverageFeesPerUnit() {
        return averageFeesPerUnit;
    }

    public void setAverageFeesPerUnit(BigDecimal averageFeesPerUnit) {
        this.averageFeesPerUnit = averageFeesPerUnit;
    }

    public BigDecimal getHighestProjectAmount() {
        return highestProjectAmount;
    }

    public void setHighestProjectAmount(BigDecimal highestProjectAmount) {
        this.highestProjectAmount = highestProjectAmount;
    }

    public BigDecimal getLowestProjectAmount() {
        return lowestProjectAmount;
    }

    public void setLowestProjectAmount(BigDecimal lowestProjectAmount) {
        this.lowestProjectAmount = lowestProjectAmount;
    }

    public String getMostRecentProjectName() {
        return mostRecentProjectName;
    }

    public void setMostRecentProjectName(String mostRecentProjectName) {
        this.mostRecentProjectName = mostRecentProjectName;
    }

    public Instant getLastProjectDate() {
        return lastProjectDate;
    }

    public void setLastProjectDate(Instant lastProjectDate) {
        this.lastProjectDate = lastProjectDate;
    }

    public String getSellerStatus() {
        return sellerStatus;
    }

    public void setSellerStatus(String sellerStatus) {
        this.sellerStatus = sellerStatus;
    }

    @Override
    public String toString() {
        return (
            "SellerSalesAggregateDTO{" +
            "sellerId=" +
            sellerId +
            ", sellerName='" +
            sellerName +
            '\'' +
            ", sellerPhoneNumber='" +
            sellerPhoneNumber +
            '\'' +
            ", sellerEmail='" +
            sellerEmail +
            '\'' +
            ", totalProjects=" +
            totalProjects +
            ", totalUnits=" +
            totalUnits +
            ", totalSalesAmount=" +
            totalSalesAmount +
            ", averageFeesPerUnit=" +
            averageFeesPerUnit +
            ", highestProjectAmount=" +
            highestProjectAmount +
            ", lowestProjectAmount=" +
            lowestProjectAmount +
            ", mostRecentProjectName='" +
            mostRecentProjectName +
            '\'' +
            ", lastProjectDate=" +
            lastProjectDate +
            ", sellerStatus='" +
            sellerStatus +
            '\'' +
            '}'
        );
    }
}
