package com.example.plc.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;

@Entity
@Table(name = "plc_address")
public class PlcAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AddressType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DataType dataType;

    private String description;
    
    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "button_scheme_id")
    private Long buttonSchemeId;

    @Column(name = "is_store_in_db", nullable = false, columnDefinition = "boolean default false")
    @JsonProperty("isStoreInDb")
    private boolean isStoreInDb = false;

    public enum AddressType {
        INPUT, OUTPUT
    }

    public enum DataType {
        BOOLEAN, NUMBER
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getButtonSchemeId() {
        return buttonSchemeId;
    }

    public void setButtonSchemeId(Long buttonSchemeId) {
        this.buttonSchemeId = buttonSchemeId;
    }

    public boolean isStoreInDb() {
        return isStoreInDb;
    }

    public void setStoreInDb(boolean storeInDb) {
        isStoreInDb = storeInDb;
    }
}