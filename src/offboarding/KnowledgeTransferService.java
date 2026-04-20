package offboarding;

/**
 * Handles knowledge transfer completion.
 * Validation is handled by GUI.
 */
public class KnowledgeTransferService {

    // ✔ Required constructor
    public KnowledgeTransferService() {}

    public void verifyKnowledgeTransfer(String empID) {
        System.out.println("KT verified via UI for " + empID);
    }

    /**
     * Completes knowledge transfer
     */
    public void completeKnowledgeTransfer(String empID, String successorID) {

        if (successorID == null || successorID.isEmpty()) {
            throw new RuntimeException("Successor not provided");
        }

        System.out.println("Knowledge transferred from "
                + empID + " to " + successorID);
    }
}