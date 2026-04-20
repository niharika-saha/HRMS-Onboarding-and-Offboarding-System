package integration;

import model.Candidate;
import model.Employee;
import onboarding.exception.OnboardingException;

/**
 * Integration interface exposed ONLY by Pre-Onboarding module.
 *
 * Used by:
 * - Customization subsystem (if they want validation hooks)
 * - Other subsystems that need candidate validation before onboarding
 *
 * SOLID:
 * - Interface Segregation Principle → separate from EmployeeIntegration
 * - Dependency Inversion → external systems depend on this abstraction
 */
public interface IPreOnboardingIntegration {

    /**
     * Run full pre-onboarding pipeline:
     * Document → Policy → Reference → Employee creation
     */
    boolean startPreOnboarding(String candidateID) throws OnboardingException;

    /**
     * Validate candidate without creating employee
     */
    boolean validateCandidate(String candidateID) throws OnboardingException;

    /**
     * Convert candidate → employee using Factory
     */
    Employee createEmployeeFromCandidate(Candidate candidate) throws OnboardingException;
}
