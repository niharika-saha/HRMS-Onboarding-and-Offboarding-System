package onboarding.service;

import data.IDocumentData;
import data.IPolicyData;
import data.IPreOnboardingData;
import data.IReferenceCheckData;
import factory.EmployeeFactory;
import model.Candidate;
import model.Employee;
import onboarding.exception.ErrorCodes;
import onboarding.exception.OnboardingException;
import onboarding.handler.DocumentVerificationHandler;
import onboarding.handler.EmployeeCreationHandler;
import onboarding.handler.OnboardingHandler;
import onboarding.handler.PolicyComplianceHandler;
import onboarding.handler.ReferenceCheckHandler;

/**
 * Entry point for the Pre-Onboarding module.
 *
 * <p>Implements {@link IPreOnboardingService} and:
 * <ol>
 *   <li>Constructs the Chain of Responsibility on each call (handlers are
 *       stateless except for {@code EmployeeCreationHandler} which holds the
 *       last-created employee).</li>
 *   <li>Exposes {@link #startPreOnboarding} and {@link #validateCandidate}
 *       as defined by the integration interface.</li>
 *   <li>Handles {@link OnboardingException} from the chain and returns a clean
 *       boolean result so callers don't need to manage pipeline internals.</li>
 * </ol>
 *
 * <p><strong>Proxy integration:</strong> Wrap this service with
 * {@code RoleAccessProxy} before exposing it to external subsystems:
 * <pre>
 *   IPreOnboardingService service =
 *       new PreOnboardingServiceProxy(
 *           new PreOnboardingService(preOnboardingData, documentData, policyData, referenceCheckData),
 *           callerEmployee);
 * </pre>
 *
 * SOLID:
 * <ul>
 *   <li>SRP — orchestrates the chain; does not contain validation logic itself.</li>
 *   <li>DIP — depends on data interfaces, not concrete implementations.</li>
 *   <li>OCP — new handlers can be added to the chain without changing this class.</li>
 * </ul>
 */
public class PreOnboardingService implements IPreOnboardingService {

    // ─── Injected data interfaces (no DB logic here) ──────────────────────
    private final IPreOnboardingData  preOnboardingData;
    private final IDocumentData       documentData;
    private final IPolicyData         policyData;
    private final IReferenceCheckData referenceCheckData;

    /**
     * All dependencies are injected via constructor — no hidden singletons,
     * no service locators.
     *
     * @param preOnboardingData  Candidate / onboarding status data access.
     * @param documentData       Document storage and verification status access.
     * @param policyData         Policy listing and compliance status access.
     * @param referenceCheckData Reference record access and status updates.
     */
    public PreOnboardingService(IPreOnboardingData  preOnboardingData,
                                IDocumentData       documentData,
                                IPolicyData         policyData,
                                IReferenceCheckData referenceCheckData) {

        if (preOnboardingData  == null) throw new IllegalArgumentException("IPreOnboardingData is required.");
        if (documentData       == null) throw new IllegalArgumentException("IDocumentData is required.");
        if (policyData         == null) throw new IllegalArgumentException("IPolicyData is required.");
        if (referenceCheckData == null) throw new IllegalArgumentException("IReferenceCheckData is required.");

        this.preOnboardingData  = preOnboardingData;
        this.documentData       = documentData;
        this.policyData         = policyData;
        this.referenceCheckData = referenceCheckData;
    }

    // ─── IPreOnboardingService implementation ─────────────────────────────

