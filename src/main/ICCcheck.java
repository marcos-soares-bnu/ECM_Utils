package main;

import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//import java.util.concurrent.TimeUnit;



import output.StreamGobbler;

public class ICCcheck {

	public static String PAR1		= null;
	public static String PAR2		= null;
	public static String PAR3		= null;
	public static String PAR4		= null;
	public static String PAR5		= null;
	//
	public static String CMD_C0N 	= "CMD /C WMIC /NODE:@HOST OS GET localdatetime";
	public static String CMD_C1N 	= "CMD /C WMIC /NODE:@HOST SERVICE WHERE \"name like '%@SERVICE%'\" GET name,state";
	public static String CMD_C2N 	= "CMD /C WMIC /NODE:@HOST PATH Win32_Process WHERE \"name like '%@PROCESS%'\" GET processid";
	public static String CMD_C3N 	= "CMD /C WMIC /NODE:@HOST DATAFILE WHERE \"extension='log' and drive='@DRIVE:' and name like '%@DATEOS%.@PID.log'\" GET name";
	public static String CMD_C3S 	= "drive='@DRIVE:' and name";
	public static String CMD_C3R 	= "drive='@DRIVE:' and path='@PATH' and name";
	//
	public static Date OS_DTIME;
	public static Date LOG_DTIME;
	public static int MAX_DTDIFF	= 10; //Max diff. between OS_DTIME, LOG_DTIME in minutes 
	public static String HOST 		= "";
	public static String SERVICE 	= "";
	public static String PROCESS 	= "";
	public static String DRIVE 		= "";
	public static String PATH 		= "";
	public static String FILE_TMP 	= "ICCcheck.tmp";
	public static String FILE_AUX 	= "ICCcheck.aux";
	static List<String>	tmp_linesi	= new ArrayList<String>();
	static List<String>	tmp_linesj	= new ArrayList<String>();
	//
	public static String[] ATTOFFILES = new String[6];
	//
	//	---ATTOFFILES[0] = PID
	//	---ATTOFFILES[1] = Path/Filename
	//	---ATTOFFILES[2] = Percent of Fine: 	(Type in LogFile)
	//	---ATTOFFILES[3] = Percent of Info: 	(Type in LogFile)
	//	---ATTOFFILES[4] = Percent of Warning:	(Type in LogFile)
	//	---ATTOFFILES[5] = Percent of Error: 	(Type in LogFile)
	//
	public static List<String[]> LISTOFFILES	= new ArrayList<String[]>();
	public static String 		SEARCH_LINE		= "@TYPE: @HOST";
	public static int[] 		COUNTTYPES		= new int[4];
	//
	//	---COUNTTYPES[0] = Count of Fine: 		(Type in LogFile)
	//	---COUNTTYPES[1] = Count of Info: 		(Type in LogFile)
	//	---COUNTTYPES[2] = Count of Warning:	(Type in LogFile)
	//	---COUNTTYPES[3] = Count of Error: 		(Type in LogFile)
	//

	
	
