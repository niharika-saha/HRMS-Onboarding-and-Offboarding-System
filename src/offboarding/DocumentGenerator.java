package offboarding;

import data.IDocumentData;
import model.Document;

/**
 * Handles backend generation of exit-related documents.
 * UI handles interaction — this class handles system-side creation.
 */
public class DocumentGenerator {

    private static IDocumentData documentData;

    // ✔ Required empty constructor
    public DocumentGenerator() {}

    // ✔ Lazy/static injection (same pattern used elsewhere)
    public static void setDocumentData(IDocumentData data) {
        documentData = data;
    }

    /**
     * Generates relieving letter and stores it
     */
    public void generateRelievingLetter(String empID) {

        if (documentData == null) {
            System.out.println("Warning: DocumentData not configured");
            return;
        }

        Document doc = new Document(
                "DOC-" + empID + "-RELIEVE",
                empID,
                "Relieving Letter",
                "GENERATED"
        );

        documentData.uploadDocument(doc);

        System.out.println("Relieving Letter generated for " + empID);
    }

    /**
     * Generates experience certificate and stores it
     */
    public void generateExperienceCertificate(String empID) {

        if (documentData == null) {
            System.out.println("Warning: DocumentData not configured");
            return;
        }

        Document doc = new Document(
                "DOC-" + empID + "-EXPERIENCE",
                empID,
                "Experience Certificate",
                "GENERATED"
        );

        documentData.uploadDocument(doc);

        System.out.println("Experience Certificate generated for " + empID);
    }

    /**
     * Generates compliance report
     */
    public void generateComplianceReport(String empID) {

        if (documentData == null) {
            System.out.println("Warning: DocumentData not configured");
            return;
        }

        Document doc = new Document(
                "DOC-" + empID + "-COMPLIANCE",
                empID,
                "Compliance Report",
                "GENERATED"
        );

        documentData.uploadDocument(doc);

        System.out.println("Compliance Report generated for " + empID);
    }
}