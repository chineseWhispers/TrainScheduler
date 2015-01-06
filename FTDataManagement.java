package iFreight;

/** @Date: Aug 14th, 2014
 *  @Author: Yingyan Samantha Wang
 *  FTDatamanagement class is the class that manages all the input file data.
 *  It reads in three EXCEL files: Station Data, Train Data, and Schedule Data.
 *  The Station Data consists information about the stations, the connectivity 
 *  of the rail network, the length of tracks, speed limits, headway constraints, etc. 
 *  The Train Data consists information about the passenger trains' weekly schedule,
 *  while the Schedule Data consists of the passenger trains' daily schedule.
 *  Specifically, it contains the arrival and departure time of each passenger train at
 *  each station.
 *  This class also contains methods that produce the graph model describing the railroad
 *  network. 
 */

import java.awt.*;
import java.io.*;
import java.util.*;
import jxl.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.decorators.*;

public class FTDataManagement {
	private Workbook stationWorkBook, trainWorkBook, scheduleWorkBook;
	private Sheet[] stationSheet, scheduleSheet, trainSheet;
	private Sheet upTrainSheet, downTrainSheet, upScheduleSheet,
			downScheduleSheet;
	private String[] stationSheetNames, trainSheetNames, scheduleSheetNames;
	private int stationCount, upTrainCount, downTrainCount;
	private int[] neighborCount;
	private Station[] stations;
	private Track[][] tracks;
	private PsgTrain[] upPsg, downPsg;
	private Network network;
	private Hashtable<String, Station> stationTable = new Hashtable<String, Station>();
	private Hashtable<String, Track> trackTable = new Hashtable<String, Track>();
	private ArrayList<Freight> FreightList;
	private MultFreight trainList;
	private String stationFilePath, trainFilePath, scheduleFilePath;
	private Hashtable<String, LinkedList<TimeWindow>> freightSolTable;
	private PsgPlot passengerPlot;

	/*
	 * Constructor
	 * 
	 * @ Variables: station file path, train file path, schedule file path The
	 * paths for the input EXCEL files. It also constructs FreightList, which
	 * will contain a list of freight trains to be scheduled.
	 */

	public FTDataManagement(String stationFilePath, String trainFilePath,
			String scheduleFilePath) {
		FreightList = new ArrayList<Freight>();
		this.stationFilePath = stationFilePath;
		this.trainFilePath = trainFilePath;
		this.scheduleFilePath = scheduleFilePath;
	}

	/*
	 * @ openStationFile
	 * 
	 * @ This method is responsible for the reading of all the input files.
	 * Information about stations, tracks, passenger trains on both directions,
	 * as well as passenger train schedules are all read through the method.
	 * Stations, and tracks are all added to the entire rail network, and all
	 * the schedule time window information are added to corresponding tracks.
	 */

	public void openStationFile() {
		try {
			System.out.println("stationFile Path: " + stationFilePath);
			stationWorkBook = Workbook.getWorkbook(new File(stationFilePath));
			System.out.println("stationFile Path: " + trainFilePath);
			trainWorkBook = Workbook.getWorkbook(new File(trainFilePath));
			System.out.println("stationFile Path: " + scheduleFilePath);
			scheduleWorkBook = Workbook.getWorkbook(new File(scheduleFilePath));
			System.out.println("File Opening...");
			stationCount = stationWorkBook.getNumberOfSheets();
			neighborCount = new int[stationCount];
			tracks = new Track[stationCount][];
			stations = new Station[stationCount];
			stationSheetNames = stationWorkBook.getSheetNames();
			trainSheetNames = trainWorkBook.getSheetNames();
			scheduleSheetNames = scheduleWorkBook.getSheetNames();
			stationSheet = new Sheet[stationSheetNames.length];
			trainSheet = new Sheet[trainSheetNames.length];
			scheduleSheet = new Sheet[scheduleSheetNames.length];
			for (int i = 0; i < stationSheetNames.length; i++) {
				stationSheet[i] = stationWorkBook
						.getSheet(stationSheetNames[i]);
				neighborCount[i] = stationSheet[i].getRows() - 1;
				tracks[i] = new Track[neighborCount[i]];
			}
			// trainSheet will only contain two sheets (UpTrain & DownTrain)
			trainSheet[0] = trainWorkBook.getSheet(trainSheetNames[0]);
			trainSheet[1] = trainWorkBook.getSheet(trainSheetNames[1]);
			upTrainSheet = trainSheet[0];
			downTrainSheet = trainSheet[1];
			upTrainCount = trainSheet[0].getRows() - 1;
			downTrainCount = trainSheet[1].getRows() - 1;
			upPsg = new PsgTrain[upTrainCount];
			downPsg = new PsgTrain[downTrainCount];
			for (int i = 0; i < upTrainCount; i++) {
				upPsg[i] = new PsgTrain(trainSheet[0].getCell(0, i + 1)
						.getContents());
			}
			for (int i = 0; i < downTrainCount; i++) {
				downPsg[i] = new PsgTrain(trainSheet[1].getCell(0, i + 1)
						.getContents());
			}
			scheduleSheet[0] = scheduleWorkBook.getSheet(scheduleSheetNames[0]);
			scheduleSheet[1] = scheduleWorkBook.getSheet(scheduleSheetNames[1]);
			upScheduleSheet = scheduleSheet[0];
			downScheduleSheet = scheduleSheet[1];

		} catch (Exception e) {
			e.printStackTrace();
		} // end of try-catch

		addStation();
		addTrack();

		LinkedList<TimeWindow> uptimewindow = addUpTimeWindow();
		LinkedList<TimeWindow> downtimewindow = addDownTimeWindow();
		sort();
		passengerPlot = new PsgPlot(trackTable);
		passengerPlot.setAllUpWindows(uptimewindow);
		passengerPlot.setAllDownWindows(downtimewindow);
	}

