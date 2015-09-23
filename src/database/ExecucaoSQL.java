package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import object.Registro;

public class ExecucaoSQL {

	private ConnectSQLServer con = new ConnectSQLServer(); // Connect();
	
	public ExecucaoSQL(String server, String dbname, String usname, String uspass){
		
		if (con.connect(server, dbname, usname, uspass) == false){
			ConnectSQLServer con = new ConnectSQLServer();
			con.connect(server, dbname, usname, uspass);
		}
	}
	
	public List<Registro> listaRegistro(String tbname, String tbcond){
		
		List<Registro> result = new ArrayList<Registro>();
		String sql = "select * from " + tbname + " where " + tbcond;
		
		try{
			Connection connect = con.getConnection();
			java.sql.PreparedStatement statement = connect.prepareStatement(sql);
			
			ResultSet resultset = statement.executeQuery();
			
			while (resultset.next()) {
				
				Registro reg = new Registro();

				if (tbname.toUpperCase().indexOf("BATCH") >= 0){
					//
					//BatchList...
					reg.name = resultset.getString(1);
					reg.id = resultset.getString(2);
					reg.creationDate = resultset.getInt(3);
					reg.processId = resultset.getString(4);
					reg.deleted = resultset.getInt(5);
					reg.priority = resultset.getInt(6);
				}
				if (tbname.toUpperCase().indexOf("TASK") >= 0){
					//
					//BatchList...
					reg.batchId = resultset.getString(1);
					reg.moduleName = resultset.getString(2);
					reg.objectId = resultset.getString(3);
					reg.state = resultset.getInt(4);
					reg.processingDate = resultset.getInt(5);
					reg.level = resultset.getInt(6);
					reg.parentId = resultset.getString(7);
				}

				result.add(reg);
			}
			resultset.close();
			statement.close();
			
		} catch (SQLException e){
			
			System.out.println("execSQL Error - " + e.getMessage());
		}
		return result;
		
	}
	
	

/*
=================================================================
	public void insereProduto(int codigo, String descricao){
		
		String sql = "insert into ts_registro (codigo, descricao) values (?, ?) ";
		
		try{
			Connection connect = con.getConnection();
			java.sql.PreparedStatement statement = connect.prepareStatement(sql);
			
			statement.setInt(1, codigo);
			statement.setString(2, descricao);
			
			statement.execute();
			statement.close();
			
			System.out.println("produto inserido com sucesso!");
			
		} catch (SQLException e){
			
			JOptionPane.showMessageDialog(null, "Algo de errado não está certo!");
		}
		
	}

	
	public void selecionaProduto(){
		
		String sql = "select * from ts_registro ";
		
		try{
			Connection connect = con.getConnection();
			java.sql.PreparedStatement statement = connect.prepareStatement(sql);
			
			ResultSet resultset = statement.executeQuery();
			
			while (resultset.next()) {
				
				String string0 = resultset.getString(1);
				String string1 = resultset.getString("descricao");

				System.out.println("Produto codigo = " + string0 + " Descricao = " + string1);
				
			}
			
			statement.close();
			
		} catch (SQLException e){
			
			System.out.println("Algo de errado não está certo!");
		}
		
	}

	
	public void atualizaProduto(int codigo, String descricao){
		
		String sql = "update ts_registro set descricao = ? where codigo = ? ";
		
		try{
			Connection connect = con.getConnection();
			java.sql.PreparedStatement statement = connect.prepareStatement(sql);
			
			statement.setInt(2, codigo);
			statement.setString(1, descricao);
			
			statement.execute();
			statement.close();
			
			System.out.println("produto atualizado com sucesso!");
		} catch (SQLException e){
			
			System.out.println("Algo de errado não está certo!");
		}
		
	}


	public void deletaProduto(int codigo){
		
		String sql = "delete from ts_registro where codigo = ? ";
		
		try{
			Connection connect = con.getConnection();
			java.sql.PreparedStatement statement = connect.prepareStatement(sql);
			
			statement.setInt(1, codigo);
			
			statement.executeUpdate();
			statement.close();
			
			System.out.println("produto deletado com sucesso!");
		} catch (SQLException e){
			
			System.out.println("Algo de errado não está certo!");
		}
		
	}

=================================================================
*/	
	
}
