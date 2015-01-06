package iFreight;

import org.jgrapht.*;
import org.jgrapht.alg.*;
import org.jgrapht.graph.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.awt.Dimension;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/* @ Date: Aug 14th, 2014
 * @ Author: Yingyan Samantha Wang
 * The Network class represent a graph model that consists of edges and vertices. 
 * In the particular case, tracks represent edges, and stations represent vertices.
 * It has methods to create graph based on track and station data.
 * It also has method to find the shortest distance path, as well as shortest time path 
 * from source station to any station within the graph.
 */

public class Network {
	Station[] stations;
	Track[][] tracks;
	Hashtable<String, Track> trackTable;
	Hashtable<String, Station> stationTable;
	SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> graph;
	Solver timeSolver;
	Freight singleFreight;
	ArrayList<Freight> FreightList;

	/*
	 * The constructor that takes in an array of all the stations within the
	 * network, all the tracks within the network, the hashtable that maps track
	 * name to actual tracks, and the hashtable that maps station names to
	 * actual stations.
	 */

	public Network(Station[] stations, Track[][] tracks,
			Hashtable<String, Track> trackTable,
			Hashtable<String, Station> stationTable) {
		this.stations = stations;
		this.tracks = tracks;
		this.trackTable = trackTable;
		this.stationTable = stationTable;
		timeSolver = new Solver(tracks, stations, trackTable);
	}

	/*
	 * This is the original Dijkstra algorithm that calculates the shortest
	 * distance from source station to every station within the network. It also
	 * keeps track of the shortest paths for each station. This method is to
	 * help understand the Dijkstra algorithm, and further develop the
	 * customized algorithm in this project. It is only for reference and not
	 * used in the actual execution of the program.
	 */

	public void Dijkstra(Station origin) {
		// initialization
		double infWeight = Double.MAX_VALUE;
		LinkedList<Station> visited = new LinkedList<Station>();
		LinkedList<Station> unvisited = new LinkedList<Station>();
		LinkedList<Station> path = new LinkedList<Station>();
		origin.setWeight(0);
		origin.setPath(path);
		for (int i = 0; i < stations.length; i++) {
			if (stations[i].getName() != origin.getName()) {
				stations[i].setWeight(infWeight);
				stations[i].setPath(path);
			}
			stationTable.put(stations[i].getName(), stations[i]);
		}

		for (int i = 0; i < stations.length; i++) {
			unvisited.add(stations[i]);
		}

		while (unvisited.size() > 0) {
			Station source = findMinWeight(unvisited);
			source = stationTable.get(source.getName());
			visited.add(source);
			unvisited.remove(source);
			for (int i = 0; i < source.getNeighbor().length; i++) {
				if (!isVisited(visited, source.getNeighbor()[i])) {
					source = stationTable.get(source.getName());
					Station tempNeighbor = stationTable.get(source
							.getNeighbor()[i]);
					Track tempTrack = trackTable.get(source.getName() + " -> "
							+ tempNeighbor.getName());
					LinkedList<Station> tempPath = new LinkedList<Station>();
					double dist = tempTrack.getTrackLength();
					if ((source.getWeight() + dist) < tempNeighbor.getWeight()) {
						tempNeighbor.setWeight(source.getWeight() + dist);
						tempPath.addAll(source.getPath());
						tempPath.add(source);
						tempNeighbor.setPath(tempPath);
						trackTable.put(tempTrack.toString(), tempTrack);
						stationTable.put(tempNeighbor.getName(), tempNeighbor);
					}
				}
			}
		}

	}

	/*
	 * This is the modified Dijkstra algorithm that calculates the shortest time
	 * path for each stations from source station within the network. It is
	 * similar to the original Dijkstra algorithm, only that it invokes the
	 * solveEachTrack() method from the Solver class. It performs algorithm to
	 * find the schedule for each freight train on each individual track. And
	 * obtain the earliest arrival time. It an earlier arrival time is found at
	 * a station, the shortest time path is updated. And the process goes on
	 * iteratively until all the stations within the network has been visited.
	 */

