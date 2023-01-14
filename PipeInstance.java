public class PipeInstance {
 
    String name;
    byte[] rdBuff;
    byte[] wrBuff;
    int bytesSkipped;
    int processesAttached; 
 
    public PipeInstance(String nm) {
        this.name = nm;
    }
}