package notification;

public class NotificationService {

    public void send(String empID, String message) {
        System.out.println("Notification to " + empID + ": " + message);
    }
}