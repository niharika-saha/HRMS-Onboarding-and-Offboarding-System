package offboarding;

import data.IExitData;
import model.ExitRequest;

public class ExitManager implements IExitData {

    private IExitData data;
    private static ExitManager instance;

    public ExitManager(IExitData data) {
        this.data = data;
        instance = this;
    }

    public void initiateExit(String empID, ExitType type) {
        if (data.getExitDetails(empID) == null) {
            data.createExitRequest(
                new ExitRequest(empID, type, "INITIATED", null));
        }
    }

    @Override
    public void createExitRequest(ExitRequest req) {
        data.createExitRequest(req);
    }

    @Override
    public ExitRequest getExitDetails(String empID) {
        ExitRequest req = data.getExitDetails(empID);
        if (req == null) {
            req = new ExitRequest(empID, null, "INITIATED", null);
            data.createExitRequest(req);
        }
        return req;
    }

    @Override
    public void updateExitStatus(String empID, String status) {
        data.updateExitStatus(empID, status);
    }

    public ExitRequest getExitRequest(String empID) {
        return getExitDetails(empID);
    }

    public static ExitRequest get(String empID) {
        if (instance == null) throw new RuntimeException("ExitManager not initialized");
        return instance.getExitDetails(empID);
    }
}