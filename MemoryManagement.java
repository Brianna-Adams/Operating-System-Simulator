import java.util.Random;
import java.util.ArrayList;
import java.util.BitSet;
 
public class MemoryManagement extends KernelandProcess implements MemoryInterface {
 
    int virtualAddress;
    Integer physicalPageNumber;
    int physicalPageOffset;
    int nextPageNumber;
    int returnAmount;
    int physicalPagesNeeded;
    int chosenKLPprocess;
    int victimPhysicalPageNumber;
    int tlbVirtual = -1;
    int tlbPhysical = -1;
    BitSet freeList;
 
    FakeFileSystem swapFile = new FakeFileSystem();
    PriorityScheduler pScheduler = new PriorityScheduler();
 
    ArrayList<VirtualToPhysicalMapping> virtualToPhysicalMappingArray = new ArrayList<>();
    ArrayList<byte[]>physicalMemory = new ArrayList<>();
    byte[] offset = new byte[1024];
    ArrayList<byte[]>diskMemoryArray = new ArrayList<>();
    byte[] diskMemory = new byte[1024];
    byte[] loadData = new byte[1024];
    public MemoryManagement() { 
 
    }
 
    public MemoryManagement(FakeFileSystem swapFile, int nextPageNumber) {
        swapFile = new FakeFileSystem();
        nextPageNumber = 0;
    }
 
    public void WriteMemory(int virtualAddress, byte value) throws RescheduleException {
        physicalPageNumber = VirtualToPhysicalMapping(virtualAddress, processID);
        physicalPageOffset = virtualAddress%1024;
 
        //Loop through physical memory
        for(int i = 0; i < 1023; i++) {
            if((i == physicalPageNumber) && (physicalMemory.get(i) != null)) {
                throw new RescheduleException();
            }
            //If the physical page number if found...
            else if((i == physicalPageNumber) && (physicalMemory.get(i) != null)) {
                //Loop through the page until the offset is found
                for(int j = 0; j < 1023; j++) {
                    if(j == physicalPageOffset) {
                        //Write the data to the page
                        offset[j] = value;
                        for(int k = 0; k < 1023; k++) {
                            if((virtualToPhysicalMappingArray.get(k).getPhysicalPageNumber() == physicalPageNumber) && (virtualToPhysicalMappingArray.get(k) == null)) {
                                throw new RescheduleException();
                            }
                            //Loop through the VPTM and find the appropriate virtual index
                            //Once the virtual index is found set the isDirty flag for the page
                            else if((virtualToPhysicalMappingArray.get(k).getPhysicalPageNumber() == physicalPageNumber) && (virtualToPhysicalMappingArray.get(k) != null)) {
                                virtualToPhysicalMappingArray.get(k).setIsDirty(true);
                            }
                        }
                    }
                }
            }
        }
    }
       
    public byte ReadMemory(int virtualAddress) throws RescheduleException {
        physicalPageNumber = VirtualToPhysicalMapping(virtualAddress, processID);
        physicalPageOffset = virtualAddress%1024;
        byte value = 0;
 
        //Loop through physical memory
        for(int i = 0; i < 1023; i++) {
            if((i == physicalPageNumber) && (physicalMemory.get(i) == null)) {
                throw new RescheduleException();
            }
            //Once the correct physical page is found...
            else if((i == physicalPageNumber) && (physicalMemory.get(i) != null)){
                for(int j = 0; j < 1023; j++) {
                    //If the previous line on the physical page is empty, then load data from disk
                    if((j == physicalPageOffset) && (offset[j-1] == 0)) {
                        //Variable to hold VPTM object's disk index
                        int diskIndex;
                        //Loop through disk memory
                        for(int k = 0; k < 1023; k++) {
                            if(virtualToPhysicalMappingArray.get(i).getPhysicalPageNumber() == physicalPageNumber) {
                                diskIndex = virtualToPhysicalMappingArray.get(i).getOnDiskPageNumber();
                                //"Save" data from disk
                                for(int l = 0; l < 1023; l++) {
                                    loadData = diskMemoryArray.get(diskIndex);
                                }
                            }
                        }
                    }
                    //If the previous offset line isn't empty, then read from the offset
                    else if ((j == physicalPageOffset) && (offset[j-1] != 0)) {
                        offset[j] = value;
                        return value;
                    }
                    //Insert data from disk into RAM
                    physicalMemory.add(loadData);
                }
            }
        }
        return value;  
    }
 
