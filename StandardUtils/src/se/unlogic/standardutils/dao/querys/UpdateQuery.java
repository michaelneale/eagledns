package se.unlogic.standardutils.dao.querys;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import se.unlogic.standardutils.db.DBUtils;

public class UpdateQuery extends PreparedStatementQuery {

	private Integer affectedRows;

	public UpdateQuery(Connection connection, boolean closeConnectionOnExit, String query) throws SQLException {
		super(connection, closeConnectionOnExit, query);
	}

	public UpdateQuery(DataSource dataSource, boolean closeConnectionOnExit, String query) throws SQLException {
		super(dataSource, closeConnectionOnExit, query);
	}

	public Integer executeUpdate() throws SQLException {

		ResultSet rs = null;

		try {
			affectedRows = this.pstmt.executeUpdate();

			if (affectedRows > 0) {
				rs = pstmt.getGeneratedKeys();

				if (rs.next()) {
					return rs.getInt(1);
				}
			}

			return null;

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

	public Integer getAffectedRows() {
		return affectedRows;
	}
}
