package br.gov.serpro.siscomex.carga.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UsuarioDAOMysql implements UsuarioDAO{
public boolean inserir(Usuario u,BancoMysql b){
		
		try{
			
			Connection conn = b.getConn();
			Statement st = conn.createStatement();
			
			 String sql = "SELECT * FROM usuario WHERE cpf='" + u.getCpf() + "'";
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
//				  System.out.println("[EXISTENTE] Usuário: " + u.getCpf() + " já previamente cadastrado!");
				  return true;
			  }
			
			
		    sql = ("INSERT into usuario(cpf,nome) " +
	                	  "values ('"+u.getCpf()+"', '"+u.getNome()+"' )") ;
	 				
//		    System.out.println("[OK]" + sql);
		    st.executeUpdate(sql);
			st.close();
			
			} catch(Exception ex){
			ex.printStackTrace();
		
			}
			
			return true;
			}

}
