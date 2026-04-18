package gui;

import data.*;
import model.*;
import java.util.*;

public class DummyData implements 
        IExitData,
        IExitInterviewData,
        IAssetData,
        IUserAccountData,
        IClearanceData,
        IPayrollData,
        ILeaveData,
        IDocumentData,
        ITimeTrackingData,
        IEmployeeProfileData {

    private Map<String, ExitRequest> exitMap = new HashMap<>();
    private Map<String, List<Document>> docMap = new HashMap<>();

    public void createExitRequest(ExitRequest request) {
        exitMap.put(request.getEmployeeID(), request);
    }

    public ExitRequest getExitDetails(String employeeID) {
        return exitMap.get(employeeID);
    }

    public void updateExitStatus(String employeeID, String status) {
        System.out.println("Dummy: Exit status updated → " + status);
    }

    @Override
    public void uploadDocument(Document doc) {
        docMap.putIfAbsent(doc.getEmployeeID(), new ArrayList<>());
        docMap.get(doc.getEmployeeID()).add(doc);

        System.out.println("Dummy: Document uploaded → " + doc.getType());
    }

    @Override
    public List<Document> getDocumentsByEmployee(String employeeID) {
        return docMap.getOrDefault(employeeID, new ArrayList<>());
    }

    @Override
    public void updateVerificationStatus(String documentID, String status) {
        System.out.println("Dummy: Document status updated → " + status);
    }

    // ===== EXIT INTERVIEW =====
    public void recordExitInterview(ExitInterview interview) {
        System.out.println("Dummy: Exit interview recorded");
    }

    public ExitInterview getInterviewByEmployee(String employeeID) {
        return null;
    }

    public void updateInterviewDetails(String interviewID, String feedback, String reason) {}

    // ===== ASSET =====
    public void allocateAsset(String employeeID, String assetType) {}

    public void updateAllocationStatus(String assetID, String status) {
        System.out.println("Dummy: Asset returned → " + assetID);
    }

    public List<Asset> getAssetsByEmployee(String employeeID) {
        return Arrays.asList(new Asset("A1"), new Asset("A2"));
    }

    // ===== USER ACCOUNT =====
    public UserAccount getUserByUsername(String username) {
        return null;
    }

    public void createUserAccount(UserAccount account) {}

    public void updatePassword(String userID, String password) {}

    public void updateAccessStatus(String userID, String status) {
        System.out.println("Dummy: Access revoked for " + userID);
    }

    // ===== CLEARANCE =====
    public void createSettlement(Clearance clearance) {
        System.out.println("Dummy: Settlement created");
    }

    public Clearance getSettlement(String employeeID) {
        return null;
    }

    public void updateClearanceStatus(String clearanceID, String status) {
        System.out.println("Dummy: Clearance completed");
    }

    // ===== PAYROLL =====
    public Payroll getPayrollByEmployee(String employeeID) {
        return new Payroll(50000); // fixed salary for demo
    }

    // ===== LEAVE =====
    public Leave getLeaveDetails(String employeeID) {
        return new Leave(10); // 10 days leave balance
    }

    // ===== ATTENDANCE (NEW) =====
    public Attendance getAttendance(String employeeID) {
        return new Attendance(18); 
    }

    // ===== EMPLOYEE PROFILE (NEW) =====
    public Employee getEmployeeById(String employeeID) {
        return new Employee(employeeID, "John Doe", 5);
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>();
    }

    public void updateEmployeeStatus(String employeeID, String status) {
        System.out.println("Dummy: Employee status updated → " + status);
    }
}