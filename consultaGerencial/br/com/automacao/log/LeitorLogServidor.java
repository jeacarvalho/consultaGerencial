package br.com.automacao.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class LeitorLogServidor implements LeitorLog{

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
	
	
	private String horaEmProcessamento = "00";
	
	public String getHoraEmProcessamento() {
		return horaEmProcessamento;
	}

	public void setHoraEmProcessamento(String horaEmProcessamento) {
		this.horaEmProcessamento = horaEmProcessamento;
	}

	//@Override
	public boolean atualizarBancoInternet(int linhaIncial, int linhaFinal) {
		// TODO Auto-generated method stub
		return false;
	}


	class estruturaNatural{
		
		public String linha1;
		public String linha2;
		public String linha3;
		public String linha4;
		
		
	}
	
	public LeitorLogServidor(String arquivo) {
		setNomeArq(arquivo);
	}
		
	public String getNomeArq() {
		return nomeArq;
	}


	public void setNomeArq(String nomeArq) {
		this.nomeArq = nomeArq;
	}


	public boolean atualizarBanco(){
		
		 try {       
			
			 File e = new File(nomeArq);
			 
			 BufferedReader in = new BufferedReader(new FileReader(e)); 
			 
			 ChamadaDAOMysql chamada = new ChamadaDAOMysql();
			 int linha= chamada.pegaLinhaAnterior(nomeArq, banco);
			 
			 estruturaNatural en = new estruturaNatural();
			 
			 System.out.println("[PRINCIPAL]" + "Iniciando programa consulta gerencial - LOG 0.1");
			 System.out.println("[PRINCIPAL]" + "Abrindo arquivo: " + nomeArq + " Tamanho: " + e.length() + " bytes. Lendo a partir da linha: " + linha);
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
				     
				     
/*				     en.linha2 = in.readLine();//
					 en.linha3 = in.readLine();//
					 en.linha4 = in.readLine();//
					 i = i + 3;
					 if(en.linha2 != null && en.linha3 != null && en.linha2.contains("00CARGA") && en.linha3.contains("G36173")){
						
						 processaLinha(en.linha2); 
						 processaLinha(en.linha3); 
						 processaLinha(en.linha4);
						 gravaEstruturaBanco();
						 
						
					 }
*/				     
					 
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
	
	

	public boolean atualizarBancoInternet(){
		
		 try {       
			
			 File e = new File(nomeArq);
			 
			 BufferedReader in = new BufferedReader(new FileReader(e)); 
			 
			 ChamadaDAOMysql chamada = new ChamadaDAOMysql();
			 int linha= chamada.pegaLinhaAnterior(nomeArq, banco);
			 
			 estruturaNatural en = new estruturaNatural();
			 
			 System.out.println("[PRINCIPAL]" + "Iniciando programa consulta gerencial - LOG 0.1");
			 System.out.println("[PRINCIPAL]" + "Abrindo arquivo: " + nomeArq + " Tamanho: " + e.length() + " bytes. Lendo a partir da linha: " + linha);
			 int i = 0 ;
			 
			 
			 while (in.ready()) { 

				 en.linha1 = in.readLine();//dadosParaLog - linha inutil
				 i++;				 
				 if(i>=linha){

				     if(en.linha1 != null && en.linha1.contains("CICSGateway-CallNat")){
				    	 extrairChamadaNaturalInternet(en.linha1);
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

	
	
	
private void processaLinha(String str){
	extrairChamadaNatural(str);
}


private void extrairChamadaNaturalInternet(String str){
	
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

		if(!hora.substring(0, 2).equals(this.getHoraEmProcessamento())){
			this.setHoraEmProcessamento(hora.substring(0, 2));
			System.out.println("[PRINCIPAL]" + "Iniciando " + hora + "-" + data + "-" + 
					new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));			
		}
		
		
		c.setHora(hora);
		
		
		
		
		//pego o CPF
		str = str.substring(str.indexOf("=")+2,str.length());
		u.setCpf(str.substring(0,11));

		//pego o Logon
		str = str.substring(str.indexOf("=")+2,str.length());
		c.setLogon(str.substring(0,6));

		//pego o Programa
		str = str.substring(str.indexOf("=")+2,str.length());
		p.setNome(str.substring(0,8));
		
		//pego A transacao
		
		str = str.substring(str.indexOf("=")+1,str.length());
		t.setCodigo(str.substring(0,str.indexOf("retorno -")-1));

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
			
			if(!hora.substring(0, 2).equals(this.getHoraEmProcessamento())){
				this.setHoraEmProcessamento(hora.substring(0, 2));
				System.out.println("Iniciando " + hora + "-" + data + "-" + 
						new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));			
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
		 
		 System.out.println("[PRINCIPAL]" + "Iniciando programa consulta gerencial - LOG 0.1");
		 System.out.println("[PRINCIPAL]" + "Abrindo arquivo: " + nomeArq + " Tamanho: " + e.length() + " bytes");
		 
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

public boolean validaConfig(){
	
	 try {       
		
		 File e = new File(nomeArq);
		 
		 BufferedReader in = new BufferedReader(new FileReader(e)); 
		 
		 estruturaNatural en = new estruturaNatural();
		 
		 System.out.println("[PRINCIPAL]" + "Iniciando programa consulta gerencial - LOG 0.1");
		 System.out.println("[PRINCIPAL]" + "Abrindo arquivo: " + nomeArq + " Tamanho: " + e.length() + " bytes");
		 
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


public boolean preparaLeituraThread(){
	
	 try {       
		
		 File e = new File(nomeArq);
		 
		 BufferedReader in = new BufferedReader(new FileReader(e)); 
		 
		 ChamadaDAOMysql chamada = new ChamadaDAOMysql();
		 int linha= chamada.pegaLinhaAnterior(nomeArq, banco);
		 
		 estruturaNatural en = new estruturaNatural();
		 
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
		 
		 int qtdThread = 10;
		 
		 int totalLinhas = linhaFinal - linhaInicial;
		 
		 int numeroLinhasCadaThread = totalLinhas/qtdThread;
		 
		 
/*		 
		 for (int j = 0; j < qtdThread-1; j++) {
			 int inicio = j*numeroLinhasCadaThread + linhaInicial;
			 int fim = inicio + numeroLinhasCadaThread;
			 Teste t = new Teste("t"+j , inicio,  fim, nomeArq); 
			 t.start();
		 }

		 int inicio = (qtdThread-1)*numeroLinhasCadaThread + linhaInicial;
		 int fim = linhaFinal+1;//no processamento, a ultima linha fica para a proxima thread. COmo eh a ultima
		 						//coloco mais uma linha.
		 Teste t = new Teste("t"+qtdThread , inicio,  fim, nomeArq); 
		 
		 t.start();*/
		 
		 int inicio = 0*numeroLinhasCadaThread + linhaInicial;
		 int fim = inicio + numeroLinhasCadaThread;
		 Teste t0 = new Teste("t0" , inicio,  fim, nomeArq);
		 t0.start();

		 inicio = 1*numeroLinhasCadaThread + linhaInicial;
		 fim = inicio + numeroLinhasCadaThread;
		 Teste t1 = new Teste("t1" , inicio,  fim, nomeArq);
		 t1.start();

		 inicio = 2*numeroLinhasCadaThread + linhaInicial;
		 fim = inicio + numeroLinhasCadaThread;
		 Teste t2 = new Teste("t2" , inicio,  fim, nomeArq);
		 t2.start();

		 inicio = 3*numeroLinhasCadaThread + linhaInicial;
		 fim = inicio + numeroLinhasCadaThread;
		 Teste t3 = new Teste("t3" , inicio,  fim, nomeArq);
		 t3.start();

		 inicio = 4*numeroLinhasCadaThread + linhaInicial;
		 fim = inicio + numeroLinhasCadaThread;
		 Teste t4 = new Teste("t4" , inicio,  fim, nomeArq);
		 t4.start();
		 
		 inicio = 5*numeroLinhasCadaThread + linhaInicial;
		 fim = inicio + numeroLinhasCadaThread;
		 Teste t5 = new Teste("t5" , inicio,  fim, nomeArq);
		 t5.start();

		 inicio = 6*numeroLinhasCadaThread + linhaInicial;
		 fim = inicio + numeroLinhasCadaThread;
		 Teste t6 = new Teste("t6" , inicio,  fim, nomeArq);
		 t6.start();

		 inicio = 7*numeroLinhasCadaThread + linhaInicial;
		 fim = inicio + numeroLinhasCadaThread;
		 Teste t7 = new Teste("t7" , inicio,  fim, nomeArq);
		 t7.start();

		 inicio = 8*numeroLinhasCadaThread + linhaInicial;
		 fim = inicio + numeroLinhasCadaThread;
		 Teste t8 = new Teste("t8" , inicio,  fim, nomeArq);
		 t8.start();

		 inicio = (qtdThread-1)*numeroLinhasCadaThread + linhaInicial;
		 fim = linhaFinal+1;//no processamento, a ultima linha fica para a proxima thread. COmo eh a ultima
		 						//coloco mais uma linha.
		 Teste t9 = new Teste("t9" , inicio,  fim, nomeArq);
		 t9.start();
	 
		 System.out.println("[PRINCIPAL]" + "Aguardando a finalizacao de todas as threads");
		 try {
			t0.join();
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
			t6.join();
			t7.join();
			t8.join();
			t9.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
		 System.out.println("[PRINCIPAL]" + "Todas as threads retornaram, finalizar....");
		 chamada.ajustaLinhaAnterior(nomeArq, Integer.toString(i), banco);
		 //chamada.ajustaCodRetorno(banco);
		 in.close();
		 
	 }
	 
	 catch (IOException e) 
	 {    
		 e.printStackTrace();
	 }
	
	return false;
}


	
}
