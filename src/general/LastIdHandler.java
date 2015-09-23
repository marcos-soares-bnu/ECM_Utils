package general;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBUtil;

public class LastIdHandler {
    DBUtil db = new DBUtil();
    public void updateLastID(String lastID) {
        db.doUPDATE("linde_otass", lastID);
    }
    
    public void selectLastID() {
        ResultSet rs;
        rs = db.doSelect("last_id", "linde_otass", "is_enabled = 1");
        try {
            if (rs.next()) {
                System.out.print(rs.getString("last_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
