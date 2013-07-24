package br.gov.serpro.siscomex.carga.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TransacaoDAOMysql implements TransacaoDAO{

	
	public boolean inserir(Transacao t,BancoMysql b){
		
		try{
			
			Connection conn = b.getConn();
			Statement st = conn.createStatement();
			
			
			 String sql = "SELECT * FROM transacao WHERE codigo='" + t.getCodigo() + "'";
			 st.executeQuery(sql);
//			 System.out.println("[VERIFICANDO] " + sql);
			 
			 ResultSet rs = st.getResultSet ( ); 
			 int count = 0;
			 
				 while (rs.next ( )) 
				 { 
				
					 ++count; 
				
				 } 
				 
			  rs.close ( );
			  
			  
			  if(count > 0){
//				  System.out.println("[EXISTENTE] Transacao: " + t.getCodigo() + " já previamente cadastrada!");
				  return true;
			  }
			
			
			
			
			sql = ("INSERT into transacao(codigo,nome) " +
	                	  "values ('"+t.getCodigo()+"','"+ t.getNome() + "' )") ;
	 				
//		    System.out.println("[OK]" + sql);
			st.executeUpdate(sql);
			st.close();
			} catch(Exception ex){
			ex.printStackTrace();
		
			}
			
			return true;
			}
}
