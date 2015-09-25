package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DBUtil {

    private Connection conn;

    public DBUtil() {
        this.conn = new ConnectionManager().getConnection();
    }

    private Connection getConn() {
        if (conn == null) {
            conn = new ConnectionManager().getConnection();
        }
        return conn;
    }
    
    public ResultSet doSelect(String fields, String table, String condition) {
        String sql = "SELECT " + fields;
        sql += " FROM " + table;

        if (!condition.isEmpty()) {
            sql += " WHERE " + condition;
        }
        sql += ";";
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
        return null;
    }

    public void doUPDATE(String table, String value) {
        String sql = "UPDATE " + table;

        if (!value.isEmpty()) {
            sql += " SET last_id = "+value;
        }
        sql += ";";

        Statement stmt = null;
        try {
            stmt = this.getConn().createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    public byte[] doSelect() {
        String sql = "SELECT pass FROM linde_otass WHERE is_enabled = 1";
        ResultSet rs = null;
        byte[] encryptedPass = null;

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next())
                encryptedPass = rs.getBytes(1);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
        return encryptedPass;
    }
    
    public void doINSERT(byte[] encryptedPass) {
        String sql = "INSERT INTO linde_otass (user,pass,is_enabled) " +
                "VALUES (?,?,?)"; 
        
        try {
            PreparedStatement ps = getConn().prepareStatement(sql);
            ps.setString(1, "ixosadm");
            ps.setBytes(2, encryptedPass);
            ps.setInt(3, 1);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    public void closeConn(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
}
