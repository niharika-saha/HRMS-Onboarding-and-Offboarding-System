package gui;

/**
 * Represents the lifecycle state of a single offboarding pipeline step.
 */
public enum StepState {
    PENDING,
    RUNNING,
    AWAITING,
    DONE,
    ERROR
}
