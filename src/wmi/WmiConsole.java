package wmi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import output.CmdLine;

public class WmiConsole {

	String host;
	String name;
	String process;
	String service;
	String state;
	String processid;
	String extension;
	String drive;
	String path;

	List<String> getWmiOutList = new ArrayList<String>();

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getDrive() {
		return drive;
	}

	public void setDrive(String drive) {
		this.drive = drive;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<String> getGetWmiOutList() {
		return getWmiOutList;
	}

	public void setGetWmiOutList(List<String> getWmiOutList) {
		this.getWmiOutList = getWmiOutList;
	}
	
	public Date getWmicLocalDateTime(String format) throws Throwable
	{
    	String dateInString 		= "";
		SimpleDateFormat formatter 	= new SimpleDateFormat(format);
		
		//*** MPS - debug
		debugSysOut("getWmicLocalDateTime", "CMD /C WMIC /NODE:" + this.host + " OS GET localdatetime");
		//***
		CmdLine cmd = new CmdLine("CMD /C WMIC /NODE:" + this.host + " OS GET localdatetime");
		
		//Set local list from Wmic...
		List<String> listOS = cmd.getGetOutList();
		
		if (listOS.size() >= 1)
		{
    		//LocalDateTime=20151009083853.234000-180
    		dateInString = listOS.get(1).substring(0, 14);
    		return formatter.parse(dateInString);
		}
		else
			return null;
	}

	public void getWmicServiceStatus() throws Throwable
	{
		//*** MPS - debug
		debugSysOut("getWmicServiceStatus", "CMD /C WMIC /NODE:" + this.host + " SERVICE WHERE \"name like '%" + this.service + "%'\" GET name,state");
		//***
		CmdLine cmd = new CmdLine("CMD /C WMIC /NODE:" + this.host + " SERVICE WHERE \"name like '%" + this.service + "%'\" GET name,state");
		
		//Set local list from Wmic...
		this.setGetWmiOutList(cmd.getGetOutList());
	}
	
	public void getWmicProcessId() throws Throwable
	{
		//*** MPS - debug
		debugSysOut("getWmicProcessId", "CMD /C WMIC /NODE:" + this.host + " PATH Win32_Process WHERE \"name like '%" + this.process + "%'\" GET processid");
		//***
		CmdLine cmd = new CmdLine("CMD /C WMIC /NODE:" + this.host + " PATH Win32_Process WHERE \"name like '%" + this.process + "%'\" GET processid");
		
		//Set local list from Wmic...
		this.setGetWmiOutList(cmd.getGetOutList());
	}	
	
	public void getWmicDataFileName() throws Throwable
	{
		if ( (this.drive.isEmpty()) || (this.extension.isEmpty()) || (this.name.isEmpty()) )
		{
			this.setGetWmiOutList(null);
		}
		String aux_cmd = "CMD /C WMIC /NODE:" + this.host + " DATAFILE WHERE \"extension='"+ this.extension +"' and drive='" + this.drive + ":' and path like '" + this.path + "' and name like '" + this.name + "'\" GET name";
		
		//*** MPS - debug
		debugSysOut("getWmicDataFileName", aux_cmd);
		//***
		CmdLine cmd = new CmdLine(aux_cmd);
		
		//Set local list from Wmic...
		this.setGetWmiOutList(cmd.getGetOutList());
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
