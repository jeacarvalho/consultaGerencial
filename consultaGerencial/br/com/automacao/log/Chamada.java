package br.com.automacao.log;

public class Chamada {

	private String data;
	private String hora;
	private String entrada;
	private String saida;
	private String logon;
	private Usuario usuario;
	private Programa programa;
	private Transacao transacao;
	private String W0;
	private String WA;
	private String NAT;
	private String linhaArquivo="";
	private String nomeArquivo="";
	
	
	
	public String getNomeArquivo() {
		return nomeArquivo;
	}
	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo.substring(nomeArquivo.indexOf("dia"), nomeArquivo.length());
	}
	public String getW0() {
		return W0;
	}
	public void setW0(String w0) {
		W0 = w0;
	}
	public String getWA() {
		return WA;
	}
	public void setWA(String wa) {
		WA = wa;
	}
	public String getNAT() {
		return NAT;
	}
	public void setNAT(String nat) {
		NAT = nat;
	}
	public String getLogon() {
		return logon;
	}
	public void setLogon(String logon) {
		this.logon = logon;
	}
	public String getData() {
		try{
			String ano = data.substring(4, 8);
			String mes = data.substring(2, 4);
			String dia = data.substring(0, 2);
			return ano+mes+dia;			
		}catch(Exception e){
			System.out.println(data + "linha: " + linhaArquivo);
			e.printStackTrace();
			return "ERRO";
		}

	}
	public void setData(String data) {
		this.data = data;
	}
	public String getEntrada() {
		return entrada;
	}
	public void setEntrada(String entrada) {
		this.entrada = entrada;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public String getSaida() {
		
		return saida.replace("'", " ");
	}
	public void setSaida(String saida) {
		this.saida = saida;
	}
	public Programa getPrograma() {
		return programa;
	}
	public void setPrograma(Programa programa) {
		this.programa = programa;
	}
	public Transacao getTransacao() {
		return transacao;
	}
	public void setTransacao(Transacao transacao) {
		this.transacao = transacao;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public String getLinhaArquivo() {
		return linhaArquivo;
	}
	public void setLinhaArquivo(String linhaArquivo) {
		this.linhaArquivo = linhaArquivo;
	}

}
