package se.unlogic.standardutils.bool;

public class BooleanUtils {

	public static boolean valueOf(Boolean bool){

		if(bool == null){
			return false;
		}else{
			return bool;
		}
	}
}
