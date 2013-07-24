package br.gov.serpro.automacao.apoiowas;

import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import utilitarios.UtilGestaoUnidade;

import br.gov.serpro.siscomex.carga.log.UtilitarioLeituraLogAutomatizado;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

public class AbreApoioDiaAnteriorSilent {
	
	public static WebDriver driver;
	public static String baseUrl;
	public static String CAMINHO_LOG = UtilGestaoUnidade.getInstanciaUtilitario().getHome() + "/apoio/FilesToday"; 
	
	public static void main(String[] args) {
/*	    FirefoxProfile firefoxProfile = new FirefoxProfile();
	    
	    
	    firefoxProfile.setPreference("browser.download.folderList",2);
	    firefoxProfile.setPreference("browser.download.manager.showWhenStarting",false);
	    firefoxProfile.setPreference("browser.download.dir", CAMINHO_LOG);
	    firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk","text/text");
*/
	    System.out.println("Iniciando log no Apoio");
	    
		//driver = new FirefoxDriver(firefoxProfile);
	    
	    
		driver = new HtmlUnitDriver() {

			@Override
			protected WebClient newWebClient(BrowserVersion version) {

				WebClient webClient = super.newWebClient(version);

				// Necessário para continuar quando ocorre exception no javascript
        		webClient.setThrowExceptionOnScriptError(false);
        		webClient.setThrowExceptionOnFailingStatusCode(false);

                return webClient;

            }
        };

		((HtmlUnitDriver) driver).setJavascriptEnabled(true);

		
		//baseUrl = "https://www4.receita.fazenda/ApoioWAS/LogonApoioWAS";
		baseUrl = "http://www.apoiowas.serpro/";
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.get(baseUrl + "/");
		driver.findElement(By.linkText("Intranet e Internet - LPAR K")).click();
		//driver.findElement(By.name("cpfTemp")).clear();
		driver.findElement(By.name("cpfTemp")).sendKeys(UtilGestaoUnidade.getInstanciaUtilitario().getUsuario());
		driver.findElement(By.name("senhaTemp")).clear();
		driver.findElement(By.name("senhaTemp")).sendKeys(UtilGestaoUnidade.getInstanciaUtilitario().getPassUsuario());
		driver.findElement(By.name("Image7")).click();
		driver.get("https://www4.receita.fazenda/ApoioWAS/ListFiles?dirName=%2Flog%2Fwas6-prd%2FKPCELL.KPNODE.KPS40K");

		GregorianCalendar calendar = new GregorianCalendar();  
		
		int mes = calendar.get(GregorianCalendar.MONTH) +1;
		String mesString;
		if(mes <= 9){
			mesString = "0" + Integer.toString(mes);
		}else{
			mesString = Integer.toString(mes);
		}
			
		
		int ano = calendar.get(GregorianCalendar.YEAR);
		String anoString = Integer.toString(ano).substring(2, 4);
		
		int dia = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
		String diaString;
		if(dia <= 9){
			diaString = "0" + Integer.toString(dia);
		}else{
			diaString = Integer.toString(dia);
		}

		UtilitarioLeituraLogAutomatizado.nucleoProcessamentoLeitura(mesString, anoString, diaString, driver);	
		
	}

	
}