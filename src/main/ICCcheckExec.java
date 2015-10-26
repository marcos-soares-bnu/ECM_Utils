package main;

import java.io.FileWriter;
import java.util.Date;

import wmi.*;
import checks.*;

public class ICCcheckExec {

	
//*** VARs ************************************************************************************************************
	public static String PAR1		= "";
	public static String PAR2		= "";
	public static String PAR3		= "";
	public static String PAR4		= "";
	public static String PAR5		= "";
	
	
//*** MAIN ************************************************************************************************************
	
    public static void main(String args[]) throws Throwable
    {
    	if (args.length < 4)
        {
            System.out.println("*** USAGE: java -jar ICCcheck \"Host(Server)\" \"Service(Chk)\" \"Process(Chk)\" \"Drive[:\\path](search Logs)\" \"[-x][-v][-n][-o][-h][ ]\"");
            System.exit(1);
        }
    	else
    	{
    		PAR1 = args[0];
    		PAR2 = args[1];
    		PAR3 = args[2];
    		PAR4 = args[3];

    		if (args.length > 4)
    			PAR5 = args[4];
    		else
    		{
    			if ( (PAR4.indexOf(":") > 0) && (!PAR4.endsWith("\\")) )
    				PAR4 = PAR4 + "\\";
    		}
    	}
    		
        try
        {
        	//Create Instances for Wmic Interface and each Check...
        	WmiConsole wmi = new WmiConsole();
        	wmi.setHost(PAR1);
        	wmi.setService(PAR2);
        	wmi.setProcess(PAR3);
        	wmi.setPath(PAR4);
        	wmi.setExtension("log");
        	
        	Date os_dtime = wmi.getWmicLocalDateTime("");
        	//wmi.setDrive("c");
        	//wmi.setPath("\\\\Temp\\\\mps\\\\Trace\\\\DOKuStar_teste\\\\");
        	
    		//Debug SysOut...
        	String[] vars	= {"OS_DTIME", "HOST", "SERVICE", "PROCESS", "DRIVE", "PATH", "USAGE"};
        	String[] vals 	= {os_dtime.toString(), PAR1, PAR2, PAR3, wmi.getDrive(), PAR4, PAR5};
        	debugSysOut(vars, vals);
        	
        	//Create ICCcheck instance, check Logs record SERVICE, set LISTOFFILES (DOKuStar)...
        	//Copy / Check Logs Files found and generate Error List File for All Logs...
        	ICCcheck iccInit = new ICCcheck(wmi);
        	iccInit.copyListLocal();
        	iccInit.outputSearchInListOfFiles("", "ICCcheck.tmp.err");
        	
        	//Create Out File depending of check (PAR5)...
        	if 		(PAR5.equals("-x"))
        	{
        		//ICCcheckExtraction iccExtraction = new ICCcheckExtraction(wmi);
        		//iccExtraction.checkExtraction("ICCcheck.tmp.ext");
        	}
        	else if (PAR5.equals("-v"))	
        	{
        		ICCcheckOverview iccOverview = new ICCcheckOverview(wmi);
        		iccOverview.checkOverview("ICCcheck.tmp.ovw");
        	}
        	else if (PAR5.equals("-n"))
        	{
        		ICCcheckClusterNodes iccClusternodes = new ICCcheckClusterNodes(wmi, 10); //MAX_DTDIFF
        		iccClusternodes.checkClusterNodes("ICCcheck.tmp.cln"); 
        	}
        	else if (PAR5.equals("-o"))
        	{
        		ICCcheckOperations iccOperations = new ICCcheckOperations(wmi, 10); //MAX_DTDIFF
        		iccOperations.checkOperations("ICCcheck.tmp.ops");
        	}
        	else if (PAR5.equals("-h"))	
        	{
        		ICCcheckHistory iccHistory = new ICCcheckHistory(wmi, 5); //MAX_PERERR
        		iccHistory.checkHistory("ICCcheck.tmp.hst");
        	}
        	else						
        	{
        		//ICCcheckExtraction iccExtraction = new ICCcheckExtraction(wmi);
        		//iccExtraction.checkExtraction("ICCcheck.tmp.ext");
        		//
        		ICCcheckOverview iccOverview = new ICCcheckOverview(wmi);
        		iccOverview.checkOverview("ICCcheck.tmp.ovw");
        		//
        		ICCcheckClusterNodes iccClusternodes = new ICCcheckClusterNodes(wmi, 10); //MAX_DTDIFF
        		iccClusternodes.checkClusterNodes("ICCcheck.tmp.cln");
        		//
        		ICCcheckOperations iccOperations = new ICCcheckOperations(wmi, 10); //MAX_DTDIFF
        		iccOperations.checkOperations("ICCcheck.tmp.ops");
        		//
        		ICCcheckHistory iccHistory = new ICCcheckHistory(wmi, 5); //MAX_PERERR
        		iccHistory.checkHistory("ICCcheck.tmp.hst");
        	}
        } 
        catch (Throwable t)
        {
    		writeExitErrOut("Throwable", t.getStackTrace().toString());
        	t.printStackTrace();
        }
    }
    
    
//*** AUX  PROCS ******************************************************************************************************

    public static void writeExitErrOut(String tag, String msg) throws Throwable
    {
    	FileWriter writer = new FileWriter("ICCcheck.tmp", false);

		String s = 	"\\" + tag + "\\ " + msg;
		writer.write(s + "\n");
		writer.close();

        System.exit(1);
    }

    public static void debugSysOut(String var, String val)
    {
    	System.out.println("-------------------------------------------------------------------------------");
       	System.out.println(">>> Debug " + padRight(var, 10)  + " = " + val);
    	System.out.println("-------------------------------------------------------------------------------");
    }

    public static void debugSysOut(String[] vars, String[] vals)
    {
    	System.out.println("===============================================================================");

    	for (int i = 0; i < vals.length; i++)
    	{
           	System.out.println(">>> Debug " + padRight(vars[i], 10)  + " = " + vals[i]);
		}

    	System.out.println("===============================================================================");
    }

    public static String padRight(String s, int n)
    {
    	return String.format("%1$-" + n + "s", s);
    }    

    
//*********************************************************************************************************************
    
}