    public static void main(String args[])
    {
    	if (args.length < 5)
        {
            System.out.println("USAGE: java -jar ICCcheck \"Host(Server)\" \"Service(Chk)\" \"Process(Chk)\" \"Drive[:\\path](search Logs)\" \"[-x][-v][-n][-o]\"");
            System.out.println(" NOTE: \"[-x] = Check SAP E_-X_traction Link Processes. \"");
            System.out.println("       \"[-v] = Check O_-V_erview Issues - DOKuStar Load Manager. \"");
            System.out.println("       \"[-n] = Check Cluster _-N_odes Issues - DOKuStar Load Manager. \"");
            System.out.println("       \"[-o] = Check _-O_perations Issues - DOKuStar Load Manager. \"");
            System.exit(1);
        }
    	else
    	{
    		PAR1 = args[0];
    		PAR2 = args[1];
    		PAR3 = args[2];
    		PAR4 = args[3];
    		PAR5 = args[4];
    	}
    		
        try
        {
            //*** MPS - SET INIT VARS **********************************************************
        	setInitVars();
        	
            //*** MPS - GET OS LOCALTIME *******************************************************
        	getOSLocalTime();
        	
            //*** MPS - Debug SysOut ***********************************************************
        	System.out.println("*******************************************************************************");
        	System.out.println("*** Debug OS_DTIME 	= " + OS_DTIME);
        	System.out.println("*** Debug HOST 		= " + HOST);
        	System.out.println("*** Debug SERVICE 	= " + SERVICE);
        	System.out.println("*** Debug PROCESS 	= " + PROCESS);
        	System.out.println("*** Debug DRIVE 	= " + DRIVE);
        	System.out.println("*** Debug PATH 		= " + PATH);
        	System.out.println("*******************************************************************************");
        	
        	//*** MPS - Check IN SERVICE *******************************************************
        	chkSERVICE();

            //*** MPS - SET ALL DOKuStar PROCESS AND LOGs **************************************
        	String aux_c2n	= CMD_C2N.replace("@PROCESS", "notepad"); //"DOKuStar");
        	setLISTOFFILES(aux_c2n);

        	//Check Log Files found...
        	if (LISTOFFILES.size() <= 0)
        		writeExitErrOut("LISTOFFILES", "LOG File(s) not found!!!");

        	//Write HST file to record Logs locally with % of status...
        	copyBatchFind("-copy", ".hst");
        	
        	//Select Out File depending PAR5...
        	if 		(PAR5.equals("-x"))	{ check_X(); }
        	else if (PAR5.equals("-v"))	{ check_N(); }
        	else if (PAR5.equals("-n"))	{ check_N(); }
        	else if (PAR5.equals("-o"))	{ check_O(); }
        } 
        catch (Throwable t)
        {
        	t.printStackTrace();
        }
    }
//
//    
//    
//*** MAIN PROCS ******************************************************************************************************

    public static void check_X() throws Throwable
    {
/*    	
    	DOKuStarHotSpotService.exe                      3124
    	--DOKuStarRemoteTracing.exe                       11620
    	DOKuStarExtractionServer.exe                    13960
    	DOKuStarExtractionServer.exe                    8616
    	DOKuStarExtractionServer.exe                    12688
    	DOKuStarExtractionServer.exe                    13444
    	DOKuStarExtractionServer.exe                    13848
    	DOKuStarExtractionServer.exe                    9064
    	DOKuStarExtractionServer.exe                    13572
    	DOKuStarExtractionServer.exe                    7904
    	DOKuStarExtractionServer.exe                    13692
    	DOKuStarExtractionServer.exe                    12880
    	DOKuStarExtractionServer.exe                    8116
    	DOKuStarExtractionServer.exe                    8184
    	----------------------------
    	FINDSTR pelos SAP Ext. Link
    	----------------------------
    	UK_PRD_CLNT006--disabled
    	DE_GPA_CLNT100_V11
    	PL_GPA_CLNT100
    	----------------------------
    	FINDSTR pelos SAP Dow. Link
    	----------------------------
    	DE_GPA_CLNT100_V11...
    	UK_PRD_CLNT006
    	UK_PRD_CLNT006-PO
    	PL_GPA_CLNT100-PO
    	PL_GPA_CLNT100
    	DE_GPA_CLNT100_V11    	
*/    	
    }

    
    public static void check_N() throws Throwable
    {
/*    	
    	DOKuStarClusterNode.exe                         14296
    	DOKuStarClusterNode.exe                         11780
    	DOKuStarClusterNode.exe                         14004
    	DOKuStarClusterNode.exe                         5756
*/
    }

    public static void check_O() throws Throwable
    {
    	//Test MPS 20/10...
    	copyBatchFind("-o", ".ope");
    	
    	
/*    	
    	DOKuStarLoadManager.exe                         10212
*/
    }
    
    public static void check_H() throws Throwable
    {
    	
    }
    
//*********************************************************************************************************************
//
//
//
//*** AUX  PROCS ******************************************************************************************************

