package iFreight;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.GraphPath;

/*
 * The Freight class mainly consists of getter and setter methods, to retrieve and set 
 * relevant freight train information. 
 */

public class Freight {
	private String origin, destination; // The origin station, and the
										// destination station
	private int length; // The length of the freight train
	private int speed; // The speed of the freight train
	private TimeStamp readyTime, prevReadyTime; // the ready time at the current
												// station, the previous ready
												// time.
	private Track[][] path_tracks; // The path consists of all tracks
	private Track[] shortest_path; 
	private Solver solver; 
	private String freightName;
	private LinkedList<Station> shortestTimePath;
	private LinkedList<TimeWindow> shortestPathTimeWindow, solution;
	private TimeStamp shortestDestTime;
	private double firstDuration; // the duration from the current station to the next station
	private double allDuration; // the duration from the current station to the destination
	private Station currentStation; 
	private Station nextStation;
	private Track currentTrack;
	private Track nextTrack;
	private Track prevTrack;
	private Station prevStation;

	public Freight(String freightName, Network network, Station[] stations,
			Track[][] tracks, Hashtable<String, Track> trackTable,
			String origin, String destination, int length, int speed,
			TimeStamp rd) {
		this.freightName = freightName;
		this.origin = origin;
		this.destination = destination;
		this.length = length;
		this.speed = speed;
		readyTime = new TimeStamp(rd);
		solution = new LinkedList<TimeWindow>();
	}

	public Freight(Freight o) {
		currentStation = o.currentStation;
		currentTrack = o.currentTrack;
		nextStation = o.nextStation;
		nextTrack = o.nextTrack;
	}

	public void setPrevStation(Station prevStation) {
		this.prevStation = prevStation;
	}

	public Station getPrevStation() {
		return prevStation;
	}

	public void setPrevTrack(Track prevTrack) {
		this.prevTrack = prevTrack;
	}

	public Track getPrevTrack() {
		return prevTrack;
	}

	public void setCurrentStation(Station currentStation) {
		this.currentStation = currentStation;
	}

	public Station getCurrentStation() {
		return currentStation;
	}

	public void setNextStation(Station nextStation) {
		this.nextStation = nextStation;
	}

	public Station getNextStation() {
		return nextStation;
	}

	public void setCurrentTrack(Track currentTrack) {
		this.currentTrack = currentTrack;
	}

	public Track getCurrentTrack() {
		return currentTrack;
	}

	public void setNextTrack(Track nextTrack) {
		this.nextTrack = nextTrack;
	}

	public Track getNextTrack() {
		return nextTrack;
	}

	public void setShortestTimePath(LinkedList<Station> shortestTimePath) {
		this.shortestTimePath = shortestTimePath;
	}

	public LinkedList<Station> getShortestTimePath() {
		return shortestTimePath;
	}

	public void setShortestTimeWindow(
			LinkedList<TimeWindow> shortestPathTimeWindow) {
		this.shortestPathTimeWindow = shortestPathTimeWindow;
	}

	public LinkedList<TimeWindow> getShortestTimeWindow() {
		return shortestPathTimeWindow;
	}

	public void setDestTime(TimeStamp shortestDestTime) {
		this.shortestDestTime = shortestDestTime;
	}

	public TimeStamp getDestTime() {
		return shortestDestTime;
	}

	public String getName() {
		return freightName;
	}

	public String getOrigin() {
		return origin;
	}

	public String getDest() {
		return destination;
	}

	public int getLength() {
		return length;
	}

	public int getSpeed() {
		return speed;
	}

	public TimeStamp getReadyTime() {
		return readyTime;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public void setDest(String destination) {
		this.destination = destination;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setReadyTime(TimeStamp readyTime) {
		this.readyTime = readyTime;
	}

	public void setPrevReadyTime(TimeStamp prevReadyTime) {
		this.prevReadyTime = prevReadyTime;
	}

	public TimeStamp getPrevReadyTime() {
		return prevReadyTime;
	}

	public void setFirstDuration(double firstDuration) {
		this.firstDuration = firstDuration;
	}

	public double getFirstDuration() {
		return firstDuration;
	}

	public void setAllDuration(double allDuration) {
		this.allDuration = allDuration;
	}

	public double getAllDuration() {
		return allDuration;
	}

	public TimeStamp getStationTime(String StationName) {
		TimeStamp readyStationTime = solver.getStationReadyTime().get(
				StationName);
		return readyStationTime;
	}

	public Track[] getShortestPath() {
		shortest_path = path_tracks[0];
		return shortest_path;
	}

	public void setSolution(LinkedList<TimeWindow> solution) {
		this.solution = solution;
	}

	public LinkedList<TimeWindow> getSolution() {
		return solution;
	}
}