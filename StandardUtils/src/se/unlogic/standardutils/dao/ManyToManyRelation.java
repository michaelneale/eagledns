package se.unlogic.standardutils.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ManyToManyRelation<LocalType, RemoteType> {

	public abstract void setValue(LocalType bean, Connection connection, RelationQuery relations) throws SQLException;

	public abstract void add(LocalType bean, Connection connection, RelationQuery relations) throws SQLException;

	public abstract void update(LocalType bean, Connection connection, RelationQuery relations) throws SQLException;

}