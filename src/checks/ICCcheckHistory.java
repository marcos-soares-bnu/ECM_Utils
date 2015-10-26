package checks;

import java.io.FileWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import output.CmdLine;
import wmi.WmiConsole;

public class ICCcheckHistory extends ICCcheck {

	public int MAX_PERERR;
	
	public int getMAX_PERERR() {
		return MAX_PERERR;
	}

	public void setMAX_PERERR(int mAX_PERERR) {
		MAX_PERERR = mAX_PERERR;
	}

	public ICCcheckHistory(WmiConsole wmi, int mAX_PERERR) throws Throwable {
		super(wmi);
		MAX_PERERR = mAX_PERERR;
	}

	public void checkHistory(String fileHistory) throws Throwable
	{
    	FileWriter writer		= new FileWriter(fileHistory, true);
    	String aux_output		= "\\percErrorM-" + this.MAX_PERERR + "\\ @STP5M";
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
			
        	//Call sumPercTypes...
    		double[] vtotType = sumPercTypes(aux_filename);
    		
    		if (vtotType[3] > this.MAX_PERERR)
    			aux_output 	= aux_output.replace("@STP5M", "TRUE");
    		else
    			aux_output 	= aux_output.replace("@STP5M", "FALSE");
    		
    		NumberFormat df = DecimalFormat.getInstance();
    		df.setMinimumFractionDigits(2);
    		df.setMaximumFractionDigits(4);
    		df.setRoundingMode(RoundingMode.DOWN);    		

    		attFile[2] 		= df.format(vtotType[0]);    		
    		attFile[3] 		= df.format(vtotType[1]);    		
    		attFile[4] 		= df.format(vtotType[2]);    		
    		attFile[5] 		= df.format(vtotType[3]);    		
    		
            //Write type File...
    		String s 		= 	aux_output 		+ 
    	    					(" \\PID\\ " 	+ attFile[0])	+
    	    					(" \\LOG\\ " 	+ attFile[1])	+
    	    					(" \\%Fine\\ " 	+ attFile[2])	+
    	    					(" \\%Info\\ " 	+ attFile[3])	+
    	    					(" \\%Warn\\ " 	+ attFile[4])	+
    	    					(" \\%Erro\\ " 	+ attFile[5]);

    		writer.write(s + "\n");			
		}
        writer.close();
	}

    private double[] sumPercTypes(String aux_filename) throws Throwable
    {
		this.COUNTTYPES 			= new int[4];
    	String aux_search 			= "@TYPE: " + this.wmi.getHost();
    	String aux_typ 				= "";
    	String[] aux_out;
    	
    	//Loop 0 to 3 where 0 = Fine, 1 = Info, 2 = Warning and 3 = Error 
		for (int i = 0; i < 4; i++)
		{
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
	    	CmdLine cmd = new CmdLine("cmd /C FIND /I /C " + "\"" + aux_search.replace("@TYPE", aux_typ) + "\" \"" + aux_filename + "\"");
			
	    	if (cmd.getGetOutList().size() >= 0)
	    	{
	    		aux_out = cmd.getGetOutList().get(0).split(":");
	    		if (aux_out.length == 2)
	    			this.COUNTTYPES[i] = this.COUNTTYPES[i] + Integer.parseInt(aux_out[1].trim());
	    	}
		}
		int totTypes = this.COUNTTYPES[0] + this.COUNTTYPES[1] + this.COUNTTYPES[2] + this.COUNTTYPES[3];
		double pFine = ((double)this.COUNTTYPES[0] / (double)totTypes) * 100;
		double pInfo = ((double)this.COUNTTYPES[1] / (double)totTypes) * 100;
		double pWarn = ((double)this.COUNTTYPES[2] / (double)totTypes) * 100;
		double pErro = ((double)this.COUNTTYPES[3] / (double)totTypes) * 100;

		double[] vtotTypes = {pFine, pInfo, pWarn, pErro};
		return vtotTypes;
    }	
}
