package se.unlogic.standardutils.dao.querys;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.db.DBUtils;

public class ObjectQuery<ReturnType> extends PopulatedQuery<ReturnType> {

	public ObjectQuery(Connection connection, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<ReturnType> bp) throws SQLException {
		super(connection, closeConnectionOnExit, query, bp);
	}

	public ObjectQuery(DataSource dataSource, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<ReturnType> bp) throws SQLException {
		super(dataSource, closeConnectionOnExit, query, bp);
	}

	public ReturnType executeQuery() throws SQLException {

		ResultSet rs = null;

		try {

			// Send query to database and store results.
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return this.beanPopulator.populate(rs);
			} else {
				return null;
			}
		} catch (SQLException sqle) {
			throw sqle;
		} finally {
			DBUtils.closeResultSet(rs);
			DBUtils.closePreparedStatement(pstmt);

			if (this.closeConnectionOnExit) {
				DBUtils.closeConnection(connection);
			}

			this.closed = true;
		}
	}
}
