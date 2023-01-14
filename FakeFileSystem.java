import java.io.RandomAccessFile;
 
public class FakeFileSystem implements Device {
 
    RandomAccessFile[] fileSystem = new RandomAccessFile[10];
    int id;
    public static FakeFileSystem fakeFileSystem = new FakeFileSystem();
 
    public static FakeFileSystem getFakeFileSystem() {
        return fakeFileSystem;
    }
 
    @Override
    public int Open(String s) {
        try {
            if(s == null || s.equals(" "))
                throw new Exception("This filename is null or empty");
            for(int id = 0; id < fileSystem.length; id++) {
                if(fileSystem[id] != null) {
                    fileSystem[id] = new RandomAccessFile(s,"rw");
                }
                else
                    continue;  
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return id;
    }
 
    //Close RandomAccessFile and clear out internal array
    @Override
    public void Close(int id) {
        try {
            //Close the RandomAccessFile and clear out internal array
            fileSystem[id].close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
 
    }
 
    @Override
    public byte[] Read(int id,int size) {
        byte[] fsByteArray = new byte[size];
        try {
            fileSystem[id].read(fsByteArray);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return fsByteArray;
    }
 
    @Override
    public void Seek(int id,int to) {
        try {
            //Set the file pointer to position indicated 
            //by variable "to"
            fileSystem[id].seek(to);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
 
    @Override
    public int Write(int id, byte[] data) {
        try {
            //Write to the file
            fileSystem[id].write(data);
            //Set file pointer at position 0
            fileSystem[id].seek(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return data.length;
    }
}