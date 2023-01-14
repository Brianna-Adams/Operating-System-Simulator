public class Interactive extends UserlandProcess {
    RunResult runResult = new RunResult();
    int ms = 10;
    
    @Override 
    public RunResult run(){
        System.out.println("I run interactively.");
        runResult.ranToTimeout = true;
        runResult.millisecondsUsed = 5;
        //OS.getOS().sleep(10);
        return runResult;
    }
    
    @Override public void sleep(int milliseconds) {
        this.ms = milliseconds;
    }
}
public class Interactive extends UserlandProcess {
    RunResult runResult = new RunResult();
    int ms = 10;
    
    @Override 
    public RunResult run(){
        System.out.println("I run interactively.");
        runResult.ranToTimeout = true;
        runResult.millisecondsUsed = 5;
        //OS.getOS().sleep(10);
        return runResult;
    }
    
    @Override public void sleep(int milliseconds) {
        this.ms = milliseconds;
    }
}