package se.unlogic.standardutils.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BeanResultSetPopulator<ReturnType> {
	public ReturnType populate(ResultSet rs) throws SQLException;
}
