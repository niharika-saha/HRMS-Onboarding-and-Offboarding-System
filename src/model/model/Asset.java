package model.model;

public class Asset {
    private String assetID;
    private String assetType;
    private String allocationStatus;

    public Asset(String assetID, String assetType, String allocationStatus) {
        this.assetID = assetID;
        this.assetType = assetType;
        this.allocationStatus = allocationStatus;
    }

    public Asset(String assetID) {
        this.assetID = assetID;
        this.allocationStatus = "PENDING";
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public void allocate() {
        this.allocationStatus = "ALLOCATED";
    }

    public String getAssetID() {
        return assetID;
    }
}
