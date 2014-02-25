package br.com.automacao.log;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import utilitarios.UtilGestaoUnidade;

public class UtilitarioLeituraLogAutomatizado {
	public static String CAMINHO_LOG = UtilGestaoUnidade.getInstanciaUtilitario().getHome() + "/apoio/FilesToday"; 
	
	public static void processaFiles(){
		ConcreteCreatorLeitor cc  = new ConcreteCreatorLeitor();
		File dir = new File(CAMINHO_LOG) ; 
		 
		File[] arquivos = dir.listFiles();

		for (int i = 0; i < arquivos.length; i++) {
			LeitorLog leitor = cc.factoryMethod(arquivos[i].toString());
			leitor.preparaLeituraThread();
			arquivos[i].delete();
		}
	}
	
	public static void verificaDownloadFiles(){

		File dir = new File(CAMINHO_LOG) ; 
		 
		File[] arquivos = dir.listFiles();
		boolean existeArquivoEmProcessamento = true;
		int j = 0;
		while(existeArquivoEmProcessamento){
			if(arquivos[j].toString().contains("part")){
				try {
					Thread.sleep(5000);
					arquivos = dir.listFiles();//leio novamente para atualizar o diretorio...
					j=0;//lendo novamente do começo do diretorio
					System.out.println("Encontrei arquivo em download: " + arquivos[j].toString()+ "-->Aguardando baixa de todos os arquivos");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				//esse arquivo nao eh um "part", vou ao proximo
				j = j+1;
			}
			
			if(j>=arquivos.length){
				//li todo o diretorio e nao encontrei arquivo "part". Assim, todos os arquivos foram baixados. Sair desse loop e processar os arquivos.
				existeArquivoEmProcessamento = false;
				System.out.println("Todos os arquivos já foram baixados");
			}
		}
		
	}

	public static void ajustaCodRetorno() {
		//depois de processar todos os arquivo, ajusto o codRetorno na base
		BancoMysql b = new BancoMysql();
		
		
		Connection conn = b.getConn();
		Statement st;
		try {
			st = conn.createStatement();
			String sql;
		    sql = ("call log.AjustaCodRetorno") ;
		    System.out.println("Leitura de arquivos finalizada. Ajustando o campo CodRetorno do Banco Mysql.....");
		   	st.executeUpdate(sql);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void verificaErrosProcessamento(){
		BancoMysql b = new BancoMysql();
		
		
		Connection conn = b.getConn();
		Statement st;
		try {
			st = conn.createStatement();
			String sql;
			sql = ("call log.VerificaErrosProcessamentoAtual") ;
		    System.out.println("Processamento do campo CodRetorno finalizado. Verificando existência de erros e enviando email para responsáveis.....");
		   	ResultSet errosNovos = st.executeQuery(sql);
		   	 
		   	if (errosNovos.first()){
		   		
		   		ResultSetMetaData rsmd = errosNovos.getMetaData();
		   		int columnCount = rsmd.getColumnCount();

		   		// The column count starts from 1
		   		String colunas="";
		   		for (int i = 1; i < columnCount + 1; i++ ) {
		   		  colunas = colunas + rsmd.getColumnName(i) + "----";
		   		}
//		   		String[columnCount] saida = {};
		   		//saida[0] = colunas;
		   		
				String[] to = {"jose-eduardo.carvalho@serpro.gov.br"}; 
				SendMail.enviarEmail(to, "Erro no processamento do log", "Verifique erros gerados no ultimo processamento do log");
		   	}
		   	
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void montaDWChamadasPorHora(String ateData){
		BancoMysql b = new BancoMysql();
		Connection conn = b.getConn();
		Statement st;
		try {
			st = conn.createStatement();
			String sql;
			sql = ("call log.ProcessaDWChamadasSimultaneas ('" + ateData + "')") ;
		    System.out.println("Montando DW com chamadas simultanes data, hora até: " + ateData);
		   	st.executeUpdate(sql);
		   	
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void montaDWCpfMaisAcessosDia(String ateData){
		BancoMysql b = new BancoMysql();
		Connection conn = b.getConn();
		Statement st;
		try {
			st = conn.createStatement();
			String sql;
			sql = ("call log.ProcessaDWCPFMaioresAcessos ('" + ateData + "')") ;
		    System.out.println("Montando DW com cpf maiores acessos dia até: " + ateData + ". Comando executado: " + sql);
		   	st.executeUpdate(sql);
		   	
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void limpaBD(String ateData){
		BancoMysql b = new BancoMysql();
		
		
		Connection conn = b.getConn();
		Statement st;
		try {
			st = conn.createStatement();
			String sql;
			sql = ("delete from log.chamada where data <=" + ateData) ;
		    System.out.println("Processamento erros finalizado. Limpando BD até: " + ateData + ". Comando executado: " + sql);
		   	st.executeUpdate(sql);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void nucleoProcessamentoLeitura(String mesString,String anoString, String diaString, WebDriver driver) {
		
		String dataIdentificaArquivoAlvo = anoString + mesString + diaString;

		System.out.println("Iniciando baixa de arquivos: " + getDataAtual());
        System.setProperty("javax.net.ssl.trustStore",UtilGestaoUnidade.getInstanciaUtilitario().getDiretorioCertificadoBaixaArquivoApoio());
        System.setProperty("javax.net.ssl.trustStorePassword", UtilGestaoUnidade.getInstanciaUtilitario().getPassCertificadoBaixaArquivoApoio());

        
        //driver.get("https://www4.receita.fazenda/ApoioWAS/ListFiles?dirName=%2Flog%2Fwas6-prd%2FKPCELL.KPNODE.KPS40K");
        
        /*
         * https://www4.receita.fazenda/ApoioWAS/ListFiles?dirName=%2Flog%2Fwas8%2FWP80CELL.WPNODEK.WPS40K - versao 8
         * https://www4.receita.fazenda/ApoioWAS/ListFiles?dirName=%2Flog%2Fwas8%2FWP80CELL.WPNODEK.WPS45K
         * 
         * A partir de 29/11/2012, passamos a ter dois lugares para baixar arquivos do Carga
         */
        
        String[] diretoriosCarga = new String[2];
        diretoriosCarga[0] = "https://www4.receita.fazenda/ApoioWAS/ListFiles?dirName=%2Flog%2Fwas8%2FWP80CELL.WPNODEK.WPS40K";
        diretoriosCarga[1] = "https://www4.receita.fazenda/ApoioWAS/ListFiles?dirName=%2Flog%2Fwas8%2FWP80CELL.WPNODEK.WPS45K";
        
        ProcessaBaixaArquivo p;
        ArrayList<ProcessaBaixaArquivo> listaThreadBaixa = new ArrayList<ProcessaBaixaArquivo>();
        
        for (int j = 0; j <= 1; j++) {
            driver.get(diretoriosCarga[j]);
            
            int i=2;
            while(existeArquivo(i, driver)){
				String nomeArquivo = driver.findElement(By.xpath("/html/body/div[3]/table/tbody/tr["+ i + "]/td[2]/nobr/a")).getText();
				if(nomeArquivo.contains(dataIdentificaArquivoAlvo) && nomeArquivo.contains("dia") && !nomeArquivo.contains("control-") 
						&& !nomeArquivo.contains("VerboseGC") ){
					//esse arquivo é de hoje e interessa ser baixado para leitura do log
					if(existeLink(i, driver)){
						FileDownloader downloadTestFile = new FileDownloader(driver);
						downloadTestFile.localDownloadPath(CAMINHO_LOG + "/");
						try {
							//20131216 o pessoal do apoio colocou um diretorio na lista de files, que não tem esse link
							//para baixar o arquivo
							WebElement downloadLink = driver.findElement(By.xpath("/html/body/div[3]/table/tbody/tr["+ i + "]/td[5]/nobr/a[2]"));
																					
							
							p = new ProcessaBaixaArquivo(downloadTestFile, downloadLink);
							p.start();
							listaThreadBaixa.add(p);
							
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}  
				i = i+1;
            }
/*            
    		for (int i = 1; i <80; i++) {
    			//lendo todos os arquivos do 40k
    			if(existeArquivo(i, driver)){
    				String nomeArquivo = driver.findElement(By.xpath("/html/body/div[3]/table/tbody/tr["+ i + "]/td[2]/nobr/a")).getText();
    				if(nomeArquivo.contains(dataIdentificaArquivoAlvo) && nomeArquivo.contains("dia") && !nomeArquivo.contains("control-")){
    					//esse arquivo é de hoje e interessa ser baixado para leitura do log
    					if(existeLink(i-1, driver)){
    						//i-1, pq no apoio o link está com indice diferente do nome do arquivo, apesar de estarem na mesma linha da table...
    						//driver.findElement(By.xpath("(//a[contains(text(),'EBCDIC para UNIX')])[" + (i-1) + "]")).click();
    						FileDownloader downloadTestFile = new FileDownloader(driver);
    						downloadTestFile.localDownloadPath(CAMINHO_LOG + "/");
    						WebElement downloadLink = driver.findElement(By.xpath("(//a[contains(text(),'EBCDIC para UNIX')])[" + (i-1) + "]"));
    						
    						p = new ProcessaBaixaArquivo(downloadTestFile, downloadLink);
    						p.start();
    						listaThreadBaixa.add(p);
    						
    						
    					}
    				}
    			}
    		}*/
		}

		for (ProcessaBaixaArquivo processaBaixaArquivo : listaThreadBaixa) {
			try {
				processaBaixaArquivo.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        
		
		System.out.println("Solicitacao de baixa de arquivos finalizada. Verificando se todos os arquivos já foram baixados");
		
		UtilitarioLeituraLogAutomatizado.verificaDownloadFiles();
		
		driver.quit();
		
		UtilitarioLeituraLogAutomatizado.processaFiles();
		
		
		UtilitarioLeituraLogAutomatizado.ajustaCodRetorno();
		
		//UtilitarioLeituraLogAutomatizado.verificaErrosProcessamento();
		
	
		System.out.println("Processamento de logs finalizado: " + getDataAtual());
	}

	
	public static boolean existeArquivo(int ordem, WebDriver driver){
		
		try {
			driver.findElement(By.xpath("/html/body/div[3]/table/tbody/tr["+ ordem + "]/td[2]/nobr/a"));
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	
	
	   private static String getDataAtual() {
	        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        Date date = new Date();
	        return dateFormat.format(date);
	    }

	   
		public static boolean existeLink(int ordem, WebDriver driver){
			
			try {
				driver.findElement(By.xpath("(//a[contains(text(),'EBCDIC para UNIX')])[" + ordem + "]"));
				return true;
			} catch (NoSuchElementException e) {
				return false;
			}
		}

	
}
