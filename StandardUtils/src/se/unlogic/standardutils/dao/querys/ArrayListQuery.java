package se.unlogic.standardutils.dao.querys;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.db.DBUtils;

public class ArrayListQuery<ReturnType> extends PopulatedQuery<ReturnType> {

	public ArrayListQuery(Connection connection, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<ReturnType> bp) throws SQLException {
		super(connection, closeConnectionOnExit, query, bp);
	}

	public ArrayListQuery(DataSource dataSource, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<ReturnType> bp) throws SQLException {
		super(dataSource, closeConnectionOnExit, query, bp);
	}

	public ArrayList<ReturnType> executeQuery() throws SQLException {

		ResultSet rs = null;
		ArrayList<ReturnType> returnTypeList = null;

		try {
			// Send query to database and store results.
			rs = pstmt.executeQuery();

			if (rs.next()) {
				rs.last();
				returnTypeList = new ArrayList<ReturnType>(rs.getRow());
				rs.beforeFirst();

				while (rs.next()) {
					returnTypeList.add(beanPopulator.populate(rs));
				}
			}

			return returnTypeList;

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