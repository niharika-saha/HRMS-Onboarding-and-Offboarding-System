package model.model;

public class ExitInterview {
    private String employeeID;
    private String interviewer;
    private String feedback;
    private String date;
    private int rating;

    public ExitInterview(String employeeID, String feedback) {
        this.employeeID = employeeID;
        this.feedback = feedback;
    }

    public ExitInterview(String employeeID, String interviewer, String feedback, String date, int rating) {
        this.employeeID = employeeID;
        this.interviewer = interviewer;
        this.feedback = feedback;
        this.date = date;
        this.rating = rating;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getInterviewer() {
        return interviewer;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getDate() {
        return date;
    }

    public int getRating() {
        return rating;
    }
}
