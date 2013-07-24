package br.com.automacao.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ChamadaDAOMysql implements ChamadaDAO{

	public boolean inserir(Chamada c,BancoMysql b){
		
		try{
			
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			Connection conn = b.getConn();
			Statement st = conn.createStatement();
			
			String sql;
		    sql = ("INSERT into chamada(data,hora,entrada,saida,codigo_programa,cpf,codigo_transacao,logon, W0, WA, NAT, linhaArquivo, nomeArquivo) " +
	                	  "values ('"+c.getData()+"', '"+c.getHora()+"', '"+c.getEntrada()+"', '"+c.getSaida()+ "', '" + c.getPrograma().getNome()+"', '"+
	                	  c.getUsuario().getCpf()+"', '"+c.getTransacao().getCodigo()+"','" + c.getLogon()+ "','" + c.getW0() +"','" +c.getWA()+ "','" + 
	                	  c.getNAT() + "', '"+ c.getLinhaArquivo() +"', '"+ c.getNomeArquivo() + "')") ;
	 		
		   	st.executeUpdate(sql);
		
			st.close();
			
			} catch(Exception ex){
			ex.printStackTrace();
		
			}
			
			return true;
			}

	public boolean inserirDescRetNatural(String cod, String Desc, BancoMysql b){
		
		try{
			
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			Connection conn = b.getConn();
			Statement st = conn.createStatement();

			String sql = ("INSERT into retornonatural(CodRetorno, descricao) " +
              	  "values ('"+cod+"', '"+Desc+"')") ;
		
		    System.out.println("[OK] " + sql);
		    st.executeUpdate(sql);
	
		    st.close();

			
			
			
			} catch(Exception ex){
			ex.printStackTrace();
		
			}
			
			return true;
			}

	
	public boolean inserirDescTransacao(String cod, String Desc, BancoMysql b){
		
		try{
			
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			Connection conn = b.getConn();
			Statement st = conn.createStatement();

			String sql = ("Update Transacao " +
					"set nome = " + "'" + Desc + "' " +    
              	  "where codigo = " + "'" +cod+ "' ") ;
		
		    System.out.println("[OK] " + sql);
		    st.executeUpdate(sql);
	
		    st.close();

			
			
			
			} catch(Exception ex){
			ex.printStackTrace();
		
			}
			
			return true;
			}
	
	public boolean inserirDescPrograma(String programa, String Desc, BancoMysql b){
		
		try{
			
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			Connection conn = b.getConn();
			Statement st = conn.createStatement();

			 String sql = "SELECT * FROM programa WHERE codigo='" + programa + "'";
			 st.executeQuery(sql);
			 System.out.println("[VERIFICANDO] " + sql);
			 
			 ResultSet rs = st.getResultSet ( ); 
			 int count = 0;
			 
				 while (rs.next ( )) 
				 { 
				
					 ++count; 
				
				 } 
				 
			  rs.close ( );
			  
			  
			  if(count > 0){
				 sql = "Update programa set descricao = '" + Desc +"' WHERE codigo='" + programa + "'";
				 st.executeUpdate(sql);
				 st.close();
				 return true;
			  }
			
			
			
	 		
		    System.out.println("[OK] " + sql);
		   	
			
			
			sql = ("Insert into Programa(codigo, descricao) values (" +
					"'" + programa + "', " +    
              	    "'" +Desc+ "')") ;
		
		    System.out.println("[OK] " + sql);
		    st.executeUpdate(sql);
	
		    st.close();

			
			
			
			} catch(Exception ex){
			ex.printStackTrace();
		
			}
			
			return true;
		}
	
	public int pegaLinhaAnterior(String NomeArq, BancoMysql b){
		
		try{
			
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			Connection conn = b.getConn();
			Statement st = conn.createStatement();
			String arquivo = NomeArq.substring(NomeArq.indexOf("dia"), NomeArq.length());
			 String sql = "SELECT * FROM arquivoLeitura WHERE NomeArquivo='" + arquivo + "'";
			 st.executeQuery(sql);
			 System.out.println("[VERIFICANDO] " + sql);
			 
			 ResultSet rs = st.getResultSet ( ); 
			 int count = 0;
			 int retorno = 0;
			 while (rs.next ( )){ 
				
					 ++count; 
					 retorno = rs.getInt("linhaProcessada");
				
			 } 
				 
			rs.close ( );
			  
			  
			  if(count > 0){
				 return retorno;
			  }
			
			
			
	 		
			sql = ("Insert into arquivoLeitura(NomeArquivo, linhaProcessada) values (" +
					"'" + arquivo + "', " +    
              	    "'0')") ;
		
		    System.out.println("[OK] " + sql);
		    st.executeUpdate(sql);
	
		    st.close();

		    
			
			
			} catch(Exception ex){
			ex.printStackTrace();
		
			}
			
			return 0;
		}

	
	public boolean ajustaLinhaAnterior(String NomeArq, String linha, BancoMysql b){
		
		try{
			
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			Connection conn = b.getConn();
			Statement st = conn.createStatement();
			String arquivo = NomeArq.substring(NomeArq.indexOf("dia"), NomeArq.length());


			 String sql = "Update arquivoLeitura set linhaProcessada = " + linha + " WHERE NomeArquivo='" + arquivo + "'";
			 st.executeUpdate(sql);

			 System.out.println("Atualizado: " + st.getUpdateCount() + " linhas no arquivo");
		    st.close();

		    
			
			
			} catch(Exception ex){
			ex.printStackTrace();
		
			}
			
			return true;
		}
	
	public boolean ajustaCodRetorno(BancoMysql b){
		
		try{
			
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			Connection conn = b.getConn();
			Statement st = conn.createStatement();

			String sql = "update chamada set codretorno = SUBSTR(saida, 1, 6)	where codretorno = \"\"";
			st.executeUpdate(sql);

			System.out.println("Atualizado: " + st.getUpdateCount() + " linhas no arquivo");
		    st.close();

		    
			
			
			} catch(Exception ex){
			ex.printStackTrace();
		
			}
			
			return true;
		}

	
}



