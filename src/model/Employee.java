package model;

public class Employee {
    private String employeeID;
    private String name;
    private int yearsOfService;


    public Employee(String employeeID, String name) {
        this.employeeID = employeeID;
        this.name = name;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getName() {
        return name;
    }

    public int getYearsOfService() {
        return yearsOfService;
    }
}