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
		
		CmdLine cmd = new CmdLine("CMD /C WMIC /NODE:" + this.host + " OS GET localdatetime");
		
		if (cmd.getGetOutList().size() >= 1)
		{
    		//LocalDateTime=20151009083853.234000-180
    		dateInString = cmd.getGetOutList().get(1).substring(0, 14);
    		return formatter.parse(dateInString);
		}
		else
			return null;
	}

	public List<String> getWmicServiceStatus() throws Throwable
	{
		List<String> serviceWmiList	= new ArrayList<String>();
		
		CmdLine cmd = new CmdLine("CMD /C WMIC /NODE:" + this.host + " SERVICE WHERE \"name like '%" + this.service + "%'\" GET name,state");
		
		for (String s : cmd.getGetOutList())
		{
			serviceWmiList.add(s);
		}
		return serviceWmiList;
	}
	
	public List<String> getWmicProcessId() throws Throwable
	{
		List<String> processWmiList	= new ArrayList<String>();
		
		CmdLine cmd = new CmdLine("CMD /C WMIC /NODE:" + this.host + " PATH Win32_Process WHERE \"name like '%" + this.process + "%'\" GET processid");
		
		for (String s : cmd.getGetOutList())
		{
			processWmiList.add(s);
		}
		return processWmiList;
	}	
	
	public List<String> getWmicDataFileName() throws Throwable
	{
		String aux_cmd = "";
		List<String> processWmiList	= new ArrayList<String>();

		if ( (this.drive.isEmpty()) || (this.extension.isEmpty()) || (this.name.isEmpty()) )
		{
			processWmiList.add("Drive OR Extension OR Name not Informed!");
			return processWmiList;
		}
			
		if (this.path.isEmpty())
			aux_cmd = "CMD /C WMIC /NODE:" + this.host + " DATAFILE WHERE \"extension='"+ this.extension +"' and drive='" + this.drive + ":' and name like '" + this.name + "'\" GET name";
		else
			aux_cmd = "CMD /C WMIC /NODE:" + this.host + " DATAFILE WHERE \"extension='"+ this.extension +"' and drive='" + this.drive + ":' and path='" + this.path + "' and name like '" + this.name + "'\" GET name";
		
		CmdLine cmd = new CmdLine(aux_cmd);
		
		for (String s : cmd.getGetOutList())
		{
			processWmiList.add(s);
		}
		return processWmiList;
	}	

	
}