	/*
	 * This method constructs network, and freightTrain list information, it
	 * calls the method within MultFreight class, that performs multiple train
	 * heuristic dispatching algorithms. Input variable mode can be integer of
	 * 0, 1, 2. Each represents a different heuristic dispatching sequence. 
	 * 0 as FCFS, 1, LCFS, 2 as shortest travel time.
	 */

	public void schedule(int mode) {
		network = new Network(stations, tracks, trackTable, stationTable);
		trainList = new MultFreight(stationTable, trackTable, network,
				FreightList, stations, tracks, mode);
		trainList.heuristicSchedule(FreightList);
		setFreightSolTable(trainList.getFreightTable());
	}

	/*
	 * Create visualization server through jung graph visualization library. Set
	 * properties of the visualization server, such as size, background color,
	 * etc. This method also takes in an array of String that represents
	 * selected vertex names. All the selected vertex will be labeled in a
	 * different color than the unselected vertexes.
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void visualization(JPanel graphPanel, Color color,
			String[] pickVertex, String pathName) {
		network = new Network(stations, tracks, trackTable, stationTable);
		// String[] testStations = {"StationA", "StationB","StationC"};
		VisualizationImageServer vs = network.getVisualizer(network.getGraph(
				stations, tracks));
		vs.setLayout(new BorderLayout());
		vs.setBackground(color);
		vs.setSize(100, 100);
		JLabel path = new JLabel(pathName);
		vs.add(path, BorderLayout.NORTH);
		if (pickVertex.length > 0) {
			System.out.println("path exist");
			System.out.println(pickVertex.length);
			for (int i = 0; i < pickVertex.length; i++) {
				System.out.println(pickVertex[i]);
				vs.getPickedVertexState().pick(pickVertex[i], true);
			}
			for (int i = 0; i < pickVertex.length - 1; i++) {
				vs.getPickedEdgeState().pick(
						pickVertex[i] + " -> " + pickVertex[i + 1], true);
				vs.getPickedEdgeState().pick(
						pickVertex[i + 1] + " -> " + pickVertex[i], true);
			}
			vs.getRenderContext().setVertexFillPaintTransformer(
					new PickableVertexPaintTransformer<Station>(vs
							.getPickedVertexState(), Color.LIGHT_GRAY,
							Color.BLUE));
			vs.getRenderContext()
					.setEdgeDrawPaintTransformer(
							new PickableVertexPaintTransformer<Track>(vs
									.getPickedEdgeState(), Color.LIGHT_GRAY,
									Color.BLUE));

			vs.setLocation(100, 100);
		}

		// graphPanel.setSize(new Dimension(400,400));
		graphPanel.add(vs);

	}

	/*
	 * Add all the stations onto the graph network, and use hashmap to map
	 * station names to station
	 */

	private void addStation() {
		for (int i = 0; i < stationCount; i++) {
			double sidingLength = 0;
			int index = 0;
			String name = stationSheetNames[i];
			String[] neighbor = new String[neighborCount[i]];
			for (int j = 0; j < neighborCount[i]; j++) {
				neighbor[j] = stationSheet[i].getCell(0, j + 1).getContents();
			}
			stations[i] = new Station(neighbor, name, sidingLength, index);
		}
		for (int i = 0; i < stationCount; i++) {
			stationTable.put(stations[i].getName(), stations[i]);
		}
	}

