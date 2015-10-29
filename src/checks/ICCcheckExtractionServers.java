package checks;

import wmi.WmiConsole;

public class ICCcheckExtractionServers extends ICCcheck {
    
    public static String        SEARCH_LINE     = "@TYPE: @HOST";
    
    public ICCcheckExtractionServers(WmiConsole wmi) throws Throwable {
        super(wmi);        
    }
    
    public void checkExtraction(String fileExtraction) throws Throwable {

    	String aux_search = SEARCH_LINE.replace("@TYPE", "Error").replace("@HOST", this.wmi.getHost());

    	//Exec generic function to generate output from search in LISTOFFILES with filters...
    	this.outputSearchInListOfFiles(aux_search, "extraction.server", (super.ICCFILE_PREFIX + ".sea.cmd"), fileExtraction);
    	this.outputSearchInListOfFiles(aux_search, "hotspot", (super.ICCFILE_PREFIX + ".sea.cmd"), fileExtraction);
    }
}
