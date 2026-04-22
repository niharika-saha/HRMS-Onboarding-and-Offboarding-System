package model.model;

public class Clearance {
    private String clearanceID;
    private String employeeID;
    private double amount;

    public Clearance(String employeeID, double amount) {
        this.employeeID = employeeID;
        this.amount = amount;
        this.clearanceID = employeeID + "_CLR";
    }

    public String getId() {
        return clearanceID;
    }
}
