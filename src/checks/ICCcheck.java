package checks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wmi.WmiConsole;
import output.CmdLine;

public class ICCcheck {

	CmdLine cmd;
	public WmiConsole wmi; 
	//
	public String aux_state 	= "";
	public String aux_node		= "";
	public String aux_client	= "";
	public String aux_label		= "";
	public String aux_start		= "";
	public String aux_tottime	= "";

	public String aux_optname	= "";
	public String aux_name		= "";	

	public String[] ATTOFFILES	= new String[6];
	//	---ATTOFFILES[0] = PID
	//	---ATTOFFILES[1] = Path/Filename
	//	---ATTOFFILES[2] = Percent of Fine: 	(Type in LogFile)
	//	---ATTOFFILES[3] = Percent of Info: 	(Type in LogFile)
	//	---ATTOFFILES[4] = Percent of Warning:	(Type in LogFile)
	//	---ATTOFFILES[5] = Percent of Error: 	(Type in LogFile)

	public List<String[]> LISTOFFILES	= new ArrayList<String[]>();
	public int[] 		COUNTTYPES		= new int[4];
	//	---COUNTTYPES[0] = Count of Fine: 		(Type in LogFile)
	//	---COUNTTYPES[1] = Count of Info: 		(Type in LogFile)
	//	---COUNTTYPES[2] = Count of Warning:	(Type in LogFile)
	//	---COUNTTYPES[3] = Count of Error: 		(Type in LogFile)	

	public List<String> LISTOFPIDS	= new ArrayList<String>();
	
	public List<String> getLISTOFPIDS() {
		return LISTOFPIDS;
	}
	public void setLISTOFPIDS(List<String> lISTOFPIDS) {
		LISTOFPIDS = lISTOFPIDS;
	}

	public String ICCFILE_PREFIX;
	
	public String getICCFILE_PREFIX() {
		return ICCFILE_PREFIX;
	}
	public void setICCFILE_PREFIX(String iCCFILE_PREFIX) {
		ICCFILE_PREFIX = iCCFILE_PREFIX;
	}

	public Date OS_DATETIME;
	
	public Date getOS_DATETIME() {
		return OS_DATETIME;
	}
	public void setOS_DATETIME(Date oS_DATETIME) {
		OS_DATETIME = oS_DATETIME;
	}
	public String getAux_state() {
		return aux_state;
	}
	public void setAux_state(String aux_state) {
		this.aux_state = aux_state;
	}
	public String getAux_node() {
		return aux_node;
	}
	public void setAux_node(String aux_node) {
		this.aux_node = aux_node;
	}
	public String getAux_client() {
		return aux_client;
	}
	public void setAux_client(String aux_client) {
		this.aux_client = aux_client;
	}
	public String getAux_label() {
		return aux_label;
	}
	public void setAux_label(String aux_label) {
		this.aux_label = aux_label;
	}
	public String getAux_start() {
		return aux_start;
	}
	public void setAux_start(String aux_start) {
		this.aux_start = aux_start;
	}
	public String getAux_tottime() {
		return aux_tottime;
	}
	public void setAux_tottime(String aux_tottime) {
		this.aux_tottime = aux_tottime;
	}
	public String getAux_optname() {
		return aux_optname;
	}
	public void setAux_optname(String aux_optname) {
		this.aux_optname = aux_optname;
	}
	public String getAux_name() {
		return aux_name;
	}
	public void setAux_name(String aux_name) {
		this.aux_name = aux_name;
	}
	public String[] getATTOFFILES() {
		return ATTOFFILES;
	}
	public void setATTOFFILES(String[] aTTOFFILES) {
		ATTOFFILES = aTTOFFILES;
	}
	public List<String[]> getLISTOFFILES() {
		return LISTOFFILES;
	}
	public void setLISTOFFILES(List<String[]> lISTOFFILES) {
		LISTOFFILES = lISTOFFILES;
	}
	public int[] getCOUNTTYPES() {
		return COUNTTYPES;
	}
	public void setCOUNTTYPES(int[] cOUNTTYPES) {
		COUNTTYPES = cOUNTTYPES;
	}
	public WmiConsole getWmi() {
		return wmi;
	}
	public void setWmi(WmiConsole wmi) {
		this.wmi = wmi;
	}
	
	public ICCcheck(WmiConsole wmi) throws Throwable {
		super();
		this.wmi = wmi;
	}

	public ICCcheck(WmiConsole wmi, List<String> ListPids) throws Throwable {
		super();
		this.wmi = wmi;
		this.setLISTOFPIDS(ListPids);
	}
	
