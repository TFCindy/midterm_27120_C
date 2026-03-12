package auca.ac.rw.carRentalHub.dto;

public class UserCreateRequest {

    private String username;
    private String email;
    private String password;
    private String roleName;
    private String villageIdentifier;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getVillageIdentifier() {
        return villageIdentifier;
    }

    public void setVillageIdentifier(String villageIdentifier) {
        this.villageIdentifier = villageIdentifier;
    }
}

