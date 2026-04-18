package model;

import offboarding.ExitType;

public class ExitRequest {

    private String employeeID;
    private ExitType exitType; 
    private String status;
    private String lastWorkingDay;
    private boolean laptopReturned;
    private boolean idCardReturned;
    private boolean accessRevoked;
    private boolean emailDisabled;
    private boolean financeCleared;

    public ExitRequest(String employeeID,
                       ExitType exitType,
                       String status,
                       String lastWorkingDay) {
        this.employeeID = employeeID;
        this.exitType = exitType;
        this.status = status;
        this.lastWorkingDay = lastWorkingDay;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public ExitType getExitType() {
        return exitType;
    }

    public String getStatus() {
        return status;
    }

    public String getLastWorkingDay() {
        return lastWorkingDay;
    }

    public boolean isLaptopReturned() { return laptopReturned; }
    public void setLaptopReturned(boolean val) { this.laptopReturned = val; }

    public boolean isIdCardReturned() { return idCardReturned; }
    public void setIdCardReturned(boolean val) { this.idCardReturned = val; }

    public boolean isAccessRevoked() { return accessRevoked; }
    public void setAccessRevoked(boolean val) { this.accessRevoked = val; }

    public boolean isEmailDisabled() { return emailDisabled; }
    public void setEmailDisabled(boolean val) { this.emailDisabled = val; }

    public boolean isFinanceCleared() { return financeCleared; }
    public void setFinanceCleared(boolean val) { this.financeCleared = val; }
}