    public static void callCMD(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static List<String> getOutList(String cmd) throws Exception{

    	//Execute args command...
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmd);

        //check Error...
        StreamGobbler errorGobbler = run(proc, "ERR");

        //check Output...
        StreamGobbler outputGobbler = run(proc, "ICC");
        
        // any error???
        int exitVal = proc.waitFor();
        System.out.println("ExitValue: " + exitVal);
        
    	//set Error List...
        List<String> tmp_linese = new ArrayList<String>();
    	tmp_linese = errorGobbler.getOs_lines();

    	//set Output List...
    	List<String> tmp_linesi = new ArrayList<String>();
    	tmp_linesi = outputGobbler.getOs_lines();
    	
    	if (tmp_linesi.size() > 0)
    		return tmp_linesi;
    	else
    		return tmp_linese;
    }
    
    
    private static StreamGobbler run(Process proc, String typ){
 
    	StreamGobbler tmpGobbler;
    	
        // any error message?
    	if (typ.equals("ERR")){
            tmpGobbler = new StreamGobbler(proc.getErrorStream(), typ);
    	}
    	else{
            tmpGobbler = new StreamGobbler(proc.getInputStream(), typ);
    	}
        // kick them off
        tmpGobbler.start();

        return tmpGobbler;
    }
    
    public static void setInitVars() throws Throwable
    {
    	//
    	Set<String> strings = new HashSet<String>();
    	strings.add("-x");
    	strings.add("-v");
    	strings.add("-n");
    	strings.add("-o");
    	strings.add("-h");
    	strings.add("-full");
    	//
    	if (!strings.contains(PAR5.toLowerCase()))
    		writeExitErrOut("InputParams", "Invalid Option, please type one these: \"[-x][-v][-n][-o][-h]\"");
    	
    	//
    	HOST 		= PAR1;
    	SERVICE 	= PAR2;
    	PROCESS 	= PAR3;
    	//
    	CMD_C0N 	= CMD_C0N.replace("@HOST", 		HOST);
    	CMD_C1N 	= CMD_C1N.replace("@HOST", 		HOST);
    	CMD_C1N 	= CMD_C1N.replace("@SERVICE", 	SERVICE);
    	CMD_C2N 	= CMD_C2N.replace("@HOST", 		HOST);
    	//
    	if (PAR4.indexOf(":") > 0)
    	{
        	DRIVE	= PAR4.substring(0, 1);
    		PATH 	= PAR4.substring(PAR4.indexOf(":") + 1);
    		PATH 	= PATH.replace("\\", "\\\\");
        	CMD_C3N = CMD_C3N.replace(CMD_C3S, CMD_C3R);
    	}
    	else
        	DRIVE	= PAR4;
    	//
    	CMD_C3N 	= CMD_C3N.replace("@HOST", 		HOST);
    	CMD_C3N 	= CMD_C3N.replace("@DRIVE", 	DRIVE);
    	CMD_C3N 	= CMD_C3N.replace("@PATH", 		PATH);
    	CMD_C3R 	= CMD_C3R.replace("@DRIVE", 	DRIVE);
    	CMD_C3R 	= CMD_C3R.replace("@PATH", 		PATH);
    	CMD_C3S 	= CMD_C3S.replace("@DRIVE", 	DRIVE);
    	//
    	SEARCH_LINE = SEARCH_LINE.replace("@HOST",	HOST);
    }
    
    //LocalDateTime=20151009083853.234000-180
    //CMD_C3N - DATAFILE '%@DATEOS%.@PID.log'
    //
    public static void getOSLocalTime() throws Throwable
    {
    	String dateInString 		= "";
		SimpleDateFormat formatter 	= new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat formatte2 	= new SimpleDateFormat("yyyy-MM-dd");
    	//
    	tmp_linesi 	= getOutList(CMD_C0N);
    	if (tmp_linesi.size() >= 1)
    	{
    		dateInString = tmp_linesi.get(1).substring(0, 14);
    		OS_DTIME = formatter.parse(dateInString);
    		//
    		String format = formatte2.format(OS_DTIME);		
        	CMD_C3N 	= CMD_C3N.replace("@DATEOS", format);
    	}
    }
    
