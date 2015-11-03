package checks;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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
			String searches[] = {"Fine: " + this.wmi.getHost(), "Info: " + this.wmi.getHost(), "Warning: " + this.wmi.getHost(), "Error: " + this.wmi.getHost()};
    		double[] vtotType = sumPercTypes(aux_filename, searches);
    		
    		if (vtotType[3] > this.MAX_PERERR)
    		{
    			aux_output 	= aux_output.replace("@STP5M", "TRUE");
    			
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
		}
        writer.close();
	}

    private double[] sumPercTypes(String aux_filename, String[] search) throws IOException
    {
		this.COUNTTYPES		= new int[4];
    	
    	// Open the file
    	FileInputStream fstream = new FileInputStream(aux_filename);
    	BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

    	String strLine;

    	//Read File Line By Line
    	while ((strLine = br.readLine()) != null)
    	{
    		if (strLine.indexOf(search[0]) > 0) this.COUNTTYPES[0]++;
    		if (strLine.indexOf(search[1]) > 0) this.COUNTTYPES[1]++;
    		if (strLine.indexOf(search[2]) > 0) this.COUNTTYPES[2]++;
    		if (strLine.indexOf(search[3]) > 0) this.COUNTTYPES[3]++;
    	}

    	//Close the input stream
    	br.close();
    	
		int totTypes = this.COUNTTYPES[0] + this.COUNTTYPES[1] + this.COUNTTYPES[2] + this.COUNTTYPES[3];
		double pFine = ((double)this.COUNTTYPES[0] / (double)totTypes) * 100;
		double pInfo = ((double)this.COUNTTYPES[1] / (double)totTypes) * 100;
		double pWarn = ((double)this.COUNTTYPES[2] / (double)totTypes) * 100;
		double pErro = ((double)this.COUNTTYPES[3] / (double)totTypes) * 100;

		double[] vtotTypes = {pFine, pInfo, pWarn, pErro};
		return vtotTypes;
    }
    
}
