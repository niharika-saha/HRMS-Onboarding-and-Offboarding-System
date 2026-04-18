package model;

public class Document {

    private String documentID;
    private String employeeID;
    private String type;
    private String status;
    

    public Document(String documentID, String employeeID, String type, String status) {
        this.documentID = documentID;
        this.employeeID = employeeID;
        this.type = type;
        this.status = status;
    }

    public String getDocumentID() {
        return documentID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}