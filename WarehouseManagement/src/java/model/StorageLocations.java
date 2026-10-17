package model;

public class StorageLocations {
    private int locationId;
    private String zoneName;
    private String shelfCode;
    private String binCode;
    private String locationType;
    private String status;

    public StorageLocations() {
    }

    public StorageLocations(int locationId, String zoneName, String shelfCode, String binCode, String locationType, String status) {
        this.locationId = locationId;
        this.zoneName = zoneName;
        this.shelfCode = shelfCode;
        this.binCode = binCode;
        this.locationType = locationType;
        this.status = status;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getShelfCode() {
        return shelfCode;
    }

    public void setShelfCode(String shelfCode) {
        this.shelfCode = shelfCode;
    }

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
