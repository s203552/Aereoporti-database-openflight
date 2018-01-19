package it.polito.tdp.flight;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.flight.model.Airline;
import it.polito.tdp.flight.model.Airport;
import it.polito.tdp.flight.model.AirportAndNum;
import it.polito.tdp.flight.model.AirportDistance;
import it.polito.tdp.flight.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FlightController {

	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField txtDistanzaInput;

	@FXML
	private TextField txtPasseggeriInput;

	@FXML
	private ComboBox<Airline> boxAirline;

	@FXML
	private ComboBox<Airport> boxAirport;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCreaGrafo(ActionEvent event) {
		
		txtResult.clear();
		
	/** SCELTA O INSERIMENTO  */
		
	//INSERIMENTO STINGA DA TRASFORMARE IN INT
		String sDist =this.txtDistanzaInput.getText();		
		if(sDist.equals("")){
			txtResult.appendText("ERRORE: Inserire una distanza massima\n");
			return;
		}	
		//trasformo in int
		int dist;
		try{
		 dist=Integer.parseInt(sDist);
		}catch(NumberFormatException e){
			txtResult.appendText("ERRORE: Inserire un numero \n");
			return;
		}
		
		
	//SCEGLIERE DA COMBOBOX AIRLINE E AIRPORT
    	Airline airline = boxAirline.getValue() ;
    	if(airline==null ) {
    		txtResult.appendText("Selezionare compagnia \n") ;
    		return ;
    	}
    	Airport start = boxAirport.getValue() ;
    	if(start==null) {
    		txtResult.appendText("Selezionare aeroporto \n") ;
    		return ;
    	}
	//	-------------
		
	//richiamo metodo void----> la stampa la devo fare nel model con una stringa o system.out.println(graph);
		model.creaGrafo(dist);

	//richiamo metodo String
		model.isConnesso();
	
	//Stampo metodi LONTANO
		txtResult.appendText("Aereoporto pi� lontano da LosAngeles non raggiungibile "+model.getLontanissimo(3484).toString()+"\n");
		txtResult.appendText("Aereoporto pi� lontano da Fiumicino "+model.getPiLontano(1555).toString()+"\n");
		txtResult.appendText("Aereoporto pi� lontano da Fiumicino con volo diretto "
										+ model.getPiLontanoByVoloDiretto(1555).toString()+"\n");
		
	
	//RICHIAMO LISTA PER POI STAMPARLA 
		
	    	List<AirportDistance> list = model.getDestinations(airline, start) ;
	     	txtResult.clear();
	    	txtResult.appendText("Distanze da "+start.getName()+"\n");
	    	for(AirportDistance ad: list)
	    		txtResult.appendText(String.format("%s (%.2f km) - %d steps\n", 
	    				ad.getAirport().getName(), ad.getDistance(), ad.getTratte()));

   // STAMPA AEREOPORTI RAGGIUNTI
	    	
	    	txtResult.clear();
	    	List<Airport> reachedAirports = model.getReachedAirports(airline) ;
	    	txtResult.appendText("Aeroporti raggiunti da "+airline.getName()+"\n");
	    	for(Airport a: reachedAirports) {
	    		txtResult.appendText(a.getName()+"\n") ;
	    	}
       
   }    	
	@FXML
	void initialize() {
		assert txtDistanzaInput != null : "fx:id=\"txtDistanzaInput\" was not injected: check your FXML file 'Untitled'.";
		assert txtPasseggeriInput != null : "fx:id=\"txtPasseggeriInput\" was not injected: check your FXML file 'Untitled'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Untitled'.";

	}

	public void setModel(Model model) {
		this.model = model;
		boxAirline.getItems().addAll(model.getAllAirlines()) ;
		boxAirport.getItems().addAll(model.getAllAirports()) ;

	}
}
