package onboarding.service;

import model.Candidate;
import model.Employee;
import onboarding.exception.ErrorCodes;
import onboarding.exception.OnboardingException;
import proxy.RoleAccessProxy;

/**
 * Proxy Pattern implementation for the Pre-Onboarding Service.
 *
 * <p>Wraps {@link IPreOnboardingService} and enforces role-based access control
 * before delegating to the real {@link PreOnboardingService}.
 *
 * <p>Only employees with the {@code "ADMIN"} or {@code "HR"} role may trigger
 * onboarding operations. All other callers receive an
 * {@link OnboardingException} with code {@link ErrorCodes#INTERNAL_ERROR}.
 *
 * <p><strong>Usage:</strong>
 * <pre>
 *   IPreOnboardingService service =
 *       new PreOnboardingServiceProxy(
 *           new PreOnboardingService(preOnboardingData, documentData, policyData, refData),
 *           callerEmployee);
 *
 *   service.startPreOnboarding("CAND-001");
 * </pre>
 *
 * SOLID:
 * <ul>
 *   <li>OCP  — security logic added here without modifying the real service.</li>
 *   <li>SRP  — this class only handles access control.</li>
 *   <li>LSP  — substitutable wherever {@link IPreOnboardingService} is expected.</li>
 * </ul>
 */
public class PreOnboardingServiceProxy implements IPreOnboardingService {

    /** Permitted roles that may invoke onboarding operations. */
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_HR    = "HR";

    private final IPreOnboardingService delegate;
    private final Employee              caller;
    private final RoleAccessProxy       accessProxy;

    /**
     * @param delegate  The real {@link PreOnboardingService} to delegate to.
     * @param caller    The employee attempting to trigger the onboarding operation.
     */
    public PreOnboardingServiceProxy(IPreOnboardingService delegate, Employee caller) {
        if (delegate == null) throw new IllegalArgumentException("Delegate service must not be null.");
        if (caller   == null) throw new IllegalArgumentException("Caller employee must not be null.");

        this.delegate    = delegate;
        this.caller      = caller;
        this.accessProxy = new RoleAccessProxy();
    }

    /** {@inheritDoc} */
    @Override
    public boolean startPreOnboarding(String candidateId) throws OnboardingException {
        checkAccess("startPreOnboarding");
        return delegate.startPreOnboarding(candidateId);
    }

    /** {@inheritDoc} */
    @Override
    public boolean validateCandidate(String candidateId) throws OnboardingException {
        checkAccess("validateCandidate");
        return delegate.validateCandidate(candidateId);
    }

    /** {@inheritDoc} */
    @Override
    public Employee createEmployeeFromCandidate(Candidate candidate) throws OnboardingException {
        checkAccess("createEmployeeFromCandidate");
        return delegate.createEmployeeFromCandidate(candidate);
    }

    // ─── Private helpers ──────────────────────────────────────────────────

    /**
     * Checks whether the caller holds an authorised role.
     *
     * <p>Delegates the generic admin check to {@link RoleAccessProxy#performAdminAction},
     * and additionally allows the {@code "HR"} role for onboarding operations.
     *
     * @param operation  Name of the operation being attempted (for logging).
     * @throws OnboardingException if the caller is not authorised.
     */
    private void checkAccess(String operation) throws OnboardingException {
        String callerRole = caller.getRole();

        System.out.println("[PreOnboardingServiceProxy] Access check for operation '"
                + operation + "' | Caller: " + caller.getEmployeeID()
                + " | Role: " + callerRole);

        if (callerRole == null
                || (!callerRole.equalsIgnoreCase(ROLE_ADMIN)
                    && !callerRole.equalsIgnoreCase(ROLE_HR))) {

            // Use RoleAccessProxy for the admin path (required by spec)
            accessProxy.performAdminAction(caller);

            // If role is neither ADMIN nor HR, block access
            throw new OnboardingException(
                    ErrorCodes.INTERNAL_ERROR,
                    "Access denied: role '" + callerRole
                            + "' is not authorised to perform '" + operation + "'.");
        }

        System.out.println("[PreOnboardingServiceProxy] Access granted for: " + operation);
    }
}
