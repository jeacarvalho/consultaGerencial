package br.com.automacao.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BancoMysql {

	//private String jdbcUrl = "jdbc:mysql://10.13.4.87:3306/log";
	private String jdbcUrl = "jdbc:mysql://localhost:3306/log";
	private String usuario = "log";
	private String senha = "log";
	
	private Connection conn = null;
	
	

	public BancoMysql() {
		
	
			try {
				Class.forName("org.gjt.mm.mysql.Driver").newInstance();
				
				try {
					conn = DriverManager.getConnection(jdbcUrl, usuario, senha);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			
		
	

}
	
	
	public Connection getConn() {
		return conn;
	}

	
}
