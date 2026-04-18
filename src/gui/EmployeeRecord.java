package gui;

import model.ExitRequest;
import offboarding.ExitType;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static gui.Theme.*;

/**
 * In-memory representation of an employee who is in (or being added to)
 * the offboarding pipeline.
 */
public class EmployeeRecord {

    // ── Core identity ─────────────────────────────────────────────────────────
    public String   empId;
    public String   name;
    public String   role;
    public String   department;
    public String   lastDay;
    public ExitType exitType;

    // ── Exit-interview fields (collected after the "exit" step completes) ─────
    public String  successorId            = "";
    public String  reason                 = "";
    public String  feedback               = "";
    public int     rating                 = 0;
    public boolean interviewDataCollected = false;

    // ── Pipeline state ────────────────────────────────────────────────────────
    public Map<String, StepState> stepStates = new LinkedHashMap<>();
    /** Each entry: { timestamp, message, colourHex } */
    public List<String[]> log = new ArrayList<>();

    public int    doneCount     = 0;
    public int    awaitCount    = 0;
    public int    errorCount    = 0;
    public String overallStatus = "Idle";
    public Integer workflowInstanceId;

    // ── Constructors ──────────────────────────────────────────────────────────

    public EmployeeRecord(String empId, String name, String role, String department,
                          String lastDay, ExitType exitType) {
        this.empId      = empId;
        this.name       = name;
        this.role       = role;
        this.department = department;
        this.lastDay    = lastDay;
        this.exitType   = exitType;
        for (String k : PipelineConfig.STEP_KEYS) stepStates.put(k, StepState.PENDING);
    }

    /**
     * Build a record from a DB {@link ExitRequest}.
     * Name / role / dept will be enriched from IEmployeeProfileData if available.
     */
    public static EmployeeRecord fromExitRequest(ExitRequest req) {
        ExitType type   = req.getExitType();
        String   lastDay = req.getLastWorkingDay() != null
                ? req.getLastWorkingDay().toString() : "—";

        EmployeeRecord r = new EmployeeRecord(
                req.getEmployeeID(),
                req.getEmployeeID(), // overwritten below if profile found
                "—", "—",
                lastDay, type);

        // Derive pipeline state from the DB status field
        String status = req.getStatus();
        if ("COMPLETE".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
            for (String k : PipelineConfig.STEP_KEYS) r.stepStates.put(k, StepState.DONE);
            r.doneCount     = PipelineConfig.STEP_KEYS.length;
            r.overallStatus = "Complete";
        } else if ("ERROR".equalsIgnoreCase(status)) {
            r.overallStatus = "Error";
        } else if (status != null && !status.isBlank()) {
            r.overallStatus = "Running";
        }
        return r;
    }

    // ── Derived helpers ───────────────────────────────────────────────────────

    public String initials() {
        String[] parts = name.split(" ");
        if (parts.length >= 2)
            return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    public float completionPct() {
        long done = stepStates.values().stream().filter(s -> s == StepState.DONE).count();
        return (float) done / PipelineConfig.STEP_KEYS.length;
    }

    /** Key of the next step that is not yet DONE, or {@code null} if all done. */
    public String nextPendingKey() {
        for (String k : PipelineConfig.STEP_KEYS)
            if (stepStates.get(k) != StepState.DONE) return k;
        return null;
    }

    public Color statusColor() {
        switch (overallStatus) {
            case "Complete": return GREEN;
            case "Error":    return RED;
            case "Awaiting": return BLUE;
            case "Running":  return AMBER;
            default:         return TEXT_MUTED;
        }
    }
}
