public class KernelandProcess {
    
    int processID;
    int timeLeft;
    int queueTracker;       
    int runToTimeoutCount; 
    int milliseconds;
    int virtualAddress;
    boolean hasMemory = false;
    UserlandProcess myNewProcess;
    PriorityEnum priority;
   
    public KernelandProcess() {
 
    }
 
     /**
    * Overloaded Constructor for KernelandProcess
    * @param myNewProcess
    * @param processID
    **/
 
    public KernelandProcess(UserlandProcess myNewProcess, int processID, PriorityEnum priority) {
    
    }
 
    /**
     * Accessor method
     * @return myNewProcess
     */
    public UserlandProcess getMyNewProcess(){
        return this.myNewProcess;
    }
 
    public KernelandProcess getKernelandProcess() {
        return this;
    }
 
    /**
     * Accessor methods
     * @return processID
     */
    public int getProcessID() {
        return this.processID;
    }
    
    public PriorityEnum getPriority() { 
       return this.priority;
    }
    
    public int getTimeLeft() {
        return this.timeLeft;
    }
    
    public int getRightQueue() {
        return this.queueTracker;
    }
    
    public int getRunToTimeoutCount() {
        return this.runToTimeoutCount;
    }
 
    public int getVirtualAddress() {
        return this.virtualAddress;
    }
 
    /**
     * Mutator method
     * @param newProcess 
     */
    public void setMyNewProcess(UserlandProcess newProcess) {
        this.myNewProcess = newProcess;
    }
    
    /**
     * Mutator methods
     * @param PID 
     */
    public void setProcessID(int PID) {
        this.processID = PID;
    }
    
    public void setPriority(PriorityEnum prior) {
        this.priority = prior;
    }
    
    public void setTimeLeft(int tl) {
        this.timeLeft = tl;
    }
    
    public void setRightQueue(int qt) {
        this.queueTracker = qt;
    }
    
    public void setRunToTimeoutCount(int count) {
        this.runToTimeoutCount = count;
    }
    
    public RunResult run() {
        return getMyNewProcess().run();
    }
    
    public void sleep(int milliseconds) {
        this.getMyNewProcess().sleep(milliseconds);
    }
 
    public void setVirtualAddress(int vA) {
        this.virtualAddress = vA;
    }
}