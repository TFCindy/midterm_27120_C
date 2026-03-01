package auca.ac.rw.carRentalHub.dto;

import java.util.UUID;
import java.util.List;

public class LocationDTO {
    private UUID id;
    private String code;
    private String name;
    private String type;
    private String parentName;
    private String parentId;
    private List<LocationDTO> children;

    // Constructors
    public LocationDTO() {}

    public LocationDTO(UUID id, String code, String name, String type) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public List<LocationDTO> getChildren() { return children; }
    public void setChildren(List<LocationDTO> children) { this.children = children; }
}