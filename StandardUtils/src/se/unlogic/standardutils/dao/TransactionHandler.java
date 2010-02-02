package se.unlogic.standardutils.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.sql.DataSource;

import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.BooleanQuery;
import se.unlogic.standardutils.dao.querys.HashMapQuery;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.db.DBUtils;

public class TransactionHandler {

	public boolean isAborted() {
		return aborted;
	}

	public boolean isCommited() {
		return commited;
	}

	private final Connection connection;
	private final ArrayList<UpdateQuery> queryList = new ArrayList<UpdateQuery>();
	private boolean aborted;
	private boolean commited;

	public TransactionHandler(DataSource dataSource) throws SQLException {
		super();
		this.connection = dataSource.getConnection();
		connection.setAutoCommit(false);
	}

	public TransactionHandler(Connection connection) throws SQLException {
		super();
		this.connection = connection;
		connection.setAutoCommit(false);
	}
	
	public UpdateQuery getUpdateQuery(String sqlExpression) throws SQLException {

		this.checkStatus();

		UpdateQuery query = new UpdateQuery(connection, false, sqlExpression);

		this.queryList.add(query);

		return query;
	}

	public BooleanQuery getBooleanQuery(String sql) throws SQLException {

		return new BooleanQuery(connection, false, sql);
	}

	public <T> ObjectQuery<T> getObjectQuery(String sql, BeanResultSetPopulator<T> populator) throws SQLException {

		return new ObjectQuery<T>(connection, false, sql, populator);
	}

	public <T> ArrayListQuery<T> getArrayListQuery(String sql, BeanResultSetPopulator<T> populator) throws SQLException {

		return new ArrayListQuery<T>(connection, false, sql, populator);
	}


	public <K,V> HashMapQuery<K, V> getHashMapQuery(String sql, BeanResultSetPopulator<? extends Entry<K, V>> populator) throws SQLException{

		return new HashMapQuery<K, V>(connection, false, sql, populator);
	}

	public synchronized void commit() throws SQLException {

		this.checkStatus();

		try {
			connection.commit();
			this.commited = true;
		} finally {

			if (!this.commited) {
				this.abort();
			} else {
				this.closeConnection();
			}
		}
	}

	public synchronized int getQueryCount() {
		return this.queryList.size();
	}

	public synchronized void abort() {

		this.checkStatus();

		for (UpdateQuery query : queryList) {
			query.abort();
		}

		if(connection != null){
			try {
				connection.rollback();
			} catch (SQLException e) {}
		}

		this.closeConnection();
		this.aborted = true;
	}

	private void closeConnection() {

		DBUtils.closeConnection(connection);
	}

	private void checkStatus() {

		if (aborted) {
			throw new TransactionAlreadyAbortedException();
		} else if (commited) {
			throw new TransactionAlreadyComittedException();
		}
	}

	@Override
	protected void finalize() throws Throwable {

		if (!commited && !aborted) {
			this.abort();
		}

		super.finalize();
	}

	// TODO rename to isClosed
	public boolean closed() {
		return commited || aborted;
	}

	public static void autoClose(TransactionHandler transactionHandler) {

		if (transactionHandler != null && !transactionHandler.closed()) {
			transactionHandler.abort();
		}
	}

	
	Connection getConnection() {
	
		return connection;
	}
}
