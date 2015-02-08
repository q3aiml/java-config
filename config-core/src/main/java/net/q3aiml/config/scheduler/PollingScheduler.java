package net.q3aiml.config.scheduler;

/**
 * Determines when polling occurs.
 */
public interface PollingScheduler {
    /**
     * Starts polling.
     * <p>
     * Implementations may not allow restarting after stopping.
     * @param doPolling called every time polling should be performed. Should never be called while a previous
     *                  invocation is still running. The scheduler may stop if the runnable throws an exception.
     */
    public void start(Runnable doPolling);

    /**
     * Stops polling.
     */
    public void stop();

    public boolean isRunning();
}
