package it.polito.tdp.flight.model;
public class Collegati{
	private Airport source;
	private Airport destination;
	private double distanza;
	public Collegati(Airport source, Airport destination, double distanza)
	{	super();
		this.source = source;
		this.destination = destination;
		this.distanza = distanza;
	}
	public Airport getSource()	{		return source;	}
	public void setSource(Airport source)	{		this.source = source;	}
	public Airport getDestination()	{		return destination;	}
	public void setDestination(Airport destination)	{		this.destination = destination;	}
	public double getDistanza()	{		return distanza;	}
	public void setDistanza(double distanza){	this.distanza = distanza;}
	@Override
	public String toString() {
		return "Collegati [source=" + source + ", destination=" + destination + ", distanza=" + distanza + "]";
	}

}
