package se.unlogic.standardutils.populators.annotated;

import se.unlogic.standardutils.dao.ResultSetField;


public class BeanResultSetPopulationException extends RuntimeException {

	private static final long serialVersionUID = 3781653780279983974L;
	private final ResultSetField resultSetField;

	public BeanResultSetPopulationException(ResultSetField resultSetField, Exception e) {
		super(e);
		this.resultSetField = resultSetField;
	}

	public ResultSetField getResultSetField() {
		return resultSetField;
	}
}
