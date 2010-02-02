package se.unlogic.standardutils.dao;

public class QueryParameterFactory<Bean, Type> {

	private Column<Bean,? super Type> column;

	QueryParameterFactory(Column<Bean,? super Type> column) {
		super();
		this.column = column;
	}

	public QueryParameter<Bean,Type> getParameter(Type value){

		return new QueryParameter<Bean, Type>(column, value);
	}

	public QueryParameter<Bean,Type> getParameter(Type value, QueryOperators queryOperator){

		return new QueryParameter<Bean, Type>(column, value, queryOperator);
	}
}
