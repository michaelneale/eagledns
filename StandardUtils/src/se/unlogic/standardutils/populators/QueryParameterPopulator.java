package se.unlogic.standardutils.populators;

import java.sql.SQLException;

import se.unlogic.standardutils.dao.querys.PreparedStatementQuery;


public interface QueryParameterPopulator<T> {

	public abstract Class<? extends T> getType();

	public void populate(PreparedStatementQuery query, int paramIndex, Object bean) throws SQLException;
}
