package output;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StreamGobbler extends Thread
{
	InputStream is;
    String type;
    OutputStream os;
    List<String> os_lines;
    
    public List<String> getOs_lines() {
		return os_lines;
	}
	public void setOs_lines(List<String> os_lines) {
		this.os_lines = os_lines;
	}
	public StreamGobbler(InputStream is, String type)
    {
        this(is, type, null);
    }
    public StreamGobbler(InputStream is, String type, OutputStream redirect)
    {
        this.is = is;
        this.type = type;
        this.os = redirect;
    }
    
    public void run()
    {
    	//
    	List<String> tmp_lines = new ArrayList<String>();  
    	
        try
        {
            PrintWriter pw = null;
            if (os != null)
                pw = new PrintWriter(os);
                
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
            {
            	if (line.length() > 0)
            	{
	            	//add to list...
	        		tmp_lines.add(line);
	            	
	                if (pw != null)
	                    pw.println(line);
	                
	                if (type == "ERR")
	                	System.out.println(type + ">" + line);
            	}
            }
            //set list...
    		this.setOs_lines(tmp_lines);
            
            if (pw != null)
                pw.flush();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();  
        }
    }
}