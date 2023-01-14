import java.util.*;
 
public class RandomDevice implements Device {
    
    Random[] randomDeviceArray = new Random[10];
    int id;
 
    public static RandomDevice randomDevice = new RandomDevice();
 
    public static RandomDevice getRandomDevice() {
        return randomDevice;
    }
    
    //Open() will create a new Random device and put it in an empty spot in the array.  
    //If the supplied string for Open is not null or empty, assume that it's the seed for the Random class.
    @Override
    public int Open(String s) {
        int seed = 0;
        if(s != null) {
            seed = Integer.parseInt(s);
        }
        else if(s == null) {
            seed = 0;
        }
        for(int id = 0; id < randomDeviceArray.length; id++) {
            if(randomDeviceArray[id] != null) {
                randomDeviceArray[id] = new Random(seed);
            }
            else  
                continue;
        }
        return id;
    }
 
    //Close will null the device entry.
    @Override
    public void Close (int id) {
        randomDeviceArray[id] = null;
    }
 
    //Read will create/fill an array with random values
    @Override
    public byte[] Read (int id,int size) {
        byte[] rdByteArray = new byte[size];
        randomDeviceArray[id].nextBytes(rdByteArray);
        return rdByteArray;
    }
 
    //Seek will read random bytes but not return them.
    @Override
    public void Seek (int id,int to) {
        byte[] byteArray = new byte[to];
        randomDeviceArray[id].nextBytes(byteArray);  
    }
 
    //Write will return 0 length and do nothing
    @Override
    public int Write (int id, byte[] data) {
        return 0;
    }
}