	//MPS - 4/11/2015
	public void setLISTOFFILES(String fileLOF) throws Throwable
	{
		List<String> listFiles;
    	List<String> lstLOF = chkRecFileLOF(fileLOF);
    	
    	if (lstLOF.isEmpty())
    	{
    		//Get All LOG Files with dateOS... so filter for PIDS List...
    		this.wmi.getWmicDataFileName();
    		listFiles = this.wmi.getGetWmiOutList();
    	}
    	else
    	{
    		listFiles = lstLOF;
    	}

    	FileWriter writer = new FileWriter(fileLOF);
		for (String sLogName : listFiles) {

			String[] sPID			= sLogName.split("\\.");
			String aux_sPID			= "";
			if (sPID.length > 2)
				aux_sPID			= sPID[sPID.length - 2];
			
			if (this.LISTOFPIDS.indexOf(aux_sPID) >= 0)
			{
				this.ATTOFFILES 	= new String[6];
				this.ATTOFFILES[0] 	= aux_sPID;
				this.ATTOFFILES[1] 	= sLogName;
				this.ATTOFFILES[2] 	= "0";
				this.ATTOFFILES[3] 	= "0";
				this.ATTOFFILES[4] 	= "0";
				this.ATTOFFILES[5] 	= "0";
				this.LISTOFFILES.add(this.ATTOFFILES);
				//
	    		writer.write(sLogName + " \n");
			}
		}
		writer.close();
	}
	
	//MPS - 4/11/2015
	private List<String> chkRecFileLOF(String fileLOF) throws Throwable
	{
    	List<String> listLOF = new ArrayList<String>(); 

    	File f = new File(fileLOF);
    	String strLine;
    	if (f.exists())
    	{
    		if (f.length() > 0)
    		{
    	    	// Open the file
    	    	FileInputStream fstream = new FileInputStream(fileLOF);
    	    	BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

    	    	//Read File Line By Line
    	    	while ((strLine = br.readLine()) != null) { listLOF.add(strLine); }

    	    	//Close the input stream
    	    	br.close();
    		}
    	}
    	return listLOF;
	}
	
	public void copyListLocal(String fileCmd) throws Throwable
	{
		//Write CMD to copy all LOG's files...
		String aux_cmd			= "";
    	FileWriter writer		= new FileWriter(fileCmd);
		
		for (String attFile[] : this.LISTOFFILES)
		{
			//***MPS debug
			aux_cmd				= "COPY \"" + "\\\\" + this.wmi.getHost() + "\\" + attFile[1].replace(":", "$").trim() + "\" /Y"; 
			debugSysOut("copyListLocal", aux_cmd);
			//***
    		writer.write(aux_cmd + " \n");
		}
		writer.close();
		
		//Execute CMD to copy all LOG's files...
		cmd = new CmdLine("cmd /C " + fileCmd);
	}
	
	public String linesMatchSearchFile(String txt_search, String aux_filename) throws IOException
	{
		String aux_return = "";
		
    	// Open the file
    	FileInputStream fstream = new FileInputStream(aux_filename);
    	BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

    	String strLine;

    	//Read File Line By Line
    	while ((strLine = br.readLine()) != null)
    	{
    		if (strLine.indexOf(txt_search) > 0) aux_return += strLine + "\n";
    	}
    	//Close the input stream
    	br.close();

		return aux_return;
	}
	
	public void outputSearchInListOfFiles(String txt_search, String filterFile, String fileOut) throws Throwable
	{
		//Write CMD to copy all LOG's files...
    	FileWriter writer		= new FileWriter(fileOut, true); //true = append to fileOut...
		String aux_filename 	= ""; 
		String aux_pathname 	= ""; 
		String[] aux_filepath;
		String aux_lines		= "";
		
		for (String attFile[] : this.LISTOFFILES)
		{
			aux_pathname 		= attFile[1].trim(); 
			aux_filepath 		= aux_pathname.replace("\\", "/").split("/");
			if (aux_filepath.length > 0 )
			{
	    		aux_filename 	= aux_filepath[aux_filepath.length - 1];

	    		//Filter per filterFile... if empty no filter applied...
	    		if ( (filterFile.equals("")) || (aux_filename.toLowerCase().indexOf(filterFile.toLowerCase()) >= 0) )
	    		{
	    			aux_lines = linesMatchSearchFile(txt_search, aux_filename);
	    			if (aux_lines.length() > 0)
	    			{
			    		//*** Print the name of LOG with errors
		    			writer.write("LogFile: " + aux_pathname + " \n");
		    			writer.write(aux_lines + " \n");
	    			}
	    		}
			}
		}
		writer.close();
	}
	
    public void debugSysOut(String var, String val)
    {
    	System.out.println("-------------------------------------------------------------------------------");
       	System.out.println(">>> Debug " + padRight(var, 30)  + " = " + val);
    	System.out.println("-------------------------------------------------------------------------------");
    }

    public void debugSysOut(String[] vars, String[] vals)
    {
    	System.out.println("===============================================================================");

    	for (int i = 0; i < vals.length; i++)
    	{
           	System.out.println(">>> Debug " + padRight(vars[i], 30)  + " = " + vals[i]);
		}

    	System.out.println("===============================================================================");
    }

    private String padRight(String s, int n)
    {
    	return String.format("%1$-" + n + "s", s);
    }    
	
}
