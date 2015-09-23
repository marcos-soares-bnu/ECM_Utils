package main;

import java.util.ArrayList;
import java.util.List;

import database.*;
import object.*;

public class TestBD {

	public static void main(String[] args) {

		//("jdbc:jtds:sqlserver://mlgmuc00csql011\sql2; instance=FISPixCoreINTERN", "fisadmin","FIS!admin");
		//
		String out_fmt1 = "%-50s  %-50s  %-25s  %-50s  %-5s  %-5s  \n";
		String out_bar1 = "--------------------------------------------------  " + "--------------------------------------------------  " + "-------------------------  " + "--------------------------------------------------  " + "-----  " + "-----  \n" ;
		String out_fmt2 = "%-50s  %-15s  %-50s  %-5s  %-25s  %-5s  %-50s \n";
		String out_bar2 = "--------------------------------------------------  " + "---------------  " + "--------------------------------------------------  " + "-----  " + "-------------------------  " + "-----  "  + "--------------------------------------------------  \n";
		//
		String server = "localhost";
		String dbname = "FISPixCoreINTERN";
		String usname = "fisadmin";
		String uspass = "FIS!admin";
		String tbname = "dbo.BatchList";
		String tbcond = "1 = 1";
		
		if (args.length < 6){
			if (args.length == 2){
				//
				if (args[0].toUpperCase().indexOf("BATCH") >= 0)
					tbname = "dbo.BatchList";
				else if (args[0].toUpperCase().indexOf("TASK") >= 0)
					tbname = "dbo.TaskList";
				else
					tbname = args[0];
				//
				tbcond = args[1];
			}
			else if (args.length == 3){

					server = args[0];
					//
					if (args[1].toUpperCase().indexOf("BATCH") >= 0)
						tbname = "dbo.BatchList";
					else if (args[1].toUpperCase().indexOf("TASK") >= 0)
						tbname = "dbo.TaskList";
					else
						tbname = args[2];
					//
					tbcond = args[1];
			}
			else{
				System.out.println("Invalid Arguments!");
				return;
			}
		}
		if (args.length == 6){
			server = args[0];
			dbname = args[1];
			usname = args[2];
			uspass = args[3];
			//
			if (args[4].toUpperCase().indexOf("BATCH") >= 0)
				tbname = "dbo.BatchList";
			else if (args[4].toUpperCase().indexOf("TASK") >= 0)
				tbname = "dbo.TaskList";
			else
				tbname = args[4];
			//
			tbcond = args[5];
		}

		//
		ExecucaoSQL execsql = new ExecucaoSQL(server, dbname, usname, uspass);
		//
		List<Registro> lstRegistro = new ArrayList<Registro>();
		lstRegistro = execsql.listaRegistro(tbname, tbcond);

		if (tbname.toUpperCase().indexOf("BATCH") >= 0){
			System.out.printf(out_fmt1, "Name  ", "Id  ", "Creation Date  ", "ProcessId  ", "Del  ", "Prio  ");
			System.out.printf(out_bar1);
		} else {
			System.out.printf(out_fmt2, "BatchId  ", "ModuleName  ", "ObjectId  ", "State  ", "Processing Date  ", "Level  ", "ParentId  ");
			System.out.printf(out_bar2);
		}
		
		for (Registro r : lstRegistro) {
			
			if (tbname.toUpperCase().indexOf("BATCH") >= 0)
				System.out.printf(out_fmt1, r.name, r.id, r.creationDate, r.processId, r.deleted, r.priority);
			else
				System.out.printf(out_fmt2, r.batchId, r.moduleName, r.objectId, r.state, r.processingDate, r.level, r.parentId);
		}
	}
}


/*
=================================================================

sqlcmd -U fisadmin -P FIS!admin -S mlgmuc00csql011\sql2 -Q "SELECT * FROM [FISPixCoreINTERN].[dbo].[BatchList]"

MLGMUC00SQL088

execsql.insereProduto(1, "produto1 de teste de Bando de Dados!");
execsql.insereProduto(2, "produto2 de teste de Bando de Dados!");
execsql.insereProduto(3, "produto3 de teste de Bando de Dados!");
execsql.insereProduto(4, "produto4 de teste de Bando de Dados!");
//
execsql.selecionaProduto();
//
execsql.atualizaProduto(1, "11111111111111111");
//
execsql.selecionaProduto();
//
execsql.deletaProduto(1);
execsql.deletaProduto(2);
execsql.deletaProduto(3);
execsql.deletaProduto(4);
=================================================================
*/
