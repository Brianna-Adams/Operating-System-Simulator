import java.security.Key;
import java.util.*;
import java.util.Random;
 
/**
 * Defines interface between the user processes and the operating system
 * @author briannaadams
 */
public class PriorityScheduler extends MemoryManagement implements OSInterface {
   
    KernelandProcess process;
    int processID = -1;
    int currRunTime;        //Holds current process's run time
    int currTimeLeft;       //Holds curren't process remaining sleep time
    boolean ranToTimeout;   //Hold current process's ranToTimeout value
    int runToTimeoutCheck;  //Holds how many times current process has ran to time out 
    int deviceID;           //Holds "storage" of device ID
 
    //Queue for sleeping processes
    ArrayList <KernelandProcess> waitQueue = new ArrayList<>(); 
    //Realtime Process Queue 
    ArrayList <KernelandProcess> realTimeQueue = new ArrayList<>(); 
     //Interactive Process Queue 
    ArrayList <KernelandProcess> interactiveQueue = new ArrayList<>(); 
    //Background Process Queue 
    ArrayList <KernelandProcess> backgroundQueue = new ArrayList<>(); 
    //Current Device Queue
    ArrayList<Integer> currentDevices = new ArrayList<>();
    //Create a new VFS object
    VFS currentDevice = new VFS();
    //Create a new MemoryManagement object
    MemoryManagement memMan = new MemoryManagement(swapFile, nextPageNumber);
    //Create a fixed size array of mutex object
     MutexObject[] mutexObjects = new MutexObject[10];
 
    public int getCurrentDeviceID() {
        return this.currentDevice.getDevice();
    }
 
    public void setCurrentDeviceID(int dID) {
        this.deviceID = dID;
    }
     
    /**
     * This is called by user programs to create a new process and manage a 
     * list of KernelandProcesses
     * Creates a new KernelandProcess object by setting the UserlandProcess 
     * (obj) process, (int) processID, and ProcessEnum priority (obj)
     * Adds new KernelandProcess object to activeQueue
     * @param myNewProcess
     * @param priority
     * @return processID
     */
    @Override 
    public int CreateProcess(UserlandProcess myNewProcess, PriorityEnum priority) {
        processID = processID + 1; 
        process = new KernelandProcess(myNewProcess, processID, priority); 
        process.setMyNewProcess(myNewProcess); 
        process.setProcessID(processID); 
        //Set priority of process  
        process.setPriority(priority);  
        switch(priority) { 
            //assign realtime process a tag of 1 for tracking and add to realtime process queue 
            case Realtime -> { 
                System.out.println("This is realtime.");
                process.setRightQueue(1); 
                realTimeQueue.add(process); 
            } 
            //assign interactive process a tag of 2 for tracking and add to interactive process queue 
            case Interactive -> { 
                System.out.println("This is interactive.");
                process.setRightQueue(2); 
                System.out.println("My tag is " + process.getRightQueue());
                interactiveQueue.add(process);
            } 
            //assign background process a tag of 3 for tracking and add to background process queue 
            case Background -> { 
                System.out.println("This is backgrouond.");
                process.setRightQueue(3); 
                System.out.println("My tag is " + process.getRightQueue());
                backgroundQueue.add(process); 
            } 
        } 
    return processID; 
    } 
        
