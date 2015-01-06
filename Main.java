package iFreight;

public class Main {
	static GUIManagement gui = new GUIManagement();
	static FTDataManagement dataInput = new FTDataManagement(
			"C:\\Users\\yingyan_521620\\Desktop\\StationData.xls",
			"C:\\Users\\yingyan_521620\\Desktop\\TrainData.xls",
			"C:\\Users\\yingyan_521620\\Desktop\\ScheduleData.xls");

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		gui.setVisible(true);
		gui.setSize(1000, 870);
	}
}
