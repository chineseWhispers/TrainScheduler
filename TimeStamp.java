package iFreight;

/*
 * The TimeStamp class mainly consists of getter and setter methods, to retrieve and set 
 * relevant time information (i.e day, hour, minute, duration, etc)
 * It also contains method that adds minutes to the current time.
 */

public class TimeStamp implements Comparable<TimeStamp> {

	public int compareTo(TimeStamp o) { // return -1 if the invocation Object is
										// earlier
		int sh = o.getHour();
		int sm = o.getMinute();
		if (sh > hour || ((sh == hour) && (sm > minute)))
			return -1;
		else if (sh == hour && sm == minute)
			return 0;
		else
			return 1;
	}

	private int hour;
	private int minute;
	private static String days[] = new String[] { "M", "Tu", "W", "Th", "F",
			"S", "Su", "M" };

	public TimeStamp(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}

	public TimeStamp(TimeStamp o) {
		hour = o.getHour();
		minute = o.getMinute();
	}

	public TimeStamp addMinutes(int min) {
		int temphour = hour;
		min = minute + min;
		if (min >= 60) {
			temphour += min / 60;
			min %= 60;
		}
		return new TimeStamp(temphour, min);
	}

	public int getDifferent(TimeStamp o) {
		if (o.compareTo(this) < 0) {
			return (hour * 60 + minute) - (o.getHour() * 60 + o.getMinute());
		} else
			return (o.getHour() * 60 + o.getMinute()) - (hour * 60 + minute);
	}

	public int toMinute() {
		return hour * 60 + minute;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public String toString() {
		String ret = "";
		if (hour < 10)
			ret += "0";
		ret += hour + ":";
		if (minute < 10)
			ret += "0";
		ret += minute;
		return ret;
	}

	public String toString2() {// days and 24 hour format
		String ret = "";
		ret += days[(hour / 24) % 7] + " ";
		if (hour % 24 < 10)
			ret += "0";
		ret += hour % 24 + ":";
		if (minute < 10)
			ret += "0";
		ret += minute;
		return ret;
	}

	public String toString3() {// only 24 hour format
		String ret = "";
		if (hour % 24 < 10)
			ret += "0";
		ret += hour % 24 + ":";
		if (minute < 10)
			ret += "0";
		ret += minute;
		return ret;
	}
}
