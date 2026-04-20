package offboarding;

import data.IExitInterviewData;
import model.ExitInterview;

public class ExitInterviewManager {

    private IExitInterviewData data;

    public ExitInterviewManager(IExitInterviewData data) {
        this.data = data;
    }

    public void conductInterview(String empID,
                                 String feedback,
                                 String reason,
                                 int rating) {

        ExitInterview interview = new ExitInterview(
                "INT-" + empID,
                empID,
                feedback,
                reason,
                rating
        );

        data.recordExitInterview(interview);

        System.out.println("Interview recorded for " + empID);
    }
}