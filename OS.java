public class OS implements OSInterface {
    
    PriorityScheduler pScheduler = new PriorityScheduler();
    public static OS oSystem = new OS();
    int dID;
    int arrayListIndex = -1;
 
    public OS() {
        
    }
    
    public static OS getOS() {
        return oSystem;
    }
    
    public int CreateProcess(UserlandProcess myNewProcess, PriorityEnum priority) {
        return pScheduler.CreateProcess(myNewProcess, priority);
    }
    
    public boolean DeleteProcess(int processID) {
        return pScheduler.DeleteProcess(processID);
    }
    
    public void run() {
        pScheduler.run();
    }
 
    public int chooseMemoryProcess() {
        return pScheduler.chooseMemoryProcess();
    }
    
    public void sleep(int milliseconds) {
        pScheduler.sleep(milliseconds);
    }  
 
    public int Open(String s) {
        arrayListIndex += 1;
        dID = pScheduler.currentDevice.Open(s);
        pScheduler.setCurrentDeviceID(dID);
        pScheduler.currentDevices.add(arrayListIndex, dID);
        return pScheduler.getCurrentDeviceID();
    }
 
    public void Close(int id) {
        pScheduler.currentDevice.Close(id);
        pScheduler.currentDevices.remove(Integer.valueOf(dID));
    }
 
    public byte[] Read(int id,int size) {
        return pScheduler.Read(id, size);
    }
 
    public void Seek(int id,int to) {
        pScheduler.Seek(id, to);
    }
 
    public int Write(int id, byte[] data) {
        return pScheduler.Write(id, data);
    }
 
    public void WriteMemory(int virtual, byte value) throws RescheduleException {
 
    }
 
    public byte ReadMemory(int virtual) throws RescheduleException{
        return pScheduler.ReadMemory(virtual);
    }
 
    public int VirtualToPhysicalMapping(int virtualAddress, int processID) throws RescheduleException {
        return pScheduler.VirtualToPhysicalMapping(virtualAddress, processID);
    }
 
    public int sbrk(int amount, int processID, int virtualAddress) {
        return pScheduler.sbrk(amount, processID, virtualAddress);
    }
 
    public void freeMemory(int processID) {
        pScheduler.freeMemory(processID);
    }
 
    public void invalidateTLB() {
        pScheduler.invalidateTLB();
        
    }
 
    public int AttachToMutex(String name, KernelandProcess process) {
        return pScheduler.AttachToMutex(name, process);
    }
 
    public boolean Lock(int mutexId, int processID) throws RescheduleException {
        return pScheduler.Lock(mutexId, processID);
    }
 
    public void Unlock(int mutexId, int processID) throws RescheduleException {
        pScheduler.Unlock(mutexId, processID);
    }
 
    public void ReleaseMutex(int mutexId, int processID) {
        pScheduler.ReleaseMutex(mutexId, processID);
    }
}