    /**
     * {@inheritDoc}
     *
     * <p>Builds and executes the full 4-handler chain:
     * <pre>
     *   DocumentVerification → PolicyCompliance → ReferenceCheck → EmployeeCreation
     * </pre>
     *
     * @return {@code true} on success, {@code false} if the pipeline raised a
     *         handled error (details are logged to stdout).
     */
    @Override
    public boolean startPreOnboarding(String candidateId) throws OnboardingException {
        System.out.println("\n========== PreOnboardingService: startPreOnboarding ["
                + candidateId + "] ==========");

        // Create the terminal handler separately so we can retrieve the employee
        EmployeeCreationHandler creationHandler =
                new EmployeeCreationHandler(preOnboardingData);

        // Build the full chain
        OnboardingHandler chain = buildFullChain(creationHandler);

        try {
            chain.process(candidateId);

            Employee created = creationHandler.getCreatedEmployee();
            if (created != null) {
                System.out.println("\n[PreOnboardingService] SUCCESS — Employee created: "
                        + created.getEmployeeID() + " | " + created.getName());
            }
            return true;

        } catch (OnboardingException e) {
            System.err.println("\n[PreOnboardingService] PIPELINE FAILED");
            System.err.println("  Error Code : " + e.getErrorCode());
            System.err.println("  Message    : " + e.getMessage());
            // Re-throw so the proxy / integration layer can react if needed
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Builds a 3-handler validation-only chain:
     * <pre>
     *   DocumentVerification → PolicyCompliance → ReferenceCheck
     * </pre>
     * Employee creation is NOT included.
     */
    @Override
    public boolean validateCandidate(String candidateId) throws OnboardingException {
        System.out.println("\n========== PreOnboardingService: validateCandidate ["
                + candidateId + "] ==========");

        OnboardingHandler chain = buildValidationOnlyChain();

        try {
            chain.process(candidateId);
            System.out.println("[PreOnboardingService] Candidate " + candidateId
                    + " passed all validation checks.");
            return true;

        } catch (OnboardingException e) {
            System.err.println("[PreOnboardingService] Validation failed for " + candidateId);
            System.err.println("  Error Code : " + e.getErrorCode());
            System.err.println("  Message    : " + e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Provides a direct Candidate → Employee conversion using
     * {@link EmployeeFactory} without re-running the full chain.
     * Useful when the caller has already run {@link #validateCandidate} and
     * now wants to create the employee as a separate step.
     */
    @Override
    public Employee createEmployeeFromCandidate(Candidate candidate)
            throws OnboardingException {

        if (candidate == null) {
            throw new OnboardingException(
                    ErrorCodes.INVALID_EMPLOYEE_DATA,
                    "Cannot create employee from a null Candidate.");
        }
        if (candidate.getCandidateID() == null || candidate.getCandidateID().isBlank()) {
            throw new OnboardingException(
                    ErrorCodes.INVALID_EMPLOYEE_DATA,
                    "Candidate has no ID — employee creation aborted.");
        }
        if (candidate.getName() == null || candidate.getName().isBlank()) {
            throw new OnboardingException(
                    ErrorCodes.INVALID_EMPLOYEE_DATA,
                    "Candidate name is missing — employee creation aborted.");
        }

        String employeeId = "EMP-" + candidate.getCandidateID();
        String email      = candidate.getName().toLowerCase().replace(" ", ".")
                            + "@company.internal";

        // Factory Pattern — object creation delegated to EmployeeFactory
        Employee employee = EmployeeFactory.createFullEmployee(
                employeeId,
                candidate.getName(),
                email,
                "UNASSIGNED",
                "NEW_HIRE",
                "PENDING",
                "ONBOARDING"
        );

        System.out.println("[PreOnboardingService] createEmployeeFromCandidate → "
                + employee.getEmployeeID());

        return employee;
    }

    // ─── Chain builders ───────────────────────────────────────────────────

    /**
     * Builds the complete 4-step chain.
     *
     * @param creationHandler  Pre-created terminal handler (caller retains reference
     *                         to read back the created employee).
     */
    private OnboardingHandler buildFullChain(EmployeeCreationHandler creationHandler) {
        DocumentVerificationHandler docHandler  =
                new DocumentVerificationHandler(documentData);
        PolicyComplianceHandler     policyHandler =
                new PolicyComplianceHandler(policyData);
        ReferenceCheckHandler       refHandler  =
                new ReferenceCheckHandler(referenceCheckData);

        // Chain: doc → policy → reference → employee creation
        docHandler.setNext(policyHandler)
                  .setNext(refHandler)
                  .setNext(creationHandler);

        System.out.println("[PreOnboardingService] Chain built: "
                + "DocumentVerification → PolicyCompliance → ReferenceCheck → EmployeeCreation");

        return docHandler; // head of chain
    }

    /**
     * Builds a validation-only 3-step chain (no employee creation).
     */
    private OnboardingHandler buildValidationOnlyChain() {
        DocumentVerificationHandler docHandler  =
                new DocumentVerificationHandler(documentData);
        PolicyComplianceHandler     policyHandler =
                new PolicyComplianceHandler(policyData);
        ReferenceCheckHandler       refHandler  =
                new ReferenceCheckHandler(referenceCheckData);

        // Chain: doc → policy → reference (terminal)
        docHandler.setNext(policyHandler)
                  .setNext(refHandler);

        System.out.println("[PreOnboardingService] Validation chain built: "
                + "DocumentVerification → PolicyCompliance → ReferenceCheck");

        return docHandler;
    }
}
