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
	public static String PAR6		= "test";
	public static String sdateOS;
	public static String sdateOSFile;
	
//*** MAIN ************************************************************************************************************
	
    public static void main(String args[]) throws Throwable
    {
    	if (args.length < 5)
        {
            System.out.println("*** USAGE: java -jar ICCcheck \"Host(Server)\" \"Service(Chk)\" \"Process(Chk)\" \"Drive[:\\path](search Logs)\" \"[-x][-v][-n][-o][-h][-all]\" \"EXECID(ID for temp files, if empty ID=test)\"");
            System.exit(1);
        }
    	else
    	{
    		PAR1 = args[0];
    		PAR2 = args[1];
    		PAR3 = args[2];
    		PAR4 = args[3];
			PAR5 = args[4];
    		if (args.length > 5)
    			PAR6 = args[5];
    	}
    		
        try
        {
        	//Initialization WmiConsole...
        	WmiConsole wmi = new WmiConsole();
        	wmi = setInitVars();
        	wmi.setDelay(5000); //Set a delay time to run slow process...
        	
        	//Check Logs record SERVICE...
        	chkService(wmi);

        	//Check and set List of PIDS...
        	List<String> listPIDs = chkProcess(wmi);
        	
        	//Set Initialization Checks...
        	ICCcheck iccInit = new ICCcheck(wmi, listPIDs);
        	iccInit = setInitCheck(wmi, listPIDs);

        	//Create Out File depending of check (PAR5)...
        	switch (PAR5)
        	{
				case "-x":
					check_X(wmi, iccInit);
					break;
				case "-v":
					check_V(wmi, iccInit);
					break;
				case "-n":
					check_N(wmi, iccInit);
					break;
				case "-o":
					check_O(wmi, iccInit);
					break;
				case "-h":
					check_H(wmi, iccInit);
					break;
				case "-all":
					check_X(wmi, iccInit);
					check_V(wmi, iccInit);
					check_N(wmi, iccInit);
					check_O(wmi, iccInit);
					check_H(wmi, iccInit);
					break;
				default:
					break;
			}
        	
        	//Exit Completed...
        	wmi.debugSysOut("Execution Status: ", "Completed!");
        	
        } 
        catch (Throwable t)
        {
    		writeExitErrOut("Throwable (Error Remote Host) ", PAR1 + " | Message: " + t.getMessage() + " | Trace: " + t);
        	t.printStackTrace();
        }
    }
    

//*** MAIN  PROCS *****************************************************************************************************

    public static void check_X(WmiConsole wmi, ICCcheck iccInit) throws Throwable
    {
		ICCcheckExtractionServers iccExtraction = new ICCcheckExtractionServers(wmi);
		iccExtraction.setLISTOFFILES(iccInit.LISTOFFILES);
		iccExtraction.setICCFILE_PREFIX(iccInit.ICCFILE_PREFIX);
		//
		String[] filesFilter 	= new String[2];
		filesFilter[0] 			= "Extraction.Server";
		filesFilter[1] 			= "HotSpot";
		iccExtraction.checkExtraction(iccInit.getICCFILE_PREFIX() + ".ext", filesFilter);
    }
    public static void check_V(WmiConsole wmi, ICCcheck iccInit) throws Throwable
    {
		ICCcheckOverview iccOverview = new ICCcheckOverview(wmi);
		iccOverview.setLISTOFFILES(iccInit.LISTOFFILES);
		iccOverview.checkOverview(iccInit.getICCFILE_PREFIX() + ".ovw");
    }
    public static void check_N(WmiConsole wmi, ICCcheck iccInit) throws Throwable
    {
		ICCcheckClusterNodes iccClusternodes = new ICCcheckClusterNodes(wmi, 10); //MAX_DTDIFF
		iccClusternodes.setLISTOFFILES(iccInit.LISTOFFILES);
		iccClusternodes.checkClusterNodes(iccInit.getICCFILE_PREFIX() + ".cln");
    }
    public static void check_O(WmiConsole wmi, ICCcheck iccInit) throws Throwable
    {
		ICCcheckOperations iccOperations = new ICCcheckOperations(wmi, 10); //MAX_DTDIFF
		iccOperations.setLISTOFFILES(iccInit.LISTOFFILES);
		iccOperations.checkOperations(iccInit.getICCFILE_PREFIX() + ".ops");
    }
    public static void check_H(WmiConsole wmi, ICCcheck iccInit) throws Throwable
    {
		ICCcheckHistory iccHistory = new ICCcheckHistory(wmi, 5); //MAX_PERERR
		iccHistory.setLISTOFFILES(iccInit.LISTOFFILES);
		iccHistory.checkHistory(iccInit.getICCFILE_PREFIX() + ".hst");
    }
    
//*** AUX  PROCS ******************************************************************************************************

    public static WmiConsole setInitVars() throws Throwable
    {
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
		Date dateOS	= wmi.getWmicLocalDateTime("yyyyMMddHHmmss");
		sdateOS 	= new SimpleDateFormat("yyyy-MM-dd").format(dateOS);
		sdateOSFile = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(dateOS);
		wmi.setName("%" + sdateOS + "%.log");
    
    	return wmi;
    }
    
    public static void chkService(WmiConsole wmi) throws Throwable
    {
    	wmi.getWmicServiceStatus();
    	List<String> listService = wmi.getGetWmiOutList();
    	if (listService.size() > 0)
    	{
    		for (String s : listService) {
    			if (s.toLowerCase().indexOf("stopped") >= 0)
    			{
    				writeExitErrOut("LogService_stopped", s);
    	            System.exit(1);
    			}
			}
    	}
		//Debug SysOut...
    	String[] vars	= {"DATEOS", "HOST", "SERVICE", "PROCESS", "LOCALPATH", "CHECK", "EXECID"};
    	String[] vals 	= {sdateOS, PAR1, PAR2, PAR3, wmi.getDrive() + wmi.getPath(), PAR5, PAR6};
    	wmi.debugSysOut(vars, vals);
    }

    public static List<String> chkProcess(WmiConsole wmi) throws Throwable
    {
		//Set local list from PIDs of process parameter...
		wmi.getWmicProcessId();
		
		List<String> listPIDs = wmi.getGetWmiOutList();
		
		//trim list itens...
		for (int i = 0; i < listPIDs.size(); i++)
		{ 
			listPIDs.set(i, listPIDs.get(i).trim()); 
		}
    	
    	return listPIDs;
    }
    
    public static ICCcheck setInitCheck(WmiConsole wmi, List<String> listPIDs) throws Throwable
    {
    	//Create ICCcheck instance and set LISTOFFILES (DOKuStar)...
    	ICCcheck iccInit = new ICCcheck(wmi, listPIDs);
    	iccInit.setLISTOFFILES();

		//Set File pattern...
    	iccInit.setICCFILE_PREFIX("ICCcheck.tmp." + wmi.getHost() + "." + sdateOSFile + "." + PAR6); //PAR6 = EXECID...
		wmi.setDelay(0); //Set a delay time to exec process...
    	
    	//Copy / Check Logs Files found and generate Error List File for All Logs...
    	iccInit.copyListLocal(iccInit.getICCFILE_PREFIX() + ".cop.cmd");
    	iccInit.outputSearchInListOfFiles("Error: " + wmi.getHost(), "", (iccInit.getICCFILE_PREFIX() + ".err") );
    	
    	return iccInit;
    }
    
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