    /**
     * This is called by user programs to delete a process based on processID.
     * Loops through activeQueue and inspects each element's processID to see
     * if it matches the processID that has been called for deletion
     * @param processID
     * @return true if processID is found and deleted
     * @throws RescheduleException
     */
    @Override
    public boolean DeleteProcess(int processID) {
        //Search sleep queue for process using processID 
        for(int i = 0; i < waitQueue.size(); i++) { 
            int waitPID = waitQueue.get(i).getProcessID(); 
            if(waitPID == processID) { 
                waitQueue.remove(i); 
                for(int j = 0; j < currentDevices.size(); j++) {
                    currentDevice.Close(currentDevices.get(j));
                }
                return true; 
            } 
            memMan.freeMemory(processID);   
            for(int k = 0; k < 10; k++) {
                ReleaseMutex(k, processID);
            }        
        } 
        //Search real time queue for process using processID 
        for(int i = 0; i < realTimeQueue.size(); i++) { 
            int realTimePID = realTimeQueue.get(i).getProcessID(); 
            if(realTimePID == processID) { 
                realTimeQueue.remove(i); 
                for(int j = 0; j < currentDevices.size(); j++) {
                    currentDevice.Close(currentDevices.get(j));
                }
                return true; 
            }   
            memMan.freeMemory(processID); 
            for(int k = 0; k < 10; k++) {
                ReleaseMutex(k, processID);
            }           
        } 
        //Search interactive queue for process using processID 
        for(int i = 0; i < interactiveQueue.size(); i++) { 
            int interactivePID = interactiveQueue.get(i).getProcessID(); 
            if(interactivePID == processID) { 
                interactiveQueue.remove(i); 
                for(int j = 0; j < currentDevices.size(); j++) {
                    currentDevice.Close(currentDevices.get(j));
                }
                return true; 
            }   
            memMan.freeMemory(processID); 
            for(int k = 0; k < 10; k++) {
                ReleaseMutex(k, processID);
            }           
        } 
        //Search background queue for process using processID 
        for(int i = 0; i < backgroundQueue.size(); i++) { 
            int backgroundPID = backgroundQueue.get(i).getProcessID(); 
            if(backgroundPID == processID) { 
                backgroundQueue.remove(i); 
                for(int j = 0; j < currentDevices.size(); j++) {
                    currentDevice.Close(currentDevices.get(j));
                }
                return true; 
            }  
            for(int k = 0; k < 10; k++) {
                ReleaseMutex(k, processID);
            } 
            memMan.freeMemory(processID);            
        }    
    return false;  
    }
    
