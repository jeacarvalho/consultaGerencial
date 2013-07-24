package br.gov.serpro.siscomex.carga.log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import javax.swing.JOptionPane;
import org.apache.commons.net.ftp.*;


/*
 * Script de cria��o das tabelas no banco mysql
 * CREATE DATABASE `log`
    CHARACTER SET 'latin1'
    COLLATE 'latin1_swedish_ci';

CREATE TABLE `arquivoLeitura` (
  `nomearquivo` VARCHAR(60) COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  `linhaprocessada` INTEGER(10) UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY (`nomearquivo`)
)ENGINE=InnoDB
CHARACTER SET 'latin1' COLLATE 'latin1_swedish_ci'
COMMENT='InnoDB free: 64512 kB';

delimiter $$
CREATE TABLE `chamada` (
  `data` varchar(15) NOT NULL DEFAULT '',
  `hora` varchar(45) NOT NULL DEFAULT '',
  `entrada` varchar(300) NOT NULL DEFAULT '',
  `saida` varchar(300) NOT NULL DEFAULT '',
  `codigo_programa` varchar(45) NOT NULL DEFAULT '',
  `cpf` varchar(15) NOT NULL DEFAULT '',
  `codigo_transacao` varchar(45) NOT NULL DEFAULT '',
  `logon` varchar(45) NOT NULL DEFAULT '',
  `W0` varchar(5) NOT NULL DEFAULT '',
  `WA` varchar(5) NOT NULL DEFAULT '',
  `NAT` varchar(5) NOT NULL DEFAULT '',
  `seq_chamada` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `codretorno` varchar(6) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `linhaArquivo` int(11) NOT NULL,
  `nomeArquivo` varchar(60) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`seq_chamada`),
  UNIQUE KEY `linhaArquivo` (`linhaArquivo`,`nomeArquivo`)
) ENGINE=MyISAM AUTO_INCREMENT=2669750 DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 64512 kB'$$






CREATE TABLE `programa` (
  `codigo` VARCHAR(45) COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  `camposnome` VARCHAR(45) COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  `campostamanho` VARCHAR(45) COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`codigo`)
)ENGINE=InnoDB
CHARACTER SET 'latin1' COLLATE 'latin1_swedish_ci'
COMMENT='InnoDB free: 64512 kB';

CREATE TABLE `retornonatural` (
  `CodRetorno` VARCHAR(6) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `Descricao` VARCHAR(255) COLLATE latin1_general_ci DEFAULT NULL,
  PRIMARY KEY (`CodRetorno`)
)ENGINE=InnoDB
CHARACTER SET 'latin1' COLLATE 'latin1_general_ci'
COMMENT='InnoDB free: 64512 kB';


CREATE TABLE `transacao` (
  `codigo` VARCHAR(45) COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  `nome` VARCHAR(60) COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`codigo`)
)ENGINE=InnoDB
CHARACTER SET 'latin1' COLLATE 'latin1_swedish_ci'
COMMENT='InnoDB free: 64512 kB';


CREATE TABLE `usuario` (
  `cpf` VARCHAR(15) COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  `nome` VARCHAR(60) COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`cpf`)
)ENGINE=InnoDB
CHARACTER SET 'latin1' COLLATE 'latin1_swedish_ci'
COMMENT='InnoDB free: 64512 kB';
 * 
 * 
 * 
 */

