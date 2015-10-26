package checks;

import java.text.SimpleDateFormat;
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

	public String USAGE_NOTE	= "*** NOTE: \"[-x] 	= Check SAP E-Xtraction + History \""	+ "\n" +
									"***       \"[-v] 	= Check O-Verview + History \""			+ "\n" +
									"***       \"[-n] 	= Check Cluster -Nodes + History \""	+ "\n" +
									"***       \"[-o] 	= Check -Operations + History \""		+ "\n" +
									"***       \"[  ] 	= Check All \"			(DOKuStar Process)";

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
		
		//Set dateOS LOG files... '%@DATEOS%.@PID.log'
		Date dateOS = this.wmi.getWmicLocalDateTime("yyyyMMddHHmmss");
		String sdateOS = new SimpleDateFormat("yyyy-MM-dd").format(dateOS);
		String sPID;
		
		//Set local list from Wmic...
		List<String> listPIDs = this.wmi.getWmicProcessId();
		
    	for (int i = 1; i < listPIDs.size(); i++)
    	{
    		sPID = listPIDs.get(i).trim();
			
			//Set name to search LOG files... '%@DATEOS%.@PID.log'
			this.wmi.setName("%" + sdateOS + "%." + sPID + ".log");
			
			//Set local list from Wmic...
			List<String> listFiles = this.wmi.getWmicDataFileName();
			
			for (String sLogName : listFiles)
			{
				if (sLogName.indexOf(".log") > 0)
				{
					this.ATTOFFILES 	= new String[6];
					//
					this.ATTOFFILES[0] 	= sPID;
					this.ATTOFFILES[1] 	= sLogName;
					this.ATTOFFILES[2] 	= "0";
					this.ATTOFFILES[3] 	= "0";
					this.ATTOFFILES[4] 	= "0";
					this.ATTOFFILES[5] 	= "0";
					this.LISTOFFILES.add(this.ATTOFFILES);				
				}
			}
		}
	}
	
	public void copyListLocal() throws Throwable
	{
		for (String attFile[] : this.LISTOFFILES)
		{
	    	cmd = new CmdLine("cmd /C COPY " + "\\\\" + this.wmi.getHost() + "\\" + attFile[1].replace(":", "$") + " /Y");
		}
	}
	
	public void outputSearchInListOfFiles(String textSearch, String fileOut) throws Throwable
	{
		String aux_filename = ""; 
		String aux_pathname = ""; 
		String[] aux_filepath;
		
		for (String attFile[] : this.LISTOFFILES)
		{
			aux_pathname 		= attFile[1].trim(); 
			aux_filepath 		= aux_pathname.replace("\\", "/").split("/");
			if (aux_filepath.length > 0 )
	    		aux_filename 	= aux_filepath[aux_filepath.length - 1]; 		
			
	    	cmd = new CmdLine("cmd /C FINDSTR /I /C:" + "\"" + textSearch + "\" \"" + aux_filename + "\" >> " + fileOut);
		}
	}	
}
