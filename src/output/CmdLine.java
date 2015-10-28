package output;

import java.util.ArrayList;
import java.util.List;

public class CmdLine {

	String cmd;
	int delay;
	
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}

	public List<String> getOutList = new ArrayList<String>();

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public List<String> getGetOutList() {
		return getOutList;
	}

	public void setGetOutList(List<String> getOutList) {
		this.getOutList = getOutList;
	}

	public CmdLine(String cmd) throws Throwable {
		super();
		this.cmd = cmd;
		
		callCMD();
	}
	public CmdLine(String cmd, int delay) throws Throwable {
		super();
		this.cmd = cmd;
		this.delay = delay;
		
		callCMD();
	}

	private void callCMD() throws Throwable
    {
    	StreamGobbler errorGobbler;
    	StreamGobbler outputGobbler;
    	
    	//Execute args command...
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(this.cmd);

        //Check Error...
    	errorGobbler = run(proc, "ERR", this.delay);

        //Check Output...
        outputGobbler = run(proc, "ICC", this.delay);
        
        //Any error???
        proc.waitFor();
        
    	//Set Error List...
        List<String> tmp_linese = new ArrayList<String>();
    	tmp_linese = errorGobbler.getOs_lines();

    	//Set Output List...
    	List<String> tmp_linesi = new ArrayList<String>();
    	tmp_linesi = outputGobbler.getOs_lines();
    	
    	if (tmp_linesi.size() > 0)
    		this.setGetOutList(tmp_linesi);
    	else
    		this.setGetOutList(tmp_linese);
    }
    
    private static StreamGobbler run(Process proc, String typ, int delay)
    {
    	StreamGobbler tmpGobbler;
    	
        //Any error message?
    	if (typ.equals("ERR"))
    	{
    		if (delay > 0)
    			tmpGobbler = new StreamGobbler(proc.getErrorStream(), typ, delay);
    		else
    			tmpGobbler = new StreamGobbler(proc.getErrorStream(), typ);
    	}
    	else
    	{
    		if (delay > 0)
    			tmpGobbler = new StreamGobbler(proc.getInputStream(), typ, delay);
    		else
    			tmpGobbler = new StreamGobbler(proc.getInputStream(), typ);
    	}
            
        //Start process...
        tmpGobbler.start();

        return tmpGobbler;
    }
}
