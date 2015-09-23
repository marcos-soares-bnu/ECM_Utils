package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class ConnectSQLServer
{
	private Connection con;	
	
	public boolean connect(String server, String dbname, String usname, String uspass){
		
		try{
			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
			this.con = DriverManager.getConnection("jdbc:jtds:sqlserver://" + server + "; instance=" + dbname, usname, uspass);
			//System.out.println("connected");
			return true;
		
		} catch (Exception e){
			System.out.println("Connection Error - " + e.getMessage());
			return false;
		}
	}	
	
	public void dbConnect(String db_connect_string, String db_userid, String db_password)
	{
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");//("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection conn = DriverManager.getConnection(db_connect_string,
                  db_userid, db_password);
			System.out.println("connected");
			Statement statement = conn.createStatement();
			String queryString = "select * from sysobjects where type='u'";
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				System.out.println(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection(){
		return con;
	}   
   
}