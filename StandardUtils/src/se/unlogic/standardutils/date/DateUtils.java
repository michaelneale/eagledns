package se.unlogic.standardutils.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	public static boolean isValidDate(SimpleDateFormat sdf, String date){
		try{
			sdf.parse(date);
		}catch(ParseException e){
			return false;
		}catch(RuntimeException e){
			return false;
		}
		return true;
	}

	public static Date getDate(SimpleDateFormat sdf, String date){
		try{
			return sdf.parse(date);
		}catch(ParseException e){
			return null;
		}catch(RuntimeException e){
			return null;
		}
	}
}
