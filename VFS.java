public class VFS {
 
    Mappings currentDevice;
    int id;
 
    public int getDevice() {
        for(int id = 0; id < currentDevice.deviceType.length; id++) {
            if(currentDevice.deviceType[id] != null) {
                return id;
            }
            else
                continue;
        }
        return id;
    }
 
    public int Open(String s) {
        String[] deviceInfo = s.split(" ", 1);
        String dName = deviceInfo[0];
        String dID = deviceInfo[1];
        int dI = getDevice();
        if(dName == "random") {
            currentDevice.deviceType[dI] = RandomDevice.getRandomDevice();
            currentDevice.deviceID[dI] = currentDevice.deviceType[dI].Open(dID);
        }
        else if(dName == "pipe") {
            currentDevice.deviceType[dI] = PipeDevice.getPipeDevice();
            currentDevice.deviceID[dI] = currentDevice.deviceType[dI].Open(dID);
        }
        else if(dName == "file") {
            currentDevice.deviceType[dI] = FakeFileSystem.getFakeFileSystem();
            currentDevice.deviceID[dI] = currentDevice.deviceType[dI].Open(dID);
        }
        return dI;
    }
 
    public void Close(int id) {
        currentDevice.deviceType[id].Close(currentDevice.deviceID[id]);
        currentDevice.deviceID[id] = 0;
    }
     
    public byte[] Read(int id,int size) {
        return currentDevice.deviceType[id].Read(currentDevice.deviceID[id], size);
    }
 
    public void Seek(int id,int to) {
        currentDevice.deviceType[id].Seek(currentDevice.deviceID[id], to);
    }
 
    public int Write(int id, byte[] data) {
        return currentDevice.deviceType[id].Write(currentDevice.deviceID[id], data);
    }
 
}