package se.unlogic.standardutils.dao;


public class QueryParameter<BeanType,ColumnType> {

	private ColumnType value;
	private Column<BeanType, ? super ColumnType> column;
	private String operator = QueryOperators.EQUALS.getOperator();

	QueryParameter(Column<BeanType,? super ColumnType> column, ColumnType value) {
		super();
		this.column = column;
		this.value = value;
	}

	QueryParameter(Column<BeanType,? super ColumnType> column, ColumnType value, QueryOperators operator) {
		super();
		this.column = column;
		this.value = value;
		this.operator = operator.getOperator();
	}

	public ColumnType getValue() {
		return value;
	}

	public Column<BeanType,? super ColumnType> getColumn() {
		return column;
	}

	public String getOperator() {
		return operator;
	}
}
