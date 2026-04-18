package gui;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the ordered set of offboarding pipeline steps and their display names.
 */
public final class PipelineConfig {

    private PipelineConfig() {}

    public static final String[] STEP_KEYS = {
        "exit", "interview", "clearance", "knowledge", "settlement", "docs", "notify"
    };

    public static final Map<String, String> STEP_NAMES = new LinkedHashMap<>();
    static {
        STEP_NAMES.put("exit",       "Exit registration");
        STEP_NAMES.put("interview",  "Exit interview");
        STEP_NAMES.put("clearance",  "Clearance & assets");
        STEP_NAMES.put("knowledge",  "Knowledge transfer");
        STEP_NAMES.put("settlement", "Final settlement");
        STEP_NAMES.put("docs",       "Document generation");
        STEP_NAMES.put("notify",     "Notifications");
    }

    // ── Exception classification ───────────────────────────────────────────────

    /** Map an exception type to the appropriate {@link StepState}. */
    public static StepState classifyException(Exception ex) {
        switch (ex.getClass().getSimpleName()) {
            case "AssetAllocationPendingException":
            case "AccessProvisionFailureException":
            case "ClearanceNotCompletedException":
            case "TrainingIncompleteException":
            case "PolicyNotAcceptedException":
                return StepState.AWAITING;
            default:
                return StepState.ERROR;
        }
    }

    /** Convert an exception into a human-readable one-liner. */
    public static String friendlyMessage(Exception ex) {
        String msg = ex.getMessage();
        if (msg != null && !msg.isBlank()) return msg;
        String name = ex.getClass().getSimpleName()
                .replace("Exception", "")
                .replaceAll("([A-Z])", " $1").trim();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }
}
