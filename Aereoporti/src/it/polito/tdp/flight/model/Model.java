package it.polito.tdp.flight.model;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flight.db.FlightDAO;

public class Model {
	
	private List<Airport> airport;
	private List<Integer> IDairportbyAirline;
	private List<Airport> esclusi;
	private List<Airport> reachedAirports;
	
	private Map<Integer,Airport> airportMap;
	private Map<Integer,Airline> airlineMap;
	private Airline myAirline;
	private List<Airline> airlines;
	private List<Route> routes;
	private FlightDAO dao;
	private SimpleDirectedWeightedGraph<Airport,DefaultWeightedEdge> graph;
	private ConnectivityInspector<Airport,DefaultWeightedEdge> ci;

	
	public Model() {
		super();
		dao=new FlightDAO();
		


		if(this.routes==null)	routes=dao.getAllRoutes();	

		/* popolo  map nel costruttore del model--> la mappa serve come controllo per non creare 2 oggetti uguali 
		 nella stessa memoria.	 se ce l'ho già prendo quello non ne creo un altro. il problema si crea solo
		  quando aggiungo attributi	all'oggetto padre  */
		
		if(this.airport==null){
			airport=dao.getAllAirports();	
		// populate a map AirportId->Airport
			this.airportMap=new HashMap<>();
			for(Airport atemp:airport){
			this.airportMap.put(atemp.getAirportId(), atemp);
			}			
			esclusi=dao.getAirportNoRoute(airportMap);
		}	

		if(this.airlines==null)	airlines=dao.getAllAirlines();	
		// populate a map AirlineId->Airline
				this.airlineMap = new HashMap<>();
				for (Airline a : airlines)
					airlineMap.put(a.getAirlineId(), a);

	}
		
	public List<Airport> getAllAirports(){
		return airport;
	}
	
	public List<Airport> getAllReachedAirport(Airline air){
		if(this.reachedAirports==null)	reachedAirports=dao.getAeroportiRaggiunti(air);	
		return reachedAirports;
	}
	public List<Integer> getIDAirportbyAirline(Airline a){
		if(this.IDairportbyAirline==null)	IDairportbyAirline=dao.getReachedAirportsID(a);	
		return IDairportbyAirline;
	}
	
	public List<Airport> getAllAirportsCountry(String country){
	if(this.airport==null)
		airport=dao.getAllAirportsCountry(country);	
	// populate a map AirportId->Airport
		this.airportMap=new HashMap<>();
		for(Airport atemp:airport){
		this.airportMap.put(atemp.getAirportId(), atemp);
		}
	 return airport;
	}
	
	public List<Route> getAllRoutes(){
		return routes;
	}
	public List<Airline> getAllAirlines() {	
		return airlines;
	}
	
	/**
	 * Permettere all’utente di selezionare una compagnia aerea dall’elenco di tutte le compagnie disponibili. In
	 * seguito, costruire un grafo, i cui vertici rappresentino tutti gli aeroporti, e la presenza di un arco (orientato)
	 * indica l’esistenza di almeno una rotta diretta tenuta dalla compagnia selezionata tra i due aeroporti
	 * adiacenti all’arco stesso. Il peso dell’arco deve misurare la distanza in linea d’aria tra i due aeroporti,
	 * espressa in kilometri. 
	 * Infine, si stampi l’elenco degli aeroporti raggiunti dalla compagnia aerea.
	 */
	
	public void buildGraph(Airline airline) {
		
			String s="";
			if (graph==null) this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

			Graphs.addAllVertices(graph, this.getAllAirports());
			System.out.println("Grafo creato: " + graph.vertexSet().size() + " nodi");	

			FlightDAO dao = new FlightDAO();
			//prendo tutte le rotte che fa quella compagnia 
			List<Route> routes = dao.getRoutesByAirline(airline);
			//per ogni rotta prendo gli aereoporti di destinazione e partenza
			for (Route r : routes) {
				if (r.getSourceAirportId() != 0 && r.getDestinationAirportId() != 0) {
					//il peso lo devo calcolare sugli id degli aereoporti che ottengo grazie alla mappa di aereoporti
					Airport a1 = airportMap.get(r.getSourceAirportId());
					Airport a2 = airportMap.get(r.getDestinationAirportId());
	
					if (a1 != null && a2 != null) {	
						LatLng c1 = new LatLng(a1.getLatitude(), a1.getLongitude());
						LatLng c2 = new LatLng(a2.getLatitude(), a2.getLongitude());
						double distance = LatLngTool.distance(c1, c2, LengthUnit.KILOMETER);
	
						Graphs.addEdge(graph, a1, a2, distance);
						// System.out.format("%s->%s %.0fkm\n", a1, a2, distance);
						//stampo
						s+= a1 + "  "+a2 + "   "+ distance +"\n";
					}
				}
			}
			System.out.println("Grafo creato: " + graph.vertexSet().size() + " nodi, " + graph.edgeSet().size() + " archi");	
			System.out.println(s);	
		}
	
	
	/**
	 * Facendo click sul pulsante “Seleziona Rotte”, costruire un grafo semplice, orientato e pesato i cui nodi siano
	 * gli aeroporti. Un arco collega due aeroporti solo se la loro distanza è inferiore ai chilometri selezionati ed
	 * almeno una compagnia aerea compie tale rotta. Il peso dell’arco è pari alla durata del volo, ipotizzando una
	 * velocità costante di crociera a 800 km/h. [Si trascurino il momento del decollo e dell’atterraggio.]
	 */
	
