package se.unlogic.standardutils.dao.querys;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.sql.DataSource;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.db.DBUtils;

public class HashMapQuery<KeyType, ValueType> extends PopulatedQuery<Entry<KeyType, ValueType>> {

	public HashMapQuery(Connection connection, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<? extends Entry<KeyType, ValueType>> bp) throws SQLException {
		super(connection, closeConnectionOnExit, query, bp);
	}

	public HashMapQuery(DataSource dataSource, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<? extends Entry<KeyType, ValueType>> bp) throws SQLException {
		super(dataSource, closeConnectionOnExit, query, bp);
	}

	public HashMap<KeyType, ValueType> executeQuery() throws SQLException {

		ResultSet rs = null;
		HashMap<KeyType, ValueType> returnTypeMap = null;

		try {
			// Send query to database and store results.
			rs = pstmt.executeQuery();

			if (rs.next()) {
				returnTypeMap = new HashMap<KeyType, ValueType>();
				rs.beforeFirst();

				while (rs.next()) {
					Entry<KeyType, ValueType> entry = beanPopulator.populate(rs);
					returnTypeMap.put(entry.getKey(), entry.getValue());
				}
			}

			return returnTypeMap;

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