package model;

public class Task {
    private String taskID;
    private String description;

    public Task(String taskID, String description) {
        this.taskID = taskID;
        this.description = description;
    }

    public String getTaskID() {
        return taskID;
    }

    public String getDescription() {
        return description;
    }
}