    public static void chkSERVICE() throws Throwable
    {
        //*** MPS - Debug SysOut ***********************************************************
    	System.out.println("*******************************************************************************");
    	System.out.println("*** Debug CMD_C1N 	= " + CMD_C1N);
    	System.out.println("*******************************************************************************");
    	//
    	tmp_linesi 	= getOutList(CMD_C1N);
    	for (int i 	= 0; i < tmp_linesi.size(); i++)
    	{
    		System.out.println("*** Debug tmp_linesi = " + tmp_linesi.get(i).trim());
    		//
    		if (tmp_linesi.get(i).toLowerCase().indexOf(SERVICE.toLowerCase()) >= 0)
    		{
    			if (tmp_linesi.get(i).toLowerCase().indexOf("stopped") >= 0)
        			writeExitErrOut("chkSERVICE", tmp_linesi.get(i).trim());
    		}
    		else if (tmp_linesi.get(i).toLowerCase().indexOf("no instance(s)") >= 0)
    			writeExitErrOut("chkSERVICE", tmp_linesi.get(i).trim());
    	}
    }
    
    public static void setLISTOFFILES(String aux_c2n) throws Throwable
    {
    	String aux_cmd 				= "";
    	String aux_pid 				= "";
		ATTOFFILES[0]				= "";
		ATTOFFILES[1]				= "";
		ATTOFFILES[2]				= "0";
		ATTOFFILES[3]				= "0";
		ATTOFFILES[4]				= "0";
		ATTOFFILES[5]				= "0";

    	//*** MPS - Debug SysOut ***********************************************************
    	System.out.println("*******************************************************************************");
    	System.out.println("*** Debug aux_c2n 	= " + aux_c2n);
    	System.out.println("*******************************************************************************");
    	//
    	tmp_linesi = getOutList	(aux_c2n);
    	for (int i = 1; i < tmp_linesi.size(); i++)
    	{
    		//
    		System.out.println("*** Debug tmp_linesi = " + tmp_linesi.get(i).trim());
    		//
    		aux_pid = tmp_linesi.get(i).trim();
    		aux_cmd = CMD_C3N.replace("@PID", aux_pid);
    		
            //*** MPS - Debug SysOut ***********************************************************
        	System.out.println("*******************************************************************************");
        	System.out.println("*** Debug aux_cmd 	= " + aux_cmd);
        	System.out.println("*******************************************************************************");
        	//
        	
    		tmp_linesj = getOutList(aux_cmd);
    		for (String s : tmp_linesj) {
				if (s.indexOf(".log") > 0)
				{
					ATTOFFILES = new String[6];
					//
					ATTOFFILES[0] = aux_pid;
					ATTOFFILES[1] = s.trim();
					ATTOFFILES[2] = "0";
					ATTOFFILES[3] = "0";
					ATTOFFILES[4] = "0";
					ATTOFFILES[5] = "0";
            		LISTOFFILES.add(ATTOFFILES);
				}
			}
    	}
    }
    
    public static void writeExitErrOut(String tag, String msg) throws Throwable
    {
    	FileWriter writer = new FileWriter(FILE_TMP, false);
    	//
		String s = 	"\\" + tag + "\\ " + msg;
		writer.write(s + "\n");
		writer.close();
        //
        System.exit(1);
    }
    
