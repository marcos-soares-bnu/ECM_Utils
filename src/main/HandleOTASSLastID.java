package main;

import general.LastIdHandler;

public class HandleOTASSLastID {
    public static void main(String[] args) {
        LastIdHandler lh = new LastIdHandler();
        
        if (args.length > 0) {
            if (args[0].equals("update")) {
                lh.updateLastID(args[1]);
            } else if (args[0].equals("select")) {
                lh.selectLastID();
            }
        }
    }
}
