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
		String sDist =this.txtDistanzaInput.getText();
		
		if(sDist.equals("")){
			txtResult.appendText("ERRORE: Inserire una distanza massima\n");
			return;
		}
		
		int dist;
		try{
		 dist=Integer.parseInt(sDist);
		}catch(NumberFormatException e){
			txtResult.appendText("ERRORE: Inserire un numero \n");
			return;
		}
		
		model.creaGrafo(dist);
			
		txtResult.appendText("Aereoporto pi� lontano da Fiumicino "+model.getPiLontanoByVoloDiretto(1555).toString()+"\n");
		
	}
	 @FXML
	    void doRaggiungibili(ActionEvent event) {
	    	
	 
	    	Airline airline = boxAirline.getValue() ;
	    	Airport start = boxAirport.getValue() ;
	    	if(airline==null || start==null) {
	    		txtResult.appendText("Selezionare compagnia e aeroporto\n") ;
	    		return ;
	    	}
	    	
	    	List<AirportDistance> list = model.getDestinations(airline, start) ;
	    	
	    	txtResult.clear();
	    	txtResult.appendText("Distanze da "+start.getName()+"\n");
	    	for(AirportDistance ad: list)
	    		txtResult.appendText(String.format("%s (%.2f km) - %d steps\n", 
	    				ad.getAirport().getName(), ad.getDistance(), ad.getTratte()));
	    }

	    @FXML
	    void doServiti(ActionEvent event) {

	    	Airline airline = boxAirline.getValue() ;
	    	if(airline==null) {
	    		txtResult.appendText("Devi selezionare una compagnia aerea\n");
	    		return ;
	    	}
	    	
	    	// Popola la seconda tendina con gli aeroporti raggiungibili
	    	List<Airport> reachedAirports = model.getReachedAirports(airline) ;
	    	boxAirport.getItems().clear();
	    	boxAirport.getItems().addAll(reachedAirports);
	    	
	    	// Costruisci il grafo
	    	model.buildGraph(airline) ;

	    	// Stampa aeroporti raggiunti
	    	txtResult.clear();
	    	txtResult.appendText("Aeroporti raggiunti da "+airline.getName()+"\n");
	    	for(Airport a: reachedAirports) {
	    		txtResult.appendText(a.getName()+"\n") ;
	    	}
	    }
	
	    
//	    Airport a = boxAirport.getValue() ;
//    	if(airline==null) {
//    		txtResult.appendText("Devi selezionare una compagnia aerea\n");
//    		return ;
//    	}
//	    //connesso  & n di connessioni
//	      txtResult.appendText(model.isConnesso());	
//	      int conn=model.getNumberConn();
//			  txtResult.appendText("connessioni :" + conn +"\n");
//		 
//		//visita in ampiezza	  
//		 List<Airport> list=model.getRaggiungibiliInAmpiezza(d,s); 
//		    for(Airport driver:list){
//		        	txtResult.appendText(driver.toString()+"\n");
//		        }  
//		 //vicini	  
//		 List<Airport> vicini=model.trovaVicini(d); 
//			for(Airport d1:vicini){
//			        txtResult.appendText(d1.toString()+"\n");
//			        }  
//		//bellman & n di connessioni
//			String bellman = model.calcolaPercorsoBELLMAN(d); 
//			txtResult.appendText(bellman.toString()+"\n");
//			int connBELLMAN=model.getNumConn(d);
//				txtResult.appendText("connessioni Belllman:" + connBELLMAN +"\n");
//			         
//		}
//		//Airport best/worst
//		model.creaGrafo();
//		Airport bestD = model.getBestAirport();
//			txtResult.appendText("Airport migliore del " + s.getYear() + " � " + bestD.toString() + "\n");
//		Airport worstD = model.getWorstAirport();
//			txtResult.appendText("Airport peggiore del " + s.getYear() + " � " + worstD.toString() + "\n");
 	    
	    
	    
	    

	
	/**  @FXML
    void calcolaPercorso(ActionEvent event) {
  

		Airport c1 = BoxAirport.getValue();
		Airport c2 = BoxAirport.getValue();

		if (c1!= null && c2 != null) {

			if (!c1.equals(c2)) {

				try {
					// Calcolo il percorso tra le due stazioni
					model.getCamminoMinimo(c1, c2);

					// Ottengo il tempo di percorrenza
					int tempoTotaleInSecondi = (int) model.getPercorsoTempoTotale();
					int ore = tempoTotaleInSecondi / 3600;
					int minuti = (tempoTotaleInSecondi % 3600) / 60;
					int secondi = tempoTotaleInSecondi % 60;
					String timeString = String.format("%02d:%02d:%02d", ore, minuti, secondi);

					StringBuilder risultato = new StringBuilder();
					// Ottengo il percorso
					risultato.append(model.getPercorsoEdgeList());
					risultato.append("\n\nTempo di percorrenza stimato: " + timeString + "\n");

					// Aggiorno la TextArea
					txtResult.setText(risultato.toString());
					
				} catch (RuntimeException e) {
					txtResult.setText(e.getMessage());	}
			} 
		  else {			txtResult.setText("Inserire una stazione di arrivo diversa da quella di partenza.");}
	     }
	   else { txtResult.setText("Inserire una stazione di arrivo ed una di partenza.");}
	}
*/   
	
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
