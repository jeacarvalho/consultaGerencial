package br.gov.serpro.siscomex.carga.log;

public interface LeitorLog {

	//public boolean atualizarBanco();
	public boolean atualizarBancoInternet();
	public boolean atualizarBancoInternet(int linhaIncial, int linhaFinal);
	public boolean atualizarBancoDescRetNatural();
	//public boolean validaConfig();
	public boolean preparaLeituraThread();
	
	
}
