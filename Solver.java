package iFreight;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;

/*
 * The Solver class contains methods that will perform the scheduling algorithm for 
 * each freight train on a single track.
 */

public class Solver {
	private Track[][] allTracks;
	private Station[] stations;
	private double freightSpeed;
	private TimeStamp readyTime, currentTime, prevReadyTime;
	private LinkedList<TimeWindow> solution;
	private TimeWindow eachTrackSol;
	private Hashtable<String, TimeStamp> station_readyTime;
	private Hashtable<String, Track> trackTable;
	private LinkedList<Station> fixedPath;
	public int currentStationIndex;
	@SuppressWarnings("unused")
	private Station origin;

	public Solver(Track[] tracks, Station[] allStations) {
		stations = new Station[tracks.length + 1];
		stations[0] = new Station(tracks[0].getS1().getName());
		for (int i = 0; i < stations.length - 1; i++) {
			stations[i] = new Station(tracks[i].getS2().getName());
		}
		station_readyTime = new Hashtable<String, TimeStamp>();
	}

	public Solver(Track[][] allTracks, Station[] allStations,
			Hashtable<String, Track> trackTable) {
		this.allTracks = allTracks;
		this.trackTable = trackTable;
	}

	/*
	 * This method sets relevant parameters to the freight trains, based on user
	 * input.
	 */
	public void setParameter(Station origin, Station destination,
			int freightLength, int freightSpeed, TimeStamp readyTime) {
		this.freightSpeed = freightSpeed;
		this.readyTime = new TimeStamp(readyTime);
	}

	public void setOrigin(Station origin) {
		this.origin = origin;
	}

	public void setFixedPath(LinkedList<Station> fixedPath) {
		this.fixedPath = fixedPath;
	}

	public LinkedList<Station> getFixedPath() {
		return fixedPath;
	}

	private boolean inBetween(TimeWindow T1, TimeWindow T2, TimeStamp AT) {
		return AT.compareTo(T1.getDeparture()) >= 0
				&& AT.compareTo(T2.getDeparture()) <= 0;
	}

	private LinkedList<TimeWindow> getUporDownWindows(Track track) {
		LinkedList<TimeWindow> windows = new LinkedList<TimeWindow>();
		if (UporDownTrack(track)) {
			windows = track.getUpTimeWindows();
		} else {
			windows = track.getDownTimeWindows();
		}
		return windows;
	}

	// upTrack as true, downTrack as false
	public boolean UporDownTrack(Track track) {
		return !track.getUpTimeWindows().isEmpty();
	}

	private void sort(Track track) {
		Collections.sort(track.getUpTimeWindows());
		Collections.sort(track.getDownTimeWindows());
	}

	// STEP 1 - get Ready Time
	// If headway constraints met, no change, if not, postponed ready time
	private TimeStamp getFreightDepart(TimeStamp AT, TimeWindow T1, double H,
			double v1) {
		TimeStamp DT = new TimeStamp(0, 0);
		if (AT.compareTo(T1.getDeparture().addMinutes((int) Math.ceil(H / v1))) < 0) {
			DT = new TimeStamp(T1.getDeparture().addMinutes(
					(int) Math.ceil(H / v1)));
		} else {
			DT = new TimeStamp(AT);
		}
		return DT;
	}

	// STEP 2 - Initialize freight train speed.
	// Set the freight train speed to the smaller one of the track speed limit,
	// and the maximum freight speed.
	private double initFreightSpeed(double maxSpeed, double speedLimit,
			TimeStamp DT) {
		double freightSpeed = 0;
		freightSpeed = (maxSpeed < speedLimit) ? maxSpeed : speedLimit;
		return freightSpeed /= 60;
	}

	// STEP 3 - Update the freight train speed.
	// If the freight train becomes close to the previous passenger train,
	// (i.e. the headway constraint is violated, reduce the freight train speed
	// so that the headway constrain is met along the movement.

	private double updateFreightSpeed(double initFreightSpeed, TimeStamp DT,
			TimeWindow T1, double H, double v1, double trackLength) {
		double freightSpeed = initFreightSpeed;
		if (DT.addMinutes((int) Math.ceil(trackLength / freightSpeed))
				.compareTo(T1.getArrival().addMinutes((int) Math.ceil(H / v1))) < 0) {
			freightSpeed = (trackLength / (T1.getArrival()
					.addMinutes((int) Math.ceil(H / v1))).getDifferent(DT));
		}
		return freightSpeed;
	}

	// STEP 4 - Check for constraints. If the headway constraints between the
	// freight train and the previous passenger train, as well as the freight
	// train, and the next passenger train are met. The freight train schedule
	// is
	// obtained, and the new arrival time to the next station from the current
	// station is updated. If the constraint is not met, the freight train will
	// wait for the next interval between two fixed time windows.
	private TimeStamp getAT(TimeStamp DT, TimeWindow T2,
			double updatedFreightSpeed, double trackLength, double H, double v2) {
		TimeStamp AT = new TimeStamp(0, 0);
		double freightSpeed = updatedFreightSpeed;
		if (DT.compareTo(T2.getDeparture().addMinutes((int) Math.ceil(-H / v2))) <= 0
				&& DT.addMinutes((int) Math.ceil(trackLength / freightSpeed))
						.compareTo(
								T2.getArrival().addMinutes(
										(int) Math.ceil(-H / v2))) <= 0) {
			AT = new TimeStamp(DT.addMinutes((int) Math.ceil(trackLength
					/ freightSpeed)));
			new TimeWindow("", "track", DT, AT, freightSpeed, 0);
		}
		return AT;
	}