	/*
	 * Add all the tracks onto the graph network. Put all the track information
	 * to a hash map, which maps track name to track
	 */

	private void addTrack() {
		for (int i = 0; i < stationCount; i++) {
			double[] trackLength = new double[neighborCount[i]];
			double[] limitSpeed = new double[neighborCount[i]];
			double[] headway = new double[neighborCount[i]];
			for (int j = 0; j < neighborCount[i]; j++) {
				trackLength[j] = Integer.parseInt(stationSheet[i].getCell(1,
						j + 1).getContents());
				limitSpeed[j] = Integer.parseInt(stationSheet[i].getCell(2,
						j + 1).getContents());
				headway[j] = Integer.parseInt(stationSheet[i].getCell(3, j + 1)
						.getContents());
				tracks[i][j] = new Track(stations[i], castStation(
						stations[i].getOneNeighbor(j), stations),
						trackLength[j], limitSpeed[j], headway[j]);
			}
		}
		for (int i = 0; i < tracks.length; i++) {
			for (int j = 0; j < tracks[i].length; j++) {
				String key = tracks[i][j].toString();
				System.out.println(key);
				trackTable.put(key, tracks[i][j]);
			}
		}
	}

	/*
	 * Add all the time windows onto the upward tracks.
	 */

	private LinkedList<TimeWindow> addUpTimeWindow() {
		LinkedList<TimeWindow> uptimewindows = new LinkedList<TimeWindow>();
		for (int i = 0; i < upTrainCount; i++) {
			for (int d = 0; d < 7; d++) {
				boolean available = upTrainSheet.getCell(d + 1, i + 1)
						.getContents().compareTo("1") == 0;
				if (available) {
					int x = 1;
					String start = upScheduleSheet.getCell(i + 2, 2 * x + 1)
							.getContents();
					while (start.compareTo("-") == 0) {
						x++;
						start = upScheduleSheet.getCell(i + 2, 2 * x + 1)
								.getContents();
					}
					int startHr = convertHour(start, d);
					int startMin = convertMin(start);
					TimeStamp startTimeStamp = new TimeStamp(startHr, startMin);
					TimeStamp arriveTimeStamp = startTimeStamp;// initialization
					TimeStamp departTimeStamp = startTimeStamp;// initialization
					String prevStation = "";
					String prevDepart = "";
					String key = "";
					String departStamp = "";
					String arriveStamp = "";
					for (int j = x; j <= stationCount; j++) {
						String arrive = upScheduleSheet.getCell(i + 2, 2 * j)
								.getContents();
						String depart = upScheduleSheet.getCell(i + 2,
								2 * j + 1).getContents();
						if (arrive.compareTo("-") == 0)
							continue;
						String arriveStation = upScheduleSheet
								.getCell(0, 2 * j).getContents();
						if (prevStation != "") {
							key = prevStation + " -> " + arriveStation;
							departStamp = prevDepart;
							arriveStamp = arrive;
							arriveTimeStamp = getArrivalStamp(startTimeStamp,
									arriveStamp, d);
							departTimeStamp = getDepartureStamp(startTimeStamp,
									departStamp, d);
							TimeWindow tempWindow = new TimeWindow(
									upPsg[i].getName(), key, departTimeStamp,
									arriveTimeStamp, 1);// upTrain
							uptimewindows.add(tempWindow);
							trackTable.get(key).addUpTimeWindow(tempWindow);
							trackTable.put(key, trackTable.get(key));
						}
						String departStation = upScheduleSheet.getCell(0,
								2 * j + 1).getContents();
						prevStation = departStation;
						prevDepart = depart;
					}
				}
			}
		}
		return uptimewindows;
	}

	/*
	 * Add all the time windows onto the downward tracks.
	 */