/*
 * select COUNT(*), ENTRADA, DATA from chamada where
codigo_programa = 'O36173CH'
and saida =  'INDETERMINADO'
GROUP BY DATA, ENTRADA;

select COUNT(*), DATA from chamada where
codigo_programa = 'O36173CH'
and saida =  'INDETERMINADO'
GROUP BY DATA;


select COUNT(*), DATA from chamada where
codigo_programa = 'O36173CH'
and saida <>  'INDETERMINADO'
GROUP BY DATA;

select count(*), data from chamada
where codigo_programa = 'O36173CH'
group by DATA
 * 
 * 
 * 
 * 
 * Script cria��o stored procedures de ajuste e verificacao de erros nao tratados
 * 
 * 
 * select count(distinct cpf), data from chamada group by data (usuarios por dia)
 * 
 * DELIMITER $$
CREATE PROCEDURE `AjustaCodRetorno`()
BEGIN
	update chamada set codretorno = SUBSTR(saida, 1, 6)
	where codretorno = '';
END$$
DELIMITER ;

CREATE DEFINER = 'root'@'localhost' PROCEDURE `EstatisticaErrosTratados`()
    NOT DETERMINISTIC
    CONTAINS SQL
    SQL SECURITY DEFINER
    COMMENT ''
begin
    SELECT COUNT(*) as total, codigo_programa, chamada.codRetorno, r.descricao, data  
    from chamada 
    left  join retornonatural r on chamada.CodRetorno = r.CodRetorno 
    where chamada.CodRetorno <> '000000'
    and (chamada.CodRetorno BETWEEN 'A' and 'Z')
    group by codigo_programa, codRetorno, data
    order by data desc, total desc;
end;

DELIMITER $$
CREATE PROCEDURE `VerificaErrosNaoTratados`()
BEGIN
     select codigo_programa, WA, W0, NAT, Entrada, CodRetorno, saida, SUBSTRING(saida, 4, 4) as CodErrNatural, SUBSTRING(saida, 8, 4)as Linha, SUBSTRING(saida, 12, 9) as ProgramaErro , SUBSTRING(saida, 4, 17),  codigo_transacao, cpf, data, hora
     from chamada
     where logon = 'G36173'
     and not (CodRetorno BETWEEN 'A' and 'Z')
     and CodRetorno <> '000000'
     and data = Hoje()
	and SUBSTRING(saida, 4, 4) <> '3145'
     order by data desc, hora desc;
END$$
DELIMITER ;

DELIMITER $$
CREATE FUNCTION `Hoje`()
    RETURNS char(8) CHARSET latin1
Begin
	DECLARE Saida char(08);
	set saida = CONCAT(SUBSTRING(now(), 1, 4), SUBSTRING(now(), 6, 2), SUBSTRING(now(), 9, 2));
    return Saida;
end$$
DELIMITER ;



select count(*), substr(hora, 1,2) as HoraCheia, data
from chamada where logon = 'G36173'
group by data, HoraCheia
order by data, HoraCheia


ls 

 * 
 */

public class Principal {

	public static void main(String[] args) {
		ConcreteCreatorLeitor cc  = new ConcreteCreatorLeitor();

		/*
		 * O c�digo abaixo � uma tentativa de tornar a opera��o desse software automatizada. O que temos
		 * � uma leitura direta ao log do servidor via ftp.
		 * A ideia era fazer com que a rotina executesse de 30 em 30 minutos, baixando os arquivos atualizados
		 * no servidor e gerando as entradas no banco de dados sql.
		 * Posteriormente, poder�amos expandir para, ao verificar-se algum erro n�o tratado,
		 * um email ser enviado ao analista respons�vel.
		 * Entretanto, por quest�es de seguran�a
		 * 
		 */
		
//		FTPClient ftp = new FTPClient();
//		String server = "10.3.84.19"; 
//		//String server = "10.3.5.1";
//	    try {
//	    	int reply;
//			ftp.connect( server );
//			ftp.login( "DU02829", "DU02829" );
//			System.out.println("Connected to " + server + ".");
//			System.out.print(ftp.getReplyString());
//			reply = ftp.getReplyCode();
//
//			if(!FTPReply.isPositiveCompletion(reply)) {
//				ftp.disconnect();
//				System.err.println("FTP server refused connection.");
//				System.exit(1);
//			}			      
//			
//			String directory = "/log/was6-hom/HHCELL.HHNODEH.HHS02H";
//			FTPFile[] files;
//			if (ftp.changeWorkingDirectory(directory)){
//				files = ftp.listFiles();
//				for (int i = 0; i < files.length; i++) {
//					System.out.println(files[i].getName() + " atualizado em: " + files[i].getTimestamp().getTimeInMillis());
//					System.out.println(files[i].getName() + " atualizado em: " + files[i].getTimestamp().getTime());
//					
//					if("dia080420.hora013601".equals(files[i].getName())){
//						File f = new File("c:\\logServidorParaProcessamento\\" + files[i].getName());
//						OutputStream fileSaida = new FileOutputStream(f);
//						ftp.retrieveFile(files[i].getName(), fileSaida);
//					}
//					
//					
//				}
//			}
//			
//		} catch (SocketException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		

		String caixa = JOptionPane.showInputDialog(null,"Insira o nome do arquivo",JOptionPane.NO_OPTION);
				
		LeitorLog leitor = cc.factoryMethod(caixa);
		leitor.preparaLeituraThread();
		
		
		
		
		

		
		
		//leitor.validaConfig();

	}

}