	public void AlternateDijkstra(Station origin, TimeStamp readyTime) {
		// initialization
		TimeStamp infReadyTime = new TimeStamp(Integer.MAX_VALUE,
				Integer.MAX_VALUE);
		TimeWindow initFreightWindow = new TimeWindow();
		initFreightWindow = null;
		LinkedList<Station> altVisited = new LinkedList<Station>();
		LinkedList<Station> altUnvisited = new LinkedList<Station>();
		LinkedList<Station> altTimePath = new LinkedList<Station>();
		origin.setReadyTime(readyTime);
		origin.setPath(altTimePath);
		for (int i = 0; i < stations.length; i++) {
			if (stations[i].getName() != origin.getName()) {
				stations[i].setReadyTime(infReadyTime);
				stations[i].setPath(altTimePath);
			}
			stationTable.put(stations[i].getName(), stations[i]);
		}

		for (int i = 0; i < tracks.length; i++) {
			for (int j = 0; j < tracks[i].length; j++) {
				tracks[i][j].setFreightWindow(initFreightWindow);
			}
		}

		for (int i = 0; i < stations.length; i++) {
			altUnvisited.add(stations[i]);
		}

		while (altUnvisited.size() > 0) {
			Station timeSource = findMinReadyTime(altUnvisited);
			timeSource = stationTable.get(timeSource.getName());
			altVisited.add(timeSource);
			altUnvisited.remove(timeSource);
			for (int i = 0; i < timeSource.getNeighbor().length; i++) {
				if (!isVisited(altVisited, timeSource.getNeighbor()[i])) {
					timeSource = stationTable.get(timeSource.getName());
					Station tempNeighbor = stationTable.get(timeSource
							.getNeighbor()[i]);
					Track tempTrack = trackTable.get(timeSource.getName()
							+ " -> " + tempNeighbor.getName());
					LinkedList<Station> tempPath = new LinkedList<Station>();
					timeSolver
							.setParameter(origin, origin,
									singleFreight.getLength(),
									singleFreight.getSpeed(),
									timeSource.getReadyTime());
					TimeWindow eachTrackSol = timeSolver
							.solveEachTrack(tempTrack);
					eachTrackSol.setFreightName(singleFreight.getName());
					if (eachTrackSol.getArrival().compareTo(
							tempNeighbor.getReadyTime()) < 0) {
						tempNeighbor.setReadyTime(eachTrackSol.getArrival());
						tempTrack.setFreight(singleFreight);
						tempTrack.setFreightWindow(eachTrackSol);
						tempPath.addAll(timeSource.getPath());
						tempPath.add(timeSource);
						tempNeighbor.setPath(tempPath);
						trackTable.put(tempTrack.toString(), tempTrack);
					}
				}
			}
		}
	}

	/*
	 * This method returns the shortest time path found, based on the input
	 * destination station. It is a linked list of Stations.
	 */

	public LinkedList<Station> getPath(String destination) {
		System.out.println("The Shortest Time Path is: ");
		LinkedList<Station> path = stationTable.get(destination).getPath();
		path.add(stationTable.get(destination));
		printPath(path);
		return path;
	}

	/*
	 * This method returns the ready time at the input station. The input is the
	 * station name, and the actual station is retreived through hashtable.
	 */

	public TimeStamp getDestReadyTime(String destination) {
		System.out.println("The readyTime at " + destination + ":");
		System.out.println(stationTable.get(destination).getReadyTime()
				.toString2());
		return stationTable.get(destination).getReadyTime();
	}

	/*
	 * This method returns the time windows, or the schedule of the freight
	 * train. Along the path from origin to destination.
	 */

