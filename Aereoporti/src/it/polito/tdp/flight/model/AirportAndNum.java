package it.polito.tdp.flight.model;
public class AirportAndNum implements Comparable<AirportAndNum>{
	Airport airport;
	int num;
	public AirportAndNum(Airport airport, int num) {
		super();
		this.airport = airport;
		this.num = num;
	}
	public Airport getAirport() {		return airport;	}
	public void setAirport(Airport airport) {		this.airport = airport;	}
	public int getNum() {		return num;	}
	public void setNum(int num) {		this.num = num;	}
	@Override
	public int compareTo(AirportAndNum o) {		
		return -(this.num-o.getNum());	}
	@Override
	public String toString() {
		return "AirportAndNum [airport=" + airport + ", num=" + num + "]";}
	
}