    public int VirtualToPhysicalMapping(int virtualAddress, int processID) throws RescheduleException {
        physicalPageNumber = virtualAddress/1024;
 
        for(int i = 0; i < 1023; i++) {
            if((virtualToPhysicalMappingArray.get(i).getPhysicalPageNumber() == physicalPageNumber) && virtualToPhysicalMappingArray.get(i) == null) {
                throw new RescheduleException();
            }
            else if(virtualToPhysicalMappingArray.get(i).getPhysicalPageNumber() == physicalPageNumber && virtualToPhysicalMappingArray.get(i) != null) {
                for(int j = 0; j < 1023; j++) {
                    //If phyiscal page already exists, return the physical page number
                    if((j == physicalPageNumber) && (physicalMemory.get(j) != null)) {
                        return physicalPageNumber;
                    }
                    //If phyiscal page doesn't exist, create a new physical page and return it
                    else if((j == physicalPageNumber) && (physicalMemory.get(j) == null)) {
                        physicalMemory.add(offset);
                        return physicalPageNumber;
                    }
                }
            }
            else if(virtualToPhysicalMappingArray.get(i).getPhysicalPageNumber() == -1 && (virtualToPhysicalMappingArray.size() == 1023)) {
                //Choose random process from PriorityScheduler class
                chosenKLPprocess = pScheduler.chooseMemoryProcess();
                //Create a random index 
                Random rand = new Random();
                int randomPage = rand.nextInt(virtualToPhysicalMappingArray.size()-1);
                //Find the process's page that is closest to the randomly generated index integer
                for(int j = randomPage; j < 1023; j++) {
                    int victimProcessID = virtualToPhysicalMappingArray.get(j).getProcessID();
                    //If the processID of the random process matches the processID of the VTPM object check
                    //to see whether it has "unsaved" changes that have not yet been written to disk
                    if(victimProcessID == chosenKLPprocess) {
                        //if isDirty is true, we have to write the process memory to disk
                        if(virtualToPhysicalMappingArray.get(j).getIsDirty() == true) {
                            //Open process for writing
                            swapFile.Open(String.valueOf(victimProcessID));
                            //Read the page memory
                            diskMemory = swapFile.Read(victimProcessID, 1024);
                            swapFile.Close(victimProcessID);
                            for(int k = 0; k < 1023; k++) {
                                //Add read page memory to disk
                                diskMemoryArray.add(k, diskMemory);
                                //Set disk index
                                virtualToPhysicalMappingArray.get(j).setOnDiskPageNumber(k);
                            }
                            //Set the physical page number to that of the thief process
                            virtualToPhysicalMappingArray.get(j).setPhysicalPageNumber(physicalPageNumber);
                            //Set isDirty flag back to false 
                            virtualToPhysicalMappingArray.get(j).setIsDirty(false);
                            for(int l = 0; l < 1023; l++) {
                                if((l == physicalPageNumber) && (physicalMemory.get(l) != null)) {
                                    //"Clear" physical pages of victim's data by removing the physical page
                                    physicalMemory.remove(l);
                                    //Load data from current process and insert it into physical page
                                    swapFile.Open(String.valueOf(processID));
                                    loadData = swapFile.Read(processID, 1024);
                                    swapFile.Close(processID);
                                    //Load the thief's data into the physical page
                                    physicalMemory.add(loadData);
                                    return physicalPageNumber;
                                }
                            }
                        }
                        //if isDirty is false, we don't have to write the process memory out to disk
                        else if(virtualToPhysicalMappingArray.get(j).getIsDirty() == false) {
                            virtualToPhysicalMappingArray.get(j).setPhysicalPageNumber(physicalPageNumber);
                            for(int l = 0; l < 1023; l++) {
                                if((l == physicalPageNumber) && (physicalMemory.get(l) != null)) {
                                    //"Clear" physical pages of victim's data by removing the physical page
                                    physicalMemory.remove(l);
                                    //Load data from current process and insert it into physical page
                                    swapFile.Open(String.valueOf(processID));
                                    loadData = swapFile.Read(processID, 1024);
                                    swapFile.Close(processID);
                                    //Load thief's data into the phyiscal page
                                    physicalMemory.add(loadData);
                                    return physicalPageNumber;
                                }
                            } 
                        }
                    }
                }
            } 
        }
        return physicalPageNumber;
    }
 
    public int sbrk(int amount, int processID, int virtualAddress)  {
        physicalPageNumber = virtualAddress/1024;
        physicalPagesNeeded = (amount/1024) + 1;
        returnAmount = 0;
 
        for(int i = 0; i < 1023; i++) {
            if(virtualToPhysicalMappingArray.get(i) == null) {
                virtualToPhysicalMappingArray.get(i).setPhysicalPageNumber(physicalPageNumber);
                virtualToPhysicalMappingArray.get(i).setProcessID(processID);
                if(physicalPagesNeeded > 1) {
                    for(int j = i; j < physicalPagesNeeded -1; j++) {
                        if(virtualToPhysicalMappingArray.get(j) == null) {
                            virtualToPhysicalMappingArray.get(j).setPhysicalPageNumber(physicalPageNumber);
                            virtualToPhysicalMappingArray.get(j).setProcessID(processID);
                        }
                        returnAmount = j*1024;
                    }
                }
                else if(physicalPagesNeeded <= 1) {
                    returnAmount = i*1024;
                }
            }
        }
        return returnAmount;  
    }
  
    public void freeMemory(int processID) {
        for(int i = 0; i < physicalMemory.size(); i++) {
                if(i == physicalPageNumber) {
                    freeList.clear(i);
                }
            for(int j = 0; j < physicalMemory.size(); j++) {
                if(j == physicalPageOffset) {
                    freeList.clear(j);
                }
            }  
        }
        for(int i = 0; i < virtualToPhysicalMappingArray.size(); i++) {
            if(virtualToPhysicalMappingArray.get(i).getPhysicalPageNumber() == physicalPageNumber) {
                virtualToPhysicalMappingArray.get(i).setPhysicalPageNumber(0);
            }
        }
    }
 
    public void invalidateTLB() {
        tlbPhysical = -1;
        tlbVirtual = -1;
    }
}