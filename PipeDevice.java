import java.util.*;
 
public class PipeDevice implements Device {
 
    PipeInstance pipe;
    PipeInstance[] pipeInstance = new PipeInstance[10];
    int id;
 
    static PipeDevice pipeDevice = new PipeDevice();
 
    public static PipeDevice getPipeDevice() {
        return pipeDevice;
    }
 
    public int Open(String s) {
        pipe = pipeInstance[id];
        for(int id = 0; id < pipeInstance.length; id++) {
            if(pipe.name.equals(s)) {
                pipe.processesAttached += 1;
                return id;
            }
            else
                continue;
        }
        for(int id = 0; id < pipeInstance.length; id++) {
            if(pipe != null) {
                pipe = new PipeInstance(s);
                pipe.processesAttached += 1;
                return id;
            }
            else if(pipe == null)
                continue;
        }
        return id;
    }
 
    public void Close(int id) {
        pipe = pipeInstance[id];
        if(pipe != null) {
            pipe.processesAttached -= 1;
            if(pipe.processesAttached == 0) {
                pipe = null;
            }
        }
    }
 
    public byte[] Read(int id,int size) {
        pipe = pipeInstance[id];
        byte[] rd = Arrays.copyOfRange(pipe.rdBuff, 0, size);
        return rd;
    }
 
    public void Seek(int id,int to) {
        pipe = pipeInstance[id];
        pipe.bytesSkipped += to;
    }
 
    public int Write(int id, byte[] data) {
        pipe = pipeInstance[id];
        pipe.wrBuff = data.clone();
        return pipe.wrBuff.length;
    }
}