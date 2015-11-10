package checks;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import wmi.WmiConsole;

public class ICCcheckClusterNodes extends ICCcheck {

	public int MAX_DTDIFF;
	
	public int getMAX_DTDIFF() {
		return MAX_DTDIFF;
	}

	public void setMAX_DTDIFF(int mAX_DTDIFF) {
		MAX_DTDIFF = mAX_DTDIFF;
	}

	public ICCcheckClusterNodes(WmiConsole wmi, int mAX_DTDIFF) throws Throwable {
		super(wmi);
		MAX_DTDIFF = mAX_DTDIFF;
	}

	public void checkClusterNodes(String fileClusterNodes) throws Throwable
	{
		String file_prefix		= "dokustarclusternode";
    	FileWriter writer		= new FileWriter(fileClusterNodes, true);
    	String aux_output		= "\\timeProcessingM-" + this.MAX_DTDIFF + "\\ TRUE";
    	String aux_filename		= "";
    	String aux_pathname		= "";
		String[] aux_filepath;
		this.aux_state			= "Processing";
		
		for (String attFile[] : this.LISTOFFILES)
		{
			aux_pathname 		= attFile[1].trim(); 
			aux_filepath 		= aux_pathname.replace("\\", "/").split("/");
			if (aux_filepath.length > 0 )
	    		aux_filename 	= aux_filepath[aux_filepath.length - 1]; 		
			//
			if (aux_filename.toLowerCase().indexOf(file_prefix) >= 0)
    		{
    			//Read All LOG and split into fields to check...
    			String[] info = null;
    			String aux_text = new String(Files.readAllBytes(Paths.get(aux_filename)));
    			
    			//Set aux_name
    			info = aux_text.split("\\[\\[ClusterNode settings:");
    			if (info.length >= 2)
    				this.aux_name = info[1].substring(info[1].indexOf("Name=") , info[1].indexOf("Description=")).replace("Name=", "").replace("\"", "").replace("\n", "").trim();
    			else
    				this.aux_name = "Fields not found (ClusterNode)!!!";
    			
    			//Set Operations...
    			info = aux_text.split("\\[\\[Operation:");
    			
    			for (String s : info)
    			{
					//Set main fields...
    				if (s.indexOf("State=\"Processing\"") >= 0)
    				{
    					this.aux_node		= s.substring(s.indexOf("ClusterNodeID=") , 	s.indexOf("Tag=")).replace("ClusterNodeID=", "").replace("\"", "").replace("\n", "").trim();
    					this.aux_client		= s.substring(s.indexOf("ClientID=") , 			s.indexOf("ClusterNodeID=")).replace("ClientID=", "").replace("\"", "").replace("\n", "").trim();
    					this.aux_label		= s.substring(s.indexOf("Label=") , 			s.indexOf("HasFailed=")).replace("Label=", "").replace("\"", "").replace("\n", "").trim();
    					this.aux_start		= s.substring(s.indexOf("StartTime=") ,			s.indexOf("TotalTime=")).replace("StartTime=", "").replace("\"", "").replace("\n", "").trim(); 
    					this.aux_tottime 	= s.substring(s.indexOf("TotalTime=") , 		s.indexOf("MethodName=")).replace("TotalTime=", "").replace("\"", "").replace("\n", "").trim();
    					this.aux_optname	= s.substring(s.indexOf("OperationTypeName=") , s.indexOf("ServiceType=")).replace("OperationTypeName=", "").replace("\"", "").replace("\n", "").trim();

    					double totime 	= 0;
    					int hours 		= Integer.parseInt(aux_tottime.substring(0, 2));
    					int minutes		= Integer.parseInt(aux_tottime.substring(3, 5));
    					int seconds		= Integer.parseInt(aux_tottime.substring(6, 8));

    					try 
    					{ 
    						totime = ( (double) hours * 60 );
    						totime = totime	+ ( (double) minutes ); 
    						totime = totime	+ ( (double) seconds / 60 ); 
    					}
    					catch(Exception ex)
    					{ 
    						totime = 0; 
    					}
    					
    	                //Write type File when tottime > MAX_DTDIFF...
    					if (totime > this.MAX_DTDIFF)
    					{
    		        		String sw =	aux_output 				+ 
    		        	    			(" \\State\\ " 			+ this.aux_state)	+
    		        	    			(" \\Profile\\ "		+ this.aux_optname)	+
    		        	    			(" \\Client\\ " 		+ this.aux_client)	+
    		        	    			(" \\Label\\ " 			+ this.aux_label)	+
    		        	    			(" \\Started\\ " 		+ this.aux_start)	+
    		        	    			(" \\tottime\\ " 		+ this.aux_tottime);

    		        		writer.write(sw + "\n");
    					}
    				}
				}
    		}
		}
        writer.close();
	}
}
