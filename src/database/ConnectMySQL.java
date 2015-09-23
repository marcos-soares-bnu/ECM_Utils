package database;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;


public class ConnectMySQL {

	private Connection con;
	
	public boolean connect(){
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			this.con = DriverManager.getConnection("jdbc:mysql://localhost/t-systems","root","");
			System.out.println("connected");
			return true;
		
		} catch (Exception e){
			
			System.out.println("Problemas ao tentar conectar com o banco de dados: " + e);
			System.out.println("erro");
			return false;
		}
	}
	
	public Connection getConnection(){
		return con;
	}
}
