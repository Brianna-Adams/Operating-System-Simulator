public class Realtime extends UserlandProcess {
    RunResult runResult = new RunResult();
    int ms = 8;
    
    @Override 
    public RunResult run(){
        runResult.ranToTimeout = true;
        runResult.millisecondsUsed = 4;
        return runResult;
    }
    
    @Override public void sleep(int milliseconds) {
        this.ms = milliseconds;
    }
  
}