	public LinkedList<TimeWindow> getTimeWindow(String destination) {
		LinkedList<TimeWindow> freightWindow = new LinkedList<TimeWindow>();
		LinkedList<Station> path = stationTable.get(destination).getPath();
		path.add(stationTable.get(destination));
		for (int i = 0; i < path.size() - 2; i++) {
			String tempTrackName = path.get(i).getName() + " -> "
					+ path.get(i + 1).getName();
			Track tempTrack = trackTable.get(tempTrackName);
			TimeWindow tempWindow = tempTrack.getFreightWindow();
			freightWindow.add(tempWindow);
			System.out.println(tempWindow.toSolutionString());
		}
		return freightWindow;
	}

	/*
	 * This method returns a boolean value of whether the station has been
	 * visited. It is used in the Dijkstra algorithms. If the station is
	 * visited, a true value is returned. If the station is not visited, a falst
	 * value is returned.
	 */

	private boolean isVisited(LinkedList<Station> visited, String name) {
		boolean isVisited = false;
		for (int i = 0; i < visited.size(); i++) {
			if (visited.get(i).getName() == name) {
				isVisited = true;
				break;
			}
		}
		return isVisited;

	}

	/*
	 * This method returns the station with the current minimum ready time. It
	 * is used in the alternate Dijkstra algorithm, when at the beginning of
	 * each iteration, the station with minimum ready time will be selected as
	 * the current source station, and all of its unvisited neighbors will be
	 * examined.
	 */

	private Station findMinReadyTime(LinkedList<Station> unvisited) {
		LinkedList<TimeStamp> readyTime = new LinkedList<TimeStamp>();
		for (int i = 0; i < unvisited.size(); i++) {
			readyTime.add(unvisited.get(i).getReadyTime());
		}
		int minIndex = readyTime.indexOf(Collections.min(readyTime));
		return unvisited.get(minIndex);
	}

	/*
	 * This method returns the station with the current shortest distance. It is
	 * a private method used in the original Dijkstra algorithm, where at the
	 * beginning of each iteration, the unvisited station with the shortest
	 * distance is selected to be the source station, and all of its neighbors
	 * will be examined.
	 */
	private Station findMinWeight(LinkedList<Station> unvisited) {
		LinkedList<Double> weight = new LinkedList<Double>();
		for (int i = 0; i < unvisited.size(); i++) {
			weight.add(unvisited.get(i).getWeight());
		}
		int minIndex = weight.indexOf(Collections.min(weight));
		return unvisited.get(minIndex);
	}

	/*
	 * This method is used to create the graph that has all the station and
	 * track information.
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public UndirectedSparseGraph getGraph(Station[] stations, Track[][] tracks) {
		UndirectedSparseGraph g = new UndirectedSparseGraph();
		for (int i = 1; i < stations.length - 1; i++) {
			g.addVertex(stations[i].getName());
		}
		for (int i = 1; i < tracks.length; i++) {
			for (int j = 0; j < tracks[i].length; j++) {
				g.addEdge(tracks[i][j].toString(), tracks[i][j].getS1()
						.getName(), tracks[i][j].getS2().getName());
			}
		}
		return g;

	}

	/*
	 * This method creates a visualization server based on the input graph
	 * created from the track and station information. It uses the open source
	 * jung visualization library.
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public VisualizationImageServer getVisualizer(
			UndirectedSparseGraph undirectedSparseGraph) {
		VisualizationImageServer vs = new VisualizationImageServer(
				new CircleLayout(undirectedSparseGraph),
				new Dimension(250, 360));
		for (int i = 0; i < stations.length; i++) {
			vs.getRenderContext().setVertexLabelTransformer(
					new ToStringLabeller() {
						public String transform(Station v) {

							return v.getName();
						}
					});
		}

		return vs;
	}

	/*
	 * The shortesPath, and kShortestPath methods are not actually used in this
	 * entire program. The shortestPath method gives the shortest distance path,
	 * while the kShortestPath gives the second, third shortest paths, and so on
	 * so forth. Both methods use the JGraphT library, which might be useful in
	 * graph algorithm
	 */

