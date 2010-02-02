package se.unlogic.standardutils.dao;

public enum QueryOperators {

	EQUALS("="),
	NOT_EQUALS("!="),
	BIGGER_THAN("<"),
	SMALLER_THAN(">"),
	BIGGER_THAN_OR_EUALS("<="),
	SMALLER_THAN_OR_EUALS(">=");

	private String value;

	private QueryOperators (String value){
		this.value = value;
	}

	public String getOperator(){
		return value;
	}
}
