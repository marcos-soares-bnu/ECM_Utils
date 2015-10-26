package main;

import java.io.FileWriter;

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
    	}
    		
        try
        {
        	//Create Instances for Wmic Interface and each Check...
        	WmiConsole wmi = new WmiConsole();
        	wmi.setHost(PAR1);
        	wmi.setService(PAR2);
        	wmi.setProcess(PAR3);
        	wmi.setExtension("log");

			if (PAR4.indexOf(":") > 0)
			{
				if (!PAR4.endsWith("\\"))
				{
					PAR4 = PAR4 + "\\";
				}
				String[] aux_path = PAR4.split(":");
				if (aux_path.length < 2)
				{
    				writeExitErrOut("Invalid_Path", PAR4);
    	            System.exit(1);
				}
				else
				{
					wmi.setDrive(aux_path[0]);
		        	wmi.setPath(aux_path[1].replace("\\", "\\\\"));
				}
			}
			else
			{
				wmi.setDrive(PAR4);
	        	wmi.setPath("\\\\");
			}
        	
        	//Check Logs record SERVICE... 
        	wmi.getWmicServiceStatus();
        	if (wmi.getGetWmiOutList().size() > 0)
        	{
        		for (String s : wmi.getGetWmiOutList()) {
        			if (s.toLowerCase().indexOf("stopped") >= 0)
        			{
        				writeExitErrOut("LogService_stopped", s);
        	            System.exit(1);
        			}
				}
        	}
    		//Debug SysOut...
        	String[] vars	= {"HOST", "SERVICE", "PROCESS", "LOCALPATH", "USAGE"};
        	String[] vals 	= {PAR1, PAR2, PAR3, wmi.getDrive() + wmi.getPath(), PAR5};
        	wmi.debugSysOut(vars, vals);
        	
        	//Create ICCcheck instance and set LISTOFFILES (DOKuStar)...
        	//Copy / Check Logs Files found and generate Error List File for All Logs...
        	ICCcheck iccInit = new ICCcheck(wmi);
        	//*** MPS debug
        	iccInit.debugSysOut("OS_DTIME", iccInit.getOS_DATETIME().toString());
        	//***
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
    
//*********************************************************************************************************************
    
}