public abstract class UserlandProcess {
    /**
     * Methods will be called by OS (acting as the kernel) to do user work.
     */
    public abstract RunResult run();
    public abstract void sleep(int milliseconds);
}