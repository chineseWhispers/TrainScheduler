package iFreight;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/*
 * The PsgPlot class contains variables and methods to create the velocity profiles 
 * visualization or both freight and passenger trains.
 */

public class PsgPlot {

	private LinkedList<TimeWindow> allupwindows;
	private Hashtable<String, Track> trackTable;
	private ArrayList<Integer> freightIndex;

	public PsgPlot(Hashtable<String, Track> trackTable) {
		this.trackTable = trackTable;
	}

	public void setAllUpWindows(LinkedList<TimeWindow> allupwindows) {
		this.allupwindows = allupwindows;
	}

	public void setAllDownWindows(LinkedList<TimeWindow> alldownwindows) {
	}

	public void printallupwindows() {
		for (int i = 0; i < allupwindows.size(); i++) {
			System.out.println(allupwindows.get(i).toString2());
		}
	}

	/*
	 * This method maps the departure time, arrival time, departing station,
	 * arriving station on to an XY plot. The X - axis is the time axis, while
	 * the Y - axis is the position axis.
	 */
	public LinkedList<XYSeriesCollection> mapPath(String[] paths,
			String currentFreight) {
		LinkedList<XYSeriesCollection> dataset = new LinkedList<XYSeriesCollection>();
		int origin = 0;
		for (int i = 0; i < paths.length - 1; i++) {
			String trackName = paths[i] + " -> " + paths[i + 1];
			System.out.println("--------" + trackName);
			double tracklength = trackTable.get(trackName).getTrackLength();
			int dest = (int) (origin + tracklength);
			LinkedList<TimeWindow> windows = trackTable.get(trackName)
					.getUpTimeWindows();
			if (windows.size() == 0) {
				windows = trackTable.get(trackName).getDownTimeWindows();
			}
			for (int j = 0; j < windows.size(); j++) {
				if (windows.get(j).getV() <= 0
						|| windows.get(j).getTrain() == currentFreight) {
					XYSeries series = new XYSeries("");
					System.out.println("Solution))))))"
							+ windows.get(j).toSolutionString());
					series.add(windows.get(j).getDeparture().toMinute(), origin);
					series.add(windows.get(j).getArrival().toMinute(), dest);
					System.out
							.println(windows.get(j).getDeparture().toMinute());
					System.out.println(windows.get(j).getArrival().toMinute());
					XYSeriesCollection tempdataset = new XYSeriesCollection();
					tempdataset.addSeries(series);
					dataset.add(tempdataset);
					System.out.println(windows.get(j).getTrain());
				}
			}
			origin = dest;
		}
		return dataset;
	}

	/*
	 * This method determines whether a train is a freight train, or a passenger
	 * train. It is determined by examining the velocity profile. Only the
	 * freight trains' velocity is set under the class Freight, and the velocity
	 * of the passenger trains are set to zero. The velocity plot for the
	 * freight train is highlighted as a different color than the passenger
	 * train.
	 */
	public ArrayList<Integer> highlightFreight(String[] paths,
			String currentFreight) {
		System.out.println("CurrentFreight: " + currentFreight);
		freightIndex = new ArrayList<Integer>();
		int count = 0;
		for (int i = 0; i < paths.length - 1; i++) {
			String trackName = paths[i] + " -> " + paths[i + 1];
			LinkedList<TimeWindow> windows = trackTable.get(trackName)
					.getUpTimeWindows();
			if (windows.size() == 0) {
				windows = trackTable.get(trackName).getDownTimeWindows();
			}
			for (int j = 0; j < windows.size(); j++) {

				if (windows.get(j).getTrain() == currentFreight) {
					freightIndex.add(count);
					System.out.println("count" + count);
				}
				if (windows.get(j).getV() <= 0
						|| windows.get(j).getTrain() == currentFreight) {
					count++;
				}
			}
		}
		return freightIndex;
	}

	/* 
	 * This is the method where the xy plot is created. 
	 */
	
	public JPanel createPlot(LinkedList<XYSeriesCollection> dataset,
			ArrayList<Integer> freightIndex) {

		String sYAxis = "Position";
		String sYAxisUOM = "km";
		String sYAxisLabel = sYAxis + "(" + sYAxisUOM + ")";
		String sXAxis = "Time";
		String sXAxisUOM = "Min";
		String sXAxisLabel = sXAxis + "(" + sXAxisUOM + ")";

		JFreeChart chart = ChartFactory.createXYLineChart("", // Title
				sXAxisLabel, // x-axis Label
				sYAxisLabel, // y-axis Label
				new XYSeriesCollection(), // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?*/
				);

		XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer();
		XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();

		XYPlot xyplot = chart.getXYPlot();
		for (int i = 0; i < dataset.size(); i++) {
			xyplot.setDataset(i, dataset.get(i));
			xyplot.setRenderer(i, renderer0);
			xyplot.getRendererForDataset(xyplot.getDataset(i)).setSeriesPaint(
					0, Color.RED);
		}

		for (int j = 0; j < freightIndex.size(); j++) {
			System.out.println(freightIndex.get(j));
			xyplot.setRenderer(freightIndex.get(j), renderer1);
			xyplot.getRendererForDataset(xyplot.getDataset(freightIndex.get(j)))
					.setSeriesPaint(0, Color.GREEN);
		}

		xyplot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);

		chart.setBackgroundPaint(Color.white);

		XYLineAndShapeRenderer rr = new XYLineAndShapeRenderer();
		rr.setSeriesLinesVisible(2, true);
		rr.setSeriesShapesVisible(2, true);
		chart.getXYPlot().setRenderer(rr);

		ChartPanel CP = new ChartPanel(chart);

		JPanel plotPane = new JPanel();
		plotPane.add(CP);

		plotPane.setSize(1000, 300);
		plotPane.setVisible(true);
		return plotPane;
	}

}
