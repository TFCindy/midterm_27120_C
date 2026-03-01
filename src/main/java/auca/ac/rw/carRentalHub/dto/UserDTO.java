package auca.ac.rw.carRentalHub.dto;

import java.util.UUID;
import auca.ac.rw.carRentalHub.model.Location;
import auca.ac.rw.carRentalHub.model.enums.ELocationType;

public class UserDTO {

	private UUID id;
	private String username;
	private String email;
	private String roleName;
	private String locationName;
	private ELocationType locationType;
	private String provinceName;

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getRoleName() { return roleName; }
	public void setRoleName(String roleName) { this.roleName = roleName; }

	public String getLocationName() { return locationName; }
	public void setLocationName(String locationName) { this.locationName = locationName; }

	public ELocationType getLocationType() { return locationType; }
	public void setLocationType(ELocationType locationType) { this.locationType = locationType; }

	public String getProvinceName() { return provinceName; }
	public void setProvinceName(String provinceName) { this.provinceName = provinceName; }
}
