package progress;

public class ProgressTracker {

    public void updateProgress(String employeeID, String status) {
        System.out.println("Progress Updated for " + employeeID + ": " + status);
    }
}