/*
 *  SFMTASimulation.java - 
 *  This program emulates the bus lines using objects created in the SFMTASimulator class
 *  An SFMTA object creates the various lines that represent the multiple train lines in MUNI
 *  Each train line object has its set of vehicles and train stations
 *  Also each line has passengers who are initalialized to start with the appropriate station to depart from
 *  each driver and passenger object is based on opening the various csv files
 *  This is synonymous with opening the respective csv files for the train lines
 *  Passengers get on and off the buses depending on their travel route created in the passenger class
 */
import java.io.IOException;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
import java.util.Random;

//class with main method that runs the program
public class SFMTASimulation {
	//main method that runs the program instantiating the SFMTA object
	public static void main(String[] args) throws IOException {
		//initalize the muni system and create every train line in the system
        SFMTA system = new SFMTA();
        SFMTA.addLine("NJudah.csv");
        SFMTA.addLine("47VanNess.csv");
        SFMTA.addLine("49Mission.csv");
        SFMTA.addLine("8xBayShore.csv");
        SFMTA.addLine("KIngleside.csv");
        SFMTA.addLine("LTaraval.csv");
        SFMTA.addLine("TThird.csv");
        //add drivers and passengers at their starting locations as indicated in csv files
        system.populateDrivers("drivers.csv");
        system.populatePassengers("passengers.csv");
        //runs the vehicles through their routes
        system.runVehicles();
        
       
        
    }
}

//class that is used to create driver objects
class Driver{
	 
	private String name; //name of the driver
	
	private int dutyStation; //assigned starting station of driver
	
	private int tripCounter; //how many trips driver has taken
	
	//constructor that accepts driver name and their assigned station
	Driver(String name, int dutyStation){
		this.name = name;
		this.dutyStation = dutyStation;
		tripCounter = 0;
		
		}
	 
	/*void waitAtStation(int stationNumber){
		for(int i = 0; i < nJudah.stations.length()-1 ; i++){
			
		}
	}
	*/
	//returns name of driver
	String getName(){
		return name;
	}
	//returns station assigned
	int getStation(){
		return dutyStation;
	}
	//increments trip numbers of driver
	void countTrips(){
		tripCounter ++;
	}
	//returns how many trips have been taken
	int tripCounter(){
		return tripCounter;
	}
	//resets the amount of trips taken
	void resetTripCounter(){
		tripCounter = 0;
	}
}

/*
waitAtStation(Station stationID){
 stationID.passengers.add(this.Person);
 
getOnBus(Vehicle vehicleID){
 vehicle
 
}
 
}
 
must getOffBus() after 7 trips
 
}
*/


//Line class used to create object for every bus line
class Line{
 
	private String lineName; //name of the bus line
	
	private String vehicleType; //type of vehicle (light rail or bus)
	//arraylist of stations in the line
	ArrayList <Station> stations = new ArrayList<Station>();
	 //arraylist to hold transferstations in the line
	ArrayList <Station> transferStations = new ArrayList<Station>();
	//arraylist to hold vehicles in line
	ArrayList <Vehicle> vehicles = new ArrayList <Vehicle>();
	//default constructor no arguments
	Line(){
		
	}
	
