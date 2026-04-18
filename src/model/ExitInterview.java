package model;

public class ExitInterview {

    private String interviewID;
    private String employeeID;
    private String feedback;
    private String reason;
    private int rating;

    public ExitInterview(String interviewID, String employeeID,
                         String feedback, String reason, int rating) {
        this.interviewID = interviewID;
        this.employeeID = employeeID;
        this.feedback = feedback;
        this.reason = reason;
        this.rating = rating;
    }

    public String getEmployeeID() { return employeeID; }
    public String getFeedback() { return feedback; }
    public String getReason() { return reason; }
    public int getRating() { return rating; }
}