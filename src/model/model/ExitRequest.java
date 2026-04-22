package model.model;

import java.util.Date;
import offboarding.ExitType;

public class ExitRequest {

    private String employeeID;
    private ExitType exitType;
    private String status;
    private Date lastWorkingDay;
    private boolean laptopReturned;
    private boolean idCardReturned;
    private boolean accessRevoked;
    private boolean emailDisabled;
    private boolean financeCleared;

    public ExitRequest(String employeeID, ExitType exitType) {
        this.employeeID = employeeID;
        this.exitType = exitType;
    }

    public ExitRequest(String employeeID, ExitType exitType, String status, Date lastWorkingDay) {
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

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastWorkingDay() {
        return lastWorkingDay;
    }

    public void setLastWorkingDay(Date lastWorkingDay) {
        this.lastWorkingDay = lastWorkingDay;
    }

    public boolean isLaptopReturned() {
        return laptopReturned;
    }

    public void setLaptopReturned(boolean laptopReturned) {
        this.laptopReturned = laptopReturned;
    }

    public boolean isIdCardReturned() {
        return idCardReturned;
    }

    public void setIdCardReturned(boolean idCardReturned) {
        this.idCardReturned = idCardReturned;
    }

    public boolean isAccessRevoked() {
        return accessRevoked;
    }

    public void setAccessRevoked(boolean accessRevoked) {
        this.accessRevoked = accessRevoked;
    }

    public boolean isEmailDisabled() {
        return emailDisabled;
    }

    public void setEmailDisabled(boolean emailDisabled) {
        this.emailDisabled = emailDisabled;
    }

    public boolean isFinanceCleared() {
        return financeCleared;
    }

    public void setFinanceCleared(boolean financeCleared) {
        this.financeCleared = financeCleared;
    }
}
