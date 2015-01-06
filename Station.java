package iFreight;

import java.util.LinkedList;

/*
 * The Station class mainly consists of getter and setter methods, to retrieve and set 
 * relevant station information. 
 */

public class Station {
	private String name;
	private String[] neighbor;
	private Track[] tracks;
	private TimeStamp readyTime;
	private int index;
	private double sidingLength;
	private LinkedList<Station> path;
	private double weight;

	public Station(String[] neighbor, String name, double sidingLength,
			int index) {
		this.name = name;
		this.neighbor = neighbor;
		this.sidingLength = sidingLength;
		this.setIndex(index);
	}

	public Station(String name) {
		this.name = name;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public void setReadyTime(TimeStamp readyTime) {
		this.readyTime = readyTime;
	}

	public TimeStamp getReadyTime() {
		return readyTime;
	}

	public String getName() {
		return name;// return
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getNeighbor() {
		return neighbor;
	}

	public String getOneNeighbor(int i) {
		return neighbor[i];
	}

	public String toString(String[] neighbor) {
		String neighbors = "";
		for (int i = 0; i < neighbor.length; i++) {
			neighbors = neighbors + neighbor[i] + " ";
		}
		return neighbors;
	}

	public void setNeighbor(String neighbor, int i) {
		this.neighbor[i] = neighbor;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getSidingLength() {
		return sidingLength;
	}

	public void setSidingLength(double sidingLength) {
		this.sidingLength = sidingLength;
	}

	public Track[] getTracks() {
		return tracks;
	}

	public Track getOneTrack(int i) {
		return tracks[i];
	}

	public LinkedList<Station> getPath() {
		return path;
	}

	public void setPath(LinkedList<Station> path) {
		this.path = path;
	}
}
