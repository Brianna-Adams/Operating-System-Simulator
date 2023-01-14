import java.util.ArrayList;
 
public class MutexObject {
 
    String mutexName;
    boolean inUse;
    int processId;
    ArrayList<KernelandProcess> attachedProcesses;
 
    public MutexObject() {
        mutexName = null;
        processId = -1;
        inUse = false;
        attachedProcesses = new ArrayList<>();
    }
}