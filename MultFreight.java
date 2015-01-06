package iFreight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;

/*
 * The MultFreight train class consists methods for scheduling multiple freight trains.
 * Specifically, it has methods for determining the dispatching sequence. It also has method that creates different dispatching criterion (i.e. FCFS, LCFS, etc)
 */

public class MultFreight {
	private Pair[] dispatchSequence;
	private ArrayList<Freight> FreightList;
	private Station[] stations;
	private Track[][] tracks;
	private int mode;
	private Network network;
	private Hashtable<String, Station> stationTable;
	private Hashtable<String, Track> trackTable;
	private Hashtable<String, LinkedList<TimeWindow>> freightSolTable;

	public MultFreight(Hashtable<String, Station> stationTable,
			Hashtable<String, Track> trackTable, Network network,
			ArrayList<Freight> FreightList, Station[] stations,
			Track[][] tracks, int mode) {
		this.stationTable = stationTable;
		this.trackTable = trackTable;
		this.network = network;
		this.FreightList = FreightList;
		this.stations = stations;
		this.tracks = tracks;
		this.mode = mode;
		freightSolTable = new Hashtable<String, LinkedList<TimeWindow>>();
	}

	public MultFreight(Hashtable<String, Station> stationTable,
			Hashtable<String, Track> trackTable,
			ArrayList<Freight> FreightList, Station[] stations,
			Track[][] tracks, int mode) {
		this.stationTable = stationTable;
		this.trackTable = trackTable;
		this.FreightList = FreightList;
		this.stations = stations;
		this.tracks = tracks;
		this.mode = mode;
	}

	/*
	 * This method helps generate dispatching sequence, based on FCFS, LCFS, and
	 * the shortest travel time.
	 */
	public void generateSequence() { // for heuristic function
		dispatchSequence = new Pair[FreightList.size()];
		if (mode == 0) {
			for (int i = 0; i < FreightList.size(); i++) {
				dispatchSequence[i] = new Pair(i,
						((Freight) FreightList.get(i)).getPrevReadyTime()
								.toMinute());
			}
		} else if (mode == 1) {
			for (int i = 0; i < FreightList.size(); i++) {
				dispatchSequence[i] = new Pair(i,
						0 - ((Freight) FreightList.get(i)).getPrevReadyTime()
								.toMinute());
			}
		} else if (mode == 2) {
			for (int i = 0; i < FreightList.size(); i++) {
				int value = (int) FreightList.get(i).getAllDuration();
				dispatchSequence[i] = new Pair(i, value);
			}
		}
		Arrays.sort(dispatchSequence);
	}

	/*
	 * This method returns the index (order) for the freight trains to dispatch,
	 * based on the dispatching sequence.
	 */
	public Freight getFreight(int index) {
		return FreightList.get(dispatchSequence[index].getIndex());
	}

	/*
	 * This method obtains the shortest time path for all the freight trains in
	 * the list.
	 */
	private void getAllPath(ArrayList<Freight> freightTrains) {
		for (int i = 0; i < freightTrains.size(); i++) {
			Freight singleFreight = freightTrains.get(i);
			network.setFreightList(freightTrains);
			network.setSingleFreight(singleFreight);
			network.AlternateDijkstra(
					stationTable.get(freightTrains.get(i).getOrigin()),
					freightTrains.get(i).getReadyTime());
			LinkedList<Station> tempPath = network.getPath(freightTrains.get(i)
					.getDest());
			freightTrains.get(i).setShortestTimePath(tempPath);
			TimeStamp tempDestTime = network.getDestReadyTime(freightTrains
					.get(i).getDest());
			freightTrains.get(i).setDestTime(tempDestTime);
			LinkedList<TimeWindow> tempTimeWindow = network
					.getTimeWindow(freightTrains.get(i).getDest()); // printSolution
																	// here
			freightTrains.get(i).setShortestTimeWindow(tempTimeWindow);
			freightTrains.get(i).setCurrentStation(tempPath.get(0)); // initialize
			freightTrains.get(i).setNextStation(tempPath.get(1));
			freightTrains.get(i).setCurrentTrack(
					trackTable.get(tempPath.get(0).getName() + " -> "
							+ tempPath.get(1).getName()));
			freightTrains.get(i).setNextTrack(
					trackTable.get(tempPath.get(1).getName() + " -> "
							+ tempPath.get(2).getName()));
			freightTrains.get(i).setPrevStation(null);
			freightTrains.get(i).setPrevTrack(null);
		}
	}

