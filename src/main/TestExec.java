package main;
import java.util.ArrayList;
import java.util.List;

import output.*;

// class StreamGobbler omitted for brevity
public class TestExec
{
    public static void main(String args[])
    {
        if (args.length < 1)
        {
            System.out.println("USAGE: java TestExec \"cmd\"");
            System.exit(1);
        }
        
        try
        {
            String cmd = args[0];
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            
            // any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(proc.getErrorStream(), "ICC");            
            
            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(proc.getInputStream(), "ICC");
                
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
                                    
            // any error???
            int exitVal = proc.waitFor();
            System.out.println("ExitValue: " + exitVal);
            
        	//
        	List<String> tmp_lines = new ArrayList<String>();
        	tmp_lines = outputGobbler.getOs_lines();
            System.out.println("Num_lines: " + tmp_lines.size());
            
            
            
        } catch (Throwable t)
          {
            t.printStackTrace();
          }
    }
}