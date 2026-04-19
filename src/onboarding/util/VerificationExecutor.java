package onboarding.util;

import onboarding.exception.OnboardingException;
import onboarding.exception.ErrorCodes;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Utility class that runs a list of validation tasks in parallel using
 * a fixed thread pool.
 *
 * <p>Used by {@code DocumentVerificationHandler} to verify multiple documents
 * simultaneously, and by {@code PolicyComplianceHandler} to check multiple
 * policy rules concurrently.
 *
 * <p>Design decisions:
 * <ul>
 *   <li>Thread pool size is capped at {@link #MAX_THREADS} to avoid resource exhaustion.</li>
 *   <li>Each task is a {@link Callable}{@code <Boolean>} — returning {@code true}
 *       on success or throwing an exception on failure.</li>
 *   <li>If ANY task fails or times out the entire batch is considered failed.</li>
 *   <li>The executor is shut down cleanly after every batch.</li>
 * </ul>
 *
 * SOLID: Single Responsibility — this class only manages thread lifecycle.
 */
public class VerificationExecutor {

    /** Maximum parallel threads. Configurable per deployment environment. */
    private static final int MAX_THREADS = 5;

    /** Timeout (seconds) for a single verification batch. */
    private static final long TIMEOUT_SECONDS = 30;

    // Private constructor: utility class, no instantiation.
    private VerificationExecutor() {}

    /**
     * Runs all supplied tasks in parallel and waits for completion.
     *
     * <p>All tasks must complete successfully within {@value #TIMEOUT_SECONDS}
     * seconds. If any task returns {@code false}, throws, or times out, an
     * {@link OnboardingException} is raised with the supplied {@code errorCode}.
     *
     * @param tasks      Non-null list of verification callables.
     * @param errorCode  Error code to use if a task fails (from {@link ErrorCodes}).
     * @param context    Short description of what is being verified (for logging).
     * @throws OnboardingException if any task fails or times out.
     */
    public static void runParallel(
            List<Callable<Boolean>> tasks,
            String errorCode,
            String context) throws OnboardingException {

        if (tasks == null || tasks.isEmpty()) {
            System.out.println("[VerificationExecutor] No tasks submitted for: " + context);
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(
                Math.min(tasks.size(), MAX_THREADS));

        List<Future<Boolean>> futures = new ArrayList<>();

        try {
            // Submit all tasks to the thread pool
            for (Callable<Boolean> task : tasks) {
                futures.add(executor.submit(task));
            }

            // Collect results — fail fast on first error
            for (int i = 0; i < futures.size(); i++) {
                Future<Boolean> future = futures.get(i);
                try {
                    Boolean result = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    if (result == null || !result) {
                        throw new OnboardingException(
                                errorCode,
                                "[" + context + "] Task #" + (i + 1) + " returned failure.");
                    }
                    System.out.println("[VerificationExecutor] Task #" + (i + 1)
                            + " passed [" + context + "]");

                } catch (TimeoutException e) {
                    future.cancel(true); // interrupt the hanging thread
                    throw new OnboardingException(
                            errorCode,
                            "[" + context + "] Task #" + (i + 1) + " timed out after "
                                    + TIMEOUT_SECONDS + "s.",
                            e);

                } catch (ExecutionException e) {
                    // The task itself threw an exception
                    throw new OnboardingException(
                            errorCode,
                            "[" + context + "] Task #" + (i + 1) + " threw: "
                                    + e.getCause().getMessage(),
                            e.getCause());

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new OnboardingException(
                            errorCode,
                            "[" + context + "] Verification was interrupted.",
                            e);
                }
            }

            System.out.println("[VerificationExecutor] All " + tasks.size()
                    + " tasks passed for: " + context);

        } finally {
            // Always shut down the pool — even if an exception was thrown
            executor.shutdownNow();
        }
    }
}
