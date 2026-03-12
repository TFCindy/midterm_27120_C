package auca.ac.rw.carRentalHub.dto;

import java.util.UUID;

import auca.ac.rw.carRentalHub.model.enums.ELocationType;

public class LocationRequest {

    private String name;
    private String code;
    private ELocationType type;
    private UUID parentId;

    public LocationRequest() {
    }

    public LocationRequest(String name, String code, ELocationType type, UUID parentId) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ELocationType getType() {
        return type;
    }

    public void setType(ELocationType type) {
        this.type = type;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }
}

