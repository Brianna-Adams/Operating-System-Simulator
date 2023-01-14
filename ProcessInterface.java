public interface ProcessInterface {
    int CreateProcess(UserlandProcess myNewProcess, PriorityEnum priority);
    boolean DeleteProcess(int processId);
    void sleep(int milliseconds);
    void run();
    int Open(String s);
    void Close(int id);
    byte[] Read(int id,int size);
    void Seek(int id,int to);
    int Write(int id, byte[] data);
}