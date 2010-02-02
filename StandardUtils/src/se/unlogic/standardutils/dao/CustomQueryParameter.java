package se.unlogic.standardutils.dao;

import java.lang.reflect.Method;

import se.unlogic.standardutils.populators.QueryParameterPopulator;

public class CustomQueryParameter<T>{

	private QueryParameterPopulator<?> queryParameterPopulator;
	private Method queryMethod;
	private Object paramValue;

	public CustomQueryParameter(QueryParameterPopulator<?> queryParameterPopulator, Method queryMethod, Object paramValue) {

		super();
		this.queryParameterPopulator = queryParameterPopulator;
		this.queryMethod = queryMethod;
		this.paramValue = paramValue;
	}
	
	public CustomQueryParameter(Column<T,?> column , T bean) {

		super();
		this.queryParameterPopulator = column.getQueryParameterPopulator();
		this.queryMethod = column.getQueryMethod();
		this.paramValue = column.getBeanValue(bean);
	}	

	public QueryParameterPopulator<?> getQueryParameterPopulator() {

		return queryParameterPopulator;
	}

	public Method getQueryMethod() {

		return queryMethod;
	}

	public Object getParamValue() {

		return paramValue;
	}
}