	public GraphPath shortestPath(Graph graph, String S1, String S2) {
		DijkstraShortestPath pp = new DijkstraShortestPath(graph, S1, S2);
		return pp.getPath();
	}

	public List kshortestPath(Graph graph, String S1, String S2, int degree) {
		KShortestPaths pp2 = new KShortestPaths(graph, S1, degree);
		List all_pp = pp2.getPaths(S2);
		return all_pp;
	}

	/*
	 * ToString methods for printing path, and alternative paths.
	 */

	public String toString(GraphPath path) {
		String formattedPath = path.getStartVertex().toString();
		String[] edges = getAllEdges(path);
		double weight = getWeight(path);
		for (int i = 1; i < edges.length; i++) {
			formattedPath = formattedPath + " -> " + parseStation(edges[i])[0];
		}
		formattedPath = formattedPath + " -> " + path.getEndVertex().toString()
				+ "\nwith weight of: " + weight
				+ "\n---------------------------";
		return formattedPath;
	}

	public String toString(List kpaths) {
		String formattedPath = "";
		GraphPath[] allPaths = getAllPaths(kpaths);
		double[] allweights = getWeight(kpaths);
		for (int i = 0; i < allPaths.length; i++) {
			formattedPath = formattedPath + toString(allPaths[i]) + "\n";
		}
		return formattedPath;
	}

	/*
	 * Private method used within toString method
	 */

	private String[] parseStation(String edge) {
		String[] stationName = new String[2];
		int parseIndex = edge.indexOf(":");
		stationName[0] = edge.substring(1, parseIndex - 1);
		stationName[1] = edge.substring(parseIndex + 2, edge.length() - 1);
		return stationName;
	}

	/*
	 * A bunch of getters and setters
	 */

	public void setSingleFreight(Freight singleFreight) {
		this.singleFreight = singleFreight;
	}

	public void setFreightList(ArrayList<Freight> FreightList) {
		this.FreightList = FreightList;
	}

	public Freight getSingleFreight() {
		return singleFreight;
	}

	public ArrayList<Freight> getFreightList() {
		return FreightList;
	}

	public Station[] getStations() {
		return stations;
	}

	public Track[][] getTracks() {
		return tracks;
	}

	public GraphPath[] getAllPaths(List kpaths) {
		GraphPath[] allPaths = new GraphPath[kpaths.size()];
		for (int i = 0; i < kpaths.size(); i++) {
			allPaths[i] = (GraphPath) kpaths.get(i);
		}
		return allPaths;
	}

	private double[] getWeight(List kpaths) {
		double[] weights = new double[kpaths.size()];
		for (int i = 0; i < kpaths.size(); i++) {
			weights[i] = ((GraphPath) kpaths.get(i)).getWeight();
		}
		return weights;
	}

	private double getWeight(GraphPath path) {
		double weights = path.getWeight();
		return weights;
	}

	public String[] getAllEdges(GraphPath path) {
		int edgeLength = path.getEdgeList().size();
		String[] edges = new String[edgeLength];
		for (int i = 0; i < edgeLength; i++) {
			edges[i] = path.getEdgeList().get(i).toString();
			// System.out.println("test---- "+ edges[i]);
		}
		return edges;
	}

	/*
	 * Below are the Debug printing methods
	 */

	private void printPath(LinkedList<Station> path) {
		if (path.size() > 0) {
			for (int i = 0; i < path.size() - 1; i++) {
				System.out.print(path.get(i).getName() + "->");
			}
			System.out.println(path.getLast().getName());
		} else {
			System.out.println("empty");

		}
	}

	private void printReadyTime(Station[] stations) {
		for (int i = 0; i < stations.length; i++) {
			System.out.println("@" + stations[i].getName());
			System.out.println("ReadyTime is: "
					+ stationTable.get(stations[i].getName()).getReadyTime()
							.toString2());
		}
	}
}
