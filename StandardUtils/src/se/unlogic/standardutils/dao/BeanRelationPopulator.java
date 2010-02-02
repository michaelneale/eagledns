package se.unlogic.standardutils.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class BeanRelationPopulator<T> implements BeanResultSetPopulator<T>{

	private BeanResultSetPopulator<T> populator;
	private List<ManyToOneRelation<T,?,?>> manyToOneRelations;
	private Connection connection;
	private RelationQuery relationQuery;

	public BeanRelationPopulator(BeanResultSetPopulator<T> populator, List<ManyToOneRelation<T,?,?>> manyToOneRelations, Connection connection, RelationQuery relationQuery) {
		super();
		this.populator = populator;
		this.manyToOneRelations = manyToOneRelations;
		this.connection = connection;
		this.relationQuery = relationQuery;
	}

	public T populate(ResultSet rs) throws SQLException {

		T bean = populator.populate(rs);

		if(bean != null){

			for(ManyToOneRelation<T, ?, ?> relation : manyToOneRelations){

				relation.setValue(bean, rs, connection, relationQuery);
			}
		}

		return bean;
	}


}
