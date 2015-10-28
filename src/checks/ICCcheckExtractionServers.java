package checks;

import output.CmdLine;
import wmi.WmiConsole;

public class ICCcheckExtractionServers extends ICCcheck {
    
    public static String        SEARCH_LINE     = "@TYPE: @HOST";
    
    public ICCcheckExtractionServers(WmiConsole wmi) throws Throwable {
        super(wmi);        
    }
    
    public void checkExtraction(String fileExtraction) throws Throwable {

    	String aux_search = SEARCH_LINE.replace("@TYPE", "Error").replace("@HOST", this.wmi.getHost());
        String aux_filename     = "";
        String aux_pathname     = "";
        String[] aux_filepath;
        
        for (String attFile[] : this.LISTOFFILES) {
            aux_pathname        = attFile[1].trim(); 
            aux_filepath        = aux_pathname.replace("\\", "/").split("/");
            if (aux_filepath.length > 0 )
                aux_filename    = aux_filepath[aux_filepath.length - 1];
            
            if (aux_filename.contains("extraction.server")) {
                cmd = new CmdLine("cmd /c echo Logfile: " + aux_filename + " >> " + fileExtraction);
                cmd = new CmdLine("cmd /c FINDSTR /I /C:" + "\"" + aux_search + "\" \"" + aux_filename + "\" >> " + fileExtraction);
                cmd = new CmdLine("cmd /c echo. >> " + fileExtraction);
            } else if (aux_filename.contains("hotspot")) {
                cmd = new CmdLine("cmd /c echo Logfile: " + aux_filename + " >> " + fileExtraction);
                cmd = new CmdLine("cmd /c FINDSTR /I /C:" + "\"" + aux_search + "\" \"" + aux_filename + "\" >> " + fileExtraction);
                cmd = new CmdLine("cmd /c echo. >> " + fileExtraction);
            }
        }
    }
}
