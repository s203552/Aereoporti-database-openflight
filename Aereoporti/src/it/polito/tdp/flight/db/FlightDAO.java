package it.polito.tdp.flight.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flight.model.Airline;
import it.polito.tdp.flight.model.Airport;
import it.polito.tdp.flight.model.Collegati;
import it.polito.tdp.flight.model.Route;

public class FlightDAO {

	//tutte le compagnie
		
	public List<Airline> getAllAirlines() {
			String sql = "SELECT * FROM airline";
			List<Airline> list = new ArrayList<>();
			try {
				Connection conn = DBConnect.getConnection();
				PreparedStatement st = conn.prepareStatement(sql);
				ResultSet res = st.executeQuery();
	
				while (res.next()) {
					list.add(new Airline(res.getInt("Airline_ID"), res.getString("Name"), res.getString("Alias"),
							res.getString("IATA"), res.getString("ICAO"), res.getString("Callsign"),
							res.getString("Country"), res.getString("Active")));
				}
				conn.close();
				return list;
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
	
	//tutte le rotte
		
	public List<Route> getAllRoutes() {
			String sql = "SELECT * FROM route";
			List<Route> list = new ArrayList<>();
			try {
				Connection conn = DBConnect.getConnection();
				PreparedStatement st = conn.prepareStatement(sql);
				ResultSet res = st.executeQuery();
	
				while (res.next()) {
					list.add(new Route(res.getString("Airline"), res.getInt("Airline_ID"), res.getString("Source_airport"),
							res.getInt("Source_airport_ID"), res.getString("Destination_airport"),
							res.getInt("Destination_airport_ID"), res.getString("Codeshare"), res.getInt("Stops"),
							res.getString("Equipment")));
				}
				conn.close();
				return list;
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
		
	//tutti gli aereoporti
		
	public List<Airport> getAllAirports() {
			String sql = "SELECT * FROM airport";
			List<Airport> list = new ArrayList<>();
			try {
				Connection conn = DBConnect.getConnection();
				PreparedStatement st = conn.prepareStatement(sql);
				ResultSet res = st.executeQuery();
	
				while (res.next()) {
					list.add(new Airport(res.getInt("Airport_ID"), res.getString("name"), res.getString("city"),
							res.getString("country"), res.getString("IATA_FAA"), res.getString("ICAO"),
							res.getDouble("Latitude"), res.getDouble("Longitude"), res.getFloat("timezone"),
							res.getString("dst"), res.getString("tz")));
				}
				conn.close();
				return list;
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
		
	//Oggetto aereoporto da ID
		
	public Airport getAirportById(int id)
		{
			String sql = "select * from airport where airport_id = ?";
			Airport a = null;
			try
			{
				Connection conn = DBConnect.getConnection();
				PreparedStatement st = conn.prepareStatement(sql);
				st.setInt(1, id);
				ResultSet res = st.executeQuery();
	
				if (res.next())
				{
					a = new Airport(res.getInt("Airport_ID"),
							res.getString("name"),
							res.getString("city"),
							res.getString("country"),
							res.getString("IATA_FAA"),
							res.getString("ICAO"),
							res.getDouble("Latitude"),
							res.getDouble("Longitude"), 
							res.getFloat("timezone"),
							res.getString("dst"), 
							res.getString("tz"));
				}	
				conn.close();
				return a;
			} 
			catch (SQLException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
	//tutti gli aereoporti che non hanno rotte
		
	public List<Airport> getAirportNoRoute(Map<Integer,Airport> map){
			String sql = "select airport.Airport_ID from airport "
					+ "where airport.Airport_ID not in(select distinct route.Source_airport_ID from route ) "
					+ "and airport.Airport_ID not in (select distinct route.Destination_airport_ID  from route)";
			
			List<Airport> list = new ArrayList<>();
			try {
				Connection conn = DBConnect.getConnection();
				PreparedStatement st = conn.prepareStatement(sql);
				ResultSet res = st.executeQuery();
	
				while (res.next()) {
					list.add(map.get(res.getInt("Airport_ID")));
				}
				conn.close();
				return list;
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
		
	//num di aereoporti raggiungibili
		
	public List<Integer> getReachedAirportsID(Airline myAirline) {
			String sql = "select distinct AirportId from ( " + 
															"select distinct r1.Source_airport_ID as AirportId " + 
															"from route r1 where r1.Airline_ID=? " + 
															"union " + 
															"select distinct r2.Destination_airport_ID as AirportId " + 
															"from route r2 where r2.Airline_ID=? " + 
															") as ports" ;
			List<Integer> list = new ArrayList<>() ;		
			try {
				Connection conn = DBConnect.getConnection() ;
				PreparedStatement st = conn.prepareStatement(sql) ;			
				st.setInt(1, myAirline.getAirlineId());
				st.setInt(2, myAirline.getAirlineId());			
				ResultSet res = st.executeQuery() ;				
				while(res.next()) {
					list.add( res.getInt("AirportID")) ;
				}			
				conn.close();			
				return list ;
			} catch (SQLException e) {
	
				e.printStackTrace();
				return null ;
			}
		}
		
	//tutte le rotte di una compagnia
		
	public List<Route> getRoutesByAirline(Airline airline) {
			String sql ="select * from route where Airline_ID=?" ;
	
			List<Route> list = new ArrayList<>() ;			
			try {
				Connection conn = DBConnect.getConnection() ;
				PreparedStatement st = conn.prepareStatement(sql) ;
				st.setInt(1, airline.getAirlineId());
				ResultSet res = st.executeQuery() ;
				while(res.next()) {
					list.add( new Route(
							res.getString("Airline"),
							res.getInt("Airline_ID"),
							res.getString("source_airport"),
							res.getInt("source_airport_id"),
							res.getString("destination_airport"),
							res.getInt("destination_airport_id"),
							res.getString("codeshare"),
							res.getInt("stops"),
							res.getString("equipment"))) ;
				}				
				conn.close();				
				return list ;
			} catch (SQLException e) {
				e.printStackTrace();
				return null ;
			}	
		}
		
	//tutti gli aereoporti raggiunti da quella compagnia
		
	public List<Airport> getAeroportiRaggiunti(Airline air)
		{
			String sql = "select distinct r.destination_airport_id "
					+ "from airport a, airline air, route r "
					+ "where air.airline_id = r.airline_id "
					+ "and r.destination_airport_id = a.airport_id "
					+ "and air.airline_id = ?";
	
			List<Airport> list = new ArrayList<>();
			try
			{
				Connection conn = DBConnect.getConnection();	
				PreparedStatement st = conn.prepareStatement(sql);				
				st.setInt(1, air.getAirlineId());	
				ResultSet res = st.executeQuery();
				System.out.println("Entro");
				while (res.next())
				{
					int id = Integer.parseInt(res.getString("destination_airport_id"));
					System.out.println(id);
					Airport a = this.getAirportById(id);
					System.out.println(a.getName());
					list.add(a);
				}
	
				conn.close();
				return list;
			} catch (SQLException e)
			{	
				e.printStackTrace();
				return null;
			}
		}
		
	//tutti gli aereoporti collegati da una rotta
	
	public List<Collegati> getCollegati(Airline air)
	{
		String sql = "select distinct a1.*, a2.*, r.*"
				+ " from airline air, airport a1, airport a2, route r "
				+ "where air.Airline_id = r.Airline_id "
				+ "and air.Airline_id = ?"
				+ " and a1.Airport_Id = r.Source_airport_Id "
				+ "and a2.Airport_id = r.Destination_airport_Id "
				+ "and a1.Airport_Id <> a2.Airport_Id";

		List<Collegati> list = new ArrayList<>();

		try
		{
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, air.getAirlineId());
			ResultSet res = st.executeQuery();
			while (res.next())
			{
				int id1 = Integer.parseInt(res.getString("Source_airport_Id"));
				int id2 = Integer.parseInt(res.getString("Destination_airport_Id"));
				
				Airport a1 = this.getAirportById(id1);
				Airport a2 = this.getAirportById(id2);
				
				LatLng c1 = new LatLng(a1.getLatitude(), a1.getLongitude());
				LatLng c2 = new LatLng(a2.getLatitude(), a2.getLongitude());
				double distanza = LatLngTool.distance(c1, c2, LengthUnit.KILOMETER);
			
				list.add(new Collegati(a1, a2, distanza));
			}
			conn.close();
			return list;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
		
	// MAIN
		public static void main(String args[]) {
			FlightDAO dao = new FlightDAO() ;
	
			List<Airline> airlines = dao.getAllAirlines();
			System.out.println(airlines);
			List<Airport> airports = dao.getAllAirports();
			System.out.println(airports);
			List<Route> routes = dao.getAllRoutes();
			System.out.println(routes);
		}

}
