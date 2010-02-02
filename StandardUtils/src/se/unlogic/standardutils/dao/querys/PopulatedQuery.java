package se.unlogic.standardutils.dao.querys;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;

public abstract class PopulatedQuery<ReturnType> extends PreparedStatementQuery {

	protected BeanResultSetPopulator<? extends ReturnType> beanPopulator;

	public PopulatedQuery(Connection connection, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<? extends ReturnType> bp) throws SQLException {
		super(connection, closeConnectionOnExit, query);
		this.beanPopulator = bp;
	}

	public PopulatedQuery(DataSource dataSource, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<? extends ReturnType> bp) throws SQLException {
		super(dataSource, closeConnectionOnExit, query);
		this.beanPopulator = bp;
	}

	public BeanResultSetPopulator<? extends ReturnType> getBeanPopulator() {
		return beanPopulator;
	}
}