	/*
	 * This method updates the freight train parameters. It updates the duration
	 * for each freight train to travel from its current station to the next
	 * station. It also updates the duration for each freight train to travel
	 * from its current station to the destination station.
	 */
	private void updateFreightPara(ArrayList<Freight> freightTrains, int mode) {
		for (int i = 0; i < freightTrains.size(); i++) {
			LinkedList<Station> tempPath = freightTrains.get(i)
					.getShortestTimePath();
			Track currentTrack = freightTrains.get(i).getCurrentTrack();
			Station currentStation = freightTrains.get(i).getCurrentStation();
			Station nextStation = null;
			Track nextTrack = null;
			nextStation = currentTrack.getS2();
			String nextTrackName = nextStation.getName() + " -> "
					+ tempPath.get(tempPath.indexOf(nextStation) + 1).getName();
			nextTrack = trackTable.get(nextTrackName);
			Solver tempSolver = new Solver(tracks, stations, trackTable);
			tempSolver.setParameter(stationTable.get(freightTrains.get(i)
					.getOrigin()), stationTable.get(freightTrains.get(i)
					.getDest()), freightTrains.get(i).getLength(),
					freightTrains.get(i).getSpeed(), freightTrains.get(i)
							.getReadyTime());
			TimeWindow tempWindow = tempSolver.solveEachTrack(currentTrack);
			LinkedList<Station> tempPath1 = new LinkedList<Station>(
					tempPath.subList(tempPath.indexOf(currentStation),
							tempPath.indexOf(tempPath.getLast())));
			// System.out.println("tempPathSize: " + tempPath1.size());
			tempSolver.setOrigin(currentStation);
			LinkedList<TimeWindow> tempAllWindows = tempSolver.solve(tempPath1);
			double tempDuration = tempWindow.getDuration();
			double tempAllDuration = tempDuration;
			if (tempAllWindows.size() > 1) {
				tempAllDuration = tempAllWindows.getLast().getArrival()
						.toMinute()
						- tempAllWindows.getFirst().getDeparture().toMinute();
			}
			freightTrains.get(i).setReadyTime(tempWindow.getArrival());
			freightTrains.get(i)
					.setPrevReadyTime(tempSolver.getPrevReadyTime());
			freightTrains.get(i).setFirstDuration(tempDuration);
			freightTrains.get(i).setAllDuration(tempAllDuration);
			freightTrains.get(i).setNextStation(nextStation);
			freightTrains.get(i).setNextTrack(nextTrack);
			if (nextStation != currentStation && nextStation != null) {
				freightTrains.get(i).setCurrentStation(nextStation);
			}
			if (nextTrack != currentTrack && nextTrack != null) {
				freightTrains.get(i).setCurrentTrack(nextTrack);
			}
			freightTrains.get(i).setPrevStation(currentStation);
			freightTrains.get(i).setPrevTrack(currentTrack);
		}
		// System.out.println("FreightTrain size: " + freightTrains.size());
	}

	/*
	 * This method first generates the dispatching sequence. And find the
	 * schedule for each freight train on the current track that they are
	 * traveling along. Once the freight schedule is found on the current track,
	 * the time window is blocked onto the track.
	 */
	private void dispatch(ArrayList<Freight> freightTrains, int mode) {
		generateSequence();
		Solver tempSolver = new Solver(tracks, stations, trackTable);
		for (int i = 0; i < freightTrains.size(); i++) {
			Freight currentFreight = getFreight(i);
			currentFreight.getPrevStation();
			Track currentTrack = getFreight(i).getPrevTrack();
			tempSolver.setParameter(
					stationTable.get(currentFreight.getOrigin()),
					stationTable.get(currentFreight.getDest()),
					currentFreight.getLength(), currentFreight.getSpeed(),
					currentFreight.getPrevReadyTime());
			TimeWindow tempSolution = tempSolver.solveEachTrack(currentTrack);
			tempSolution.setFreightName(currentFreight.getName());
			LinkedList<TimeWindow> tempFreightSol = currentFreight
					.getSolution();
			tempFreightSol.add(tempSolution);
			currentFreight.setSolution(tempFreightSol);
			freightSolTable.put(currentFreight.getName(), tempFreightSol);

			System.out.println(tempSolution.toSolutionString());
			currentFreight.setReadyTime(tempSolution.getArrival());
			if (currentTrack.UporDownTrack()) {
				currentTrack.addUpTimeWindow(tempSolution);
			} else {
				currentTrack.addDownTimeWindow(tempSolution);
			}
			sort();
		}
	}

	/*
	 * This method takes in getAllPath() method, updateFreightPara() method, as
	 * well as the dispatch method. It iteratively solved for the schedule of
	 * each freight train along their corresponding path found by the
	 * getAllPath() method. The iteration process terminates until all freight
	 * trains have reached their destination.
	 */
	public void heuristicSchedule(ArrayList<Freight> freightTrains) {
		getAllPath(freightTrains);
		while (freightTrains.size() > 0) {
			LinkedList<Freight> toBeRemoved = new LinkedList<Freight>();
			for (int i = 0; i < freightTrains.size(); i++) {
				Station currentStation = freightTrains.get(i)
						.getCurrentStation();
				Station lastStation = freightTrains.get(i)
						.getShortestTimePath().getLast();
				if (currentStation.equals(lastStation)) { // freightTrain
															// movement
					toBeRemoved.add(freightTrains.get(i)); // complete
				}
			}
			if (toBeRemoved.size() > 0) {
				for (int i = 0; i < toBeRemoved.size(); i++) {
					freightTrains.remove(toBeRemoved.get(i));
				}
			}
			updateFreightPara(freightTrains, mode);
			dispatch(freightTrains, mode);
		}
	}

	public Hashtable<String, LinkedList<TimeWindow>> getFreightTable() {
		return freightSolTable;
	}

	/*
	 * The time window is sorted on all the tracks. 
	 */
	private void sort() {
		for (int i = 0; i < tracks.length; i++) {
			for (int j = 0; j < tracks[i].length; j++) {
				Collections.sort(tracks[i][j].getUpTimeWindows());
				Collections.sort(tracks[i][j].getDownTimeWindows());
			}
		}
	}

	
	/*
	 * This method pairs up heuristic criterion value with index.
	 * It is a helper method to find the dispatching sequence.
	 */
	@SuppressWarnings("rawtypes")
	private class Pair implements Comparable {
		private int index;
		private int value;

		public Pair(int i, int v) {
			index = i;
			value = v;
		}

		public int getIndex() {
			return index;
		}

		public int getValue() {
			return value;
		}

		public int compareTo(Object arg0) {
			if (value < (((Pair) arg0).getValue()))
				return -1;
			else if (value == (((Pair) arg0).getValue()))
				return 0;
			else
				return 1;
		}
	}
}
