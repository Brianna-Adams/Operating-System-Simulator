public class VirtualToPhysicalMapping {
    int processID;
    Integer physicalPageNumber;
    int onDiskPageNumber;
    boolean isDirty;

    public VirtualToPhysicalMapping() {

    }

    public VirtualToPhysicalMapping(int physicalPageNumber, int onDiskPageNumber, boolean isDirty) {
        physicalPageNumber = -1;
        onDiskPageNumber = -1;
        isDirty = false;
    }

    public int getProcessID() {
        return this.processID;
    }

    public Integer getPhysicalPageNumber() {
        return this.physicalPageNumber;
    }

    public int getOnDiskPageNumber() {
        return this.onDiskPageNumber;
    }

    public boolean getIsDirty() {
        return this.isDirty;
    }

    public void setProcessID(int pID) {
        this.processID = pID;
    }

    public void setPhysicalPageNumber(int pPgNum) {
        this.physicalPageNumber = pPgNum;
    }

    public void setOnDiskPageNumber(int dPgNum) {
        this.onDiskPageNumber = dPgNum;
    }

    public void setIsDirty(boolean is) {
        this.isDirty = is;
    }
}