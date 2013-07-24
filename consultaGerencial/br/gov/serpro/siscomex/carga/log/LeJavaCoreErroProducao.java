package br.gov.serpro.siscomex.carga.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class LeJavaCoreErroProducao {
	
	public static void main(String[] args){
		String caixa = JOptionPane.showInputDialog(null,"Insira o nome do arquivo",JOptionPane.NO_OPTION);
		 
		try{
			FileWriter arquivo = new FileWriter(new File("/home/desenvolvimento/SaidaErroJavaCoreProducao.txt"));;
			PrintWriter saida = new PrintWriter(arquivo); 

			File javaCore = new File(caixa);
			BufferedReader in = new BufferedReader(new FileReader(javaCore));
			int i = 0;
			while (in.ready()){
				String linhaArquivo = in.readLine();
				i = i+1;
				 if(linhaArquivo != null && linhaArquivo.contains("siscomex/carga")){
					saida.println(linhaArquivo + ";" + caixa + ";linha: "+ Integer.toString(i));
				 }

			}

			in.close();
			saida.close();
			arquivo.close();
			
		} catch (Exception e) 
		 {    
			 e.printStackTrace();
		 }
		
	}
	
	
	
}
