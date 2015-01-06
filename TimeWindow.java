package iFreight;

/*
 * The TimeWindow class mainly consists of getter and setter methods, to retrieve and set 
 * relevant schedule information.
 * It has two separate constructor, for the freight trains and the passenger trains.  
 */

public class TimeWindow implements Comparable<TimeWindow> {
	private String train;
	private String track;
	private TimeStamp departure; //depart from S1
	private TimeStamp arrival; //arrive S2
	private double velocity; //velocity profile (use only for freight trains)
	private int status; 
	//status 0=up and 1=down and 2=updown
	
	public TimeWindow(){
		
	}
	
	// Passenger trains constructor
	public TimeWindow(String train,String track, TimeStamp departure,TimeStamp arrival,int status){
		this.train = train;
		this.track = track;
		this.departure = departure;
		this.arrival = arrival;
		this.status = status;
	}
	
	// Freight trains constructor
	public TimeWindow(String train,String track,TimeStamp departure,TimeStamp arrival,double velocity,int status){
		this.train=train;
		this.track=track;
		this.departure = departure;
		this.arrival = arrival;
		this.velocity=velocity*60.0;
	}
	
	public void setFreightName(String train){
		this.train = train;
	}
	
	@Override
	public int compareTo(TimeWindow o) {
		return departure.compareTo(o.departure);
	}
	//checks which window is greater
	public boolean checkValid(TimeWindow o){
		return (departure.compareTo(o.departure)>=0&& arrival.compareTo(o.arrival)>=0);
	}
	
	//in minutes
	public double getDuration(){  
		return (arrival.getHour()*60+arrival.getMinute())-(departure.getHour()*60+departure.getMinute());
	}
	
	public String getTrain(){// get the train no
		return train;
	}
	public int getStatus(){// get the status
		return status;
	}
	
	// departure in upward and vice versa
	public String getS1(){
		return track.substring(0,track.indexOf("-")-1);
	}
	// arrival in upward and vice versa
	public String getS2(){
		return track.substring(track.indexOf(">")+2);
	}
	//get the departure time	
	public TimeStamp getDeparture(){
		return departure;
	}
	//get the arrival time
	public TimeStamp getArrival(){
		return arrival;
	}
	//get the velocity	
	public double getV(){
		return velocity;
	}
	
	public double roudedV(){
		double tempV = velocity;
		tempV = Math.round(tempV*100);
		tempV = tempV/100.0;
		return tempV;
	}
	
	public String getTrackName(){
		return track;
	}
	//set the velocity
	public void setV(double V){
		velocity=V*60;
	}
	// in hour format
	public String toString(){
		return train +" | "+departure.toString()+" to "+arrival.toString();
	}
	//in days and hours
	public String toString2(){
		return train +" | "+departure.toString2()+" to "+arrival.toString2();
	}
	
	public String toSolutionString(){
		return train + " | "+ track + " | "+departure.toString2()+" to "+arrival.toString2()+ " speed="+velocity;
	}
}

