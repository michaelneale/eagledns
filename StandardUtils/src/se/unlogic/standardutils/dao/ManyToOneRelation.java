package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ManyToOneRelation<LocalType, RemoteType, RemoteKeyType> extends Column<LocalType,RemoteType>{

	public abstract RemoteKeyType getBeanValue(LocalType bean);

	public abstract RemoteKeyType getParamValue(Object bean);

	public abstract void setValue(LocalType bean, ResultSet resultSet, Connection connection, RelationQuery relations) throws SQLException;

	public abstract void add(LocalType bean, Connection connection, RelationQuery relations) throws SQLException;

	public abstract void update(LocalType bean, Connection connection, RelationQuery relations) throws SQLException;

	public abstract Field getField();
}