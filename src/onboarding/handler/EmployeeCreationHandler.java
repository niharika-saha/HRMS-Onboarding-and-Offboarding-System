package onboarding.handler;

import data.IPreOnboardingData;
import factory.EmployeeFactory;
import model.Candidate;
import model.Employee;
import onboarding.exception.ErrorCodes;
import onboarding.exception.OnboardingException;

/**
 * Handler #4 — Terminal handler in the Pre-Onboarding chain.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Retrieves the {@link Candidate} record from the pre-onboarding data layer.</li>
 *   <li>Validates that the candidate record has the minimum fields required for
 *       conversion.</li>
 *   <li>Delegates object creation to {@link EmployeeFactory} (Factory Pattern).</li>
 *   <li>Updates the candidate's onboarding status to {@code "EMPLOYEE_CREATED"} in
 *       the data layer.</li>
 *   <li>Stores the newly created {@link Employee} so the service layer can retrieve
 *       it after the chain completes.</li>
 * </ul>
 *
 * <p>This is the terminal handler — it does NOT call {@code passToNext()} because
 * there is no subsequent step in the pre-onboarding pipeline.
 *
 * <p><strong>Factory usage:</strong> Employee instantiation is exclusively
 * performed by {@link EmployeeFactory#createFullEmployee}. This handler never
 * uses {@code new Employee(...)} directly.
 *
 * SOLID: SRP — only concerns itself with candidate-to-employee conversion.
 */
public class EmployeeCreationHandler extends OnboardingHandler {

    /** Status written to the pre-onboarding record after successful creation. */
    private static final String STATUS_EMPLOYEE_CREATED = "EMPLOYEE_CREATED";

    /**
     * Default values used when optional candidate fields are absent.
     * In production these would come from a lookup/configuration service.
     */
    private static final String DEFAULT_DEPARTMENT = "UNASSIGNED";
    private static final String DEFAULT_ROLE       = "NEW_HIRE";
    private static final String DEFAULT_STATUS     = "ONBOARDING";

    private final IPreOnboardingData preOnboardingData;

    /**
     * Holds the created employee after {@link #process(String)} completes.
     * The service layer calls {@link #getCreatedEmployee()} to retrieve it.
     */
    private Employee createdEmployee;

    /**
     * @param preOnboardingData  Injected data interface for candidate operations.
     *                            Must not be {@code null}.
     */
    public EmployeeCreationHandler(IPreOnboardingData preOnboardingData) {
        if (preOnboardingData == null) {
            throw new IllegalArgumentException("IPreOnboardingData must not be null.");
        }
        this.preOnboardingData = preOnboardingData;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Converts a validated {@link Candidate} into an {@link Employee} using
     * {@link EmployeeFactory}, then records the status update. Throws
     * {@link OnboardingException} if the candidate cannot be found or has
     * insufficient data for conversion.
     */
    @Override
    public void process(String candidateId) throws OnboardingException {
        System.out.println("[" + handlerName() + "] Converting candidate → employee: "
                + candidateId);

        // Step 1: fetch candidate from data layer
        Candidate candidate = preOnboardingData.getCandidateById(candidateId);

        if (candidate == null) {
            throw new OnboardingException(
                    ErrorCodes.EMPLOYEE_NOT_FOUND,
                    "Candidate not found in pre-onboarding data: " + candidateId);
        }

        // Step 2: validate minimum required fields
        validateCandidate(candidate);

        // Step 3: generate employee ID (production: sequence/UUID from DB)
        String employeeId = generateEmployeeId(candidate.getCandidateID());

        // Step 4: create Employee via EmployeeFactory (Factory Pattern)
        Employee employee = EmployeeFactory.createFullEmployee(
                employeeId,
                candidate.getName(),
                deriveEmail(candidate),
                DEFAULT_DEPARTMENT,   // will be updated by EmployeeService.assignRole()
                DEFAULT_ROLE,         // will be updated via lookup
                deriveContact(candidate),
                DEFAULT_STATUS
        );

        System.out.println("[" + handlerName() + "] Employee created via EmployeeFactory: "
                + employee.getEmployeeID() + " | " + employee.getName());

        // Step 5: persist onboarding status update
        preOnboardingData.updateOnboardingStatus(candidateId, STATUS_EMPLOYEE_CREATED);

        // Step 6: store result for retrieval by service layer
        this.createdEmployee = employee;

        System.out.println("[" + handlerName() + "] Onboarding status updated → "
                + STATUS_EMPLOYEE_CREATED + " for candidate: " + candidateId);

        // Terminal handler — no passToNext()
    }

    /**
     * Returns the {@link Employee} produced by the last successful
     * {@link #process(String)} call.
     *
     * @return The newly created employee, or {@code null} if process() has not
     *         yet been called or failed.
     */
    public Employee getCreatedEmployee() {
        return createdEmployee;
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    /**
     * Validates that the candidate record has the minimum fields needed for
     * Employee creation.
     */
    private void validateCandidate(Candidate candidate) throws OnboardingException {
        if (candidate.getCandidateID() == null || candidate.getCandidateID().isBlank()) {
            throw new OnboardingException(
                    ErrorCodes.INVALID_EMPLOYEE_DATA,
                    "Candidate has no ID — cannot create employee.");
        }
        if (candidate.getName() == null || candidate.getName().isBlank()) {
            throw new OnboardingException(
                    ErrorCodes.INVALID_EMPLOYEE_DATA,
                    "Candidate name is missing for ID: " + candidate.getCandidateID());
        }
    }

    /**
     * Derives an employeeID from the candidateID.
     *
     * <p>Production pattern: use a DB sequence or UUID generator here.
     * Prefixing with "EMP-" makes IDs visually distinct from candidate IDs.
     */
    private String generateEmployeeId(String candidateId) {
        return "EMP-" + candidateId;
    }

    /**
     * Derives the employee email from the candidate.
     * Falls back to a generated placeholder if the candidate has no email field.
     *
     * <p>Production: Candidate model would expose getEmail(). Since the current
     * model only has candidateID and name, we generate a placeholder.
     */
    private String deriveEmail(Candidate candidate) {
        // Placeholder — replace with candidate.getEmail() once model is extended
        return candidate.getName().toLowerCase().replace(" ", ".") + "@company.internal";
    }

    /**
     * Derives contact info from the candidate.
     * Falls back to a placeholder if the candidate model has no contact field.
     */
    private String deriveContact(Candidate candidate) {
        // Placeholder — replace with candidate.getContactInfo() once model is extended
        return "PENDING";
    }
}
