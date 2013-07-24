package br.gov.serpro.siscomex.carga.log;
import java.sql.*;

public class ProgramaDAOMysql implements ProgramaDAO{

	
public boolean inserir(Programa p,BancoMysql b){
		
		try{
			
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			Connection conn = b.getConn();
			Statement st = conn.createStatement();
			
			 String sql = "SELECT * FROM programa WHERE codigo='" + p.getNome() + "'";
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
//				  System.out.println("[EXISTENTE] Programa: " + p.getNome() + " já previamente cadastrado!");
				  return true;
			  }
			
			
			
		    sql = ("INSERT into programa(codigo,camposnome,campostamanho) " +
	                	  "values ('"+p.getNome()+"', '"+p.getLayoutNomeCampos()+"','"+p.getLayoutTamanhoCampos()+"' )") ;
	 				
//		    System.out.println("[OK] " + sql);
		    st.executeUpdate(sql);
		    
			st.close();
			
			} catch(Exception ex){
			ex.printStackTrace();
		
			}
			
			return true;
			}

}



