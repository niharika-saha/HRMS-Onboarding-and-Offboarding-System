package offboarding;

import notification.NotificationService;
import progress.ProgressTracker;

/**
 * Main controller for offboarding flow.
 */
public class OffboardingService {

    private ExitManager exitManager;
    private ExitInterviewManager interviewManager;
    private ClearanceManager clearanceManager;
    private SettlementService settlementService;
    private DocumentGenerator documentGenerator;
    private NotificationService notificationService;
    private ProgressTracker progressTracker;
    private KnowledgeTransferService knowledgeService;

    private double lastSettlementAmount = 0;

    // ✔ MUST MATCH setupServices EXACTLY
    public OffboardingService(
            ExitManager exitManager,
            ExitInterviewManager interviewManager,
            ClearanceManager clearanceManager,
            SettlementService settlementService,
            DocumentGenerator documentGenerator,
            NotificationService notificationService,
            ProgressTracker progressTracker,
            KnowledgeTransferService knowledgeService) {

        this.exitManager = exitManager;
        this.interviewManager = interviewManager;
        this.clearanceManager = clearanceManager;
        this.settlementService = settlementService;
        this.documentGenerator = documentGenerator;
        this.notificationService = notificationService;
        this.progressTracker = progressTracker;
        this.knowledgeService = knowledgeService;
    }

    /**
     * Executes individual pipeline step
     */
    public void processStep(String empID,
                            ExitType type,
                            String stepKey,
                            String successorID,
                            String feedback,
                            String reason,
                            Integer rating) {

        switch (stepKey) {

            // ───── EXIT INITIATION ─────
            case "exit":
                exitManager.initiateExit(empID, type);
                progressTracker.updateProgress(empID, "EXIT INITIATED");
                break;

            // ───── EXIT INTERVIEW ─────
            case "interview":
                interviewManager.conductInterview(empID, feedback, reason, rating);
                progressTracker.updateProgress(empID, "INTERVIEW COMPLETED");
                break;

            // ───── CLEARANCE ─────
            case "clearance":
                clearanceManager.processClearance(empID);
                progressTracker.updateProgress(empID, "CLEARANCE COMPLETED");
                break;

            // ───── KNOWLEDGE TRANSFER ─────
            case "knowledge":
                knowledgeService.verifyKnowledgeTransfer(empID);
                knowledgeService.completeKnowledgeTransfer(empID, successorID);
                progressTracker.updateProgress(empID, "KNOWLEDGE TRANSFER COMPLETED");
                break;

            // ───── SETTLEMENT ─────
            case "settlement":
                lastSettlementAmount = settlementService.calculateSettlement(empID, type);
                progressTracker.updateProgress(empID, "SETTLEMENT COMPLETED");
                break;

            // ───── DOCUMENTS ─────
            case "docs":
                documentGenerator.generateRelievingLetter(empID);
                documentGenerator.generateExperienceCertificate(empID);
                documentGenerator.generateComplianceReport(empID);
                progressTracker.updateProgress(empID, "DOCUMENTS GENERATED");
                break;

            // ───── NOTIFICATION ─────
            case "notify":
                notificationService.send(empID, "Offboarding completed successfully");
                progressTracker.updateProgress(empID, "PROCESS COMPLETED");
                break;

            default:
                throw new IllegalArgumentException("Invalid step: " + stepKey);
        }
    }

    public double getLastSettlementAmount() {
        return lastSettlementAmount;
    }
}