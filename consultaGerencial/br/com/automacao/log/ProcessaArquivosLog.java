package br.com.automacao.log;

import java.io.File;

import org.junit.Test;

import utilitarios.UtilGestaoUnidade;

public class ProcessaArquivosLog {
	@Test
	public void testProcessaFiles(){
		ConcreteCreatorLeitor cc  = new ConcreteCreatorLeitor();
		File dir = new File(UtilGestaoUnidade.getInstanciaUtilitario().getHome() + "/Documentos/Downloads/apoio") ; 
		 
		 File[] arquivos = dir.listFiles();
		
		for (int i = 0; i < arquivos.length; i++) {
			LeitorLog leitor = cc.factoryMethod(arquivos[i].toString());
			leitor.preparaLeituraThread();
			//arquivos[i].delete();
			
		}
	}
}
