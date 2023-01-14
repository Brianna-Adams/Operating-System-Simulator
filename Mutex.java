public interface Mutex {
    public int AttachToMutex(String name, KernelandProcess process);
    public boolean Lock(int mutexId, int processID) throws RescheduleException;
    public void Unlock(int mutexId, int processID) throws RescheduleException;
    public void ReleaseMutex(int mutexId, int processID);
}