	/**
	 * Main constructor for line object reads line station information from parameter 
	 * file and instantiates a new station object for each line in file, instantiate line vehicles, populates line with passengers and drivers.
	 * @param filePath
	 * @throws IOException
	 */
	Line(String filePath) throws IOException{
	
		//Instantiate new file and scanner objects to read station information from file
		File lineStations = new File (filePath);
		
		Scanner input = new Scanner(lineStations);
		
		//Split first line of station information to get line name from first line
		String[] array = input.nextLine().split(",");
		
		lineName = array[0].trim();
		
		//If the line starts with a digit, its a bus line. Else, its an LRV line
		if(Character.isDigit(lineName.charAt(0)))
	        vehicleType = "BUS";
		else
	        vehicleType = "LRV";
		
		/*
		 * Use while loop to instantiate a new station object for each station
		 * and add each object to an Arraylist of station objects
		 */
			
		while (input.hasNext()){
			String[] stationInfo = input.nextLine().split(",");
			stationInfo[0].trim();
			stationInfo[1].trim();
			Station station = new Station(stationInfo[0], Integer.parseInt(stationInfo[1]));
			//If station is last in the list, it is the terminus station
			if (input.hasNext() == false)
				station.setAsTerminus();
			stations.add(station);
		}
		
		//Declare first station in the list as the origin station. 
		stations.get(0).setAsOrigin();
		
		//Use assignTransferStations() to populate list of transferStations			
		assignTransferStations("TransferStops.csv");
		
		/*
		//populate drivers using populateDrivers() method
		populateDrivers("drivers.csv");
		
		//populate passengers using populatePassengers() method
		populatePassengers("passengers.csv");
		*/
		//Instantiate the two vehicles that will serve this line into vehicle ArrayList
		vehicles.add(new Vehicle(this,1)); 
		
		vehicles.add(new Vehicle(this,2)); 
		
		/*
		//Print some transfer station info into console for testing purposes
		for(int i =0; i < transferStations.size(); i++)
			for(int j =0; j < transferStations.get(i).transferLines.size(); j++){
			System.out.println(transferStations.get(i).getStationID());	
			System.out.println(transferStations.get(i).transferLines.get(j));
			if (stations.get(i).hasDriver())
				for(int k =0; k < 2 ; k++){
					System.out.println(stations.get(i).drivers.get(k).getName() + "driver");
			}
			if (stations.get(i).hasPassenger())
				for(int k =0; k < 2 ; k++){
					System.out.println(stations.get(i).passengers.get(k).getName() + "  passenger");
			}
			
						
			}
		*/
		input.close();
		}
	
	
	/**
	 * this method finds transfer stations that match the station ID's of stations
	 * on this line, and adds those stations to the transferStation ArrayList.
	 * It also adds the names of the transfer lines to each transfer station's
	 * transferLines ArrayList.
	 * @param filePath
	 * @throws IOException
	 */
	void assignTransferStations(String filePath) throws IOException{
		//open file with transfer station names
		File people = new File(filePath);
		Scanner input = new Scanner(people);
		
		input.nextLine();
		//while loop that runs while there is another line not blank
		while (input.hasNext()){
			//Split each row into tokens
			String[] transferInfo = input.nextLine().split(",");
			//Trim each token
			for(int i = 0; i < transferInfo.length ; i++)
				transferInfo[i].trim();
			/*
			 * Examines which of the stations on the line correspond to transfer stations.
			 * It would be unneccessary to test both of the first columns using a test
			 * for whether the line is an LRV or bus line, except that the columns are 
			 * not actually sorted by vehicle type. The second column in the first row 
			 * refers to a bus line. 
			 * If station is transfer station, the station object's boolean value 
			 * isTransfer gets true, and the station is added to the ArrayList of 
			 * transfer stations, which exists for route calculation purposes.
			 */
			for(int i = 0; i < stations.size(); i++){
				if (Integer.parseInt(transferInfo[0]) == stations.get(i).getStationID() || 
					Integer.parseInt(transferInfo[1]) == stations.get(i).getStationID()	){
						stations.get(i).isTransferStation();
						transferStations.add(stations.get(i));
						String[] transferLines = transferInfo[3].split("-");
							for(int j = 0; j < transferLines.length ; j++){
								transferLines[j].trim();
								stations.get(i).addTransferLines(transferLines[j]);
								
								
							}
				}
			}
		}
		input.close();
	}

	
	/**
	 * This method finds drivers in the driver information file whose duty station IDs
	 * are within this line, then adds those drivers to the ArrayList of drivers waiting
	 * at that station.
	 * @param filePath the location of the file with driver information
	 * @throws IOException
	 */
	void populateDrivers(String filePath) throws IOException{
		File people = new File(filePath);
		Scanner input = new Scanner(people);

		input.nextLine();
		//while file has another non blank line
		while (input.hasNext()){
			//Tokenize driver info
			String[] driverInfo = input.nextLine().split(",");
			driverInfo[0].trim();
			driverInfo[1].trim();
			//If driver's station ID matches a station ID, add driver to Station's driver Arraylist
			for(int i = 0; i < stations.size(); i++){
				if (Integer.parseInt(driverInfo[1]) == stations.get(i).getStationID()){
					
					Driver driver = new Driver(driverInfo[0], Integer.parseInt(driverInfo[1]));
					stations.get(i).drivers.add(driver);
					
				}
			}
		}
		
		//Instantiate new BufferedWriter object to print stations with drivers to a file.
		BufferedWriter driverCounter = new BufferedWriter(new FileWriter("StationDriverCount.txt",true));
		
		for(int i = 0; i < stations.size(); i++){
			
			if(stations.get(i).hasDriver()){
				driverCounter.newLine();
				driverCounter.write(String.valueOf(stations.get(i).getStationID()));
				int driverCount = stations.get(i).drivers.size();
				driverCounter.write("," + String.valueOf(driverCount));
				
				
			}
				
		}driverCounter.close();
		
		input.close();
		
		
	}
	
	
	/**
	 * This method finds passengers in the passenger information file whose origin station IDs
	 * are within this line, then adds those passengers to the ArrayList of passengers waiting
	 * at that station.
	 * @param filePath the location of the file with passenger information
	 * @throws IOException
	 */
	void populatePassengers(String filePath) throws IOException{
		File people = new File(filePath);
		Scanner input = new Scanner(people);
		
		input.nextLine();
		//while file has another blank line
		while (input.hasNext()){
			String[] passengerInfo = input.nextLine().split(",");
			passengerInfo[0].trim();
			passengerInfo[1].trim();
			passengerInfo[2].trim();
			for(int i = 0; i < stations.size()-1 ; i++){
				if (Integer.parseInt(passengerInfo[1]) == stations.get(i).getStationID()){
					Passenger passenger = new Passenger(passengerInfo[0], this, Integer.parseInt(passengerInfo[1]), Integer.parseInt(passengerInfo[2]));
					stations.get(i).passengers.add(passenger);
				}
			}
		}
		//Instantiate new BufferedWriter object to print station passenger count to a file.
				BufferedWriter passengerCounter = new BufferedWriter(new FileWriter("StationPeopleCount.txt",true));
				
				for(int i = 0; i < stations.size(); i++){
						passengerCounter.newLine();
						passengerCounter.write(String.valueOf(stations.get(i).getStationID()));
						int passengerCount = stations.get(i).passengers.size();
						passengerCounter.write("," + String.valueOf(passengerCount));
						
						
				}passengerCounter.close();
				input.close();
	}
	
