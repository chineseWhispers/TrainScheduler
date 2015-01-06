package iFreight;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;

/* @ Date: Aug 14th, 2014
 * @ Yingyan Samantha Wang
 * The GUI Management class includes all the Swing components need to created the GUI interface.
 * It consists of JLabel, JButton, JCombolist, JPanel, JTabel, JScrollPane components. 
 * It has methods to initialize the GUI window. It also has individual methods to update individual parts of the interface.
 * Also, it has an actionPerformed method, that is responsible for the actions performed after certain buttons are clicked.
 */

public class GUIManagement extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JLabel stationLabel, trainLabel, scheduleLabel, freightName,
			originLabel, destLabel, readyTimeLabel, lengthLabel, speedLabel,
			heuristicLabel;
	private JPanel inputPanel, inputPanel2, outputPanel, outputPanel2,
			graphPanel, graphPanel2, upperPanel, lowerPanel;
	private JButton browseStation, browseTrain, browseSchedule, addTrain,
			removeTrain, scheduleTrain;
	private JTextField stationText, trainText, scheduleText, freightNameText,
			lengthText, speedText;
	private JComboBox<String> originList, destList, dayComboList,
			hourComboList, minComboList, heuristicList;
	String[] dayList, hourList, minList;;
	private String stationFilePath, trainFilePath, scheduleFilePath;
	private String tempStationPath, tempTrainPath, tempSchedulePath;
	private FTDataManagement dataManage, tempDataManage;
	private String selectedDate, selectedMin, selectedHour, inputFreightName,
			originName, destName;
	private int lengthValue, speedValue;
	private ArrayList<Freight> freightList;
	private JTable inputTable;
	private JScrollPane inputScrollPane;
	private ArrayList<JScrollPane> outputPathPaneList, outputPlotPaneList;
	private ArrayList<JTable> outputTableList;
	private ArrayList<JScrollPane> outputScrollPaneList;
	private ArrayList<JPanel> pathOutputList, plotOutputList;
	private JTabbedPane tabbedPane1, tabbedPane2, tabbedPane3;
	private ArrayList<Freight> freightHold;
	private GridBagConstraints gbc;
	private PsgPlot trainPlot;

	/*
	 * The constructor initialized the entire JFrame.
	 */
	public GUIManagement() {
		setTitle("Freight Train Scheduler");
		initPanels();
		fileInputPanel();
		freightInputPanel();
		freightList = new ArrayList<Freight>();
		freightHold = new ArrayList<Freight>();
		inputTablePanel();
		outputTablePanel();
		graphInputPanel();
		graphOutputPanel();

	}

	/*
	 * This method initializes all the JPanels within the frame. The upperPanel
	 * consists the four equally sized sections on the top. The lower Panel
	 * consists of the two bottom panels, that are equally sized.
	 */

	private void initPanels() {
		this.setResizable(true);
		this.getContentPane().setLayout(new GridLayout(2, 0));
		upperPanel = new JPanel();
		upperPanel.setLayout(new GridLayout(2, 2));
		lowerPanel = new JPanel();
		lowerPanel.setLayout(new GridLayout(0, 2));
		getContentPane().add(upperPanel);
		getContentPane().add(lowerPanel);

		inputPanel = new JPanel(); // The upper left panel
		inputPanel.setBackground(Color.CYAN);
		inputPanel.setSize(new Dimension(100, 100));
		inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		upperPanel.add(inputPanel);

		outputPanel = new JPanel(); // The upper right panel
		outputPanel.setBackground(Color.BLUE);
		outputPanel.setPreferredSize(new Dimension(100, 100));
		upperPanel.add(outputPanel);

		inputPanel2 = new JPanel();
		inputPanel2.setBackground(Color.GREEN); // The middle left panel
		inputPanel2.setPreferredSize(new Dimension(100, 100));
		inputPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
		upperPanel.add(inputPanel2);

		outputPanel2 = new JPanel();
		outputPanel2.setBackground(Color.MAGENTA); // The middle left panel
		outputPanel2.setPreferredSize(new Dimension(100, 100));
		upperPanel.add(outputPanel2);

		graphPanel = new JPanel();
		graphPanel.setBackground(Color.YELLOW); // The lower left panel
		graphPanel.setPreferredSize(new Dimension(100, 100));
		lowerPanel.add(graphPanel);

		graphPanel2 = new JPanel();
		graphPanel2.setBackground(Color.ORANGE); // The lower right panel
		graphPanel2.setPreferredSize(new Dimension(100, 100));
		lowerPanel.add(graphPanel2);
	}

	/*
	 * This method defines all the components (i.e, buttons, labels, textFields)
	 * in the upper left panel. This is the file input panel, where the user can
	 * browse the file directory of the local computer, and select relevant
	 * excel files.
	 */
	private void fileInputPanel() {
		stationLabel = new JLabel("Station Data:     ");
		inputPanel.add(stationLabel);
		stationText = new JTextField("");
		stationText.setColumns(25);
		stationText.setPreferredSize(new Dimension(1, 20));
		inputPanel.add(stationText);
		browseStation = new JButton("Browse");
		browseStation.addActionListener((ActionListener) this);
		inputPanel.add(browseStation);

		trainLabel = new JLabel("Train Data:         ");
		inputPanel.add(trainLabel);
		trainText = new JTextField("");
		trainText.setColumns(25);
		inputPanel.add(trainText);
		browseTrain = new JButton("Browse");
		browseTrain.addActionListener((ActionListener) this);
		inputPanel.add(browseTrain);

		scheduleLabel = new JLabel("Schedule Data: ");
		inputPanel.add(scheduleLabel);
		scheduleText = new JTextField("");
		scheduleText.setColumns(25);
		inputPanel.add(scheduleText);
		browseSchedule = new JButton("Browse");
		browseSchedule.addActionListener((ActionListener) this);
		inputPanel.add(browseSchedule);
	}

	/*
	 * This method defines all the components in the middle left panel, where
	 * the users can input information about freight trains.
	 */

	private void freightInputPanel() {

		freightName = new JLabel(" Freight Name:  ");
		freightNameText = new JTextField("none");
		freightNameText.setColumns(7);
		inputPanel2.add(freightName);
		inputPanel2.add(freightNameText);

		originLabel = new JLabel("     From:      ");
		inputPanel2.add(originLabel);
		originList = new JComboBox<String>(new String[] { "   none   " });
		// originList.addActionListener(this);

		inputPanel2.add(originList);
		destLabel = new JLabel("      To:      ");
		inputPanel2.add(destLabel);

		destList = new JComboBox<String>(new String[] { "   none   " });
		// destList.addActionListener(this);

		inputPanel2.add(destList);
		readyTimeLabel = new JLabel(" Ready Time:   ");
		dayList = new String[] { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
				"Sun", "Mon" };
		hourList = new String[] { "00", "01", "02", "03", "04", "05", "06",
				"07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
				"17", "18", "19", "20", "21", "22", "23" };
		minList = new String[] { "00", "01", "02", "03", "04", "05", "06",
				"07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
				"17", "18", "19", "20", "21", "22", "23", "24", "25", "26",
				"27", "28", "29", "30", "31", "32", "33", "34", "35", "36",
				"37", "38", "39", "40", "41", "42", "43", "44", "45", "46",
				"47", "48", "49", "50", "51", "52", "53", "54", "55", "56",
				"57", "58", "59" };
		dayComboList = new JComboBox<String>(dayList);
		hourComboList = new JComboBox<String>(hourList);
		minComboList = new JComboBox<String>(minList);

		inputPanel2.add(readyTimeLabel);
		inputPanel2.add(dayComboList);
		inputPanel2.add(hourComboList);
		inputPanel2.add(minComboList);

		lengthLabel = new JLabel("Length: ");
		lengthText = new JTextField("0");
		lengthText.setColumns(5);

		speedLabel = new JLabel("Speed: ");
		speedText = new JTextField("0");
		speedText.setColumns(5);

		inputPanel2.add(lengthLabel);
		inputPanel2.add(lengthText);
		inputPanel2.add(speedLabel);
		inputPanel2.add(speedText);

		heuristicLabel = new JLabel(" Heuristic Options:  ");
		heuristicList = new JComboBox<String>(new String[] { "FCFS", "LCFS",
				"ReadyTime" });

		inputPanel2.add(heuristicLabel);

		inputPanel2.add(heuristicList);
		inputPanel2.add(new JLabel("   "));
		addTrain = new JButton("Add");
		addTrain.addActionListener((ActionListener) this);
		removeTrain = new JButton("Remove");
		removeTrain.addActionListener((ActionListener) this);
		scheduleTrain = new JButton("Schedule");
		scheduleTrain.addActionListener((ActionListener) this);
		inputPanel2.add(addTrain);
		inputPanel2.add(removeTrain);
		inputPanel2.add(scheduleTrain);
	}

	/*
	 * This method defines all the components (i.e. tables, tabbedPane) in the
	 * uppper right panel. This panel will display the input freight train
	 * information onto the excel tables.
	 */
	private void inputTablePanel() {
		outputPanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		tabbedPane1 = new JTabbedPane();
		inputTable = new JTable(new DefaultTableModel(new String[] {
				"Freight Name", "Origin", "Destination", "Ready Time",
				"Length", "Max Speed" }, 30));
		inputTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		centerCell(inputTable);
		inputScrollPane = new JScrollPane(inputTable);
		tabbedPane1.addTab(" Input ", inputScrollPane);
		outputPanel.add(tabbedPane1, gbc);

	}

	/*
	 * This method defines all the components in the middle right panel
	 */
	private void outputTablePanel() {
		outputPanel2.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		tabbedPane2 = new JTabbedPane();
		outputTableList = new ArrayList<JTable>();
		outputScrollPaneList = new ArrayList<JScrollPane>();
		outputPanel2.add(tabbedPane2, gbc);
	}

	/*
	 * This method sets the lower left panel, which will display a graphic
	 * representation of the input rail network.
	 */
	private void graphInputPanel() {
		graphPanel.setLayout(new BorderLayout());
	}

	/*
	 * This method defines components, and properties of the lower right panel
	 */

	private void graphOutputPanel() {
		graphPanel2.setLayout(new BorderLayout());
		tabbedPane3 = new JTabbedPane();
		pathOutputList = new ArrayList<JPanel>();
		plotOutputList = new ArrayList<JPanel>();
		outputPathPaneList = new ArrayList<JScrollPane>();
		outputPlotPaneList = new ArrayList<JScrollPane>();
		graphPanel2.add(tabbedPane3);
	}

	/*
	 * This method sets all the text display in JTables to center aligned.
	 */
	private void centerCell(JTable table) {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
	}

	/*
	 * This method is a helper method that determines whether all the input file
	 * data should be read. If all the input file path are valid, the files will
	 * be read, otherwise, the files will not be read.
	 */
	private void browseAction() {
		if (isValidPath()) {
			dataManage = new FTDataManagement(tempStationPath, tempTrainPath,
					tempSchedulePath);
			tempDataManage = dataManage;
			dataManage.openStationFile();
			originList.removeItem("   none   ");
			destList.removeItem("   none   ");
			addNames(dataManage.getStationNames());
			String[] emptyString = new String[0];
			dataManage.visualization(graphPanel, Color.YELLOW, emptyString, "");
			trainPlot = dataManage.getPlot();
		}
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		// Browse for files when the first browse button clicked.
		// Set the file path name onto the textfield.
		if (source == browseStation) {
			getStationFileName();
			if (stationFilePath != "") {
				tempStationPath = stationFilePath;
				browseAction();
			}
		}
		// Browse for files when the second browse button clicked.
		// Set the file path name onto the textfield.

		else if (source == browseTrain) {
			getTrainFileName();
			if (trainFilePath != "") {
				tempTrainPath = trainFilePath;
				browseAction();
			}
		}
		// Browse for files when the third browse button clicked.
		// Set the file path name onto the textfield.
		else if (source == browseSchedule) {
			getScheduleFileName();
			tempSchedulePath = scheduleFilePath;
			if (scheduleFilePath != "") {
				browseAction();
			}
		}

		// When the add button is clicked, add all the freight train
		// information.
		// All the freight information will be displayed onto the input table
		// panel, on the upper right of the frame.
		else if (source == addTrain) {
			Network tempNet = tempDataManage.getNetwork();
			Station[] tempStations = tempDataManage.getStations();
			Track[][] tempTracks = tempDataManage.getTracks();
			Hashtable<String, Track> tempTrackTable = tempDataManage
					.getTrackTable();

			inputFreightName = freightNameText.getText();
			originName = (String) originList.getSelectedItem();
			destName = (String) destList.getSelectedItem();
			selectedDate = (String) dayComboList.getSelectedItem();
			selectedHour = (String) hourComboList.getSelectedItem();
			selectedMin = (String) minComboList.getSelectedItem();
			lengthValue = Integer.parseInt(lengthText.getText());
			speedValue = Integer.parseInt(speedText.getText());
			int dayIndex = Arrays.asList(dayList).indexOf(selectedDate);
			int hourValue = Integer.parseInt(selectedHour);
			int minuteValue = Integer.parseInt(selectedMin);

			TimeStamp tempReadyTime = new TimeStamp(dayIndex * 24 + hourValue,
					minuteValue);
			System.out.println("days:" + dayIndex * 24 + hourValue);

			System.out.println("freightName input: " + inputFreightName);
			System.out.println(inputFreightName == "none");
			if (!inputFreightName.equals("none") && originName != destName
					&& lengthValue > 0 && speedValue > 0) {
				Freight addedSingleFreight = new Freight(inputFreightName,
						tempNet, tempStations, tempTracks, tempTrackTable,
						originName, destName, lengthValue, speedValue,
						tempReadyTime);
				freightList.add(addedSingleFreight);
				freightHold.add(addedSingleFreight);

				inputTable.setValueAt(inputFreightName, freightList.size() - 1,
						0);
				inputTable.setValueAt(originName, freightList.size() - 1, 1);
				inputTable.setValueAt(destName, freightList.size() - 1, 2);
				inputTable.setValueAt(tempReadyTime.toString2(),
						freightList.size() - 1, 3);
				inputTable.setValueAt(lengthValue, freightList.size() - 1, 4);
				inputTable.setValueAt(speedValue, freightList.size() - 1, 5);
			}
			if (inputFreightName.equals("none")) {
				JOptionPane.showMessageDialog(null,
						"Please Enter a Freight Name.", "",
						JOptionPane.PLAIN_MESSAGE);
			} else if (originName == destName) {
				JOptionPane.showMessageDialog(null,
						"Please Select a Different Station.", "",
						JOptionPane.PLAIN_MESSAGE);
			} else if (lengthValue <= 0) {
				JOptionPane.showMessageDialog(null,
						"Please Enter Length Value.", "",
						JOptionPane.PLAIN_MESSAGE);
			} else if (speedValue <= 0) {
				JOptionPane.showMessageDialog(null,
						"Please Enter Speed Value.", "",
						JOptionPane.PLAIN_MESSAGE);
			}

		}

		// When the remove button is clicked. The freight train selected on the
		// input table, will be removed from the list, as well as from the input
		// table panel.
		else if (source == removeTrain) {
			DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
			int[] rows = inputTable.getSelectedRows();
			Freight removeFreight = null;
			if (rows.length > 0) {
				for (int i = 0; i < rows.length; i++) {
					String freightName = (String) inputTable.getValueAt(rows[i]
							- i, 0);
					for (int j = 0; j < freightList.size(); j++) {
						if (freightList.get(j).getName() == freightName) {
							removeFreight = freightList.get(j);
						}
					}
					freightList.remove(removeFreight);
					freightHold.remove(removeFreight);
				}
			}

			if (rows.length > 0) {
				for (int i = 0; i < rows.length; i++) {
					model.removeRow(rows[i] - i);
				}
			}
		}

		// When the schedule button is clicked, scheduling and routing
		// algorithms are performed for each freight train. The graphical result
		// will be displayed on the lower right panel. It will display the
		// highlighted paths within the network, as well as a velocity profile,
		// with x-axis as time, y-axis as postion.
		else if (source == scheduleTrain) {
			trainPlot = dataManage.getPlot();
			gbc = new GridBagConstraints();
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			dataManage.setFreightList(freightList);
			dataManage.schedule(getMode((String) heuristicList
					.getSelectedItem()));
			Hashtable<String, LinkedList<TimeWindow>> solTable = dataManage
					.getFreightSolTable();
			System.out.println(solTable);
			System.out.println("freightListsize:" + freightHold.size());

			for (int i = 0; i < freightHold.size(); i++) {

				LinkedList<TimeWindow> freightSol = solTable.get(freightHold
						.get(i).getName());

				outputTableList.add(new JTable(new DefaultTableModel(
						new String[] { "From", "To", "Depart", "Arrive",
								"Speed" }, 40)));
				for (int j = 0; j < freightSol.size(); j++) {
					setOutput(outputTableList.get(i), freightSol.get(j), j);
				}
				outputTableList.get(i).setSelectionMode(
						ListSelectionModel.SINGLE_SELECTION);
				outputScrollPaneList
						.add(new JScrollPane(outputTableList.get(i)));
				JPanel tempPanel = new JPanel();
				tempPanel.setLayout(new BorderLayout());
				String[] highVer = highlightVertex(freightSol);
				String path = "";
				for (int k = 0; k < highVer.length - 1; k++) {
					path = path + highVer[k] + " >> ";
				}
				path = path + highVer[highVer.length - 1];
				System.out.println(highVer.length);
				dataManage
						.visualization(tempPanel, Color.ORANGE, highVer, path);

				pathOutputList.add(tempPanel);
				outputPathPaneList.add(new JScrollPane(pathOutputList.get(i)));

				JPanel tempPlot = trainPlot.createPlot(trainPlot.mapPath(
						highVer, freightHold.get(i).getName()),
						trainPlot.highlightFreight(highVer, freightHold.get(i)
								.getName()));

				plotOutputList.add(tempPlot);
				outputPlotPaneList.add(new JScrollPane(plotOutputList.get(i)));

				tabbedPane2.addTab(" Output " + freightHold.get(i).getName(),
						outputScrollPaneList.get(i));

				tabbedPane3.addTab(" Output " + freightHold.get(i).getName(),
						outputPathPaneList.get(i));
				tabbedPane3.addTab(freightHold.get(i).getName() + " Plot",
						outputPlotPaneList.get(i));
			}
		}
	}

	/*
	 * This method returns an array of station names. All the stations are on
	 * the determined shortest time path.
	 */
	private String[] highlightVertex(LinkedList<TimeWindow> solutions) {
		String[] vertexNames = new String[solutions.size() + 1];
		for (int i = 0; i < solutions.size(); i++) {
			vertexNames[i] = solutions.get(i).getS1();
			System.out.println(vertexNames[i]);
		}
		vertexNames[solutions.size()] = solutions.get(solutions.size() - 1)
				.getS2();
		return vertexNames;
	}

	/*
	 * This method sets all the output values of the freight train schedule, and
	 * displays it on to the table. The table for output schedule display is the
	 * middle right panel.
	 */
	private void setOutput(JTable outputTable, TimeWindow sol, int rowNdx) {
		outputTable.setValueAt(sol.getS1(), rowNdx, 0);
		outputTable.setValueAt(sol.getS2(), rowNdx, 1);
		outputTable.setValueAt(sol.getDeparture().toString2(), rowNdx, 2);
		outputTable.setValueAt(sol.getArrival().toString2(), rowNdx, 3);
		outputTable.setValueAt(sol.roudedV(), rowNdx, 4);
		centerCell(outputTable);
	}

	/*
	 * This method adds all the station names on to the JCombolist of the origin
	 * and destination list in the freight input panel. It is the middle left
	 * panel.
	 */
	private void addNames(String[] names) {
		for (int i = 0; i < names.length; i++) {
			originList.addItem(names[i]);
			destList.addItem(names[i]);
		}
	}

	/*
	 * This method maps input heursitic options from the JCombolist, to
	 * integers. That are used in the dispatching sequence method in the
	 * Multfreight class.
	 */
	private int getMode(String heuristicOption) {
		int mode = 0;
		if (heuristicOption == "FCFS") {
			mode = 0;
		} else if (heuristicOption == "LCFS") {
			mode = 1;
		}

		else if (heuristicOption == "Shortest Travel Time") {
			mode = 2;
		}
		return mode;
	}

	/*
	 * This method returns a boolean value of true if all three input file paths
	 * are valid. Else, returns false.
	 */

	public boolean isValidPath() {
		return stationFilePath != null && trainFilePath != null
				&& scheduleFilePath != null;
	}

	/*
	 * This method checks whether the input station data file name is valid. It
	 * checks whether the file name contains "Station". It also checks whether
	 * it is an .xls file.
	 */
	public void getStationFileName() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setCurrentDirectory(new File(
				"C:\\Users\\yingyan_521620\\Desktop"));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;
		if (result == JFileChooser.APPROVE_OPTION) {
			File filepath = new File(fileChooser.getSelectedFile()
					.getAbsolutePath());
			stationFilePath = filepath.toString();
			if (!(stationFilePath.endsWith("xls") || stationFilePath
					.endsWith("XLS"))) {
				JOptionPane.showMessageDialog(null,
						"Please Select an xls File.", "",
						JOptionPane.PLAIN_MESSAGE);
				stationText.setText("");
				stationFilePath = "";

			} else if (!stationFilePath.contains("Station")) {
				JOptionPane.showMessageDialog(null,
						"Please Select the Station Data File.", "",
						JOptionPane.PLAIN_MESSAGE);
				stationText.setText("");
				stationFilePath = "";
			} else {
				stationText.setText(stationFilePath);
			}
		}
	}

	/*
	 * This method checks whether the input train file path is valid. It checks
	 * wheher it contains the word "Train" in the file. It also checks whether
	 * it is an .xls file.
	 */
	public void getTrainFileName() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setCurrentDirectory(new File(
				"C:\\Users\\yingyan_521620\\Desktop"));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;
		if (result == JFileChooser.APPROVE_OPTION) {
			File filepath = new File(fileChooser.getSelectedFile()
					.getAbsolutePath());
			trainFilePath = filepath.toString();
			if (!(trainFilePath.endsWith("xls") || trainFilePath
					.endsWith("XLS"))) {
				JOptionPane.showMessageDialog(null,
						"Please Select an xls File.", "",
						JOptionPane.PLAIN_MESSAGE);
				trainText.setText("");
				trainFilePath = "";
			} else if (!trainFilePath.contains("Train")) {
				JOptionPane.showMessageDialog(null,
						"Please Select the Train Data File.", "",
						JOptionPane.PLAIN_MESSAGE);
				trainText.setText("");
				trainFilePath = "";
			} else {
				trainText.setText(trainFilePath);
			}
		}
	}

	/*
	 * This method will determine whether the schedule data file path is valid.
	 * It checks to see if it contains the word "Schedule" in the file name. It
	 * also checks whether it is an .xls file.
	 */
	public void getScheduleFileName() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setCurrentDirectory(new File(
				"C:\\Users\\yingyan_521620\\Desktop"));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;
		if (result == JFileChooser.APPROVE_OPTION) {
			File filepath = new File(fileChooser.getSelectedFile()
					.getAbsolutePath());
			scheduleFilePath = filepath.toString();
			if (!(scheduleFilePath.endsWith("xls") || scheduleFilePath
					.endsWith("XLS"))) {
				JOptionPane.showMessageDialog(null,
						"Please Select an xls File.", "",
						JOptionPane.PLAIN_MESSAGE);
				scheduleText.setText("");
				scheduleFilePath = "";
			} else if (!scheduleFilePath.contains("Schedule")) {
				JOptionPane.showMessageDialog(null,
						"Please Select the Schedule Data File.", "",
						JOptionPane.PLAIN_MESSAGE);
				scheduleText.setText("");
				scheduleFilePath = "";
			} else {
				scheduleText.setText(scheduleFilePath);
			}
		}
	}

	/*
	 * A bunch of setters and getters
	 */
	public String getTrainPath() {
		return trainFilePath;
	}

	public String getStationPath() {
		return stationFilePath;
	}

	public String getSchedulePath() {
		return scheduleFilePath;
	}

	public void setStationFilePath(String stationFilePath) {
		this.stationFilePath = stationFilePath;
	}

	public void setTrainFilePath(String trainFilePath) {
		this.stationFilePath = trainFilePath;
	}

	public void setScheduleFilePath(String scheduleFilePath) {
		this.stationFilePath = scheduleFilePath;
	}

}
