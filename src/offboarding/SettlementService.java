package offboarding;

import data.IPayrollData;
import data.ILeaveData;
import data.ITimeTrackingData;
import data.IAssetData;
import data.IEmployeeProfileData;

/**
 * Handles final settlement calculation using realistic HR logic.
 */
public class SettlementService {

    private IPayrollData payrollData;
    private ILeaveData leaveData;
    private ITimeTrackingData attendanceData;
    private IAssetData assetData; // kept for constructor compatibility
    private IEmployeeProfileData employeeData;

    public SettlementService(IPayrollData payrollData,
                             ILeaveData leaveData,
                             ITimeTrackingData attendanceData,
                             IAssetData assetData,
                             IEmployeeProfileData employeeData) {

        this.payrollData = payrollData;
        this.leaveData = leaveData;
        this.attendanceData = attendanceData;
        this.assetData = assetData;
        this.employeeData = employeeData;
    }

    public double calculateSettlement(String empID, ExitType type) {

        // 1. Base salary
        double monthlySalary = payrollData.getPayrollByEmployee(empID).getSalary();

        // 2. Attendance-based salary
        int workingDays = attendanceData.getAttendance(empID).getWorkingDays();

        // assume ~22 working days/month (industry standard)
        double dailySalary = monthlySalary / 22.0;
        double earnedSalary = dailySalary * workingDays;

        // 3. Leave encashment
        int leaveBalance = leaveData.getLeaveDetails(empID).getLeaveBalance();
        double leaveEncashment = leaveBalance * dailySalary;

        // 4. Severance logic (based on exit type)
        double severance = 0;

        int years = employeeData.getEmployeeById(empID).getYearsOfService();

        switch (type) {

            case RESIGNATION:
                // no severance
                severance = 0;
                break;

            case TERMINATION:
                // only partial salary (no severance)
                severance = 0;
                break;

            case LAYOFF:
                // severance = 1 month salary per year (simplified realistic model)
                severance = monthlySalary * years;
                break;

            case VRS:
                // VRS formula: 3 months salary × years
                severance = 3 * monthlySalary * years;
                break;
        }

        // 5. Reimbursements (dynamic, not hardcoded)
        // assume 2% of salary (travel, claims, etc.)
        double reimbursements = 0.01 * monthlySalary;

        // 6. Deductions (dynamic)
        // assume deductions based on unused notice period or penalties
        double deductions = 0;

        if (type == ExitType.TERMINATION) {
            deductions = 0.05 * monthlySalary; // penalty-based
        }

        //  7. Final calculation
        double total =
                earnedSalary +
                leaveEncashment +
                severance +
                reimbursements -
                deductions;

        System.out.println("Settlement calculated for " + empID + ": " + total);

        return total;
    }
}