	/*void  runVehicles() throws IOException{
		
		while(one.outbound){
			
			for(int i = 0; i < stations.size();i++){
				one.stopAtStation(stations.get(i));
			//Set route as inbound or outbound depending on if station is an origin or terminus.
			
				
			
			//If next station has no vehicle occupying it in the same direction, proceed to station.
			//&& (route.get(i).hasInboundVehicle() == false)		
		}
		if(one.inbound){
				stopAtStation(route.get(i));
				i--;
			}
					
			
			if(one.outbound){
				stopAtStation(route.get(i));
				i++;
			}
	
		}
	}
						
	}*/
	
	/**
	 * This method returns String vehicleType;
	 * @return the type of vehicle that runs on this line
	 */
	String vehicleType(){
		return vehicleType;
	}
	
	/**
	 * This method returns String lineName
	 * @return the name of this line
	 */
	String lineName(){
		return lineName;
	}
	
	/**
	 * This method searches the line for the stationID that is passed to it.
	 * This method is for itinerary calculation purposes, 
	 * @param stationName
	 * @return
	 */
	boolean searchLineStations(int stationName){
		boolean isOnThisLine = false;
		//Search station ID list for match of the parameter station ID
		for(int i = 0; i < stations.size(); i++){
			if (stationName == stations.get(i).getStationID())
				isOnThisLine = true;
		}
		return isOnThisLine;
	}
}

/*
  
if(stationID = transferStationID)
 station.isTransferStation = true
 station.transferLines = (read transfer lines from transfer station ID list)
}
*/


