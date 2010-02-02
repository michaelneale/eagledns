package se.unlogic.standardutils.populators;

import java.sql.ResultSet;
import java.sql.SQLException;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.datatypes.SimpleEntry;

public class StringEntryPopulator implements BeanResultSetPopulator<SimpleEntry<String, String>> {

	public SimpleEntry<String, String> populate(ResultSet rs) throws SQLException {

		return new SimpleEntry<String, String>(rs.getString(1),rs.getString(2));
	}
}
