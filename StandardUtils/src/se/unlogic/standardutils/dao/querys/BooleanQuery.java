package se.unlogic.standardutils.dao.querys;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import se.unlogic.standardutils.db.DBUtils;

public class BooleanQuery extends PreparedStatementQuery {

	public BooleanQuery(Connection connection, boolean closeConnectionOnExit, String query) throws SQLException {
		super(connection, closeConnectionOnExit, query);
	}

	public BooleanQuery(DataSource dataSource, boolean closeConnectionOnExit, String query) throws SQLException {
		super(dataSource, closeConnectionOnExit, query);
	}

	public boolean executeQuery() throws SQLException {

		ResultSet rs = null;

		try {
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return true;
			} else {
				return false;
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
