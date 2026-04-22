package model.model;

/**
 * Employee class represents the core entity of the onboarding system.
 * 
 * GRASP: Information Expert → stores all employee-related data.
 * 
 * This class is used across multiple modules:
 * - Profile Management
 * - Role Assignment
 * - Asset Allocation
 * - Training Management
 */
public class Employee {

    private String employeeID;
    private String name;
    private String email;
    private String department;
    private String role;
    private String contactInfo;
    private String status; // onboarding, active, exited
    private int yearsOfService;

    /**
     * Basic constructor used during initial employee creation.
     */
    public Employee(String employeeID, String name) {
        this.employeeID = employeeID;
        this.name = name;
        this.status = "ONBOARDING";
    }

    /**
     * Constructor with years of service.
     */
    public Employee(String employeeID, String name, int yearsOfService) {
        this.employeeID = employeeID;
        this.name = name;
        this.yearsOfService = yearsOfService;
        this.status = "ACTIVE";
    }

    /**
     * Full constructor used when complete employee details are available.
     */
    public Employee(String employeeID, String name, String email,
                    String department, String role,
                    String contactInfo, String status) {
        this.employeeID = employeeID;
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
        this.contactInfo = contactInfo;
        this.status = status;
    }

    // Getter methods (encapsulation - SOLID: SRP)
    public String getEmployeeID() { return employeeID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getRole() { return role; }
    public String getContactInfo() { return contactInfo; }
    public String getStatus() { return status; }
    public int getYearsOfService() { return yearsOfService; }

    // Setter methods (controlled updates)
    public void setRole(String role) { this.role = role; }
    public void setStatus(String status) { this.status = status; }
}
