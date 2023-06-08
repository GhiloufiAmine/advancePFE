package com.example.demoo.domain;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "DriverLicence")
@Table(name = "DriverLicence")
public class DriverLicence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driver_licence_id;

    @OneToOne
    @JoinColumn(name = "client_id")
    private DefClient client_id;

    @Column(name = "licence_id", nullable = false)
    private Long licence_id;

    @Column(name = "licence_number", nullable = false)
    private String licence_number;

    @Column(name = "licence_type", nullable = false)
    private String licence_type;

    @Column(name = "date_of_issue", nullable = false)
    private LocalDate date_of_issue;

    @Column(name = "date_of_expiration", nullable = false)
    private LocalDate date_of_expiration;

    @Column(name = "suspended")
    private Boolean suspended;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "first_name", nullable = false)
    private String first_name;

    @Column(name = "last_name", nullable = false)
    private String last_name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate date_of_birth;

    public DriverLicence() {
    }

    public DriverLicence(Long driver_licence_id, DefClient client_id, Long licence_id, String licence_number, String licence_type, LocalDate date_of_issue, LocalDate date_of_expiration, Boolean suspended, String address, String first_name, String last_name, LocalDate date_of_birth) {
        this.driver_licence_id = driver_licence_id;
        this.client_id = client_id;
        this.licence_id = licence_id;
        this.licence_number = licence_number;
        this.licence_type = licence_type;
        this.date_of_issue = date_of_issue;
        this.date_of_expiration = date_of_expiration;
        this.suspended = suspended;
        this.address = address;
        this.first_name = first_name;
        this.last_name = last_name;
        this.date_of_birth = date_of_birth;
    }

    public Long getDriver_licence_id() {
        return driver_licence_id;
    }

    public void setDriver_licence_id(Long driver_licence_id) {
        this.driver_licence_id = driver_licence_id;
    }

    public DefClient getClient_id() {
        return client_id;
    }

    public void setClient_id(DefClient client_id) {
        this.client_id = client_id;
    }

    public Long getLicence_id() {
        return licence_id;
    }

    public void setLicence_id(Long licence_id) {
        this.licence_id = licence_id;
    }

    public String getLicence_number() {
        return licence_number;
    }

    public void setLicence_number(String licence_number) {
        this.licence_number = licence_number;
    }

    public String getLicence_type() {
        return licence_type;
    }

    public void setLicence_type(String licence_type) {
        this.licence_type = licence_type;
    }

    public LocalDate getDate_of_issue() {
        return date_of_issue;
    }

    public void setDate_of_issue(LocalDate date_of_issue) {
        this.date_of_issue = date_of_issue;
    }

    public LocalDate getDate_of_expiration() {
        return date_of_expiration;
    }

    public void setDate_of_expiration(LocalDate date_of_expiration) {
        this.date_of_expiration = date_of_expiration;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public LocalDate getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(LocalDate date_of_birth) {
        this.date_of_birth = date_of_birth;
    }
}