    /**
     * Runs the tasks in an infinite loop
     * Decide which queue to put it in
     * @return: void
     */
    @Override
    public void run(){
        //while(true) {
            Random rand = new Random();
            //create random int variable from 1 to 10
            int num = rand.nextInt(9);
            
            //if int variable chosen is 6/10, attempt to run a realtime process
            if(num >= 4) { 
                //if realtime queue is empty, create another random int variable from 1 to 4
                if(realTimeQueue.isEmpty()) {
                    int num2 = rand.nextInt(3);
                    //if random int variable is 1/4, run a background process
                    if(num2 == 0) {
                        num = 0;
                    }
                    //if random int variable is 3/4, run an interactive process
                    else if (num2 > 0) {
                        num = 1;
                    }  
                }
                else
                    //choose the first element in the FIFO arraylist to run
                    for(int i = 0; i < 1; i++) {
                        RunResult rrunResult = realTimeQueue.get(i).run();
                        //use currRunTime variable to hold current process's runtime
                        currRunTime = rrunResult.millisecondsUsed;
                        //use ranToTimeout variable to hold current process' ranToTimeout value
                        ranToTimeout = rrunResult.ranToTimeout; 
                        for(int j = 0; j < waitQueue.size(); j++) {
                            //use currTimeLeft variable to get current sleep process's time left to sleep
                            currTimeLeft = waitQueue.get(j).getTimeLeft();
                            //update current sleep process's time left to sleep with current process's runtime
                            waitQueue.get(j).setTimeLeft(currTimeLeft - currRunTime);
                            //if time left to sleep for current sleep process is 0 or less put it back in its queue
                            if(waitQueue.get(j).getTimeLeft() <= 0) {
                                //get process's tag
                                switch(waitQueue.get(j).getRightQueue()) { 
                                    //if tag is 1, put in realtime queue
                                    case 1 ->  {
                                        waitQueue.remove(waitQueue.get(i));
                                        realTimeQueue.add(waitQueue.get(j));
                                    }
                                    //if tag is 2, put in interactive queue
                                    case 2 ->  {
                                        waitQueue.remove(waitQueue.get(i));
                                        interactiveQueue.add(waitQueue.get(j));
                                    }
                                    //if tag is 3, put in background queue
                                    case 3 ->  {
                                        waitQueue.remove(waitQueue.get(i));
                                        backgroundQueue.add(waitQueue.get(j));
                                    }                                   
                                }
                             }
                        }
                        //check to see if current realtime process ran to timeout
                        if(ranToTimeout) {
                            //use runToTimeoutCheck variable to hold current process's run to timeout count 
                            runToTimeoutCheck = realTimeQueue.get(i).getRunToTimeoutCount();
                            //add to current process's run to timeout count
                            realTimeQueue.get(i).setRunToTimeoutCount(runToTimeoutCheck + 1);
                            //if current process has run to timeout more than 5 times, downgrade their priority
                            if(runToTimeoutCheck >= 5) {
                                //downgrade realtime process priority to interactive
                                realTimeQueue.get(i).setPriority(PriorityEnum.Interactive);
                                //downgrade realtime process's tag from 1 to 2
                                realTimeQueue.get(i).setRightQueue(2);
                                //set process's ranToTimeout variable to false so it's can't be downgraded again
                                ranToTimeout = false;
                                //add the downgraded process to the sleep queue                        
                                waitQueue.add(realTimeQueue.get(i));
                            }
                            //if current realtime process hasn't run to timeout more than 5 times, add to sleep queue
                            else {
                                //realTimeQueue.remove(realTimeQueue.get(i));
                                waitQueue.add(realTimeQueue.get(i));
                            }
                        }
                        //if current realtime process hasn't run to timeout, add to sleep queue
                        else {
                            realTimeQueue.remove(realTimeQueue.get(i));
                            waitQueue.add(realTimeQueue.get(i));
                        }
                    }
                    memMan.invalidateTLB();
            } 
            //if int variable chosen is 3/10, attempt to run an interactive process
            // OR if realtime queue is empty and int variable chosen is 3/4
            if((num >= 1) && (num <= 3)) {
                //check to see if interactive queue is empty
                if(interactiveQueue.isEmpty()) {
                 
                }
                else {
                    //choose the first element in the FIFO arraylist to run
                    for(int i = 0; i < 1; i++) {
                        RunResult irunResult = interactiveQueue.get(i).run();
                        //use currRunTime variable to hold current process's runtime
                        currRunTime = irunResult.millisecondsUsed;
                        //use ranToTimeout variable to hold current process' ranToTimeout value
                        ranToTimeout = irunResult.ranToTimeout; 
                        for(int j = 0; j < waitQueue.size(); j++) {
                            //use currTimeLeft variable to get current sleep process's time left to sleep
                            currTimeLeft = waitQueue.get(j).getTimeLeft();
                            //update current sleep process's time left to sleep with current process's runtime
                            waitQueue.get(j).setTimeLeft(currTimeLeft - currRunTime);
                            //if time left to sleep for current sleep process is 0 or less put it back in its queue
                            if(waitQueue.get(j).getTimeLeft() <= 0) {
                                //get process's tag
                                switch(waitQueue.get(j).getRightQueue()) {
                                    //if tag is 1, put in realtime queue
                                    case 1: {
                                        waitQueue.remove(waitQueue.get(i));
                                        realTimeQueue.add(waitQueue.get(j));
                                        break;
                                    }
                                    //if tag is 2, put in interactive queue
                                    case 2: {
                                        waitQueue.remove(waitQueue.get(i));
                                        interactiveQueue.add(waitQueue.get(j));
                                        break;
                                    }
                                    //if tag is 3, put in background queue
                                    case 3: {
                                        waitQueue.remove(waitQueue.get(i));
                                        backgroundQueue.add(waitQueue.get(j));
                                        break;
                                    }                                   
                                }
                            }
                        }
                        //check to see if current interactive process ran to timeout
                        if(ranToTimeout) {
                            //use runToTimeoutCheck variable to hold current process's run to timeout count
                            runToTimeoutCheck = interactiveQueue.get(i).getRunToTimeoutCount();
                            //add to current process's run to timeout count
                            interactiveQueue.get(i).setRunToTimeoutCount(runToTimeoutCheck + 1);
                            //if current process has run to timeout more than 5 times, downgrade their priority
                            if(runToTimeoutCheck >= 5) {
                                //downgrade interactive process priority to background
                                interactiveQueue.get(i).setPriority(PriorityEnum.Background);
                                //downgrade realtime process's tag from 2 to 3
                                interactiveQueue.get(i).setRightQueue(3);
                                //set process's ranToTimeout variable to false so it's can't be downgraded again
                                ranToTimeout = false;
                                //add the downgraded process to the sleep queue 
                                waitQueue.add(interactiveQueue.get(i));
                            }
                            //if current interactive process hasn't run to timeout more than 5 times, add to sleep queue
                            else {
                                interactiveQueue.remove(interactiveQueue.get(i));
                                waitQueue.add(interactiveQueue.get(i));
                            }
                        }
                        //if current interactive process hasn't run to timeout, add to sleep queue
                        else {
                            interactiveQueue.remove(interactiveQueue.get(i));
                            waitQueue.add(interactiveQueue.get(i));
                        } 
                    }
                }
            memMan.invalidateTLB();
            }
            //if int variable chosen is 3/10, attempt to run a background process
            // OR if realtime queue is empty and int variable chosen is 1/4
            if(num == 0) {
                //check to see if background queue is empty
                if(backgroundQueue.isEmpty()) {
         
                }
                else {
                    //choose the first element in the FIFO arraylist to run
                    for(int i = 0; i < 1; i++) {
                        //use currRunTime variable to hold current process's runtime
                        currRunTime = backgroundQueue.get(i).run().millisecondsUsed;
                        for(int j = 0; j < waitQueue.size(); j++) {
                            //use currTimeLeft variable to get current sleep process's time left to sleep
                            currTimeLeft = waitQueue.get(j).getTimeLeft();
                            //update current sleep process's time left to sleep with current process's runtime
                            waitQueue.get(j).setTimeLeft(currTimeLeft - currRunTime);
                            //if time left to sleep for current sleep process is 0 or less put it back in its queue
                           if(waitQueue.get(j).getTimeLeft() <= 0) {
                                //get process's tag
                                switch(waitQueue.get(j).getRightQueue()) {
                                    //if tag is 1, put in realtime queue
                                    case 1: {
                                        waitQueue.remove(waitQueue.get(i));
                                        realTimeQueue.add(waitQueue.get(j));
                                        break;
                                    }
                                    //if tag is 2, put in interactive queue
                                    case 2: {
                                        waitQueue.remove(waitQueue.get(i));
                                        interactiveQueue.add(waitQueue.get(j));
                                        break;
                                    }
                                    //if tag is 3, put in background queue
                                    case 3: {
                                        waitQueue.remove(waitQueue.get(i));
                                        backgroundQueue.add(waitQueue.get(j));
                                        break;
                                    }                                   
                                }
                            }
                        }
                        //add background process to sleep queue
                        backgroundQueue.remove(backgroundQueue.get(i));
                        waitQueue.add(backgroundQueue.get(i));
                    }
                }
                memMan.invalidateTLB();
            }
            
       // }
    }
 
