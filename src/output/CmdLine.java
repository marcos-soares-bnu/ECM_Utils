package output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CmdLine {

	String cmd;
	List<String> getOutList = new ArrayList<String>();

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public List<String> getGetOutList() throws Throwable {

		if (this.getOutList.size() > 0)
			return this.getOutList;
		
    	//Execute args command...
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(this.cmd);

        //Check Error...
        StreamGobbler errorGobbler = run(proc, "ERR");

        //Check Output...
        StreamGobbler outputGobbler = run(proc, "ICC");
        
        //Any error???
        proc.waitFor();
        
    	//Set Error List...
        List<String> tmp_linese = new ArrayList<String>();
    	tmp_linese = errorGobbler.getOs_lines();

    	//Set Output List...
    	List<String> tmp_linesi = new ArrayList<String>();
    	tmp_linesi = outputGobbler.getOs_lines();
    	
    	if (tmp_linesi.size() > 0)
    		getOutList = tmp_linesi;
    	else
    		getOutList = tmp_linese;
		
		return getOutList;
	}

	public void setGetOutList(List<String> getOutList) {
		this.getOutList = getOutList;
	}

	public CmdLine(String cmd) throws Throwable {
		super();
		this.cmd = cmd;
		this.getOutList = this.getGetOutList();
	}

    public void callCMD() {
        try {
            Process p = Runtime.getRuntime().exec(this.cmd);
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static StreamGobbler run(Process proc, String typ){
 
    	StreamGobbler tmpGobbler;
    	
        //Any error message?
    	if (typ.equals("ERR"))
    	{
            tmpGobbler = new StreamGobbler(proc.getErrorStream(), typ);
    	}
    	else
    	{
            tmpGobbler = new StreamGobbler(proc.getInputStream(), typ);
    	}
    	
        //Start process...
        tmpGobbler.start();

        return tmpGobbler;
    }
}
