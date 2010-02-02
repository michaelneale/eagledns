package se.unlogic.standardutils.populators;

import java.sql.ResultSet;
import java.sql.SQLException;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;

public class StringPopulator extends BaseTypePopulator<String> implements BeanResultSetPopulator<String>, TypePopulator<String>{

	public StringPopulator() {
		super();
	}

	private static final StringPopulator POPULATOR = new StringPopulator();

	public String populate(ResultSet rs) throws SQLException {
		return rs.getString(1);
	}

	public static StringPopulator getPopulator(){
		return POPULATOR;
	}

	public String getValue(String value) {
		return value;
	}

	@Override
	public boolean validateDefaultFormat(String value) {
		return true;
	}

	public Class<? extends String> getType() {
		return String.class;
	}
}
