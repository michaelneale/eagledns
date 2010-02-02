package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Deprecated
public interface GenericDAO<KeyType,BeanType> {

	public void add(BeanType bean) throws SQLException;

	public void add(BeanType bean, TransactionHandler transactionHandler) throws SQLException;

	public void add(BeanType bean, Connection connection) throws SQLException;

	public void update(BeanType bean) throws SQLException;

	public void update(BeanType bean, TransactionHandler transactionHandler) throws SQLException;

	public void update(BeanType bean, Connection connection) throws SQLException;

	public BeanType get(KeyType id, Field... relations) throws SQLException;

	public BeanType get(KeyType id, TransactionHandler transactionHandler, Field... relations) throws SQLException;

	public BeanType get(KeyType id, Connection connection, Field... relations) throws SQLException;

	public List<BeanType> getAll(Field... relations) throws SQLException;

	public List<BeanType> getAll(TransactionHandler transactionHandler, Field... relations) throws SQLException;

	public List<BeanType> getAll(Connection connection, Field... relations) throws SQLException;

	public void delete(BeanType bean) throws SQLException;

	public void delete(BeanType bean, TransactionHandler transactionHandler) throws SQLException;

	public void delete(BeanType bean, Connection connection) throws SQLException;
}
