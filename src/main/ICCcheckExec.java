package main;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
				if (PAR4.length() > 1 )
				{
					wmi.setDrive(PAR4.substring(0, 1));
					wmi.setPath("\\\\" + PAR4.substring(1) + "\\\\%");
				}
				else
				{
					wmi.setDrive(PAR4);
					wmi.setPath("\\\\");
				}
			}
        	
			//Set dateOS to find name of files based on dateOS...
			//Set wmi.setName LOG files... '%@DATEOS%.log'
			Date dateOS = wmi.getWmicLocalDateTime("yyyyMMddHHmmss");
			String sdateOS = new SimpleDateFormat("yyyy-MM-dd").format(dateOS);
			String sdateOSFile = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(dateOS);
			wmi.setName("%" + sdateOS + "%.log");
			
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
        	String[] vars	= {"DATEOS", "HOST", "SERVICE", "PROCESS", "LOCALPATH", "USAGE"};
        	String[] vals 	= {sdateOS, PAR1, PAR2, PAR3, wmi.getDrive() + wmi.getPath(), PAR5};
        	wmi.debugSysOut(vars, vals);
        	
    		//Set local list from PIDs of process parameter...
    		wmi.getWmicProcessId();
    		List<String> listPIDs = wmi.getGetWmiOutList();
    		//trim list itens...
    		for (int i = 0; i < listPIDs.size(); i++) { listPIDs.set(i, listPIDs.get(i).trim()); }

        	//Create ICCcheck instance and set LISTOFFILES (DOKuStar)...
    		wmi.setDelay(5000);
        	ICCcheck iccInit = new ICCcheck(wmi, listPIDs);
        	iccInit.setLISTOFFILES();

    		//Set File pattern...
        	iccInit.setICCFILE_PREFIX("ICCcheck.tmp." + wmi.getHost() + "." + sdateOSFile);
        	
        	//Copy / Check Logs Files found and generate Error List File for All Logs...
        	iccInit.copyListLocal(iccInit.getICCFILE_PREFIX() + ".cop.cmd");
        	iccInit.outputSearchInListOfFiles("Error: " + wmi.getHost(), (iccInit.getICCFILE_PREFIX() + ".sea.cmd"), (iccInit.getICCFILE_PREFIX() + ".err") );
        	
        	//Create Out File depending of check (PAR5)...
    		wmi.setDelay(0);
        	if 		(PAR5.equals("-x"))
        	{
        		ICCcheckExtractionServers iccExtraction = new ICCcheckExtractionServers(wmi);
        		iccExtraction.setLISTOFFILES(iccInit.LISTOFFILES);
        		iccExtraction.checkExtraction(iccInit.getICCFILE_PREFIX() + ".ext");
        	}
        	else if (PAR5.equals("-v"))	
        	{
        		ICCcheckOverview iccOverview = new ICCcheckOverview(wmi);
        		iccOverview.setLISTOFFILES(iccInit.LISTOFFILES);
        		iccOverview.checkOverview(iccInit.getICCFILE_PREFIX() + ".ovw");
        	}
        	else if (PAR5.equals("-n"))
        	{
        		ICCcheckClusterNodes iccClusternodes = new ICCcheckClusterNodes(wmi, 10); //MAX_DTDIFF
        		iccClusternodes.setLISTOFFILES(iccInit.LISTOFFILES);
        		iccClusternodes.checkClusterNodes(iccInit.getICCFILE_PREFIX() + ".cln"); 
        	}
        	else if (PAR5.equals("-o"))
        	{
        		ICCcheckOperations iccOperations = new ICCcheckOperations(wmi, 10); //MAX_DTDIFF
        		iccOperations.setLISTOFFILES(iccInit.LISTOFFILES);
        		iccOperations.checkOperations(iccInit.getICCFILE_PREFIX() + ".ops");
        	}
        	else if (PAR5.equals("-h"))	
        	{
        		ICCcheckHistory iccHistory = new ICCcheckHistory(wmi, 5); //MAX_PERERR
        		iccHistory.setLISTOFFILES(iccInit.LISTOFFILES);
        		iccHistory.checkHistory(iccInit.getICCFILE_PREFIX() + ".hst");
        	}
        	else						
        	{
        		ICCcheckExtractionServers iccExtraction = new ICCcheckExtractionServers(wmi);
        		iccExtraction.setLISTOFFILES(iccInit.LISTOFFILES);
        		iccExtraction.checkExtraction(iccInit.getICCFILE_PREFIX() + ".ext");
        		//
        		ICCcheckOverview iccOverview = new ICCcheckOverview(wmi);
        		iccOverview.checkOverview(iccInit.getICCFILE_PREFIX() + ".ovw");
        		//
        		ICCcheckClusterNodes iccClusternodes = new ICCcheckClusterNodes(wmi, 10); //MAX_DTDIFF
        		iccClusternodes.checkClusterNodes(iccInit.getICCFILE_PREFIX() + ".cln");
        		//
        		ICCcheckOperations iccOperations = new ICCcheckOperations(wmi, 10); //MAX_DTDIFF
        		iccOperations.checkOperations(iccInit.getICCFILE_PREFIX() + ".ops");
        		//
        		ICCcheckHistory iccHistory = new ICCcheckHistory(wmi, 5); //MAX_PERERR
        		iccHistory.checkHistory(iccInit.getICCFILE_PREFIX() + ".hst");
        	}
        } 
        catch (Throwable t)
        {
    		writeExitErrOut("Throwable", t.getMessage() + t.getLocalizedMessage() + t.getCause() + " | Trace: " + t.getStackTrace().toString());
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