//class used to make passenger objects
class Passenger{
	//name of passenger
	private String name;
	//name of original line that passenger boards
	private Line originLine;
	//name of line passenger wants to get to
	private Line destinationLine;
	//ID of station assigned from populating
	private int originStationID;
	 //ID of station the passenger wants to get to
	private int destinationStationID;
	//arraylist of station numbers passenger wants to go to
	public ArrayList <Integer> stationList = new ArrayList <Integer>();
	//arraylist of inbound or outbound directiosn for the passenger
	public ArrayList <Boolean> directionList = new ArrayList <Boolean>();
	//arraylist of lines passenger wants to use
	public ArrayList <Line> lineList = new ArrayList <Line>();
	//constructor for passenger that accepts their name, origin, and destination
	Passenger(String name, Line originLine, int originStation, int destinationStation){
		this.name = name;
		this.originLine = originLine; 
		this.originStationID = originStation;
		this.destinationStationID = destinationStation;
		calculateRoute();
		
	}
	//returns name of passenger
	String getName(){
		return name;
	}
	//returns their original place
	String getOriginLine(){
		return originLine.lineName();
	}
	//metnhod that calculates itinerary for the passenger
	void calculateRoute(){
		//Line destinationLine 
		//destinationLine = SFMTA.destinationLine(destinationStationID); 
		
		//Add destinationStation ID to list of stations where passengers will exit
		stationList.add(destinationStationID);
		directionList.add(true);
	
		
		if(originLine.searchLineStations(destinationStationID)){
			destinationLine = originLine;
			
		}
		else{
			//Search SFMTA lines for destination line
			for(int i = 0; i < SFMTA.muni.size(); i++){
				//Use Line's searchLineStations() method to see if parameter station is on that line
				if(SFMTA.muni.get(i).searchLineStations(destinationStationID)){
					destinationLine = SFMTA.muni.get(i);
			
			}
			}
		}
}
			/*
				for(int j = 0; j < destinationLine.transferStations.size(); j ++)
					for(int k = 0; k < destinationLine.transferStations.get(j).transferLines.size(); k ++){
						System.out.println(destinationLine.transferStations.get(j).transferLines.get(k).charAt(0));
						if(Character.isDigit(destinationLine.transferStations.get(j).transferLines.get(k).charAt(0)))
							if(destinationLine.transferStations.get(j).transferLines.get(k).regionMatches(true, 0, originLine.lineName(), 0, 2)){
								stationList.add(destinationLine.transferStations.get(j).getStationID());
								System.out.println("I have only one transfer! -"  + name);
							}
						if(Character.isLetter(destinationLine.transferStations.get(j).transferLines.get(k).charAt(0)))
							if(destinationLine.transferStations.get(j).transferLines.get(k).regionMatches(true, 0, originLine.lineName(), 0, 1)){
								stationList.add(destinationLine.transferStations.get(j).getStationID());
								System.out.println("I have only one transfer! -"  + name);
							}
					}
			
			}*/
						/*else{
							for(int i = 0; i < SFMTA.muni.size(); i ++)
								if(SFMTA.muni.get(i).searchLineStations(destinationStationID)){
									 for(int j = 0; j < SFMTA.muni.get(i).transferStations.size(); j ++)
										for(int k = 0; k < SFMTA.muni.get(i).transferStations.get(j).transferLines.size(); k ++)
											if(SFMTA.muni.get(i).transferStations.get(j).transferLines.get(k).charAt(0) == (destinationLine.lineName().charAt(0)))
												if(SFMTA.muni.get(i).transferStations.get(j).transferLines.get(k).charAt(1) == (destinationLine.lineName().charAt(1)));
								}
						}
						
			}
									
						//for(int k =0; k < SFMTA.muni.get(i).transferStations.size(); k++);
								
	}*/
	
		
	
	
		
		
	
	//returns station id of their next location
	int nextStation(){
		if(stationList.size()>0)
			return stationList.get(0);
		else
			return -1;
	}
	//returns stop number of their destination
	int destinationStationID(){
		return destinationStationID;
	}
	
	
 
}


//class taht creates the SFMTA object that represents the muni system
class SFMTA {
	//arraylist muni to hold the various lines in the muni system
	public static ArrayList <Line> muni = new ArrayList <Line>();
	
