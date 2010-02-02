package se.unlogic.standardutils.time;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

	public static SimpleDateFormat HOUR_FORMATTER = new SimpleDateFormat("HH");
	public static SimpleDateFormat MINUTE_FORMATTER = new SimpleDateFormat("mm");

	public static String secondsToString(long time)
	{
		return millisecondsToString(time * 1000);
	}

	public static String millisecondsToString(long time)
	{
	    int milliseconds = (int)(time % 1000);
	    int seconds = (int)((time/1000) % 60);
	    int minutes = (int)((time/60000) % 60);
	    int hours = (int)((time/3600000) % 24);
	    String millisecondsStr = (milliseconds<10 ? "00" : (milliseconds<100 ? "0" : ""))+milliseconds;
	    String secondsStr = (seconds<10 ? "0" : "")+seconds;
	    String minutesStr = (minutes<10 ? "0" : "")+minutes;
	    String hoursStr = (hours<10 ? "0" : "")+hours;
	    return new String(hoursStr+":"+minutesStr+":"+secondsStr+"."+millisecondsStr);
	}

	public static int getMinutes(long time)
	{
		return Integer.valueOf(MINUTE_FORMATTER.format(new Date(time)));
	}

	public static int getHour(long time)
	{
		return Integer.valueOf(HOUR_FORMATTER.format(new Date(time)));
	}

	public static String hourAndMinutesToString(int hours, int minutes)
	{

		String minutesStr = (minutes < 10 ? "0" : "") + minutes;
	    String hoursStr = (hours < 10 ? "0" : "") + hours;

		return new String(hoursStr + ":" + minutesStr);

	}
}
