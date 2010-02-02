package se.unlogic.standardutils.string;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import se.unlogic.standardutils.reflection.ReflectionUtils;

public class StringUtils {
	public static boolean isValidUUID(String uuidstring){
		try {
			UUID.fromString(uuidstring);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static boolean isValidURL(String urlstring){
		try {
			new URL(urlstring);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	public static String toQuotedCommaSeparatedString(List<? extends Object> list){
		String arrayString = Arrays.deepToString(list.toArray());
		arrayString = arrayString.substring(1,arrayString.length()-1);
		arrayString = "\"" + arrayString.replaceAll(", ","\", \"") + "\"";
		return arrayString;
	}

	public static String toQuotedCommaSeparatedString(Object[] array){
		String arrayString = Arrays.deepToString(array);
		arrayString = arrayString.substring(1,arrayString.length()-1);
		arrayString = "\"" + arrayString.replaceAll(", ","\", \"") + "\"";
		return arrayString;
	}

	public static String toCommaSeparatedString(Collection<? extends Object> list){
		String arrayString = Arrays.deepToString(list.toArray());
		return arrayString.substring(1,arrayString.length()-1);
	}

	public static String toCommaSeparatedString(Object[] array){
		String arrayString = Arrays.deepToString(array);
		return arrayString.substring(1,arrayString.length()-1);
	}
	
	public static String toCommaSeparatedString(List<? extends Object> list, Field field) throws IllegalArgumentException, IllegalAccessException{
				
		ReflectionUtils.fixFieldAccess(field);
		
		StringBuilder arrayString = new StringBuilder();
		
		for(int i = 0; i < list.size(); i++){
			
			Object value = field.get(list.get(i));
			
			arrayString.append(value + ",");

		}
		
		return arrayString.substring(0, arrayString.length()-1);
		
	}

	public static String readFileAsString(String filePath) throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			fileData.append(buf, 0, numRead);
		}
		reader.close();
		return fileData.toString();
	}

	public static String readFileAsString(File file) throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			fileData.append(buf, 0, numRead);
		}
		reader.close();
		return fileData.toString();
	}

	public static String readStreamAsString(InputStream inputStream) throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			fileData.append(buf, 0, numRead);
		}
		reader.close();
		return fileData.toString();
	}

	public static boolean isEmpty(String string){

		if(string == null){
			return true;
		}else if(string.trim().length() == 0){
			return true;
		}else{
			return false;
		}
	}

	public static String toAsciiFilename(String string){
		return string.replaceAll("[^0-9a-zA-Z-.]", "_");
	}

	public static String toValidHttpFilename(String string){
		return string.replaceAll("[^0-9a-öA-Ö-+. ()-+!@é&%$§=´]", "_");
	}

	public static String substring(String string, int maxChars, String suffix) {

		if (string.length() > maxChars) {
			return string.substring(0, maxChars - 1) + suffix;
		}

		return string;
	}

	public static String toSentenceCase(String string) {

		return string.substring(0, 1).toUpperCase() + string.toLowerCase().substring(1);
	}

	public static String toFirstLetterUppercase(String string) {

		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	public static String repeatString(String string, int repetitionCount) {

		if(repetitionCount >= 1){
			
			StringBuilder stringBuilder = new StringBuilder();
			
			for(int i=1; i <= repetitionCount; i++){
				
				stringBuilder.append(string);
			}
			
			return stringBuilder.toString();
			
		}else{
			
			return "";
		}
	}
}