	public void creaGrafo(int maxDistance){  //(int maxDistance,String country)
		
		String s="";
		if (graph==null)  graph= new SimpleDirectedWeightedGraph<Airport,DefaultWeightedEdge>(DefaultWeightedEdge.class);	
		Graphs.addAllVertices(graph, this.getAllAirports());
		//Graphs.addAllVertices(graph, this.getAllAirportsCountry(country));
	    graph.removeAllVertices(esclusi);
	    
		System.out.println("Grafo creato: " + graph.vertexSet().size() + " nodi");	
		
		//start time
		long time0 = System.nanoTime() ;
	    
		for(Route rtemp: this.getAllRoutes()){
			//il peso lo devo calcolare sugli id degli aereoporti che ottengo grazie alla mappa di aereoporti
			Airport source=this.airportMap.get(rtemp.getSourceAirportId());
			Airport destination=this.airportMap.get(rtemp.getDestinationAirportId());
				
			if(source!=null && destination !=null && !source.equals(destination)){
				//peso	
				double distance=LatLngTool.distance(new LatLng(source.getLatitude(),source.getLongitude()),
							new LatLng(destination.getLatitude(),destination.getLongitude()),LengthUnit.KILOMETER);
				// time= spazio/velocita  velocita = Spazio/tempo
			
					if(distance<=maxDistance){
						DefaultWeightedEdge e=graph.addEdge(source, destination);
						if(e!=null)
							graph.setEdgeWeight(e, distance/800);
						//stampo
						s+= source + "  "+destination + "   "+ distance +"\n";
						}	
				}
			}
			//end time
			long time1 = System.nanoTime() ;
			System.out.println("Grafo creato: " + graph.vertexSet().size() + " nodi, " + graph.edgeSet().size() + " archi");	
			//System.out.println(s);
			System.out.println("Tempo esecuzione grafo: " + (time1-time0)/1000000 + "  MilliSecondi");	
			
		}
	
	
	/* Escludendo gli aeroporti con zero rotte, determinare se nel grafo ottenuto è possibile da ogni aeroporto
	raggiungere ogni altro aeroporto.
	----->>> fortemente connesso?  */	
	
	public String isConnesso () {
		ci = new ConnectivityInspector<>(this.graph);
	    if (ci.isGraphConnected()){ return "Il grafo--> fortemente connesso";	 }
		else return "il grafo---> non fortemente connesso";
	}
	
	//stampare l’aeroporto raggiungibile da “Fiumicino” più lontano da “Fiumicino” stesso.
	
	 public Airport getPiLontano(int fiumicinoId){
		 //aereoporto pi� vicino tra quelli raggiungibili anche facendo scali
		 Airport fiumicino= this.airportMap.get(fiumicinoId);
		 Airport lontano=fiumicino;
		 Double distance=0.0;
		ConnectivityInspector<Airport,DefaultWeightedEdge> ci=new ConnectivityInspector<>(graph);
		 for(Airport atemp :ci.connectedSetOf(fiumicino)){
			 DijkstraShortestPath<Airport,DefaultWeightedEdge> djk =new DijkstraShortestPath<>(graph,fiumicino,atemp);
			 if(djk.getPathLength()>distance){
				 distance=djk.getPathLength();
				 lontano=atemp;
			 }
			 
		 }
		 return lontano;
		 
	 }
	 
	 public Airport getPiLontanoByVoloDiretto(int fiumicinoId){
		 //aereoporto pi� vicino tra quelli raggiungibili con voli diretti
		 Airport fiumicino= this.airportMap.get(fiumicinoId);
		 Airport lontano=fiumicino;
		 Double distance=(double) Integer.MIN_VALUE;
		 for(DefaultWeightedEdge e: graph.outgoingEdgesOf(fiumicino)){
			 if(distance<graph.getEdgeWeight(e)){
				 distance=graph.getEdgeWeight(e);
				 lontano=graph.getEdgeTarget(e);
			 }			 
		 }
		 return lontano;
		 
	 }
	
	// stampare l’aeroporto NON RAGGIUNGIBILE da “Los Angeles Intl” più lontano da “Los Angeles Intl” stesso.
	