	private LinkedList<TimeWindow> addDownTimeWindow() {
		LinkedList<TimeWindow> downtimewindows = new LinkedList<TimeWindow>();
		for (int i = 0; i < downTrainCount; i++) {
			for (int d = 0; d < 7; d++) {
				boolean available = downTrainSheet.getCell(d + 1, i + 1)
						.getContents().compareTo("1") == 0;
				if (available) {
					int x = 1;
					String start = downScheduleSheet.getCell(i + 2, 2 * x + 1)
							.getContents();
					while (start.compareTo("-") == 0) {
						x++;
						start = downScheduleSheet.getCell(i + 2, 2 * x + 1)
								.getContents();
					}
					int startHr = convertHour(start, d);
					int startMin = convertMin(start);
					TimeStamp startTimeStamp = new TimeStamp(startHr, startMin);
					TimeStamp arriveTimeStamp = startTimeStamp;// initialization
					TimeStamp departTimeStamp = startTimeStamp;// initialization
					String prevStation = "";
					String prevDepart = "";
					String key = "";
					String departStamp = "";
					String arriveStamp = "";
					for (int j = x; j <= stationCount; j++) {
						String arrive = downScheduleSheet.getCell(i + 2, 2 * j)
								.getContents();
						String depart = downScheduleSheet.getCell(i + 2,
								2 * j + 1).getContents();
						if (arrive.compareTo("-") == 0)
							continue;
						String arriveStation = downScheduleSheet.getCell(0,
								2 * j).getContents();
						if (prevStation != "") {
							key = prevStation + " -> " + arriveStation;
							departStamp = prevDepart;
							arriveStamp = arrive;
							arriveTimeStamp = getArrivalStamp(startTimeStamp,
									arriveStamp, d);
							departTimeStamp = getDepartureStamp(startTimeStamp,
									departStamp, d);
							TimeWindow tempWindow = new TimeWindow(
									downPsg[i].getName(), key, departTimeStamp,
									arriveTimeStamp, 1);// downTrain
							downtimewindows.add(tempWindow);
							trackTable.get(key).addDownTimeWindow(tempWindow);
							trackTable.put(key, trackTable.get(key));
						}
						String departStation = downScheduleSheet.getCell(0,
								2 * j + 1).getContents();
						prevStation = departStation;
						prevDepart = depart;
					}
				}
			}
		}
		return downtimewindows;
	}

	/*
	 * Obtain the hour component of time
	 */

	private int convertHour(String time, int day) {
		return 24 * day
				+ Integer.parseInt(time.substring(0, time.indexOf(":")));
	}

	/*
	 * Obtain the minute component of time
	 */

	private int convertMin(String time) {
		return Integer.parseInt(time.substring(time.indexOf(":") + 1));
	}

	/*
	 * A bunch of getter and setter methods
	 */

	public PsgPlot getPlot() {
		return passengerPlot;
	}

	public void setFreightList(ArrayList<Freight> FreightList) {
		this.FreightList = FreightList;
	}

	public void setFreightSolTable(
			Hashtable<String, LinkedList<TimeWindow>> freightSolTable) {
		this.freightSolTable = freightSolTable;
	}

	public Hashtable<String, LinkedList<TimeWindow>> getFreightSolTable() {
		return freightSolTable;
	}

	public String[] getStationNames() {
		String[] stationNames = new String[stationCount];
		for (int i = 0; i < stationCount; i++) {
			stationNames[i] = stations[i].getName();
		}
		return stationNames;
	}

	public Network getNetwork() {
		return network;
	}

	public Station[] getStations() {
		return stations;
	}

	public Track[][] getTracks() {
		return tracks;
	}

	public Hashtable<String, Track> getTrackTable() {
		return trackTable;
	}

	public void setStationFileName(String stationFilePath) {
		this.stationFilePath = stationFilePath;
	}

	public void setTrainFileName(String trainFilePath) {
		this.trainFilePath = trainFilePath;
	}

	public void setScheduleFileName(String scheduleFilePath) {
		this.scheduleFilePath = scheduleFilePath;
	}

	public String getStationFileName() {
		return stationFilePath;
	}

	public String getTrainFileName() {
		return trainFilePath;
	}

	public String getScheduleFileName() {
		return scheduleFilePath;
	}

	private TimeStamp getArrivalStamp(TimeStamp startTime, String arriveTime,
			int d) {
		int arriveHr = convertHour(arriveTime, d);
		int arriveMin = convertMin(arriveTime);
		TimeStamp arrivalStamp = new TimeStamp(arriveHr, arriveMin);
		if (arrivalStamp.compareTo(startTime) < 0) {
			arriveHr = 24 + convertHour(arriveTime, d);
			arrivalStamp = new TimeStamp(arriveHr, arriveMin);
		}
		return arrivalStamp;
	}

