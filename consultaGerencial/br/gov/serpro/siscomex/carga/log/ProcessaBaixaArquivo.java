package br.gov.serpro.siscomex.carga.log;

import org.openqa.selenium.WebElement;

public class ProcessaBaixaArquivo extends Thread {

	WebElement downloadLink;
	FileDownloader downloadTestFile;
	
	public ProcessaBaixaArquivo(FileDownloader downloadTestFilePar, WebElement downloadLinkPar){
		this.downloadTestFile = downloadTestFilePar;
		this.downloadLink = downloadLinkPar;
	}
	
	@Override
	public void run() {

	    String downloadedFileAbsoluteLocation = "";

	    try {
			downloadedFileAbsoluteLocation = downloadTestFile.downloadFile(downloadLink);
		} catch (Exception e) {
			e.printStackTrace();
		}
	   System.out.println("Finalizado baixo do arquivo: " + downloadedFileAbsoluteLocation);
	
	}

	

}
