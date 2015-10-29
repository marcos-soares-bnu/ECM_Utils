package checks;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import wmi.WmiConsole;

public class ICCcheckOverview extends ICCcheck {

	public ICCcheckOverview(WmiConsole wmi) throws Throwable {
		super(wmi);
	}

	//
	public void checkOverview(String fileOverview) throws Throwable
	{
		String file_prefix		= "dokustarclusternode";
    	FileWriter writer		= new FileWriter(fileOverview, true);
    	String aux_output		= "\\statusNotReady\\ @STNR";
    	String aux_filename		= "";
    	String aux_pathname		= "";
		String[] aux_filepath;
		
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

				//Set fields to set Nodes properties...
    			info = aux_text.split("\\[\\[ClusterNode");
    			if (info.length >= 3)
    			{
    				this.aux_name		= info[1].substring(info[1].indexOf("Name=") , 				info[1].indexOf("Description=")).replace("Name=", "").replace("\"", "").replace("\n", "").trim();
    				this.aux_state		= info[2].substring(info[2].indexOf("State=") , 			info[2].indexOf("Name=")).replace("State=", "").replace("\"", "").replace("\n", "").trim();
    				this.aux_optname	= info[2].substring(info[2].indexOf("OperationTypeName=") ,	info[2].indexOf("ServiceType=")).replace("OperationTypeName=", "").replace("\"", "").replace("\n", "").trim();
    				this.aux_label		= info[2].substring(info[2].indexOf("Label=") ,				info[2].indexOf("HasFailed=")).replace("Label=", "").replace("\"", "").replace("\n", "").trim();
    				this.aux_client		= info[2].substring(info[2].indexOf("ClientID=") , 			info[2].indexOf("ClusterNodeID=")).replace("ClientID=", "").replace("\"", "").replace("\n", "").trim();

    				//
    				if (aux_state.equals("Ready"))
    					aux_output 			= aux_output.replace("@STNR", "FALSE");
    				else
    					aux_output 			= aux_output.replace("@STNR", "TRUE");
    				
                    //Write type File...
            		String sw =	aux_output 				+ 
            	    			(" \\State\\ " 			+ this.aux_state)	+
            	    			(" \\Name\\ "			+ this.aux_name)	+
            	    			(" \\Profile\\ " 		+ this.aux_optname)	+
            	    			(" \\Client\\ " 		+ this.aux_client)	+
            	    			(" \\Label\\ " 			+ this.aux_label);

            		writer.write(sw + "\n");
    			}

            	aux_output = "\\statusNotReady\\ @STNR";
    		}
		}
        writer.close();
	}
}