	private TimeStamp getDepartureStamp(TimeStamp startTime, String departTime,
			int d) {
		int departHr = convertHour(departTime, d);
		int departMin = convertMin(departTime);
		TimeStamp departStamp = new TimeStamp(departHr, departMin);
		if (departStamp.compareTo(startTime) < 0) {
			departHr = 24 + convertHour(departTime, d);
			departStamp = new TimeStamp(departHr, departMin);
		}
		return departStamp;
	}

	/*
	 * This method sorts all the up and down time windows on tracks in order.
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
	 * This method maps the station name in String back to the Station class,
	 * which contains all other information, such as neighbors, etc.
	 */
	private Station castStation(String name, Station[] stations) {
		Station station = null;
		for (int i = 0; i < stations.length; i++) {
			if (name.equals(stations[i].getName())) {
				station = stations[i];
			}
		}
		return station;
	}

	/** Below are all Debug Helping Methods - Printing methods **/

	private void printStation(Station[] stations) {
		System.out.println("----------- Print Station Names -----------");
		for (int i = 0; i < stations.length; i++) {
			System.out.println("Station Name: " + stations[i].getName());
			System.out.println(stations[i].getName() + " has neighbors of "
					+ stations[i].toString(stations[i].getNeighbor()));
		}

	}

	private void printTrack(Track[][] tracks) {
		System.out.println("---------- Print Track Info----------");
		for (int i = 0; i < tracks.length; i++) {
			for (int j = 0; j < tracks[i].length; j++) {
				System.out.print(tracks[i][j].getS1().getName() + "-->");
				System.out.println(tracks[i][j].getS2().getName());
				System.out.println("Track's length is: "
						+ tracks[i][j].getTrackLength());
				System.out.println("Track's limitSpeed is "
						+ tracks[i][j].getLimitSpeed());
				System.out.println("Track's headway is "
						+ tracks[i][j].getHeadway());
				System.out.println("******************");
			}
		}
	}

	private String printUpTimeWindow(String key) {
		String fullTimeWindow = "";
		String fullTimeWindow2 = "";
		for (int i = 0; i < trackTable.get(key).getUpTimeWindows().size(); i++) {
			String timeWindowInfo = trackTable.get(key).getUpTimeWindows()
					.get(i).toString()
					+ trackTable.get(key).getUpTimeWindows().get(i).toString2()
							.substring(2);
			fullTimeWindow2 = fullTimeWindow2 + timeWindowInfo + "\n";
			fullTimeWindow = fullTimeWindow + timeWindowInfo;
		}
		if (fullTimeWindow != "") {
			fullTimeWindow = fullTimeWindow2;
		}
		return fullTimeWindow;
	}

	private String printDownTimeWindow(String key) {
		String fullTimeWindow = "";
		String fullTimeWindow2 = "";
		for (int i = 0; i < trackTable.get(key).getDownTimeWindows().size(); i++) {
			String timeWindowInfo = trackTable.get(key).getDownTimeWindows()
					.get(i).toString()
					+ trackTable.get(key).getDownTimeWindows().get(i)
							.toString2().substring(2);
			fullTimeWindow2 = fullTimeWindow2 + timeWindowInfo + "\n";
			fullTimeWindow = fullTimeWindow + timeWindowInfo;
		}
		if (fullTimeWindow != "") {
			fullTimeWindow = fullTimeWindow2;
		}
		return fullTimeWindow;
	}

	private void printAllUpTimeWindow(Track[][] tracks) {
		System.out.println("-------Print Up TimeWindow----------");
		for (int i = 0; i < tracks.length; i++) {
			for (int j = 0; j < tracks[i].length; j++) {
				String fullTimeWindow = printUpTimeWindow(tracks[i][j]
						.toString());
				if (fullTimeWindow != "") {
					System.out.println(tracks[i][j].toString() + ": ");
					System.out.println(fullTimeWindow);
				}

			}
		}
	}

	private void printAllDownTimeWindow(Track[][] tracks) {
		System.out.println("-------Print Down TimeWindow----------");
		for (int i = 0; i < tracks.length; i++) {
			for (int j = 0; j < tracks[i].length; j++) {
				String fullTimeWindow = printDownTimeWindow(tracks[i][j]
						.toString());
				if (fullTimeWindow != "") {
					System.out.println(tracks[i][j].toString() + ": ");
					System.out.println(fullTimeWindow);
				}
			}
		}
	}

	private void printSolution(LinkedList<TimeWindow> solution) {
		System.out.println("Print Solution: ");
		for (int i = 0; i < solution.size(); i++) {
			System.out.println(solution.get(i).toSolutionString());
		}
	}
}
