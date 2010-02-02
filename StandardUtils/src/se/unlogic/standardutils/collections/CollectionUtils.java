package se.unlogic.standardutils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CollectionUtils {

	public static <T> List<T> getGenericList(Class<T> clazz, int size){
		
		return new ArrayList<T>(size);
	}
	
	public static <T> List<T> getGenericList(Class<T> clazz){
		
		return new ArrayList<T>();
	}
	
	public static <T> List<T> getGenericSingletonList(T bean){
		
		ArrayList<T> list = new ArrayList<T>(1);
		
		list.add(bean);
		
		return list;
	}

	public static boolean isEmpty(Collection<?> collection) {

		if(collection == null || collection.isEmpty()){
			
			return true;
		}
		
		return false;
	}
}
