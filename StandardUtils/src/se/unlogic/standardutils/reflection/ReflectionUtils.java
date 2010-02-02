package se.unlogic.standardutils.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils {

	public static Object getInstance(String className) throws NoClassDefFoundError, ClassNotFoundException, InstantiationException, IllegalAccessException {
		return Class.forName(className).newInstance();
	}

	public static boolean isGenericlyTyped(Field field) {

		if (field.getGenericType() instanceof ParameterizedType) {

			return true;
		}

		return false;
	}

	public static int getGenericlyTypeCount(Field field) {

		if (field.getGenericType() instanceof ParameterizedType) {

			ParameterizedType type = (ParameterizedType) field.getGenericType();

			return type.getActualTypeArguments().length;
		}

		return 0;
	}

	public static boolean checkGenericTypes(Field field, Class<?>... classes) {

		if (field.getGenericType() instanceof ParameterizedType) {

			ParameterizedType type = (ParameterizedType) field.getGenericType();

			if (type.getActualTypeArguments().length != classes.length) {
				return false;
			}

			for (int i = 0; i < classes.length; i++) {

				if (!type.getActualTypeArguments()[i].equals(classes[i])) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	public static Type getGenericType(Field field) {
		return ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
	}

	public static void fixFieldAccess(Field field) {

		if(!field.isAccessible()){
			field.setAccessible(true);
		}
	}

	public static void fixMethodAccess(Method method) {

		if(!method.isAccessible()){
			method.setAccessible(true);
		}
	}

	public static Field getField(Class<?> bean, String fieldName) {


		List<Field> fields = getFields(bean);

		for(Field field : fields){

			if(field.getName().equals(fieldName)){

				return field;
			}
		}

		throw new RuntimeException(new NoSuchFieldError(fieldName));
	}

	public static boolean isAvailable(String classname) {
		try {
			Class.forName(classname);
			return true;
		} catch (ClassNotFoundException cnfe) {
			return false;
		}
	}

	public static List<Field> getFields(Class<?> clazz){
		
		ArrayList<Field> fields = new ArrayList<Field>();
		
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		
		clazz = clazz.getSuperclass();
		
		while(clazz != Object.class){
			
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			
			clazz = clazz.getSuperclass();
		}
		
		return fields;
	}

	public static Method getMethod(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... inputParams) {

		if(inputParams == null){
		
			inputParams = new Class<?>[0];
		}
		
		Method[] methods = clazz.getDeclaredMethods();
		
		for(Method method : methods){
			
			if(method.getName().equals(methodName) && returnType.isAssignableFrom(method.getReturnType()) && Arrays.equals(inputParams, method.getParameterTypes())){
				
				return method;
			}
		}
		
		return null;
	}
}
