package onboarding.handler;

import data.IDocumentData;
import model.Document;
import onboarding.exception.ErrorCodes;
import onboarding.exception.OnboardingException;
import onboarding.util.VerificationExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Handler #1 in the Pre-Onboarding chain.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Fetches all documents linked to the candidate from the document data layer.</li>
 *   <li>Verifies each document in <strong>parallel</strong> using
 *       {@link VerificationExecutor} (multithreading requirement).</li>
 *   <li>Updates individual document verification statuses via the data interface.</li>
 *   <li>Throws {@link OnboardingException} if any document fails; otherwise
 *       passes control to the next handler.</li>
 * </ul>
 *
 * <p>Note: The {@code candidateId} is used here as the employeeID lookup key
 * because, at the pre-onboarding stage, the candidate has not yet received a
 * formal employeeID. The data layer is expected to handle this mapping.
 *
 * SOLID: SRP — only concerns itself with document verification.
 */
public class DocumentVerificationHandler extends OnboardingHandler {

    /** Status written to the data layer when a document passes verification. */
    private static final String STATUS_VERIFIED = "VERIFIED";

    /** Status written to the data layer when a document fails verification. */
    private static final String STATUS_FAILED = "FAILED";

    private final IDocumentData documentData;

    /**
     * @param documentData  Injected data interface for document operations.
     *                      Must not be {@code null}.
     */
    public DocumentVerificationHandler(IDocumentData documentData) {
        if (documentData == null) {
            throw new IllegalArgumentException("IDocumentData must not be null.");
        }
        this.documentData = documentData;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Validates all documents for {@code candidateId} in parallel threads.
     * If no documents exist, or any document verification task fails, an
     * {@link OnboardingException} is thrown with code
     * {@link ErrorCodes#DOCUMENT_VERIFICATION_FAILED}.
     */
    @Override
    public void process(String candidateId) throws OnboardingException {
        System.out.println("[" + handlerName() + "] Starting document verification for candidate: "
                + candidateId);

        // Fetch documents from data layer (candidateId used as the lookup key)
        List<Document> documents = documentData.getDocumentsByEmployee(candidateId);

        if (documents == null || documents.isEmpty()) {
            throw new OnboardingException(
                    ErrorCodes.NO_DOCUMENTS_FOUND,
                    "No documents found for candidate: " + candidateId);
        }

        System.out.println("[" + handlerName() + "] Found " + documents.size()
                + " document(s) — verifying in parallel...");

        // Build one callable per document for parallel verification
        List<Callable<Boolean>> verificationTasks = new ArrayList<>();

        for (Document doc : documents) {
            verificationTasks.add(() -> verifySingleDocument(doc));
        }

        // Run all tasks in parallel; throws OnboardingException if any fail
        VerificationExecutor.runParallel(
                verificationTasks,
                ErrorCodes.DOCUMENT_VERIFICATION_FAILED,
                "DocumentVerification[" + candidateId + "]");

        System.out.println("[" + handlerName() + "] All documents verified successfully.");

        // Chain continues
        passToNext(candidateId);
    }

    /**
     * Verifies a single document.
     *
     * <p>Simulates verification logic: a document is considered valid when its
     * type is non-null/non-blank and its current status is not already FAILED.
     * In a production system this would call an external verification API.
     *
     * @param doc  The document to verify.
     * @return {@code true} if the document passes.
     * @throws OnboardingException if the document fails verification.
     */
    private boolean verifySingleDocument(Document doc) throws OnboardingException {
        System.out.println("  [Thread:" + Thread.currentThread().getName()
                + "] Verifying document: " + doc.getDocumentID()
                + " | Type: " + doc.getType());

        // Basic validation: type must be present
        if (doc.getType() == null || doc.getType().isBlank()) {
            documentData.updateVerificationStatus(doc.getDocumentID(), STATUS_FAILED);
            throw new OnboardingException(
                    ErrorCodes.DOCUMENT_VERIFICATION_FAILED,
                    "Document " + doc.getDocumentID() + " has no type — verification failed.");
        }

        // Guard: already marked FAILED upstream
        if (STATUS_FAILED.equalsIgnoreCase(doc.getStatus())) {
            throw new OnboardingException(
                    ErrorCodes.DOCUMENT_VERIFICATION_FAILED,
                    "Document " + doc.getDocumentID() + " was pre-marked as FAILED.");
        }

        // Mark as verified in the data layer
        documentData.updateVerificationStatus(doc.getDocumentID(), STATUS_VERIFIED);

        return true;
    }
}
