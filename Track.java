package iFreight;

import java.util.LinkedList;

/*
 * The Track class mainly consists of getter and setter methods, to retrieve and set 
 * relevant track information. 
 */

public class Track {
	private Station S1; // Station1 at the beginning of the track
	private Station S2; // Station2 at the other end of the track
	private double trackLength;
	private double limitSpeed;
	private double headway;
	private LinkedList<TimeWindow> UpTimeWindow;
	private LinkedList<TimeWindow> DownTimeWindow;
	private LinkedList<TimeWindow> upDownTimeWindow;
	private TimeWindow freightWindow;
	private Freight singleFreight;
	public Track(Station S1, Station S2, double tracklength, double limitSpeed,
			double headway) {
		this.S1 = S1;
		this.S2 = S2;
		this.trackLength = tracklength;
		this.limitSpeed = limitSpeed;
		this.headway = headway;
		UpTimeWindow = new LinkedList<TimeWindow>();
		DownTimeWindow = new LinkedList<TimeWindow>();
		upDownTimeWindow = new LinkedList<TimeWindow>();
	}
	
	public Track(double tracklength, int capacity){
		this.trackLength = tracklength;
		
	}

	public boolean UporDownTrack() {
		return !UpTimeWindow.isEmpty();
	}

	public void setFreight(Freight singleFreight) {
		this.singleFreight = singleFreight;
	}

	public Freight getFreight() {
		return singleFreight;
	}

	public TimeWindow getFreightWindow() {
		return freightWindow;
	}

	public void setFreightWindow(TimeWindow freightWindow) {
		this.freightWindow = freightWindow;
	}

	public LinkedList<TimeWindow> getUpTimeWindows() {
		return UpTimeWindow;
	}

	public void addUpTimeWindow(TimeWindow t) {
		UpTimeWindow.add(t);
	}

	public LinkedList<TimeWindow> getDownTimeWindows() {
		return DownTimeWindow;
	}

	public void addDownTimeWindow(TimeWindow t) {
		DownTimeWindow.add(t);
	}

	public LinkedList<TimeWindow> getUpdownTimeWindows() {
		return upDownTimeWindow;
	}

	public void addUpdownTimeWindow(TimeWindow t) {
		upDownTimeWindow.add(t);
	}

	public Station getS1() {
		return S1;
	}

	public void setS1(Station S1) {
		this.S1 = S1;
	}

	public Station getS2() {
		return S2;
	}

	public void setS2(Station S2) {
		this.S2 = S2;
	}

	public double getTrackLength() {
		return trackLength;
	}

	public void setTrackLength(double trackLength) {
		this.trackLength = trackLength;
	}

	public double getLimitSpeed() {
		return limitSpeed;
	}

	public void setLimitSpeed(double limitSpeed) {
		this.limitSpeed = limitSpeed;
	}

	public double getHeadway() {
		return headway;
	}

	public void setHeadway(double headway) {
		this.headway = headway;
	}

	public String toString() {
		return S1.getName() + " -> " + S2.getName();
	}
}
