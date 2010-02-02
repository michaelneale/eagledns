package se.unlogic.standardutils.string;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import se.unlogic.standardutils.reflection.ReflectionUtils;


public class BeanTagSourceFactory<T> {

	private Class<T> beanClass;
	private HashMap<String,Method> tagMethodMap = new HashMap<String, Method>();
	private HashMap<String,Field> tagFieldMap = new HashMap<String, Field>();
	private HashSet<String> tagsSet = new HashSet<String>();

	public BeanTagSourceFactory(Class<T> beanClass) {

		this.beanClass = beanClass;
	}

	public void addMethodMapping(String tag, String methodName) throws NoSuchMethodException{

		Method method = ReflectionUtils.getMethod(beanClass, methodName, Object.class);

		if(method == null){

			throw new NoSuchMethodException("Method " + methodName + " with no input parameters not found in " + beanClass);
		}

		if(!method.isAccessible()){

			ReflectionUtils.fixMethodAccess(method);
		}

		tagMethodMap.put(tag, method);
		this.tagsSet.add(tag);
	}

	public void addFieldMapping(String tag, String fieldName) throws NoSuchMethodException{

		Field field = ReflectionUtils.getField(beanClass, fieldName);

		if(field == null){

			throw new NoSuchFieldError("Field " + fieldName + " not found in " + beanClass);
		}

		if(!field.isAccessible()){

			ReflectionUtils.fixFieldAccess(field);
		}

		tagFieldMap.put(tag, field);
		this.tagsSet.add(tag);
	}

	public void addAllFields(String fieldPrefix, String... excludedFields){

		List<String> exclusionList = null;

		if(excludedFields != null){

			exclusionList = Arrays.asList(excludedFields);
		}


		List<Field> fields = ReflectionUtils.getFields(beanClass);

		for(Field field : fields){

			if(exclusionList == null || !exclusionList.contains(field.getName())){

				if(!field.isAccessible()){

					ReflectionUtils.fixFieldAccess(field);
				}

				this.tagFieldMap.put(fieldPrefix + field.getName(), field);
				this.tagsSet.add(fieldPrefix + field.getName());
			}
		}
	}

	public BeanTagSource<T> getTagSource(T bean){

		return new BeanTagSource<T>(bean, tagMethodMap, tagFieldMap, tagsSet);
	}


	public HashSet<String> getTagsSet() {
		return new HashSet<String>(tagsSet);
	}
}
