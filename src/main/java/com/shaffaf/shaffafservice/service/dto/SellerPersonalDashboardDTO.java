package com.shaffaf.shaffafservice.service.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for seller's personal dashboard containing their aggregated data and projects.
 */
public class SellerPersonalDashboardDTO {

    private String sellerName;
    private String phoneNumber;
    private String email;
    private BigDecimal totalPayment;
    private Long totalProjects;
    private BigDecimal totalSales;
    private Long newSellers; // Sellers referred by this seller
    private BigDecimal totalDues;
    private List<SellerProjectDTO> projects;
    private Long totalElements;
    private Integer currentPage;
    private Integer totalPages;

    public SellerPersonalDashboardDTO() {}

    public SellerPersonalDashboardDTO(
        String sellerName,
        String phoneNumber,
        String email,
        BigDecimal totalPayment,
        Long totalProjects,
        BigDecimal totalSales,
        Long newSellers,
        BigDecimal totalDues,
        List<SellerProjectDTO> projects,
        Long totalElements,
        Integer currentPage,
        Integer totalPages
    ) {
        this.sellerName = sellerName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.totalPayment = totalPayment;
        this.totalProjects = totalProjects;
        this.totalSales = totalSales;
        this.newSellers = newSellers;
        this.totalDues = totalDues;
        this.projects = projects;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    // Getters and Setters
    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(BigDecimal totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(Long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public Long getNewSellers() {
        return newSellers;
    }

    public void setNewSellers(Long newSellers) {
        this.newSellers = newSellers;
    }

    public BigDecimal getTotalDues() {
        return totalDues;
    }

    public void setTotalDues(BigDecimal totalDues) {
        this.totalDues = totalDues;
    }

    public List<SellerProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<SellerProjectDTO> projects) {
        this.projects = projects;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public String toString() {
        return (
            "SellerPersonalDashboardDTO{" +
            "sellerName='" +
            sellerName +
            '\'' +
            ", phoneNumber='" +
            phoneNumber +
            '\'' +
            ", email='" +
            email +
            '\'' +
            ", totalPayment=" +
            totalPayment +
            ", totalProjects=" +
            totalProjects +
            ", totalSales=" +
            totalSales +
            ", newSellers=" +
            newSellers +
            ", totalDues=" +
            totalDues +
            ", projects=" +
            (projects != null ? projects.size() : 0) +
            " projects" +
            ", totalElements=" +
            totalElements +
            ", currentPage=" +
            currentPage +
            ", totalPages=" +
            totalPages +
            '}'
        );
    }
}