    public int chooseMemoryProcess() {
        int chosenKLPprocess = 0;
        Random rand = new Random();
        int num = rand.nextInt(9);
 
        if(num == 1) {
            chosenKLPprocess = process.getProcessID();
            return chosenKLPprocess;
        }
        else if(num == 2 || num == 3) {
            for(int i = 0; i < waitQueue.size(); i++) {
                if(i == num) {
                    chosenKLPprocess = waitQueue.get(i).getProcessID();
                    return chosenKLPprocess;
                }
            } 
        }
        else if(num == 4 || num == 5) {
            if(realTimeQueue.isEmpty()) {
                Random rand2 = new Random();
                int num2 = rand2.nextInt(9);
                num = num2;
            }
            else if(!realTimeQueue.isEmpty()) {
                for(int i = 0; i < realTimeQueue.size(); i++) {
                    if(i == num) {
                        chosenKLPprocess = realTimeQueue.get(i).getProcessID();
                        return chosenKLPprocess;
                    }
                }
            }
        }
        else if(num == 6 || num == 7) {
            for(int i = 0; i < interactiveQueue.size(); i++) {
                if(i == num) {
                    chosenKLPprocess = interactiveQueue.get(i).getProcessID();
                    return chosenKLPprocess;
                }
            }
        }
        else if(num == 8 || num == 9) {
            for(int i = 0; i < backgroundQueue.size(); i++) {
                if(i == num) {
                    chosenKLPprocess = backgroundQueue.get(i).getProcessID();
                    return chosenKLPprocess;
                }
            }
        }
        return chosenKLPprocess;
    }
 
