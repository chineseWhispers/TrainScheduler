package iFreight;

import java.util.LinkedList;

/*
 * The PsgTrain class mainly consists of getter and setter methods, to retrieve and set 
 * relevant passenger train information.
 */

public class PsgTrain {

	private String name;
	private LinkedList<TimeWindow> timeWindow; // The fixed passenger train
												// schedule.
	private int trackcount;

	public PsgTrain(String name) {
		this.name = name;
		timeWindow = new LinkedList<TimeWindow>();
	}

	public LinkedList<TimeWindow> getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(LinkedList<TimeWindow> timeWindow) {
		this.timeWindow = timeWindow;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int gettrackcont() {
		return trackcount;
	}

	public void settrackcont(int count) {
		this.trackcount = count;
	}

	public void addTimeWindow(TimeWindow t) {
		timeWindow.add(t);
	}

}
