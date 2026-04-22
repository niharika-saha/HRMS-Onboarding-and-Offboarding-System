package model.model;

public class Document {
    private String documentID;
    private String employeeID;
    private String type;
    private String name;
    private String content;
    private String status;

    public Document(String documentID) {
        this.documentID = documentID;
    }

    public Document(String employeeID, String type, String name, String content) {
        this.employeeID = employeeID;
        this.type = type;
        this.name = name;
        this.content = content;
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

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
