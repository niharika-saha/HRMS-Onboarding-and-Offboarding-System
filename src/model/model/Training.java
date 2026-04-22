package model.model;

/**
 * Represents a training module assigned to an employee.
 * 
 * Used in Training Management component.
 * 
 * Works with Strategy Pattern (behavioral) for flexible assignment logic.
 */
public class Training {

    private String trainingID;
    private String trainingName;
    private String trainingStatus; // NOT_STARTED, IN_PROGRESS, COMPLETED

    public Training(String trainingID) {
        this.trainingID = trainingID;
        this.trainingStatus = "NOT_STARTED";
    }

    /**
     * Assigns name/type of training (can come from lookup/customization).
     */
    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    /**
     * Marks training as completed.
     */
    public void completeTraining() {
        this.trainingStatus = "COMPLETED";
    }

    public String getTrainingStatus() {
        return trainingStatus;
    }
}