    public static void copyBatchFind(String type, String ext) throws Throwable
    {
    	FileWriter writer 			= new FileWriter(FILE_TMP + ext, true);
    	String aux_output 			= "\\percErrorM-5\\ @STP5M";
    	//
    	String[] aux_filepath;
    	String[] aux_cmd 			= new String[4];
    	String aux_filename 		= "";
    	String aux_pathname			= "";
		String aux_search 			= SEARCH_LINE;
    	//
    	
    	for (String[] attFile : LISTOFFILES)
    	{
    		//
    		aux_pathname 	= attFile[1].trim(); 
    		aux_filepath 	= aux_pathname.replace("\\", "/").split("/");
    		if (aux_filepath.length > 0 )
        		aux_filename = aux_filepath[aux_filepath.length - 1]; 
    		
        	//Copy remote files to local...
        	if (type.equals("-copy"))
        	{
        		aux_search = aux_search.replace("@TYPE", "Error");
        		//
        		aux_cmd[0] = "COPY " + "\\\\" + HOST + "\\" + aux_pathname.replace(":", "$") + " /Y";
        		aux_cmd[1] = "FINDSTR /I /C:" + "\"" + aux_search + "\" \"" + aux_filename + "\" >> " + FILE_AUX;
        		aux_cmd[2] = "DEL \"" + aux_filename + "\"";
        		//
        		callCMD(aux_cmd[0]);
        		callCMD(aux_cmd[1]);
        		//callCMD(aux_cmd[2]);

        		//*** MPS - Debug SysOut ***********************************************************
            	System.out.println("*******************************************************************************");
        		System.out.println("*** Debug aux_cmd[0] = " + aux_cmd[0]);
        		System.out.println("*** Debug aux_cmd[1] = " + aux_cmd[1]);
        		System.out.println("*** Debug aux_cmd[2] = " + aux_cmd[2]);
            	System.out.println("*******************************************************************************");

            	//*** Call Calc Percent Types ***
        		double[] vtotType = sumPercTypes(aux_filename, attFile[0].trim());
        		//
        		if (vtotType[3] > 5)
        			aux_output = aux_output.replace("@STP5M", "TRUE");
        		else
        			aux_output = aux_output.replace("@STP5M", "FALSE");
        		//
        		NumberFormat df = DecimalFormat.getInstance();
        		df.setMinimumFractionDigits(2);
        		df.setMaximumFractionDigits(4);
        		df.setRoundingMode(RoundingMode.DOWN);    		
        		//
        		attFile[2] = df.format(vtotType[0]);    		
        		attFile[3] = df.format(vtotType[1]);    		
        		attFile[4] = df.format(vtotType[2]);    		
        		attFile[5] = df.format(vtotType[3]);    		
        		
                //*** MPS - Write type File *********************************************************
        		String s = 	aux_output + 
        	    			(" \\PID\\ " + attFile[0])		+
        	    			(" \\LOG\\ " + attFile[1])		+
        	    			(" \\%Fine\\ " + attFile[2])	+
        	    			(" \\%Info\\ " + attFile[3])	+
        	    			(" \\%Warn\\ " + attFile[4])	+
        	    			(" \\%Erro\\ " + attFile[5]);
        		//
        		writer.write(s + "\n");
            	//
            	aux_search = SEARCH_LINE;
            	aux_output = "\\percErrorM-5\\ @STP5M";
        	}
        	//Check Operation Tab...
        	else if (type.equals("-o"))
        	{
        		if (aux_filename.toLowerCase().indexOf("dokustarloadmanager") >= 0)
        		{
        			//Read All LOG and split into fields to check...
        			String[] info;
        			String aux_text = new String(Files.readAllBytes(Paths.get(aux_filename)));
        			info = aux_text.split("[[");
        			//
        			for (String s : info) {
						
        				if (s.indexOf("State=\"Processing\"") >= 0)
        				{
        					//Faz o tratamento...
        					String[] infoi = s.split("=");
        					for (String si : infoi) {

        						String test = si;
        						
							}
        				}
					}
        			
        		}
        		
        	
        		
        	}
        	//End if Types...
		}
        //
        writer.close();
    }

