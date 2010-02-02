package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import se.unlogic.standardutils.populators.TypePopulator;

public class ResultSetField {

	public static final LinkedHashMap<Class<?>,Method> RESULTSET_METHODS = new LinkedHashMap<Class<?>, Method>();

	static{
		try {
			RESULTSET_METHODS.put(String.class, ResultSet.class.getMethod("getString", String.class));

			RESULTSET_METHODS.put(Timestamp.class, ResultSet.class.getMethod("getTimestamp", String.class));

			RESULTSET_METHODS.put(Blob.class, ResultSet.class.getMethod("getBlob", String.class));

			RESULTSET_METHODS.put(Date.class, ResultSet.class.getMethod("getDate", String.class));

			RESULTSET_METHODS.put(Boolean.class, ResultSet.class.getMethod("getBoolean", String.class));
			RESULTSET_METHODS.put(boolean.class, ResultSet.class.getMethod("getBoolean", String.class));

			RESULTSET_METHODS.put(Integer.class, ResultSet.class.getMethod("getInt", String.class));
			RESULTSET_METHODS.put(int.class, ResultSet.class.getMethod("getInt", String.class));

			RESULTSET_METHODS.put(Long.class, ResultSet.class.getMethod("getLong", String.class));
			RESULTSET_METHODS.put(long.class, ResultSet.class.getMethod("getLong", String.class));

			RESULTSET_METHODS.put(Float.class, ResultSet.class.getMethod("getFloat", String.class));
			RESULTSET_METHODS.put(float.class, ResultSet.class.getMethod("getFloat", String.class));

			RESULTSET_METHODS.put(Double.class, ResultSet.class.getMethod("getDouble", String.class));
			RESULTSET_METHODS.put(Double.class, ResultSet.class.getMethod("getDouble", String.class));
		} catch (SecurityException e) {

			throw new RuntimeException(e);

		} catch (NoSuchMethodException e) {

			throw new RuntimeException(e);
		}
	}

	private final Field beanField;
	private final Method resultSetMethod;
	private final TypePopulator<?> typePopulator;
	private final String alias;

	public ResultSetField(Field beanField, Method resultSetMethod, String alias , TypePopulator<?> typePopulator) {
		super();
		this.beanField = beanField;
		this.resultSetMethod = resultSetMethod;
		this.alias = alias;
		this.typePopulator = typePopulator;
	}

	public TypePopulator<?> getTypePopulator() {
		return typePopulator;
	}

	public Field getBeanField() {
		return beanField;
	}

	public Method getResultSetMethod() {
		return resultSetMethod;
	}

	public String getAlias() {
		return alias;
	}
}
