package model.model;

import java.time.LocalDateTime;

public class Notification {
    private String employeeID;
    private String message;
    private LocalDateTime time;

    public Notification(String employeeID, String message) {
        this.employeeID = employeeID;
        this.message = message;
        this.time = LocalDateTime.now();
    }
}
