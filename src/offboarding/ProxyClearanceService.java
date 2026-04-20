package offboarding;

import model.ExitRequest;

/**
 * Proper Proxy: controls access using ExitRequest state
 */
public class ProxyClearanceService implements ClearanceManager {

    private ClearanceManager realService;

    public ProxyClearanceService(ClearanceManager realService) {
        this.realService = realService;
    }

    @Override
    public void processClearance(String empID) {

        // Get ExitRequest via ExitManager
        ExitRequest req = ExitManager.get(empID);

        if (req == null) {
            throw new RuntimeException("Exit request not found");
        }

        // REAL VALIDATION (proxy responsibility)
        if (!req.isLaptopReturned()) {
            throw new RuntimeException("Laptop not returned");
        }
        if (!req.isIdCardReturned()) {
            throw new RuntimeException("ID card not returned");
        }
        if (!req.isAccessRevoked()) {
            throw new RuntimeException("System access not revoked");
        }
        if (!req.isEmailDisabled()) {
            throw new RuntimeException("Email not disabled");
        }
        if (!req.isFinanceCleared()) {
            throw new RuntimeException("Finance clearance pending");
        }

        System.out.println("Proxy: validation passed");

        // Delegate only after validation
        realService.processClearance(empID);
    }
}