    @Override 
    public void sleep(int milliseconds) {
        process.setTimeLeft(milliseconds);    
    }    
    @Override
    public int Open(String s) {
        return currentDevice.Open(s);
    }
 
    @Override
    public void Close(int id) {
        currentDevice.Close(id);
    }
 
    @Override
    public byte[] Read(int id,int size) {
        return currentDevice.Read(id, size);
    }
 
    @Override
    public void Seek(int id,int to) {
        currentDevice.Seek(id, to);
    }
 
    @Override
    public int Write(int id, byte[] data) {
        return currentDevice.Write(id, data);
    }
 
    @Override
    public void WriteMemory(int virtual, byte value) throws RescheduleException {
 
    }
 
    @Override
    public byte ReadMemory(int virtual) throws RescheduleException {
        return memMan.ReadMemory(virtual);
    }
 
    @Override
    public int VirtualToPhysicalMapping(int virtualAddress, int processID) throws RescheduleException {
        return memMan.VirtualToPhysicalMapping(virtualAddress, processID);
    }
 
    @Override
    public int sbrk(int amount, int processID, int virtualAddress) {
        return memMan.sbrk(amount, processID, virtualAddress);
    }
 
    @Override
    public void freeMemory(int processID) {
        memMan.freeMemory(processID);
    }
 
    @Override
    public void invalidateTLB() {
        memMan.invalidateTLB();
    }
 
    @Override
    public int AttachToMutex(String name, KernelandProcess process){
        //Loop through mutex object array
        for(int i = 0; i < mutexObjects.length; i++) {
            //If a mutex object matches the parsed name and is not null, add the process to the mutex object's 
            //attached processes list 
            if((mutexObjects[i] != null) && (mutexObjects[i].mutexName.equalsIgnoreCase(name))) { 
                mutexObjects[i].attachedProcesses.add(process); 
                return i; 
            }
        }
        for(int j = 0; j < mutexObjects.length; j++) {
            if(mutexObjects[j] == null) {
                mutexObjects[j] = new MutexObject(); 
                mutexObjects[j].mutexName = name; 
                mutexObjects[j].attachedProcesses.add(process); 
                return j; 
            } 
        }
        return -1;
    }
 
    @Override
    public boolean Lock(int mutexId, int processID) throws RescheduleException {
        if(mutexObjects[mutexId].inUse == true) {
            //If the Lock() caller already holds the mutex, throw an error 
            if(mutexObjects[mutexId].processId == processID) { 
                throw new RescheduleException();
            }
            //If another process currently holds the mutex, add the Lock() caller to the wait queue
            else if(mutexObjects[mutexId].processId != processID) {
                waitQueue.add(process);
                return false;
            }
        }
        //If no process currently holds the mutex, the Lock() caller will sucessfully lock the mutex
        else if(mutexObjects[mutexId].inUse == false) {
            mutexObjects[mutexId].inUse = true;
            mutexObjects[mutexId].processId = processID;
            return true;
        }
        return false;
    }
 
