package integration;

import model.Employee;

/**
 * Integration interface exposed by Employee Setup module (Member 2).
 * 
 * Used by:
 * - Customization subsystem (workflow, lookup, forms)
 * - Benefits subsystem
 * 
 * SOLID: Interface Segregation + Dependency Inversion
 */
public interface IEmployeeIntegration {

    /**
     * Fetch employee details from database
     */
    Employee getEmployee(String employeeID);

    /**
     * Assign role to employee using lookup values
     */
    void assignRole(Employee employee, String role);

    /**
     * Trigger onboarding workflow
     */
    int startOnboarding(Employee employee);

    /**
     * Load onboarding form (for UI / workflow usage)
     */
    void loadOnboardingForm();
}