	/**
	 * addLine method instantiates a new line from the file it takes as 
	 * a parameter and adds the line to the muni ArrayList of lines.
	 * @param filePath the file with line station information.
	 * @throws IOException 
	 */
	public static void addLine(String filePath) throws IOException{
		Line line = new Line (filePath);
		muni.add(line);
		
	}
	
	/**
	 * runVehicles runs the vehicles on all the lines while the system has
	 * passengers. The vehicles all run simultaniously becuase the method moves 
	 * all the vehicles in the system forward one stop, then checks if the system
	 * still has passengers.
	 * @throws IOException
	 */
	void runVehicles() throws IOException{
		//Put drivers in vehicles
		for(int i = 0; i < muni.size(); i++)
			for(int j = 0; j < muni.get(i).vehicles.size(); j++)
				muni.get(i).vehicles.get(j).driver = muni.get(i).stations.get(0).drivers.get(j);
		do{
			for(int x = 0; x < muni.size(); x++){
				for(int y = 0; y < muni.get(x).vehicles.size(); y++){
					muni.get(x).vehicles.get(y).moveOneStop();
				}
			}
		}while (hasPassengers()); 
		
	}
	
	/**
	 * destinationLine takes the integer ID of a given station and returns the line object
	 * reference for the line containing that station. This method is used primarily
	 * for calculating a passenger's itinerary.
	 * @param stationID the integer stationID to search for in the muni arraylists Line objects.
	 * @return reference to the line object containing the stationID passed to the method.
	 */
	public static Line destinationLine(int stationID){
		//Initialize line object reference as null in case destination station is not in system.
		Line line = new Line();
		for(int i = 0; i < muni.size(); i++){
			//Use Line's searchLineStations() method to see if parameter station is on that line
			if(muni.get(i).searchLineStations(stationID))
				line = muni.get(i);
				
		}
		//return null if stationID is not found in ArrayList of lines.
		return line;			
		
	}
	
	/**
	 * Uses two nested for loops to search every station in every line,
	 * returns true when the first station whose hasPassenger method returns
	 * true is found, otherwise returns false.
	 * @return whether the system has passengers.
	 */
	boolean hasPassengers(){
		
		for(int i = 0; i < muni.size(); i++){
			for(int j = 0; j < muni.get(i).stations.size(); j++)
				if(muni.get(i).stations.get(j).hasPassenger())
					return true;
			
		}
		for(int i = 0; i < muni.size(); i++){
			for(int j = 0; j < muni.get(i).stations.size(); j++)
				if(muni.get(i).vehicles.get(j).hasPassenger())
					return true;
		}
		return false;
	}
		//method that puts the driveres in their assigned stations
	void populateDrivers(String filePath) throws IOException{
		//open the file with the driver list 
		File people = new File(filePath);
		Scanner input = new Scanner(people);

		input.nextLine();
			//while there is another line non blank
		while (input.hasNext()){
			//Tokenize driver info
			String[] driverInfo = input.nextLine().split(",");
			driverInfo[0].trim();
			driverInfo[1].trim();
			//If driver's station ID matches a station ID, add driver to Station's driver Arraylist
			for(int i = 0; i < muni.size(); i++)
				for(int j = 0; j < muni.get(i).stations.size(); j++){
					//Add drivers to station if the station IDs match & the station is an origin or terminus
					if (Integer.parseInt(driverInfo[1]) == muni.get(i).stations.get(j).getStationID() &&(muni.get(i).stations.get(j).isOrigin()||muni.get(i).stations.get(j).isTerminus())){
						Driver driver = new Driver(driverInfo[0], Integer.parseInt(driverInfo[1]));
						muni.get(i).stations.get(j).drivers.add(driver);
						
					}
				}
			}
		//Instantiate new BufferedWriter object to print stations with drivers to a file.
		BufferedWriter driverCounter = new BufferedWriter(new FileWriter("StationDriverCount.txt",true));
		
		for(int i = 0; i < muni.size(); i++)
			for(int j = 0; j < muni.get(i).stations.size(); j++){
				if(muni.get(i).stations.get(j).hasDriver()){
					driverCounter.newLine();
					driverCounter.write(String.valueOf(muni.get(i).stations.get(j).getStationID()));
					int driverCount = muni.get(i).stations.get(j).drivers.size();
					driverCounter.write("," + String.valueOf(driverCount));
							
			}
				
		}driverCounter.close();
		input.close();
	}
	//method that places passengers in the system
	void populatePassengers(String filePath) throws IOException{
		//open the file with all the passenger info
		File people = new File(filePath);
		Scanner input = new Scanner(people);
		
		input.nextLine();
		//while there is non blank line in file
		while (input.hasNext()){
			String[] passengerInfo = input.nextLine().split(",");
			passengerInfo[0].trim();
			passengerInfo[1].trim();
			passengerInfo[2].trim();
			for(int i = 0; i < muni.size(); i++)
				for(int j = 0; j < muni.get(i).stations.size(); j++){
					if (Integer.parseInt(passengerInfo[1]) == muni.get(i).stations.get(j).getStationID()){
						Passenger passenger = new Passenger(passengerInfo[0], muni.get(i), Integer.parseInt(passengerInfo[1]), Integer.parseInt(passengerInfo[2]));
						muni.get(i).stations.get(j).passengers.add(passenger);
				}
			}
		}
		//Instantiate new BufferedWriter object to print station passenger count to a file.
				BufferedWriter passengerCounter = new BufferedWriter(new FileWriter("StationPeopleCount.txt",true));
				
				for(int i = 0; i < muni.size(); i++)
					for(int j = 0; j < muni.get(i).stations.size(); j++){
						passengerCounter.newLine();
						passengerCounter.write(String.valueOf(muni.get(i).stations.get(j).getStationID()));
						int passengerCount = muni.get(i).stations.get(j).passengers.size();
						passengerCounter.write("," + String.valueOf(passengerCount));
						if (passengerCount > 0) passengerCounter.write(muni.get(i).stations.get(j).passengers.get(0).getOriginLine());
						
						
				}passengerCounter.close();
				input.close();
	}
		
	
	
		
			

	


}

