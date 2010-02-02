package se.unlogic.standardutils.populators;

import java.sql.ResultSet;
import java.sql.SQLException;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class DoublePopulator extends BaseTypePopulator<Double> implements BeanResultSetPopulator<Double>, TypePopulator<Double> {

	public DoublePopulator() {
		super();
	}

	public DoublePopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public DoublePopulator(String populatorID) {
		super(populatorID);
	}

	public Double populate(ResultSet rs) throws SQLException {

		return rs.getDouble(1);
	}

	public Double getValue(String value) {

		return Double.valueOf(value);
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return NumberUtils.isDouble(value);
	}

	public Class<? extends Double> getType() {

		return Double.class;
	}

}