    @Override
    public void Unlock(int mutexId, int processID) throws RescheduleException {
        //If Unlock() caller is calling Unlock() on an unlocked mutex, throw an error
        if(mutexObjects[mutexId].inUse == false) {
            throw new RescheduleException();
        }
        //If the Unlock() caller doesn't hold the mutex, throw an error
        else if((mutexObjects[mutexId].inUse == true) && (mutexObjects[mutexId].processId != processID)){
            throw new RescheduleException();
        }
        //Checks to make sure the current process holds the mutex
        else if((mutexObjects[mutexId].inUse == true) && (mutexObjects[mutexId].processId == processID)) {
            //Now no one owns the mutex and the mutex is unlocked
            mutexObjects[mutexId].inUse = false;
            mutexObjects[mutexId].processId = -1;
            //Loop through mutex object's interal process array
            for(int i = 0; i < mutexObjects[mutexId].attachedProcesses.size(); i++){
                //Loop through the wait queue
                for(int j = 0; j < waitQueue.size(); j++) {
                    //If a process attached to the mutex is waiting in the wait queue, pull it from the wait queue to a runnable queue
                    if(mutexObjects[mutexId].attachedProcesses.get(i).getProcessID() == waitQueue.get(j).getProcessID()) {
                        switch(waitQueue.get(j).getPriority()) { 
                            case Realtime -> {  
                                waitQueue.remove(waitQueue.get(j)); 
                                realTimeQueue.add(waitQueue.get(j));  
                            }   
                            case Interactive -> {  
                                waitQueue.remove(waitQueue.get(j)); 
                                interactiveQueue.add(process); 
                            }  
                            case Background -> {  
                                waitQueue.remove(waitQueue.get(j)); 
                                backgroundQueue.add(process);  
                            }  
                        } 
                    }
                }
            }
        }
    }
 
    @Override
    public void ReleaseMutex(int mutexId, int processID)  {
        //If the process to be released holds the mutex, unlock it and change its process owner ID to 0 
        if((mutexObjects[mutexId].inUse == true) && (mutexObjects[mutexId].processId == processID)) {
            mutexObjects[mutexId].inUse = false; 
            mutexObjects[mutexId].processId = -1;
            for(int i = 0; i < mutexObjects[mutexId].attachedProcesses.size(); i++){
                //Find the process and detach it from the mutex's internal process array
                if(mutexObjects[mutexId].attachedProcesses.get(i).getProcessID() == processID) {
                    mutexObjects[mutexId].attachedProcesses.remove(i);
                }
                //Pull a process from the wait queue
                for(int j = 0; j < waitQueue.size(); j++) {
                    //Moves process from the wait queue to a runnable queue
                    if(mutexObjects[mutexId].attachedProcesses.get(i).getProcessID() == waitQueue.get(j).getProcessID()) {
                        switch(waitQueue.get(j).getPriority()) { 
                            case Realtime -> { 
                                waitQueue.remove(waitQueue.get(j));
                                realTimeQueue.add(waitQueue.get(j)); 
                            }  
                            case Interactive -> { 
                                waitQueue.remove(waitQueue.get(j));
                                interactiveQueue.add(process);
                            } 
                            case Background -> { 
                                waitQueue.remove(waitQueue.get(j));
                                backgroundQueue.add(process); 
                            } 
                        } 
                    }
                }
            }
        }
        //If the process doesn't hold the mutex, dettach it from the mutex's internal process array
        else { 
            for(int i = 0; i < mutexObjects[mutexId].attachedProcesses.size(); i++) {
                if(mutexObjects[mutexId].attachedProcesses.get(i).getProcessID() == processID) {
                    mutexObjects[mutexId].attachedProcesses.remove(i);
                }
            }
        }
    }
}