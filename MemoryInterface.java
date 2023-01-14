public interface MemoryInterface{
    void WriteMemory(int virtual, byte value) throws RescheduleException;
    byte ReadMemory(int virtual) throws RescheduleException;
    int VirtualToPhysicalMapping(int virtual, int processID) throws RescheduleException;
    int sbrk(int amount, int processID, int virtualMemory);
    void freeMemory(int processID);
    void invalidateTLB();
}