    public static double[] sumPercTypes(String aux_filename, String PID) throws Throwable
    {
		COUNTTYPES 					= new int[4];
    	List<String> tmp_linesi 	= new ArrayList<String>();
    	String[] aux_out;
    	String aux_search 			= "";
    	String aux_cmd 				= "";
    	String aux_typ 				= "";
    	aux_search 					= SEARCH_LINE.replace("@PID", PID);
    	
    	//Loop 0 to 3 where 0 = Fine, 1 = Info, 2 = Warning and 3 = Error 
		for (int i = 0; i < 4; i++) {

			switch (i)
			{
				case 0:	
					aux_typ = "Fine";
					break;
				case 1:	
					aux_typ = "Info";
					break;
				case 2:	
					aux_typ = "Warning";
					break;
				case 3:	
					aux_typ = "Error";
					break;
			}
			//
			aux_cmd = "CMD /C FIND /I /C " + "\"" + aux_search.replace("@TYPE", aux_typ) + "\" \"" + aux_filename + "\"";
	    	tmp_linesi = getOutList(aux_cmd);
	    	if (tmp_linesi.size() >= 0)
	    	{
	    		aux_out = tmp_linesi.get(0).split(":");
	    		if (aux_out.length == 2)
	    			COUNTTYPES[i] = COUNTTYPES[i] + Integer.parseInt(aux_out[1].trim());
	    	}
		}
		int totTypes = COUNTTYPES[0] + COUNTTYPES[1] + COUNTTYPES[2] + COUNTTYPES[3];
		double pFine = ((double)COUNTTYPES[0] / (double)totTypes) * 100;
		double pInfo = ((double)COUNTTYPES[1] / (double)totTypes) * 100;
		double pWarn = ((double)COUNTTYPES[2] / (double)totTypes) * 100;
		double pErro = ((double)COUNTTYPES[3] / (double)totTypes) * 100;
		//
		double[] vtotTypes = {pFine, pInfo, pWarn, pErro};
		return vtotTypes;
    }

//*********************************************************************************************************************
    
    
    
    
    

/*
 * 
 *
 * 
 * 
 * 
 */
    
//    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit)
//    {
//        long diffInMillies = date1.getTime() - date2.getTime();
//        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
//    }
    
    
//    private static void showLogFilesWriteTMP(List<String> tmp_linesf) throws Exception
//    {
//    	//
//    	long dt_diff				= 0;    	
//    	String dateInString 		= "";
//		SimpleDateFormat formatter 	= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
//    	//
//    	FileWriter writer 		= new FileWriter(FILE_TMP, false);
//    	List<String> tmp_linesi = new ArrayList<String>(); 
//    	String aux_cmd 			= "";
//    	String aux_PSEXEC 		= "";
//    	//
//    	if (!HOST.equals("localhost"))
//    	{ aux_PSEXEC = "CMD /C PSEXEC \\\\" + HOST + " "; }
//
//        for (String sf : tmp_linesf)
//        {
//    		aux_cmd = aux_PSEXEC + "CMD /C FINDSTR \"" + FIND_TXT + "\" \"" + sf.trim() + "\"";
//    		
//            //*** MPS - args test - ini ********************************************************
//        	System.out.println("*******************************************************************************");
//    		System.out.println("*** Debug aux_cmd = " + aux_cmd);
//
//			try
//			{
//	    		// Show Results of search
//	        	tmp_linesi 	= getOutList(aux_cmd);
//	        	for (String s : tmp_linesi)
//	        	{
//	        		//Test IF ERR>
//	        		if (!s.substring(0, 4).equals("ERR>"))
//	        		{
//	        			try
//	        			{
//	                        //*** MPS - check Dates diff - ini *************************************************
//	                    	dateInString = s.substring(0, 16);
//	                		LOG_DTIME = formatter.parse(dateInString);
//	                		dt_diff = getDateDiff(OS_DTIME, LOG_DTIME, TimeUnit.MINUTES);
//	                		//
//	                		if (dt_diff < MAX_DTDIFF)
//	                		{
//	                    		//System.out.println("*** Debug tmp_linesi = " + s);
//	                    		writer.write(s + "\n");
//	                		}
//	        			}
//	        	        catch (Throwable t)
//	        			{
//	        	        	dt_diff = MAX_DTDIFF + 1;
//	        			}
//	        		}
//	        	}
//
//	        	System.out.println("*** Debug size()	= " + tmp_linesi.size());
//	        	System.out.println("*******************************************************************************");
//	        	//
//			}
//			catch (Throwable t)
//			{
//	            //*** MPS - args test - ini ********************************************************
//	        	System.out.println("*******************************************************************************");
//	    		System.out.println("*** Debug Error Reading = " + sf);
//	        	System.out.println("*******************************************************************************");
//			}
//		}
//        //
//        writer.close();
//    }



    
}