	/*
	 * This method iteratively performs STEP 1 - STEP 4 to schedule each freight
	 * train on each track segment.
	 */

	public TimeWindow solveEachTrack(Track track) {
		prevReadyTime = readyTime;
		currentTime = new TimeStamp(readyTime.getHour(), readyTime.getMinute());
		double v1, v2, actualSpeed = 0;
		TimeStamp AT, temp;
		AT = new TimeStamp(currentTime);
		TimeStamp init = new TimeStamp(0, 0);
		new TimeStamp(Integer.MAX_VALUE, Integer.MAX_VALUE);
		TimeStamp DT = new TimeStamp(0, 0);
		TimeWindow T1, T2 = new TimeWindow("", "", init, init, 0, 0);
		LinkedList<TimeWindow> allWindows = new LinkedList<TimeWindow>();
		allWindows = getUporDownWindows(track);
		eachTrackSol = new TimeWindow("", "trackName", DT, AT, actualSpeed, 0);
		sort(track);

		double H = track.getHeadway();
		double trackLength = track.getTrackLength();
		double speedLimit = track.getLimitSpeed();

		if (allWindows.size() == 0) {
			DT = AT;
			actualSpeed = initFreightSpeed(freightSpeed, speedLimit, DT);
			AT = new TimeStamp(DT.addMinutes((int) Math.ceil(trackLength
					/ actualSpeed)));
			eachTrackSol = new TimeWindow("", track.toString(), DT, AT,
					actualSpeed, 0);
			eachTrackSol.setV(actualSpeed);
		}

		int j = 0;
		for (; j < allWindows.size(); j++) {
			if (j == allWindows.size() - 1) {
				break;
			} else {
				T1 = allWindows.get(j);
				T2 = allWindows.get(j + 1);
				if (inBetween(T1, T2, AT)) {
					break;
				}
			}
		}

		for (; j < allWindows.size(); j++) {
			if (j == allWindows.size() - 1) {
				T1 = allWindows.get(j);
				v1 = trackLength / T1.getDuration();
				DT = getFreightDepart(AT, T1, H, v1);
				actualSpeed = initFreightSpeed(freightSpeed, speedLimit, DT);
				AT = new TimeStamp(DT.addMinutes((int) Math.ceil(trackLength
						/ actualSpeed)));
				eachTrackSol = new TimeWindow("", track.toString(), DT, AT,
						actualSpeed, 0);
				eachTrackSol.setV(actualSpeed);
			} else {
				T1 = allWindows.get(j);
				T2 = allWindows.get(j + 1);
				v1 = trackLength / T1.getDuration(); // km. per minute
				v2 = trackLength / T2.getDuration();
				// Step 1
				DT = getFreightDepart(AT, T1, H, v1);
				// Step 2
				actualSpeed = initFreightSpeed(freightSpeed, speedLimit, DT);
				// Step 3
				actualSpeed = updateFreightSpeed(actualSpeed, DT, T1, H, v1,
						trackLength);
				// Step 4
				temp = getAT(DT, T2, actualSpeed, trackLength, H, v2);
				currentTime = new TimeStamp(AT);
				if (temp.compareTo(AT) > 0) {
					AT = temp;
					readyTime = new TimeStamp(AT);
					currentTime = new TimeStamp(AT);
					eachTrackSol = new TimeWindow("", track.toString(), DT, AT,
							actualSpeed, 0);
					eachTrackSol.setV(actualSpeed);
					break;
				}
			}

		}
		return eachTrackSol;
	}

	/*
	 * This method solves for the schedule for each freight train along the
	 * entire path, by iteratively performing the solveEachTrack() method.
	 */

	public LinkedList<TimeWindow> solve(LinkedList<Station> path) {
		solution = new LinkedList<TimeWindow>();
		currentTime = new TimeStamp(readyTime.getHour(), readyTime.getMinute());
		for (int i = 0; i < path.size() - 2; i++) {
			String tempTrackName = path.get(i).getName() + " -> "
					+ path.get(i + 1).getName();
			Track tempTrack = trackTable.get(tempTrackName);
			solution.add(solveEachTrack(tempTrack));
		}
		return solution;
	}

	public TimeStamp getPrevReadyTime() {
		return prevReadyTime;
	}

	public TimeWindow[][] toArray(LinkedList<TimeWindow> solution) {
		TimeWindow[][] timeWindowArray = new TimeWindow[allTracks.length][];

		return timeWindowArray;
	}

	public TimeStamp getReadyTime() {
		return readyTime;
	}

	public Hashtable<String, TimeStamp> getStationReadyTime() {
		return station_readyTime;
	}
}