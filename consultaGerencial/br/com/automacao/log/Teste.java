package br.com.automacao.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Teste extends Thread {

	
	int linhaInicial;
	int linhaFinal;
	String arquivo;
	
	
	
	String str;
    public Teste(String str) {
        super(str);
    }

    public Teste(String str, int linhaInicial, int linhaFinal, String arquivo) {
        super(str);
        this.str = str;
        this.linhaInicial = linhaInicial;
        this.linhaFinal = linhaFinal;
        this.arquivo = arquivo;
    }
    
    
    public void run() {
        System.out.println("[THREAD]" + str + "linhaInicial: "+ linhaInicial + "linhaFinal " + linhaFinal +  " INICIO: "+(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format(new Date()));
        LeitorLogServidorThread l = new LeitorLogServidorThread(arquivo);
        l.atualizarBancoInternet(linhaInicial, linhaFinal);
        System.out.println("[THREAD]" +str + " FIM: "+(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format(new Date()));
        
    }

	public int getLinhaInicial() {
		return linhaInicial;
	}

	public void setLinhaInicial(int linhaInicial) {
		this.linhaInicial = linhaInicial;
	}

	public int getLinhaFinal() {
		return linhaFinal;
	}

	public void setLinhaFinal(int linhaFinal) {
		this.linhaFinal = linhaFinal;
	}


}

class LeitorLogServidorThread implements LeitorLog{

	private ProgramaDAOMysql  pMysql = new ProgramaDAOMysql();
	private UsuarioDAOMysql   uMysql = new UsuarioDAOMysql();
	private TransacaoDAOMysql tMysql = new TransacaoDAOMysql();
	private ChamadaDAOMysql   cMysql = new ChamadaDAOMysql();
	private BancoMysql  	  banco  = new BancoMysql();
	
	private String nomeArq;

	private Transacao t = new Transacao();
	private Chamada c = new Chamada();
	private Usuario u = new Usuario();
	private Programa p = new  Programa();
	
	
	
	
	class estruturaNatural{
		
		public String linha1;
		public String linha2;
		public String linha3;
		public String linha4;
		
		
	}
	
	public LeitorLogServidorThread(String arquivo) {
		setNomeArq(arquivo);
	}
		
	public String getNomeArq() {
		return nomeArq;
	}


	public void setNomeArq(String nomeArq) {
		this.nomeArq = nomeArq;
	}


/*	public boolean atualizarBanco(){
		
		 try {       
			
			 File e = new File(nomeArq);
			 
			 BufferedReader in = new BufferedReader(new FileReader(e)); 
			 
			 ChamadaDAOMysql chamada = new ChamadaDAOMysql();
			 int linha= chamada.pegaLinhaAnterior(nomeArq, banco);
			 
			 estruturaNatural en = new estruturaNatural();
			 
			 System.out.println("Iniciando programa consulta gerencial - LOG 0.1");
			 System.out.println("Abrindo arquivo: " + nomeArq + " Tamanho: " + e.length() + " bytes. Lendo a partir da linha: " + linha);
			 int i = 0 ;
			 
			 
			 while (in.ready()) { 

				 en.linha1 = in.readLine();//dadosParaLog - linha inutil
				 i++;				 
				 if(i>=linha){

				     if(en.linha1 != null && en.linha1.contains("dadosParaLog: *00CARGA")){
				    	 processaLinha(en.linha1);
				    	 if (in.ready()){
						     en.linha1 = in.readLine();
						 	 i++;
				    	 }else{
				    		 //gravou log incompleto na ultima linha do log. Volto uma linha para poder ler correto na proxima
				    		 i=i-1;
				    		 continue;
				    	 }
				     }

			    	 if(!en.linha1.contains("CICSGateway")){
			    		 continue;
			    	 }else{
			    		 processaLinha(en.linha1);
			    	 }
				     
				     
				     en.linha2 = in.readLine();//
					 en.linha3 = in.readLine();//
					 en.linha4 = in.readLine();//
					 i = i + 3;
					 if(en.linha2 != null && en.linha3 != null && en.linha2.contains("00CARGA") && en.linha3.contains("G36173")){
						
						 processaLinha(en.linha2); 
						 processaLinha(en.linha3); 
						 processaLinha(en.linha4);
						 gravaEstruturaBanco();
						 
						
					 }
				     
					 
				 }
					
				 
			 }          
			 i++;
			 
			 chamada.ajustaLinhaAnterior(nomeArq, Integer.toString(i), banco);
			 in.close();
			 
		 }
		 
		 catch (IOException e) 
		 {    
			 e.printStackTrace();
		 }
		
		return false;
	}
	*/
	

	//@Override
	public boolean atualizarBancoInternet() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean atualizarBancoInternet(int linhaIncial, int linhaFinal){
		
		 try {       
			
			FileWriter arquivo = new FileWriter(new File("/home/desenvolvimento/SaidaErroJavaLogProducao.txt"));;
			PrintWriter saida = new PrintWriter(arquivo); 

			 
			 File e = new File(nomeArq);
			 
			 BufferedReader in = new BufferedReader(new FileReader(e)); 
			 estruturaNatural en = new estruturaNatural();

			 int i = 0 ;
			 
			 
			 String dataErroCICS = "";
			 String horaErroCICS = "";
			 boolean jaChamouLogNatural = false;
			 while (in.ready()) { 

				 en.linha1 = in.readLine();//dadosParaLog - linha inutil
				 i++;				 
				 if(i>=linhaIncial && i<linhaFinal ){

				     if(en.linha1 != null && en.linha1.contains("CICSGateway-CallNat")){
				    	 extrairChamadaNaturalInternet(en.linha1, i, nomeArq);
				    	 jaChamouLogNatural = true;
				    	 //para que qualquer log posterior soh seja gravado se o primeiro programa natural foi logado.
				    	 //isso eh necessario pq CICS volta as 03:00h e web antes. Entre o retorno da web e o retorno
				    	 //do CICS, muitos erros sao logados mas o ambiente como um todo ainda esta fora.
				     }else{
				    	 if(en.linha1 != null && en.linha1.contains("Parametros : LPG36173") && jaChamouLogNatural){
				    		 //pega erros na chamada ao CICS
				    		 extrairChamadaNaturalErroCICS(en.linha1, i, dataErroCICS, horaErroCICS, i, nomeArq);
				    		 dataErroCICS = "";
							 horaErroCICS = "";
				    	 }else{
				    		 //tentativa de pegar hora correta do erro cics
				    		 //vou guardar data e hora e assumir que quando chegar LPG36173 são essas as data e hora que valem
				    		 if(en.linha1 != null && en.linha1.contains("ERRO NA CHAMADA AO CICS") && jaChamouLogNatural){
				    			String str = en.linha1;
				    			try {
				    				dataErroCICS = str.substring(0, str.indexOf("-"));
				    				if(dataErroCICS.length() < 10 ){
				    					//data sem 0 no inicio
				    					dataErroCICS = "0" + dataErroCICS.replaceAll("/","");
				    				}else{
				    					dataErroCICS = dataErroCICS.replaceAll("/","");
				    				}
								} catch (Exception e2) {
									e2.printStackTrace();
									dataErroCICS = "ERRDTCIC";
								} 
				    			 
			    				//processei data, tiro ela
			    				str = str.substring(str.indexOf("-")+1, str.length());
			    				
			    				try {
				    				horaErroCICS =  str.substring(0, str.indexOf("C"));
				    				if(horaErroCICS.length() < 10 ){
				    					//hora sem 0 no inicio
				    					horaErroCICS = "0" + horaErroCICS.trim().substring(0, horaErroCICS.length() - 2);
				    				}else{
				    					//retirar o : do fim da hora
				    					horaErroCICS = horaErroCICS.trim().substring(0, horaErroCICS.length() - 2);
				    				}
									
								} catch (Exception e2) {
									e2.printStackTrace();
									horaErroCICS = "ERRHRCIC";
								}
			    				
				    			 
				    		 }else{
				    			 //tratamento inicial, apenas gerando um arquivo txt, de exceptions java do siscomex
				    			 if(en.linha1 != null && en.linha1.contains("siscomex.") && jaChamouLogNatural){
				    				saida.println(en.linha1 + ";" + nomeArq + ";linha: "+ Integer.toString(i));
				    			 }
				    		 }
				    		 
				    	 }
				     }
					 
				 }
					
				 
			 }          
			i++;
			

			in.close();
			 
			saida.close();
			arquivo.close();
			 
		 }
		 
		 catch (IOException e) 
		 {    
			 e.printStackTrace();
		 }
		
		return false;
	}

	
	
	
private void processaLinha(String str){
	extrairChamadaNatural(str);
}


private void extrairChamadaNaturalInternet(String str, int linha, String nomeArquivo){
	
	try{
		t = new Transacao();
		c = new Chamada();
		u = new Usuario();
		p = new  Programa();
		
		String data = str.substring(0, str.indexOf("-"));
		if(data.length() < 10 ){
			//data sem 0 no inicio
			data = "0" + data.replaceAll("/","");
		}else{
			data = data.replaceAll("/","");
		}

		c.setData(data);
		
		
		//processei data, tiro ela
		str = str.substring(str.indexOf("-")+1, str.length());
		
		String hora =  str.substring(0, str.indexOf("C"));
		if(hora.length() < 10 ){
			//hora sem 0 no inicio
			hora = "0" + hora.trim().substring(0, hora.length() - 2);
		}else{
			//retirar o : do fim da hora
			hora = hora.trim().substring(0, hora.length() - 2);
		}

		c.setHora(hora);
		
		
		
		//pego o CPF
		//str = str.substring(str.indexOf("=")+2,str.length());
		str = str.substring(str.indexOf("CPF =")+6,str.length());
		u.setCpf(str.substring(0,11));

		//pego o Logon
		//str = str.substring(str.indexOf("=")+2,str.length());
		str = str.substring(str.indexOf("Logon=")+7,str.length());
		c.setLogon(str.substring(0,6));

		//apenas se estou tratando de um registro de log para o carga.
		//Qualquer outro sistema deve atualizar esse m�todo, uma vez que os log podem ser em formatos diferentes,
		//e mudar o if abaixo, para poder gravar entradas de seu sistema
		if (c.getLogon().equalsIgnoreCase("G36173")){
			//pego o Programa
			//str = str.substring(str.indexOf("=")+2,str.length());
			str = str.substring(str.indexOf("Programa =")+11,str.length());
			p.setNome(str.substring(0,8));
			
			//pego A transacao
			
			str = str.substring(str.indexOf("=")+1,str.length());
			t.setCodigo(str.substring(0,str.indexOf("retorno -")-1));
			//t.setCodigo(str.substring(0,7));

			//pego W0
			//str = str.substring(str.indexOf("=")+1,str.length());
			str = str.substring(str.indexOf("W0=")+3,str.length());
			c.setW0(str.substring(0,2));
			
			//pego WA
			//str = str.substring(str.indexOf("=")+1,str.length());
			str = str.substring(str.indexOf("WA=")+3,str.length());
			c.setWA(str.substring(0,2));

			//pego NAT
			//str = str.substring(str.indexOf("=")+1,str.length());
			str = str.substring(str.indexOf("NAT=")+4,str.length());
			c.setNAT(str.substring(0,2));

			//pego entrada
			//str = str.substring(str.indexOf("=")+2,str.length());
			str = str.substring(str.indexOf("Entrada =")+10,str.length());
			int TamanhoEntrada = str.indexOf("Retorno = ")-1;
			
			if(TamanhoEntrada < 0){
				//a linha de log está quebrada. Nao encontrou retorno... finalizar leitura e gravar informação padrão nos campos que faltam
				c.setEntrada("Arquivo corrompido nessa linha");
				c.setSaida("Arquivo corrompido nessa linha");
			}else{
				if (TamanhoEntrada > 288 ) TamanhoEntrada = 288;
				c.setEntrada(str.substring(0,TamanhoEntrada));
				//pego retorno
				//str = str.substring(str.indexOf("=")+2,str.length());
				str = str.substring(str.indexOf("Retorno =")+10,str.length());
				//c.setSaida(str.substring(0,str.length()));
				int TamanhoSaida = str.length();
				if (TamanhoSaida > 288) TamanhoSaida = 288;
				c.setSaida(str.substring(0,TamanhoSaida)); //para log homologa��o que n�o limita a saida
			}
			c.setPrograma(p);
			c.setUsuario(u);
			c.setTransacao(t);
			c.setNomeArquivo(nomeArquivo);
			c.setLinhaArquivo(Integer.toString(linha));
			gravaEstruturaBanco();
		}/*else{
			if(c.getLogon().equalsIgnoreCase("G36154")){
				//pego o Programa
				str = str.substring(str.indexOf("=")+2,str.length());
				p.setNome(str.substring(0,8));
				

				//pego W0
				str = str.substring(str.indexOf("=")+1,str.length());
				c.setW0(str.substring(0,2));
				
				//pego WA
				str = str.substring(str.indexOf("=")+1,str.length());
				c.setWA(str.substring(0,2));

				//pego NAT
				str = str.substring(str.indexOf("=")+1,str.length());
				c.setNAT(str.substring(0,2));

				//pego entrada
				str = str.substring(str.indexOf("=")+2,str.length());
				c.setEntrada(str.substring(0,str.indexOf("Retorno = ")-1));

				//pego retorno
				str = str.substring(str.indexOf("=")+2,str.length());
				c.setSaida(str.substring(0,str.length()));
				c.setPrograma(p);
				c.setUsuario(u);
				c.setTransacao(t);
				gravaEstruturaBanco();				
			}
		}*/
	}catch(Exception e){
		System.out.println("[THREAD]" +"String de leitura com erro: " + str + "Arquivo Lido: " + nomeArquivo);
		e.printStackTrace();
	}

	
	
}

private void extrairChamadaNaturalErroCICS(String str, int i, String data, String hora, int linha, String nomeArq){
	
	try{
		t = new Transacao();
		c = new Chamada();
		u = new Usuario();
		p = new  Programa();
		
/*		String data = nomeArq.substring(nomeArq.indexOf("dia"), nomeArq.length());
		
		String dataPura = "20"+data.substring(3, 9);
		String ano = dataPura.substring(0, 4);
		String mes = dataPura.substring(4, 6);
		String dia = dataPura.substring(6, 8);
*/		
		c.setData(data);
		

		c.setHora(hora);
		
		c.setLinhaArquivo(Integer.toString(i));
		
		//pego o Programa
		str = str.substring(str.indexOf("O36173"),str.length());
		p.setNome(str.substring(0,8));		
		
		
		//pego o CPF
		str = str.substring(str.indexOf("CARGA")-11,str.length());
		u.setCpf(str.substring(0,11));

		//pego o Logon
		c.setLogon("G36173");
		
		//pego A transacao
		str = str.substring(str.indexOf("RIO02")+10,str.length());
		t.setCodigo(str.substring(0,10));

		//apos o codigo da transacao, temos varios caracteres de controle do chamaPGMNatural e então a entrada do programa
		//pego entrada
		str = str.substring(73,157);
		c.setEntrada(str);
		//pego W0
		c.setW0("99");
		//pego WA
		c.setWA("99");
		//pego NAT
		c.setNAT("99");


		//pego retorno
		c.setSaida("INDETERMINADO");
		c.setPrograma(p);
		c.setUsuario(u);
		c.setTransacao(t);
		c.setLinhaArquivo(Integer.toString(linha));
		c.setNomeArquivo(nomeArq);
		gravaEstruturaBanco();

	}catch(Exception e){
		e.printStackTrace();
	}

	
	
}

private void extrairChamadaNatural(String str){
	
	if(str.contains("dadosParaLog")){
		
		try{
			
			str = str.substring(str.indexOf("*"),str.lastIndexOf("*"));
			str = str.substring(28,str.length());
			//t.setCodigo(str.substring(0,9).trim());
			
			//verifica se mudou o codigo da transacao. no caso de varios logs sem transacao anterior
			if(!t.getCodigo().equalsIgnoreCase(str.substring(0,10).trim())){
				t = new Transacao();
				c = new Chamada();
				u = new Usuario();
				p = new  Programa();			
			}
			
			t.setCodigo(str.substring(0,10).trim());
			c.setEntrada(str.substring(10,str.length()));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	if(str.contains("CICSGateway") && !str.contains("Retorno")){
		
		try{
			
			String data = str.substring(0, str.indexOf("-"));
			if(data.length() < 10 ){
				//data sem 0 no inicio
				data = "0" + data.replaceAll("/","");
			}else{
				data = data.replaceAll("/","");
			}

			//processei data, tiro ela
			str = str.substring(str.indexOf("-")+1, str.length());
			
			String hora =  str.substring(0, str.indexOf("C"));
			if(hora.length() < 10 ){
				//hora sem 0 no inicio
				hora = "0" + hora.trim().substring(0, hora.length() - 2);
			}else{
				//retirar o : do fim da hora
				hora = hora.trim().substring(0, hora.length() - 2);
			}
			
				
			//vou para o CPF
			str = str.substring(str.indexOf("=")+1, str.length());
			String CPF = str.substring(0, str.indexOf("L")).trim();
			
			
			//pego logon 
			str = str.substring(str.indexOf("=")+1, str.length());
			String Logon = str.substring(0, str.indexOf("P")).trim();
			
			//pego Programa 
			str = str.substring(str.indexOf("=")+1, str.length());
			String Programa = str.substring(0, str.indexOf(".")).trim();

			//Monto a classe
			c.setData(data);
			c.setHora(hora);
			u.setCpf(CPF);
			c.setLogon(Logon);
			p.setNome(Programa);
			
			
	/*		//erro pela data sem o primeiro caracter
			
			
			str = "0" + str;
			//String hora = "" + str.substring(11,18);
			
			if (hora.substring(0, 1).equalsIgnoreCase("1")){
				hora = "" + str.substring(11,19);
			}
			
			c.setData(str.substring(0,10).replaceAll("/",""));
			
			
			if(str.substring(11,13).contains(":")){
				hora = "0" + str.substring(11,18);
			}
			c.setHora(hora);
			str = str.substring(str.indexOf("="),str.indexOf("."));
			u.setCpf(str.substring(2,13));
			c.setLogon(str.substring(21,27));
			//p.setNome(str.substring(41,49));
			p.setNome(str.substring(39,47));
	*/			
			
		}catch(Exception e ){
			e.printStackTrace();
		}
		
	}
	
	if(str.contains("Retorno")){
		try{
			
			str = str.substring(str.indexOf("Retorno")+1, str.length());
			str = str.substring(str.indexOf("W")+1, str.length());
			str = str.substring(str.indexOf("=")+1, str.length());
			String W0 = str.substring(0, str.indexOf("W"));
			str = str.substring(str.indexOf("W")+1, str.length());
			str = str.substring(str.indexOf("=")+1, str.length());
			String WA = str.substring(0, str.indexOf("N"));
			str = str.substring(str.indexOf("N")+1, str.length());
			str = str.substring(str.indexOf("=")+1, str.length());
			String NAT =  str.substring(0, str.indexOf("-")).trim();
			
			
			String saida = str.substring(str.indexOf("-")+2, str.length());
			
			
			c.setSaida(saida);
			c.setW0(W0);
			c.setWA(WA);
			c.setNAT(NAT);
			c.setPrograma(p);
			c.setUsuario(u);
			c.setTransacao(t);
			
			gravaEstruturaBanco();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
}


private void gravaEstruturaBanco(){
	
//	System.out.println("----------------------------------");
	pMysql.inserir(p,banco);
	tMysql.inserir(t,banco);
	uMysql.inserir(u,banco);
	cMysql.inserir(c,banco);

	
}

public boolean atualizarBancoDescRetNatural(){
	
	 try {       
		
		 File e = new File(nomeArq);
		 
		 BufferedReader in = new BufferedReader(new FileReader(e)); 
		 
		 estruturaNatural en = new estruturaNatural();
		 
		 System.out.println("[THREAD]" +"Iniciando programa consulta gerencial - LOG 0.1");
		 System.out.println("[THREAD]" +"Abrindo arquivo: " + nomeArq + " Tamanho: " + e.length() + " bytes");
		 
		 while (in.ready()) { 
			 
			 	 en.linha1 = in.readLine();
			 	 String programa = en.linha1.substring(0, en.linha1.indexOf("*"));
			 	 String descricao = en.linha1.substring(en.linha1.indexOf(":")+1,en.linha1.length());
			 	 descricao.replaceAll("'", "");
			 	descricao.replaceAll("/", "");
			 	 			 	 
			 	 ChamadaDAOMysql ch = new ChamadaDAOMysql();
			 	 ch.inserirDescPrograma(programa, descricao, banco);
			 	 
				
			 
		 }          
		 
		 in.close();
		 
	 }
	 
	 catch (IOException e) 
	 {    
		 e.printStackTrace();
	 }
	
	return false;
}

/*public boolean validaConfig(){
	
	 try {       
		
		 File e = new File(nomeArq);
		 
		 BufferedReader in = new BufferedReader(new FileReader(e)); 
		 
		 estruturaNatural en = new estruturaNatural();
		 
		 System.out.println("Iniciando programa consulta gerencial - LOG 0.1");
		 System.out.println("Abrindo arquivo: " + nomeArq + " Tamanho: " + e.length() + " bytes");
		 
		 while (in.ready()) { 
			 
			 	 en.linha1 = in.readLine();
			 	 if (!(en.linha1.indexOf("type")==-1) && (en.linha1.length()>0)){
			 		 en.linha1 = en.linha1.substring(en.linha1.indexOf("/")+1,en.linha1.length());
			 		 
			 		 String path = en.linha1.substring(0,en.linha1.indexOf('"'));
			 		 en.linha1 = en.linha1.substring(en.linha1.indexOf("="),en.linha1.length());
			 		 
			 		 if(en.linha1.indexOf(path)==-1){
			 			String programa = "diferente";		 
			 		 }
			 		 
			 	 }
			 
		 }          
		 
		 in.close();
		 
	 }
	 
	 catch (IOException e) 
	 {    
		 e.printStackTrace();
	 }
	
	return false;
}
*/

public boolean preparaLeituraThread(){
	
	 try {       
		
		 File e = new File(nomeArq);
		 
		 BufferedReader in = new BufferedReader(new FileReader(e)); 
		 
		 ChamadaDAOMysql chamada = new ChamadaDAOMysql();
		 int linha= chamada.pegaLinhaAnterior(nomeArq, banco);
		 
		 estruturaNatural en = new estruturaNatural();
		 
		 System.out.println("[THREAD]" +"Iniciando programa consulta gerencial - LOG 0.1");
		 System.out.println("[THREAD]" +"Abrindo arquivo: " + nomeArq + " Tamanho: " + e.length() + " bytes. Lendo a partir da linha: " + linha);
		 int i = 0 ;
		 int linhaInicial=0;
		 int linhaFinal=0;
		 boolean acheiLinha = false;
		 while (in.ready()) { 

			 en.linha1 = in.readLine();//dadosParaLog - linha inutil
			 i++;				 
			 if(i>=linha && !acheiLinha){
				 linhaInicial = i;
				 acheiLinha = true;
				 
			 }
				
			 
		 }          
		 
		 linhaFinal = i;
		 
		 int totalLinhas = linhaFinal - linhaInicial;
		 
		 int numeroLinhasCadaThread = totalLinhas/5;
		 
		 for (int j = 0; j < 5; j++) {
			
		 }
		 
		 
		 chamada.ajustaLinhaAnterior(nomeArq, Integer.toString(i), banco);
		 in.close();
		 
	 }
	 
	 catch (IOException e) 
	 {    
		 e.printStackTrace();
	 }
	
	return false;
}


	
}
