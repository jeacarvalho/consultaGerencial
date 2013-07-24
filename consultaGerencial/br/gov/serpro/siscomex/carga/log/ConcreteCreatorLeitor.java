package br.gov.serpro.siscomex.carga.log;

public class ConcreteCreatorLeitor implements CreatorLeitor{

	/**
	 * @param args
	 */
	public LeitorLog factoryMethod(String nomeArq){
		return new LeitorLogServidor(nomeArq);

	}

}
