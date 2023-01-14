public class Startup {
    public static void main (String[] args){
       OS.getOS().CreateProcess(new Background(), PriorityEnum.Background);
       OS.getOS().CreateProcess(new Interactive(), PriorityEnum.Interactive);
       OS.getOS().CreateProcess(new Realtime(), PriorityEnum.Realtime);    
       OS.getOS().run(); 
    }
}