	 public Airport getLontanissimo(int losAngeles){
			Airport los=this.airportMap.get(losAngeles);
			Airport lontano=null;
			List<Airport> nonRaggiungibili=new ArrayList<>();
			nonRaggiungibili.addAll(graph.vertexSet());
			//tolgo quelli raggiungibili (connessi)
			ConnectivityInspector<Airport,DefaultWeightedEdge> ci=new ConnectivityInspector<>(graph);
			nonRaggiungibili.removeAll(ci.connectedSetOf(los));
		
			double maxDistance=Integer.MIN_VALUE;
			for(Airport atemp: nonRaggiungibili){
				if(!atemp.equals(los)){
				double d=LatLngTool.distance(new LatLng(atemp.getLatitude(),atemp.getLongitude()),
						new LatLng(los.getLatitude(),los.getLongitude()) , LengthUnit.KILOMETER);
				if(d>maxDistance){
					lontano=atemp;
					maxDistance=d;
				}
				}
			}			
			return lontano;			
		}

	//get raggiungibili	
		
		public List<Airport> getReachedAirports(Airline airline) {  //reached=raggiunti
				if (this.myAirline == null || !this.myAirline.equals(airline)) {
					this.myAirline = airline;
					
					FlightDAO dao = new FlightDAO();
				//prendo lista e trasformo in mappa	
					List<Integer> airportIds = dao.getReachedAirportsID(this.myAirline);				
					this.reachedAirports = new ArrayList<Airport>();
					for (Integer id : airportIds)
						this.reachedAirports.add(airportMap.get(id));
				//ordino per nome
					this.reachedAirports.sort(new Comparator<Airport>() {
						@Override
						public int compare(Airport o1, Airport o2) {
							return o1.getName().compareTo(o2.getName());
						}
					});

				}
				return this.reachedAirports;
			}	

		
		//get raggiungibili QUERY
		public List<Airport> getAeroportiRaggiunti(Airline air){
			List<Airport>a=dao.getAeroportiRaggiunti(air);
			a.sort(new Comparator<Airport>() {
				@Override
				public int compare(Airport o1, Airport o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

			return a;
		}	
		
	/*Si permetta all’utente di selezionare un aeroporto tra quelli raggiunti dalla compagnia aerea, e determinare
		tutti gli aeroporti da esso raggiungibili con viaggi di una o più tratte. L’elenco deve essere ordinato per
		distanza crescente (in km) rispetto all’aeroporto di partenza. */
	 
	public List<AirportDistance> getDestinations(Airline airline, Airport start) {

			List<AirportDistance> list = new ArrayList<>();
			for (Airport end : this.getAllReachedAirport(airline)) {
				DijkstraShortestPath<Airport, DefaultWeightedEdge> dsp = new DijkstraShortestPath<>(graph, start, end);
				GraphPath<Airport, DefaultWeightedEdge> p = dsp.getPath();
				if (p != null) {
					list.add(new AirportDistance(end, p.getWeight(), p.getEdgeList().size()));
				}
			}
			list.sort(new Comparator<AirportDistance>() {
				@Override
				public int compare(AirportDistance o1, AirportDistance o2) {
					return Double.compare(o1.getDistance(), o2.getDistance());
				}
			});
		return list;
		}
	 
	


	 


	
	/**		 * 		test model		 * 		 * 		 * 			 * 		 */		
	
	public static void main(String[] args) {
			
			Model model = new Model();
			model.creaGrafo(500) ;
			
			Airline ar= new Airline(3463); 
			// 2297  easyjet
			// 3463 meridiana
			Airport a= new Airport(1555);
            //1555 fiumicino
			//3484 losAngeles
			
			System.out.println("\n");
			System.out.println("-------- Aereoporto Più Lontano ---------");
			System.out.println("\n");
			
			Airport n =model.getPiLontano(1555);
			System.out.println(n);
			
			System.out.println("\n");
			System.out.println("------- Aereoporto Più Lontano per volo diretto ----------");
			System.out.println("\n");
			
			Airport l =model.getPiLontanoByVoloDiretto(1555);
			System.out.println(l);	
			
			System.out.println("\n");
			System.out.println("------- Aereoporto Più Lontano non Raggiungibile ----------");
			System.out.println("\n");
			
			Airport  s=model.getLontanissimo(3484);
			System.out.println(s);
			
			System.out.println("\n");
			System.out.println("-------- connesso? ---------");
			System.out.println("\n");
						
			System.out.println(model.isConnesso());
			
			System.out.println("\n");
			System.out.println("------- Aereoporti di destinazione e distanza ----------");
			System.out.println("\n");
			
			List<AirportDistance>  ad=model.getDestinations(ar, a);
			System.out.println(ad);
			
			System.out.println("\n");
			System.out.println("------- Aereoporti raggiungibili ----------");
			System.out.println("\n");
			
			List<Airport>  Ra=model.getReachedAirports(ar);
			System.out.println(Ra);
			
			System.out.println("\n");
			System.out.println("------- Aereoporti raggiungibili QUERY ----------");
			System.out.println("\n");
			
			List<Airport> AR= model.getAeroportiRaggiunti(ar);
			System.out.println(AR);

		}

	
}	
	
	
	
	


