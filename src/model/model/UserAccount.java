package model.model;

public class UserAccount {
    private String userID;
    private String username;
    private String password;
    private String accessStatus;

    public UserAccount(String userID, String username) {
        this.userID = userID;
        this.username = username;
        this.accessStatus = "INACTIVE";
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void activate() {
        this.accessStatus = "ACTIVE";
    }

    public void disable() {
        this.accessStatus = "DISABLED";
    }

    public String getUserID() { return userID; }
    public String getUsername() { return username; }
    public String getAccessStatus() { return accessStatus; }
}