//class that creates station objects that can contain pasengers or drivers
class Station {
	
	String stationName; //name of station
	private final int stationID; //id number of station
	private boolean isTransferStation = false; //boolean if it is a transfer station or not
	private boolean isOrigin = false; //boolean if it is an origin of a route
	private boolean isTerminus = false; //boolean if it is a terminus of a route
	private boolean hasOutboundVehicle = false; //boolean is it is going outbound
	private boolean hasInboundVehicle = false; //boolean if it is going inbound
	//arraylist of transferlines that run through the station
	ArrayList <String> transferLines = new ArrayList <String>();
	//arraylist of passengers waiting at station
	ArrayList<Passenger> passengers = new ArrayList<Passenger>();
	//arraylist of drivers waiting at the station
	ArrayList<Driver> drivers = new ArrayList<Driver>();
	//construcor that accepts station name and id number
	Station(String stationName, int stationID){
		this.stationName = stationName;
		this.stationID = stationID;
	}
	//returns the id number of the station
	public int getStationID(){
		return stationID;
	}
	//sets true if point is origin of route
	void setAsOrigin(){
		isOrigin = true;
	}
	//returns boolean true or false if it is origin
	boolean isOrigin(){
		return isOrigin;
	}
	//sets true if point is terminus of route
	void setAsTerminus(){
		isTerminus = true;
	}
	//returns boolean true or false if it is terminus
	boolean isTerminus(){
		return isTerminus;
	}
	//sets the boolean true or false if it is a transferstation
	void setTransferStation(){
		isTransferStation = true;
	}
	//returns true if point is a transferstation
	boolean isTransferStation(){
		return isTransferStation;
	}
	//adds line to transferstation arraylist
	void addTransferLines(String input){
		transferLines.add(input);
	}
	//returns true if station is occupied by a driver
	boolean hasDriver(){
		if (drivers.size() > 0)
			return true;
		else 
			return false;
	}
	//returns true if station is occupied by passengers
	boolean hasPassenger(){
		if (passengers.size() > 0)
			return true;
		else 
			return false;
		
	}
	//sets true if station has outbound bus
	void setOutboundVehicle(){
		hasOutboundVehicle = true;
	}
	//returns to default of having no outbound vehicle
	void resetOutboundVehicle(){
		hasOutboundVehicle = false;
	}
	//returns whether or not there is an outbound vehicle at station
	boolean hasOutboundVehicle(){
		return hasOutboundVehicle;
	}
	//set true if there is an inbound vehicle at station
	void setInboundVehicle(){
		hasInboundVehicle = true;
	}
	//returns to default of having no inbound vehicle
	void resetInboundVehicle(){
		hasInboundVehicle = false;
	}
	//returns whether or not the station has an inbound vehicle
	boolean hasInboundVehicle(){
		return hasInboundVehicle;
	}
	
	

}



