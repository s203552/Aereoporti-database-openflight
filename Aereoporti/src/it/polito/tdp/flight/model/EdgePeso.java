package it.polito.tdp.flight.model;
import org.jgrapht.graph.DefaultWeightedEdge;

public  class EdgePeso implements Comparable<EdgePeso>{
	
	private DefaultWeightedEdge e;
	private Double p;
	
	public EdgePeso(DefaultWeightedEdge e, Double p) {
		super();
		this.e = e;
		this.p = p;
	}
	public DefaultWeightedEdge getE() {
		return e;
	}
	public void setE(DefaultWeightedEdge e) {
		this.e = e;
	}
	public Double getP() {
		return p;
	}
	public void setP(Double p) {
		this.p = p;
	}
	@Override
	public String toString() {
		return  e + ", p=" + p ;
	}
	
	//COMPARE TO---> ordine decrescente peso
	@Override
	public int compareTo( EdgePeso o2) {
		return  (int) (this.getP()-o2.getP());
	}
	
}
