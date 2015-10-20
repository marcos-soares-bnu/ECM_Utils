package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
//import java.util.Map;

import javax.swing.JOptionPane;

public class Console {

	public static void mainX(String args[])
    {
		String srvName = "\\\\MLGMUC00APP571";
		String srvPath = "D:\\IAS_Monitoring\\APP_Dev\\SCHEDScripts\\";
		String sqlServ = "\"SELECT * FROM [FISPixCoreINTERN].[dbo].[BatchList]\""; 
		String batPath = "sqlcmd -U fisadmin -P FIS!admin -S mlgmuc00csql011\\sql2 -Q " + sqlServ;
		String command = "cmd /c " + srvPath + "psexec " + srvName + " " + batPath;
		
        try
        {            
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command);
            InputStream stdin = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            System.out.println("<OUTPUT>");
            while ( (line = br.readLine()) != null)
                System.out.println(line);
            System.out.println("</OUTPUT>");
            int exitVal = proc.waitFor();            
            System.out.println("Process exitValue: " + exitVal);
            
            if (exitVal != 0){
            	
                InputStream stder = proc.getErrorStream();
                InputStreamReader iser = new InputStreamReader(stder);
                BufferedReader bre = new BufferedReader(iser);
	  	        while ((line = bre.readLine()) != null) {
	  	          System.out.println(line);
	  	        }
            }
            
        } catch (Throwable t)
          {
            t.printStackTrace();
          }
    }	
	
	
	public static void main3(String args[]) throws InterruptedException,IOException
	{
		
		String srvName = "\\\\MLGMUC00APP571";
		String srvPath = "D:\\IAS_Monitoring\\APP_Dev\\SCHEDScripts\\";
		String sqlServ = "\"SELECT * FROM [FISPixCoreINTERN].[dbo].[BatchList]\""; 
		String batPath = "sqlcmd -U fisadmin -P FIS!admin -S mlgmuc00csql011\\sql2 -Q " + sqlServ;
		//String command = "cmd /c " + //   
		//			srvPath + "psexec " + srvName + " " + batPath;
		
		
	    List<String> command = new ArrayList<String>();
	    command.add(srvPath+"psexec.exe");
	    command.add(srvName);
	    command.add(batPath);

	    System.out.println(command);
	    
	    ProcessBuilder builder = new ProcessBuilder(command);
//	    Map<String, String> environ = builder.environment();
	    builder.directory(new File(System.getenv("temp")));

	    System.out.println("Directory : " + System.getenv("temp") );
	    final Process process = builder.start();
	    InputStream is = process.getInputStream();
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String line;
	    while ((line = br.readLine()) != null) {
	      System.out.println(line);
	    }
	    System.out.println("Program terminated!");
	}	
	
	public static void mainI(String[] args) {

		/* 	----------------------------------------------------------------------------------
			psexec \\mlgmuc00app571 "c:\temp\PIXCORE_BatchList.bat"		
		 * 
			D:\IAS_Monitoring>psexec \\mlgmuc00app571 sqlcmd -U fisadmin -P FIS!admin -S mlg
			muc00csql011\sql2 -Q "SELECT * FROM [FISPixCoreINTERN].[dbo].[BatchList]"
			----------------------------------------------------------------------------------			 
		 */
	
		String srvName = "\\\\MLGMUC00APP571";
		String srvPath = "D:\\IAS_Monitoring\\APP_Dev\\SCHEDScripts\\";
		String sqlServ = "\"SELECT * FROM [FISPixCoreINTERN].[dbo].[BatchList]\""; 
		String batPath = "sqlcmd -U fisadmin -P FIS!admin -S mlgmuc00csql011\\sql2 -Q " + sqlServ;
	
		//----------------------------------------------------------------------------------			 
		String command = "cmd /c " + //   
					srvPath + "psexec " + srvName + " " + batPath;
		
	    try {
	        String line;
	        Process p = Runtime.getRuntime().exec(command); //("cmd /c dir");
	        BufferedReader bri = new BufferedReader
	          (new InputStreamReader(p.getInputStream()));
	        BufferedReader bre = new BufferedReader
	          (new InputStreamReader(p.getErrorStream()));
	        while ((line = bri.readLine()) != null) {
	          System.out.println(line);
	        }
	        bri.close();
	        while ((line = bre.readLine()) != null) {
	          System.out.println(line);
	        }
	        bre.close();
	        p.waitFor();
	        System.out.println("Done.");
	      }
	      catch (Exception err) {
	        err.printStackTrace();
            JOptionPane.showMessageDialog(null, err);
	      }		

	}
		
	public static void mainO(String[] args) {
		
		
		/* ----------------------------------------------------------------------------------
			psexec \\mlgmuc00app571 "c:\temp\PIXCORE_BatchList.bat"		
		 * 
			D:\IAS_Monitoring>psexec \\mlgmuc00app571 sqlcmd -U fisadmin -P FIS!admin -S mlg
			muc00csql011\sql2 -Q "SELECT * FROM [FISPixCoreINTERN].[dbo].[BatchList]"
		   ----------------------------------------------------------------------------------			 
		 */
		
//		String srvName = "\\\\MLGMUC00APP571";
//		String srvPath = "D:\\IAS_Monitoring\\APP_Dev\\SCHEDScripts\\";
//		String sqlServ = "\"SELECT * FROM [FISPixCoreINTERN].[dbo].[BatchList]\""; 
//		String batPath = "sqlcmd -U fisadmin -P FIS!admin -S mlgmuc00csql011\\sql2 -Q " + sqlServ;
		
		//----------------------------------------------------------------------------------			 
//    	String command = "cmd /c start /wait " + //   
//    					srvPath + "psexec " + srvName + " " + batPath;

    	//Call the .bat and show output
    	try {
            Process p = Runtime.getRuntime().exec( "cmd /c start /wait echo HelloWorld2 > test.tmp" ); //(command);
            //Catch the exit code from .bat
            int exitCode = p.waitFor();
        
            if (exitCode == 0) {

            	//
            	String line;

            	BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            	while((line = error.readLine()) != null){
            	    System.out.println(line);
            	}
            	error.close();

            	BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            	while((line=input.readLine()) != null){
            	    System.out.println(line);
            	}

            	input.close();

            	OutputStream outputStream = p.getOutputStream();
            	PrintStream printStream = new PrintStream(outputStream);
            	printStream.println();
            	printStream.flush();
            	printStream.close();            	
            	//
            	System.out.println("Command executed!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
	}

}