//class that creates vehicle object
class Vehicle{
	
	 //type of vehicle
	private String vehicleType;
	//name of the bus line it runs on
	private String lineName;
	//number of vehicle in line
	private int vehicleNumber;
	 //max number of passengers that can be in vehicle
	private final int passengerCapacity;
	//inbound or outbound status of vehicle
	public boolean inbound = false;
	
	public boolean outbound = true;
	
	BufferedWriter tripCounter;
	
	int positionInRoute;
	//arraylist of passengers currently on bus
	ArrayList <Passenger> passengers = new ArrayList<Passenger>();
	 //driver of the bus
	Driver driver;
	//arraylist of stations in the route 
	ArrayList <Station> route = new ArrayList <Station>();
	

	//constructor that accepts line and vehicle number	
	Vehicle (Line line, int number) throws IOException{
		vehicleNumber = number;
		Random num = new Random();
		passengerCapacity = (num.nextInt(2) + 1) * 20;
		this.vehicleType = line.vehicleType();
		this.lineName = line.lineName();	
		this.route = line.stations; 
		//driver = line.stations.get(0).drivers.get(number-1); 
		positionInRoute = 0;
		
		
	}
	

		
	
	
	void stop(){
		
	}
	//method to make vehicle stop at the desired station
	void stopAtStation(Station station) throws IOException{
	//If the station is not occupied by a vehicle traveling in the same direction, then proceed to stop at station!
	if((outbound &&(outbound != station.hasOutboundVehicle())) ||(inbound &&(inbound != station.hasInboundVehicle()))){
		
		//If station is origin or terminus, change vehicle to outbound or inbound respectively		
		if(station.isOrigin())
			outBoundRoute();
		if(station.isTerminus())
			inBoundRoute();
		
		//Instantiate ArrayLists of departing and boarding people to print information
		ArrayList <String> departingPeople = new ArrayList <String>();
		ArrayList <String> boardingPeople = new ArrayList <String>();
		
		//Print origin or terminus stop information to file
		tripCounter = new BufferedWriter(new FileWriter("SFMTA_Trips.txt",true));
		if(station.isOrigin() || station.isTerminus()){
			tripCounter.newLine();
			tripCounter.write(String.valueOf(vehicleType.charAt(0)) + ":");
			tripCounter.write(String.valueOf(vehicleNumber) + ":");
			tripCounter.write(driver.getName() + ":");
			tripCounter.write(direction() + ":");
			tripCounter.write(lineName + ":");
			tripCounter.write(String.valueOf(passengerCapacity/20) + ":");
		}
		
		
		//Set station's boolean values indicating the presence of a inbound or outbound vehicle.
		if (inbound){
			station.setInboundVehicle();
		}
		
		if (outbound){
			station.setOutboundVehicle();
		}
		
		/*
		 * If station is origin or terminus, add a trip to Driver's trip counter.
		 */
		if(station.isOrigin() || station.isTerminus())
			driver.countTrips();
				
		/*
		 * If driver's trip count is 7, then he must trade with a new driver 
		 * at the next available opportuniy. 
		 */
		if(station.hasDriver() && driver.tripCounter() == 7){
			//Add driver to the back of the line at the station and reset the tripCounter
			driver.resetTripCounter();
			
			//Add driver to the back of the line of drivers at the station
			station.drivers.add(station.drivers.size(),driver);
			
			//Record driver's name in list of people that got off.
			departingPeople.add(driver.getName());
			//Point driver reference to new driver object from station.
			driver = station.drivers.get(0);
			//Add new Driver's name to boarding list.
			boardingPeople.add(driver.getName());
		}
		
				
		
		/*
		 * Search passenger ArrayList in vehicle for passengers whose nextStation
		 * field matches parameter station's station id. 
		 */			
		for(int i=0; i < this.passengers.size();i++){
			//if passenger's nextStation matches station ID, he/she is getting off
			if(this.passengers.get(i).nextStation()==(station.getStationID())){
				//if passenger has reached final destination, remove him/her
				if(this.passengers.get(i).nextStation() == this.passengers.get(i).destinationStationID()){
//					Add passenger to ArrayList of departing people.
					departingPeople.add(this.passengers.get(i).getName());
					this.passengers.remove(i);
				}
				/*
				 * Else, passenger must be transfering. Add him/her to station passenger
				 * ArrayList, then remove.
				 */
				else {
					
					//Add passenger to the back of the line
					station.passengers.add(station.passengers.size(),this.passengers.get(i));
					//Add passenger to ArrayList of departing people.
					departingPeople.add(this.passengers.get(i).getName());
					//Remove passenger object reference from vehicle passenger ArrayList
					this.passengers.remove(i);
				}
					
				
			}	
		}
		
		//if there are passengers at the station and there's room on vehicle, take them on board!
		if (station.hasPassenger() && (this.passengers.size() < (passengerCapacity))){
			/*
			 * If the number of elements in the bus's passenger array is
			 * less that its passenger capacity, add passengers to the 
			 * vehicle's passenger array until capacity is reached.
			 */
			do{int i=0; 
				if(station.passengers.get(i).directionList.get(0) == outbound){
					this.passengers.add(station.passengers.get(0));
					boardingPeople.add(station.passengers.get(0).getName());
					station.passengers.remove(0);
				}
					i++;
			}while(this.passengers.size() < (passengerCapacity));		
					
						
			
		}
		//write the current event to the txt file
		tripCounter.newLine();
		tripCounter.write("E:");
		tripCounter.write(String.valueOf(vehicleNumber) + ":");
		tripCounter.write(String.valueOf(station.getStationID()) + ":");
		tripCounter.write(String.valueOf(boardingPeople.size()) + ":");
		for(int i=0; i < boardingPeople.size();i++){
			tripCounter.write(boardingPeople.get(i) + ",");
		}
		//write the current event to the txt file
		tripCounter.newLine();
		tripCounter.write("D:");
		tripCounter.write(String.valueOf(vehicleNumber) + ":");
		tripCounter.write(String.valueOf(station.getStationID()) + ":");
		tripCounter.write(String.valueOf(departingPeople.size()) + ":");
		for(int i=0; i < departingPeople.size();i++){
			tripCounter.write(departingPeople.get(i) + ",");
		}
		
		tripCounter.close();
		
		//Depending on inbound or outbound, specify which direction next stop is
		if (outbound)
			positionInRoute ++;
		if(inbound)
			positionInRoute --;
		
		//And finally, reset the station's hasVehicle boolean values
		if (inbound){
			station.resetInboundVehicle();
		}
		
		if (outbound){
			station.resetOutboundVehicle();
		}
		
		}		
		
		
	}
	//increments the bus one stop 
	void moveOneStop() throws IOException{
		stopAtStation(route.get(positionInRoute));
	}
	//sets to bus to be going inbound
	void inBoundRoute(){
		inbound = true;
		outbound = false;
	}
	//sets the bus to be going outbound
	void outBoundRoute(){
		outbound = true;
		inbound = false;
	}
	
	//returns direction bus is going
	String direction(){
		if (outbound)
			return "Outbound";
		else if(inbound)
			return "Inbound";
		else
			return "Lost";
	}
	//returns vehicle number
	int vehicleNumber(){
		return vehicleNumber;
	}
	//returns true or false if passengers are on bus
	boolean hasPassenger(){
		if (passengers.size() > 0)
			return true;
		else
			return false;
	}
	
}
/*
public void stopAtStation()
 
public void nextStation(){
	
		
}
 
public boolean outBoundRoute(){
	if station.isTerminus
	  inboundRoute();
}
 
 if station.isTerminus
  inboundRoute()
 
public boolean inBoundRoute()
  
 if station.isOrigin
  outBoundRoute()
 
 
}
*/
   
    	
    	
    	
    	
   
