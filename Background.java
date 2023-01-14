public class Background extends UserlandProcess {
    RunResult runResult = new RunResult();
    int ms = 14;
     
    @Override 
    public RunResult run(){
        System.out.println("I run in the Background.");
        runResult.ranToTimeout = false;
        runResult.millisecondsUsed = 7;
        //OS.getOS().sleep(14);
        return runResult;
    }
    
    @Override public void sleep(int milliseconds) {
        this.ms = milliseconds;
    }
}