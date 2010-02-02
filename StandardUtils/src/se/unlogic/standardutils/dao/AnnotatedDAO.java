package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.sql.DataSource;

import se.unlogic.standardutils.annotations.UnsupportedFieldTypeException;
import se.unlogic.standardutils.bool.BooleanSignal;
import se.unlogic.standardutils.dao.annotations.DAOPopulate;
import se.unlogic.standardutils.dao.annotations.ManyToMany;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.OneToOne;
import se.unlogic.standardutils.dao.annotations.OrderBy;
import se.unlogic.standardutils.dao.annotations.PrimaryKey;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.dao.enums.Order;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.BooleanQuery;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.PreparedStatementQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.numbers.Counter;
import se.unlogic.standardutils.populators.QueryParameterPopulator;
import se.unlogic.standardutils.populators.annotated.AnnotatedResultSetPopulator;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;

public class AnnotatedDAO<T> {

	private static final OrderByComparator ORDER_BY_COMPARATOR = new OrderByComparator();
	
	protected final BeanResultSetPopulator<T> populator;
	protected final DataSource dataSource;
	protected final Class<T> beanClass;
	protected final QueryParameterPopulator<?>[] queryParameterPopulators;

	protected ArrayList<SimpleColumn<T, ?>> simpleKeys = new ArrayList<SimpleColumn<T, ?>>();
	protected ArrayList<SimpleColumn<T, ?>> simpleColumns = new ArrayList<SimpleColumn<T, ?>>();
	protected HashMap<Field, Column<T, ?>> columnMap = new HashMap<Field, Column<T, ?>>();

	protected HashMap<Field, ManyToOneRelation<T, ?, ?>> manyToOneRelations = new HashMap<Field, ManyToOneRelation<T, ?, ?>>();
	protected HashMap<Field, ManyToOneRelation<T, ?, ?>> manyToOneRelationKeys = new HashMap<Field, ManyToOneRelation<T, ?, ?>>();
	protected HashMap<Field, OneToManyRelation<T, ?>> oneToManyRelations = new HashMap<Field, OneToManyRelation<T, ?>>();
	protected HashMap<Field, ManyToManyRelation<T, ?>> manyToManyRelations = new HashMap<Field, ManyToManyRelation<T, ?>>();

	protected TreeMap<OrderBy,Column<T,?>> columnOrderMap = new TreeMap<OrderBy, Column<T,?>>(ORDER_BY_COMPARATOR);
	
	protected String tableName;

	protected String insertSQL;
	protected String updateSQL;
	protected String deleteSQL;
	protected String checkIfExistsSQL;
	protected String deleteByFieldSQL;
	protected String getSQL;
	protected String defaultSortingCriteria;

	public AnnotatedDAO(DataSource dataSource, Class<T> beanClass, AnnotatedDAOFactory daoFactory) {

		this(dataSource, beanClass, daoFactory, new AnnotatedResultSetPopulator<T>(beanClass));
	}

	public AnnotatedDAO(DataSource dataSource, Class<T> beanClass, AnnotatedDAOFactory daoFactory, BeanResultSetPopulator<T> populator,
			QueryParameterPopulator<?>... queryParameterPopulators) {

		super();
		this.populator = populator;
		this.dataSource = dataSource;
		this.beanClass = beanClass;
		this.queryParameterPopulators = queryParameterPopulators;

		Table table = beanClass.getAnnotation(Table.class);

		if (table == null) {

			throw new RuntimeException("No @Table annotation found in  " + beanClass);
		} else {
			tableName = table.name();
		}

		this.tableName = table.name();

		List<Field> fields = ReflectionUtils.getFields(beanClass);

		for (Field field : fields) {

			DAOPopulate daoManaged = field.getAnnotation(DAOPopulate.class);
			OrderBy orderBy = field.getAnnotation(OrderBy.class);
			
			if (daoManaged != null) {

				ReflectionUtils.fixFieldAccess(field);

				if (field.getAnnotation(OneToOne.class) != null) {

					// TODO Relation use this class pk, no extra field
					throw new RuntimeException("OneToOne relations are not implemented yet!");

				} else if (field.getAnnotation(OneToMany.class) != null) {
					
					this.checkOrderByAnnotation(field,orderBy);
					
					if (field.getType() != List.class) {

						throw new UnsupportedFieldTypeException("The annotated field " + field.getName() + " in  " + beanClass + " is of unsupported type "
								+ field.getType() + ". Fields annotated as @OneToMany have to be a genericly typed " + List.class, field, OneToMany.class,
								beanClass);
					}

					if (ReflectionUtils.getGenericlyTypeCount(field) != 1) {

						throw new UnsupportedFieldTypeException("The annotated field " + field.getName() + " in  " + beanClass
								+ " is genericly typed. Fields annotated as @OneToMany have to be a genericly typed " + List.class, field, OneToMany.class,
								beanClass);
					}

					// This is a bit ugly but still necessary until someone else
					// comes up with something smarter...
					Class<?> remoteClass = (Class<?>) ReflectionUtils.getGenericType(field);

					// Use this class pks, no extra field
					this.oneToManyRelations.put(field, DefaultOneToManyRelation.getGenericInstance(beanClass, remoteClass, field, daoFactory, daoManaged));

				} else if (field.getAnnotation(ManyToOne.class) != null) {
					
					ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
					
					Field[] remoteClassFields = field.getType().getDeclaredFields();

					Field matchingRemoteField = null;

					if (remoteClassFields != null) {

						for (Field remoteField : remoteClassFields) {

							if (remoteField.isAnnotationPresent(DAOPopulate.class) && remoteField.isAnnotationPresent(OneToMany.class)
									&& remoteField.getType() == List.class && ReflectionUtils.isGenericlyTyped(remoteField)
									&& ((Class<?>) ReflectionUtils.getGenericType(remoteField) == this.beanClass)) {

								matchingRemoteField = remoteField;

								break;
							}
						}
					}

					if (matchingRemoteField == null) {

						throw new RuntimeException("No corresponding @OneToMany annotated field found in  " + field.getType()
								+ " matching @ManyToOne relation of field " + field.getName() + " in  " + beanClass + "!");
					}

					Field remoteKeyField = null;

					try {
						remoteKeyField = field.getType().getDeclaredField(manyToOne.remoteKeyField());
					} catch (NoSuchFieldException e) {}
					
					//Check if the remote key field is @DAOPopluate annotated?
					
					if (remoteKeyField == null) {

						throw new RuntimeException("Unable to find key field " + manyToOne.remoteKeyField() + " in " + field.getType() + " specified for @ManyToOne annotated field "
								+ field.getName() + " in " + beanClass);
					}

					DefaultManyToOneRelation<T, ?, ?> relation = null;

					PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);

					if (primaryKey != null) {

						relation = DefaultManyToOneRelation.getGenericInstance(beanClass, field.getType(), remoteKeyField.getType(), field, remoteKeyField,daoManaged, daoFactory, primaryKey.autoGenerated());

						manyToOneRelationKeys.put(field, relation);

					} else {

						relation = DefaultManyToOneRelation.getGenericInstance(beanClass, field.getType(), remoteKeyField.getType(), field, remoteKeyField,	daoManaged, daoFactory, false);

						this.manyToOneRelations.put(field, relation);
					}

					this.columnMap.put(field, relation);

					if(orderBy != null){
						this.columnOrderMap.put(orderBy, relation);
					}
					
				} else if (field.getAnnotation(ManyToMany.class) != null) {

					this.checkOrderByAnnotation(field,orderBy);
					
					if (field.getType() != List.class) {

						throw new UnsupportedFieldTypeException("The annotated field " + field.getName() + " in  " + beanClass + " is of unsupported type "
								+ field.getType() + ". Fields annotated as @ManyToMany have to be a genericly typed " + List.class, field, ManyToMany.class,
								beanClass);
					}

					if (ReflectionUtils.getGenericlyTypeCount(field) != 1) {

						throw new UnsupportedFieldTypeException("The annotated field " + field.getName() + " in  " + beanClass
								+ " is genericly typed. Fields annotated as @ManyToMany have to be a genericly typed " + List.class, field, ManyToMany.class,
								beanClass);
					}

					// This is a bit ugly but still necessary until someone else
					// comes up with something smarter...
					Class<?> remoteClass = (Class<?>) ReflectionUtils.getGenericType(field);

					this.manyToManyRelations.put(field, DefaultManyToManyRelation.getGenericInstance(beanClass, remoteClass, field, daoFactory, daoManaged));

				} else {

					QueryParameterPopulator<?> queryPopulator = this.getQueryParameterPopulator(field.getType());

					Method method = null;

					if (queryPopulator == null) {

						method = PreparedStatementQueryMethods.getQueryMethod(field.getType());

						if (method == null) {

							throw new RuntimeException("No query method found for @DAOManaged annotate field " + field.getName() + " in  " + beanClass);
						}
					}

					String columnName = daoManaged.columnName();

					if (StringUtils.isEmpty(columnName)) {

						columnName = field.getName();
					}

					SimpleColumn<T, ?> simpleColumn = null;

					PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);

					if (primaryKey != null) {

						simpleColumn = SimpleColumn.getGenericInstance(beanClass, field.getType(), field, method, queryPopulator, columnName, primaryKey
								.autoGenerated());

						this.simpleKeys.add(simpleColumn);

					} else {

						simpleColumn = SimpleColumn.getGenericInstance(beanClass, field.getType(), field, method, queryPopulator, columnName, false);

						this.simpleColumns.add(simpleColumn);

					}

					this.columnMap.put(field, simpleColumn);
					
					if(orderBy != null){
						this.columnOrderMap.put(orderBy, simpleColumn);
					}
				}
			}
		}

		if (this.simpleKeys.isEmpty() && this.manyToOneRelationKeys.isEmpty()) {

			throw new RuntimeException("No @PrimaryKey annotated field found in  " + beanClass + "!");
		}

		// Genearate SQL statements
		this.generateInsertSQL();
		this.generateUpdateSQL();
		this.generateDeleteSQL();
		this.generateCheckIfExistsSQL();
		this.generateDeleteByFieldSQL();
		this.generateGetSQL();
		this.generateDefaultSortingCriteria();
	}

	@SuppressWarnings("unchecked")
	public <QPT> QueryParameterPopulator<QPT> getQueryParameterPopulator(Class<QPT> type) {

		if (queryParameterPopulators != null) {

			for (QueryParameterPopulator<?> queryParameterPopulator : queryParameterPopulators) {

				if (type.equals(queryParameterPopulator.getType())) {

					return (QueryParameterPopulator<QPT>) queryParameterPopulator;
				}
			}
		}

		return null;
	}

	private void checkOrderByAnnotation(Field field, OrderBy orderBy) {

		if(orderBy != null){
			
			throw new RuntimeException("Invalid @OrderBy annotation on field " + field.getName() + " in " + this.beanClass + ", the @OrderBy annotation is not allowed on @ManyToOne and @ManyToMany annotated fields.");
		}
	}	

	private void generateDefaultSortingCriteria() {

		if(!this.columnOrderMap.isEmpty()){
		
			StringBuilder stringBuilder = new StringBuilder(" ORDER BY ");
			
			boolean first = true;
			
			for(Entry<OrderBy,Column<T,?>> entry : this.columnOrderMap.entrySet()){
			
				if(first){
					
					first = false;
					
				}else{
					
					stringBuilder.append(", ");
				}
				
				stringBuilder.append(entry.getValue().getColumnName() + " " + entry.getKey().order().toString());
			}
			
			this.defaultSortingCriteria = stringBuilder.toString();
			
		}else{
			
			this.defaultSortingCriteria = "";
		}
	}	
	
	protected void generateInsertSQL() {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("INSERT INTO " + this.tableName + "(");

		boolean first = true;

		for (Column<T, ?> column : this.columnMap.values()) {

			if (first) {

				first = false;

			} else {

				stringBuilder.append(", ");
			}

			stringBuilder.append(column.getColumnName());
		}

		stringBuilder.append(") VALUES (");

		first = true;

		for (@SuppressWarnings("unused")
		Column<T, ?> column : this.columnMap.values()) {

			if (first) {

				first = false;

			} else {

				stringBuilder.append(", ");
			}

			stringBuilder.append("?");
		}

		stringBuilder.append(")");

		this.insertSQL = stringBuilder.toString();
	}

	protected void generateUpdateSQL() {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("UPDATE " + this.tableName + " SET ");

		boolean first = true;

		for (Column<T, ?> column : this.columnMap.values()) {

			if (first) {

				first = false;

			} else {

				stringBuilder.append(", ");
			}

			stringBuilder.append(column.getColumnName() + " = ?");
		}

		stringBuilder.append(" WHERE ");

		this.appendPrimaryKeyWhereStatement(stringBuilder);

		this.updateSQL = stringBuilder.toString();
	}

	protected void generateCheckIfExistsSQL(){
		
		StringBuilder stringBuilder = new StringBuilder("SELECT 1 FROM " + tableName + " WHERE ");
				
		this.appendPrimaryKeyWhereStatement(stringBuilder);
		
		this.checkIfExistsSQL = stringBuilder.toString();
	}
	
	private void appendPrimaryKeyWhereStatement(StringBuilder stringBuilder) {

		boolean first = true;

		for (Column<T, ?> column : this.simpleKeys) {

			if (first) {

				first = false;

			} else {

				stringBuilder.append(" AND");
			}

			stringBuilder.append(column.getColumnName() + " = ?");
		}

		for (Column<T, ?> column : this.manyToOneRelationKeys.values()) {

			if (first) {

				first = false;

			} else {

				stringBuilder.append(" AND ");
			}

			stringBuilder.append(column.getColumnName() + " = ?");
		}
	}

	protected void generateDeleteSQL() {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("DELETE FROM ");
		stringBuilder.append(tableName);
		stringBuilder.append(" WHERE ");

		this.appendPrimaryKeyWhereStatement(stringBuilder);

		this.deleteSQL = stringBuilder.toString();
	}

	protected void generateDeleteByFieldSQL() {

		this.deleteByFieldSQL = "DELETE FROM " + tableName;
	}

	protected void generateGetSQL() {

		this.getSQL = "SELECT * FROM " + tableName;
	}

	public TransactionHandler createTransaction() throws SQLException {

		return new TransactionHandler(dataSource);
	}

	public void add(T bean) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {

			transactionHandler = new TransactionHandler(dataSource);

			this.add(bean, transactionHandler.getConnection(), null);

			transactionHandler.commit();
		} finally {
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public void add(T bean,RelationQuery relationQuery) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {

			transactionHandler = new TransactionHandler(dataSource);

			this.add(bean, transactionHandler.getConnection(), relationQuery);

			transactionHandler.commit();
		} finally {
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public void addAll(Collection<T> beans,RelationQuery relationQuery) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {

			transactionHandler = new TransactionHandler(dataSource);

			this.addAll(beans, transactionHandler.getConnection(), relationQuery);

			transactionHandler.commit();
		} finally {
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public void add(T bean, TransactionHandler transactionHandler,RelationQuery relationQuery) throws SQLException {

		this.add(bean, transactionHandler.getConnection(), relationQuery);
	}

	public void addAll(List<T> beans, TransactionHandler transactionHandler,RelationQuery relationQuery) throws SQLException {

		this.addAll(beans, transactionHandler.getConnection(), relationQuery);
	}

	public void addAll(Collection<T> beans, Connection connection,RelationQuery relationQuery) throws SQLException {

		for (T bean : beans) {

			this.add(bean, connection, relationQuery);
		}
	}

	public void add(T bean, Connection connection,RelationQuery relationQuery) throws SQLException {

		try {
			this.preAddRelations(bean, connection, relationQuery);
			
			UpdateQuery query = null;

			try {

				query = new UpdateQuery(connection, false, this.insertSQL);

				Counter counter = new Counter();

				setQueryValues(bean, query, counter, this.columnMap.values());

				if (this.simpleKeys.size() == 1 && this.simpleKeys.get(0).isAutoGenerated()) {

					this.simpleKeys.get(0).getBeanField().set(bean, query.executeUpdate());

				} else {

					query.executeUpdate();
				}

				this.addRelations(bean, connection, relationQuery);

			} finally {

				PreparedStatementQuery.autoCloseQuery(query);
			}

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);

		}
	}

	private void preAddRelations(T bean, Connection connection, RelationQuery relationQuery) throws SQLException{
		
		if(RelationQuery.hasRelations(relationQuery)){
			
			for (Field relation : relationQuery.getRelations()) {
				
				ManyToOneRelation<T, ?, ?> manyToOneRelation = this.manyToOneRelations.get(relation);
				
				if(manyToOneRelation != null){
					
					manyToOneRelation.add(bean, connection, relationQuery);
					continue;
				}
				
				manyToOneRelation = this.manyToOneRelationKeys.get(relation);
				
				if(manyToOneRelation != null){
					
					manyToOneRelation.add(bean, connection, relationQuery);
					continue;
				}	
			}
		}	
	}
	
	private void addRelations(T bean, Connection connection, RelationQuery relationQuery) throws SQLException {

		if(RelationQuery.hasRelations(relationQuery)){
			
			for (Field relation : relationQuery.getRelations()) {

				OneToManyRelation<T, ?> oneToManyRelation = this.oneToManyRelations.get(relation);

				if (oneToManyRelation != null) {

					oneToManyRelation.add(bean, connection, relationQuery);
					continue;
				}

				ManyToManyRelation<T, ?> manyToManyRelation = this.manyToManyRelations.get(relation);

				if (manyToManyRelation != null) {

					manyToManyRelation.add(bean, connection, relationQuery);
					continue;
				}
			}			
		}
	}
	
	private void preUpdateRelations(T bean, Connection connection, RelationQuery relationQuery) throws SQLException{

		if(RelationQuery.hasRelations(relationQuery)){
			
			for (Field relation : relationQuery.getRelations()) {
				
				ManyToOneRelation<T, ?, ?> manyToOneRelation = this.manyToOneRelations.get(relation);
				
				if(manyToOneRelation != null){
					
					manyToOneRelation.update(bean, connection, relationQuery);
					continue;
				}
				
				manyToOneRelation = this.manyToOneRelationKeys.get(relation);
				
				if(manyToOneRelation != null){
					
					manyToOneRelation.update(bean, connection, relationQuery);
					continue;
				}	
			}			
		}	
	}	
		
	private void updateRelations(T bean, Connection connection, RelationQuery relationQuery) throws SQLException {

		if(RelationQuery.hasRelations(relationQuery)){
			
			for (Field relation : relationQuery.getRelations()) {

				OneToManyRelation<T, ?> oneToManyRelation = this.oneToManyRelations.get(relation);

				if (oneToManyRelation != null) {

					oneToManyRelation.update(bean, connection, relationQuery);
					continue;
				}

				ManyToManyRelation<T, ?> manyToManyRelation = this.manyToManyRelations.get(relation);

				if (manyToManyRelation != null) {

					manyToManyRelation.update(bean, connection, relationQuery);
				}
			}			
		}		
	}

	private void setQueryValues(T bean, PreparedStatementQuery query, Counter counter, Collection<? extends Column<T, ?>> columns) throws SQLException {

		for (Column<T, ?> column : columns) {
			
			if (column.getQueryParameterPopulator() != null) {
				
				column.getQueryParameterPopulator().populate(query, counter.increment(), column.getBeanValue(bean));

			} else {

				try {
					column.getQueryMethod().invoke(query, counter.increment(), column.getBeanValue(bean));

				} catch (IllegalArgumentException e) {

					throw new RuntimeException(e);

				} catch (IllegalAccessException e) {

					throw new RuntimeException(e);

				} catch (InvocationTargetException e) {

					throw new RuntimeException(e);
				}
			}
		}
	}

	private void setQueryValues(List<T> beans, PreparedStatementQuery query, Counter counter, Collection<? extends Column<T, ?>> columns) throws SQLException {

		for (Column<T, ?> column : columns) {

			for (T bean : beans) {

				if (column.getQueryParameterPopulator() != null) {

					column.getQueryParameterPopulator().populate(query, counter.increment(), column.getBeanValue(bean));

				} else {

					try {
						column.getQueryMethod().invoke(query, counter.increment(), column.getBeanValue(bean));

					} catch (IllegalArgumentException e) {

						throw new RuntimeException(e);

					} catch (IllegalAccessException e) {

						throw new RuntimeException(e);

					} catch (InvocationTargetException e) {

						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	public void addOrUpdateAll(Collection<T> beans, Connection connection,RelationQuery relationQuery) throws SQLException {

		for (T bean : beans) {
			this.addOrUpdate(bean, connection, relationQuery);
		}
	}

	public void addOrUpdate(T bean, Connection connection,RelationQuery relationQuery) throws SQLException {

		for(Column<T,?> column : this.simpleKeys){
			
			if(column.getBeanValue(bean) == null){
				
				//PrimaryKey not set, presume new bean
				this.add(bean, connection, relationQuery);
				return;
			}
		}
		
		for(Column<T,?> column : this.manyToOneRelationKeys.values()){
			
			if(column.getBeanValue(bean) == null){
				
				//PrimaryKey not set, presume new bean
				this.add(bean, connection, relationQuery);
				return;
			}
		}
		
		//Unable to determine if beans is new or not so far, do db query to check
		if(beanExists(bean, connection)){
			
			this.update(bean, connection, relationQuery);
			
		}else{
			
			this.add(bean, connection, relationQuery);
		}
	}

	public boolean beanExists(T bean, Connection connection) throws SQLException {

		BooleanQuery query = null;
		
		try{
			
			query = new BooleanQuery(connection, false, this.checkIfExistsSQL); //Resume here
			
			Counter counter = new Counter();
			
			// Keys from SimpleColumns for where statement
			this.setQueryValues(bean, query, counter, this.simpleKeys);

			// Keys from many to one relations for where statement
			this.setQueryValues(bean, query, counter, this.manyToOneRelationKeys.values());
			
			return query.executeQuery();
			
		}finally{
			
			BooleanQuery.autoCloseQuery(query);
		}
	}

	public void update(T bean) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {

			transactionHandler = new TransactionHandler(dataSource);

			this.update(bean, transactionHandler.getConnection(), null);

			transactionHandler.commit();
		} finally {
			TransactionHandler.autoClose(transactionHandler);
		}
	}
	
	public void update(T bean,RelationQuery relationQuery) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {

			transactionHandler = new TransactionHandler(dataSource);

			this.update(bean, transactionHandler.getConnection(), relationQuery);

			transactionHandler.commit();
		} finally {
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public void update(T bean, TransactionHandler transactionHandler,RelationQuery relationQuery) throws SQLException {

		this.update(bean, transactionHandler.getConnection(), relationQuery);
	}

	public Integer update(T bean, Connection connection,RelationQuery relationQuery) throws SQLException {

		UpdateQuery query = null;

		try{
			
			this.preUpdateRelations(bean, connection, relationQuery);
			
			query = new UpdateQuery(connection, false, this.updateSQL);

			Counter counter = new Counter();

			// All fields
			this.setQueryValues(bean, query, counter, this.columnMap.values());

			// Keys from SimpleColumns for where statement
			this.setQueryValues(bean, query, counter, this.simpleKeys);

			// Keys from many to one relations for where statement
			this.setQueryValues(bean, query, counter, this.manyToOneRelationKeys.values());

			query.executeUpdate();			
			
		}finally{
			
			UpdateQuery.autoCloseQuery(query);
		}

		this.updateRelations(bean, connection, relationQuery);

		return query.getAffectedRows();
	}

	public Integer update(LowLevelQuery lowLevelQuery) throws SQLException {

		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();

			return this.update(lowLevelQuery, connection);

		} finally {
			DBUtils.closeConnection(connection);
		}
	}
	
	public Integer update(LowLevelQuery lowLevelQuery, TransactionHandler transactionHandler) throws SQLException {

		return this.update(lowLevelQuery, transactionHandler.getConnection());
	}
	
	public Integer update(LowLevelQuery lowLevelQuery, Connection connection) throws SQLException {
		
		UpdateQuery query = null;

		try {

			query = new UpdateQuery(connection, false, lowLevelQuery.getSql());

			this.setCustomQueryParameters(query, lowLevelQuery.getParameters());

			return query.executeUpdate();

		} finally {

			PreparedStatementQuery.autoCloseQuery(query);
		}
	}		
	
	public T get(LowLevelQuery lowLevelQuery) throws SQLException {

		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();

			return this.get(lowLevelQuery, connection);

		} finally {
			DBUtils.closeConnection(connection);
		}
	}
	
	public T get(LowLevelQuery lowLevelQuery, TransactionHandler transactionHandler) throws SQLException {

		return this.get(lowLevelQuery, transactionHandler.getConnection());
	}
	
	public T get(LowLevelQuery lowLevelQuery, Connection connection) throws SQLException {
		
		BeanResultSetPopulator<T> populator = this.getPopulator(connection, lowLevelQuery);

		ObjectQuery<T> query = null;

		try {

			query = new ObjectQuery<T>(connection, false, lowLevelQuery.getSql(), populator);

			this.setCustomQueryParameters(query, lowLevelQuery.getParameters());

			T bean = query.executeQuery();

			if (bean != null && RelationQuery.hasRelations(lowLevelQuery)) {

				this.populateRelations(bean, connection, lowLevelQuery);
			}

			return bean;

		} finally {

			PreparedStatementQuery.autoCloseQuery(query);
		}
	}	
	
	public T get(HighLevelQuery<T> highLevelQuery) throws SQLException {

		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();

			return this.get(highLevelQuery, connection);

		} finally {
			DBUtils.closeConnection(connection);
		}
	}

	public T get(HighLevelQuery<T> highLevelQuery, TransactionHandler transactionHandler) throws SQLException {

		return this.get(highLevelQuery, transactionHandler.getConnection());
	}

	public T get(HighLevelQuery<T> highLevelQuery, Connection connection)	throws SQLException {

		BeanResultSetPopulator<T> populator = this.getPopulator(connection, highLevelQuery);

		ObjectQuery<T> query = null;

		try {
			query = new ObjectQuery<T>(connection, false, this.getSQL + this.getCriterias(highLevelQuery, true), populator);

			if(highLevelQuery.getParameters() != null){
				
				setQueryParameters(query, highLevelQuery, 1);
			}

			T bean = query.executeQuery();

			if (bean != null && RelationQuery.hasRelations(highLevelQuery)) {

				this.populateRelations(bean, connection, highLevelQuery);
			}

			return bean;

		} finally {

			PreparedStatementQuery.autoCloseQuery(query);
		}
	}

	public boolean getBoolean(HighLevelQuery<T> highLevelQuery) throws SQLException{
		
		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();

			return this.getBoolean(highLevelQuery, connection);

		} finally {
			DBUtils.closeConnection(connection);
		}
	}
	
	public boolean getBoolean(HighLevelQuery<T> highLevelQuery, Connection connection) throws SQLException{

		BooleanQuery query = null;

		try {
			query = new BooleanQuery(connection, false, this.getSQL + this.getCriterias(highLevelQuery, true));

			if(highLevelQuery.getParameters() != null){
				
				setQueryParameters(query, highLevelQuery, 1);
			}

			return query.executeQuery();

		} finally {

			PreparedStatementQuery.autoCloseQuery(query);
		}
	}
	
	private String getCriterias(HighLevelQuery<T> highLevelQuery, boolean orderBy) {

		if(highLevelQuery == null){
			
			if(orderBy){
				
				return this.defaultSortingCriteria;
				
			}else{
				
				return "";
			}
		}
		
		
		StringBuilder stringBuilder = new StringBuilder();

		if(highLevelQuery.getParameters() != null){
		
			boolean first = true;

			for (QueryParameter<T, ?> queryParameter : highLevelQuery.getParameters()) {

				if (first) {

					stringBuilder.append(" WHERE " + queryParameter.getColumn().getColumnName() + " " + queryParameter.getOperator() + " ?");

					first = false;

				} else {

					stringBuilder.append(" AND " + queryParameter.getColumn().getColumnName() + " " + queryParameter.getOperator() + " ?");
				}
			}			
		}

		if(orderBy && highLevelQuery.getOrderByCriterias() != null){
			
			boolean first = true;
			
			for(OrderByCriteria<T> criteria : highLevelQuery.getOrderByCriterias()){
				
				if(first){
					
					stringBuilder.append(" ORDER BY " + criteria.getColumn().getColumnName() + " " + criteria.getOrder().toString());
					
					first = false;
					
				}else{
					
					stringBuilder.append(", " + criteria.getColumn().getColumnName() + " " + criteria.getOrder().toString());
				}
			}
			
		}else{
			
			stringBuilder.append(this.defaultSortingCriteria);
		}
		
		return stringBuilder.toString();
	}

	@SuppressWarnings("unchecked")
	private <ColumnType> Column<T, ? super ColumnType> getColumn(Field field, Class<ColumnType> paramClass) {

		Column<T, ?> column = this.columnMap.get(field);

		if (column == null) {

			throw new RuntimeException("Field " + field + " not found in  " + this.beanClass + "!");

		} else if (!column.getParamType().isAssignableFrom(paramClass)) {

			throw new RuntimeException(" " + paramClass + " is not compatible with type " + column.getParamType() + " of field " + field + " in  "
					+ this.beanClass + "!");
		}

		return (Column<T, ColumnType>) column;
	}

	protected BeanResultSetPopulator<T> getPopulator(Connection connection, RelationQuery relationQuery) {

		if (RelationQuery.hasRelations(relationQuery)) {

			ArrayList<ManyToOneRelation<T, ?, ?>> manyToOneRelations = null;

			for (Field relation : relationQuery.getRelations()) {

				ManyToOneRelation<T, ?, ?> manyToOneRelation = this.manyToOneRelations.get(relation);

				if (manyToOneRelation == null) {

					manyToOneRelation = this.manyToOneRelationKeys.get(relation);
				}

				if (manyToOneRelation != null) {

					if (manyToOneRelations == null) {

						manyToOneRelations = new ArrayList<ManyToOneRelation<T, ?, ?>>();
					}

					manyToOneRelations.add(manyToOneRelation);
				}
			}

			if (manyToOneRelations != null) {

				return new BeanRelationPopulator<T>(this.populator, manyToOneRelations, connection, relationQuery);
			}
		}

		return this.populator;
	}

	protected void populateRelations(T bean, Connection connection, RelationQuery relationQuery) throws SQLException {

		for (Field relation : relationQuery.getRelations()) {

			OneToManyRelation<T, ?> oneToManyRelation = this.oneToManyRelations.get(relation);

			if (oneToManyRelation != null) {

				oneToManyRelation.setValue(bean, connection, relationQuery);
				continue;
			}

			ManyToManyRelation<T, ?> manyToManyRelation = this.manyToManyRelations.get(relation);

			if (manyToManyRelation != null) {

				manyToManyRelation.setValue(bean, connection, relationQuery);
			}
		}
	}

	public List<T> getAll(LowLevelQuery lowLevelQuery) throws SQLException {

		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();

			return this.getAll(lowLevelQuery, connection);

		} finally {
			DBUtils.closeConnection(connection);
		}
	}
	
	public List<T> getAll(LowLevelQuery lowLevelQuery, TransactionHandler transactionHandler) throws SQLException {

		return this.getAll(lowLevelQuery, transactionHandler.getConnection());
	}	
		
	public List<T> getAll(LowLevelQuery lowLevelQuery, Connection connection) throws SQLException {
		
		BeanResultSetPopulator<T> populator = this.getPopulator(connection, lowLevelQuery);

		ArrayListQuery<T> query = null;

		try {

			query = new ArrayListQuery<T>(connection, false, lowLevelQuery.getSql(), populator);

			setCustomQueryParameters(query, lowLevelQuery.getParameters());

			ArrayList<T> beans = query.executeQuery();

			if (beans != null && RelationQuery.hasRelations(lowLevelQuery)) {

				for (T bean : beans) {

					this.populateRelations(bean, connection, lowLevelQuery);
				}
			}

			return beans;

		} finally {

			PreparedStatementQuery.autoCloseQuery(query);
		}
	}
	
	private void setCustomQueryParameters(PreparedStatementQuery query, List<?> parameters) {

		if(parameters != null){
			
			int i = 1;
			
			for(Object object : parameters){
		
				Method queryMethod;
				
				if(object == null){
					
					queryMethod = PreparedStatementQueryMethods.getObjectQueryMethod();
					
				}else{
					
					queryMethod = PreparedStatementQueryMethods.getQueryMethod(object.getClass());
				}
				
				if(queryMethod == null){
					throw new RuntimeException("Unable to find suitable prepared statement query method for parameter " + object.getClass());
				}
				
				try {
					queryMethod.invoke(query, i++, object);
					
				} catch (IllegalArgumentException e) {

					throw new RuntimeException(e);
					
				} catch (IllegalAccessException e) {

					throw new RuntimeException(e);
					
				} catch (InvocationTargetException e) {

					throw new RuntimeException(e);
				}
			}
		}
	}

	//TODO replace all calls to this method with high or low level queries
	public List<T> getAll(String sql, CustomQueryParameter<?> queryParameter, Connection connection,
			RelationQuery relationQuery) throws SQLException {

		BeanResultSetPopulator<T> populator = this.getPopulator(connection, relationQuery);

		ArrayListQuery<T> query = null;

		try {

			query = new ArrayListQuery<T>(connection, false, sql, populator);

			if (queryParameter.getQueryParameterPopulator() != null) {

				queryParameter.getQueryParameterPopulator().populate(query, 1, queryParameter.getParamValue());

			} else {

				try {
					queryParameter.getQueryMethod().invoke(query, 1, queryParameter.getParamValue());

				} catch (IllegalArgumentException e) {

					throw new RuntimeException(e);

				} catch (IllegalAccessException e) {

					throw new RuntimeException(e);

				} catch (InvocationTargetException e) {

					throw new RuntimeException(e);
				}
			}

			ArrayList<T> beans = query.executeQuery();

			if (beans != null && RelationQuery.hasRelations(relationQuery)) {

				for (T bean : beans) {

					this.populateRelations(bean, connection, relationQuery);
				}
			}

			return beans;

		} finally {

			PreparedStatementQuery.autoCloseQuery(query);
		}
	}

	public List<T> getAll(HighLevelQuery<T> highLevelQuery) throws SQLException {

		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();

			return this.getAll(highLevelQuery, connection);

		} finally {
			DBUtils.closeConnection(connection);
		}
	}

	public List<T> getAll(HighLevelQuery<T> highLevelQuery, TransactionHandler transactionHandler) throws SQLException {

		return this.getAll(highLevelQuery, transactionHandler.getConnection());
	}

	public List<T> getAll(HighLevelQuery<T> highLevelQuery, Connection connection)
			throws SQLException {

		BeanResultSetPopulator<T> populator = this.getPopulator(connection, highLevelQuery);

		ArrayListQuery<T> query = null;

		try {
			query = new ArrayListQuery<T>(connection, false, this.getSQL + this.getCriterias(highLevelQuery, true), populator);

			setQueryParameters(query, highLevelQuery, 1);

			ArrayList<T> beans = query.executeQuery();

			if (beans != null && RelationQuery.hasRelations(highLevelQuery)) {

				for (T bean : beans) {

					this.populateRelations(bean, connection, highLevelQuery);
				}
			}

			return beans;

		} finally {

			PreparedStatementQuery.autoCloseQuery(query);
		}
	}

	public List<T> getAll()	throws SQLException {

		return this.getAll((HighLevelQuery<T>)null);
	}	
	
	public void delete(T bean) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {

			transactionHandler = new TransactionHandler(dataSource);

			this.delete(bean, transactionHandler);

			transactionHandler.commit();
		} finally {
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public void delete(T bean, TransactionHandler transactionHandler) throws SQLException {

		this.delete(bean, transactionHandler.getConnection());
	}

	public void delete(T bean, Connection connection) throws SQLException {

		UpdateQuery query = null;

		try {

			query = new UpdateQuery(connection, false, this.deleteSQL);

			Counter counter = new Counter();

			// Keys from SimpleColumns for where statement
			this.setQueryValues(bean, query, counter, this.simpleKeys);

			// Keys from many to one relations for where statement
			this.setQueryValues(bean, query, counter, this.manyToOneRelationKeys.values());

			query.executeUpdate();

		} finally {
			PreparedStatementQuery.autoCloseQuery(query);
		}
	}

	public Integer delete(HighLevelQuery<T> highLevelQuery) throws SQLException {

		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();

			return this.delete(highLevelQuery, connection);

		} finally {
			DBUtils.closeConnection(connection);
		}
	}

	public Integer delete(HighLevelQuery<T> highLevelQuery, TransactionHandler transactionHandler) throws SQLException {

		return this.delete(highLevelQuery, transactionHandler.getConnection());
	}

	public Integer delete(HighLevelQuery<T> highLevelQuery, Connection connection) throws SQLException {

		UpdateQuery query = null;

		try {
			query = new UpdateQuery(connection, false, this.deleteByFieldSQL + this.getCriterias(highLevelQuery, false));

			setQueryParameters(query, highLevelQuery, 1);

			query.executeUpdate();

			return query.getAffectedRows();

		} finally {

			PreparedStatementQuery.autoCloseQuery(query);
		}
	}

	public Integer deleteWhereNotIn(List<T> beans, Connection connection, QueryParameter<T, ?>... queryParameters)
			throws SQLException {

		// Generate SQL
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("DELETE FROM ");
		stringBuilder.append(tableName);
		stringBuilder.append(" WHERE");

		int beanCount = beans.size();

		BooleanSignal signal = new BooleanSignal();

		this.generateColumWhereNotInSQL(this.simpleKeys, stringBuilder, beanCount, signal);
		this.generateColumWhereNotInSQL(this.manyToOneRelationKeys.values(), stringBuilder, beanCount, signal);

		if (queryParameters != null) {

			for (QueryParameter<T, ?> queryParameter : queryParameters) {

				stringBuilder.append(" AND " + queryParameter.getColumn().getColumnName() + " " + queryParameter.getOperator() + " ?");
			}
		}

		UpdateQuery query = null;

		try {

			query = new UpdateQuery(connection, false, stringBuilder.toString());

			Counter counter = new Counter();

			// Keys values from SimpleColumns for where not in statement
			this.setQueryValues(beans, query, counter, this.simpleKeys);

			// Keys values from many to one relations for where not in statement
			this.setQueryValues(beans, query, counter, this.manyToOneRelationKeys.values());

			if (queryParameters != null) {

				// Set query param values
				this.setQueryParameters(query, new HighLevelQuery<T>(queryParameters), counter.getValue());
			}

			return query.executeUpdate();

		} finally {
			PreparedStatementQuery.autoCloseQuery(query);
		}
	}

	private void generateColumWhereNotInSQL(Collection<? extends Column<T, ?>> keyColumns, StringBuilder stringBuilder, int beanCount, BooleanSignal signal) {

		for (Column<T, ?> column : keyColumns) {

			if (signal.isSignal()) {

				stringBuilder.append(" AND");

			} else {

				signal.setSignal(true);
			}

			stringBuilder.append(" ");
			stringBuilder.append(column.getColumnName());
			stringBuilder.append(" NOT IN (");

			this.addQuestionMarks(beanCount, stringBuilder);

			stringBuilder.append(")");
		}

	}

	private void addQuestionMarks(int size, StringBuilder stringBuilder) {

		stringBuilder.append("?");

		if (size > 1) {

			for (int i = 2; i < size; i++) {

				stringBuilder.append("?,");
			}
		}
	}

	public <ParamType> QueryParameterFactory<T, ParamType> getParamFactory(Field field, Class<ParamType> paramClass) {

		return new QueryParameterFactory<T, ParamType>(this.getColumn(field, paramClass));
	}

	public <ParamType> QueryParameterFactory<T, ParamType> getParamFactory(String fieldName, Class<ParamType> paramClass) {

		Field field = ReflectionUtils.getField(beanClass, fieldName);

		if (field == null) {
			throw new RuntimeException("Field " + fieldName + " not found in  " + this.beanClass + "!");
		}

		return new QueryParameterFactory<T, ParamType>(this.getColumn(field, paramClass));
	}

	public OrderByCriteria<T> getOrderByCriteria(Field field, Order order){
		
		Column<T, ?> column = this.columnMap.get(field);
		
		if (column == null) {
			throw new RuntimeException("No @DAOPopulate annotated field with name " + field.getName() + " not found in  " + this.beanClass + "!");
		}
		
		return new OrderByCriteria<T>(order, column);
	}
	
	public OrderByCriteria<T> getOrderByCriteria(String fieldName, Order order){
		
		Field field = ReflectionUtils.getField(beanClass, fieldName);

		if (field == null) {
			throw new RuntimeException("Field " + fieldName + " not found in  " + this.beanClass + "!");
		}
		
		return this.getOrderByCriteria(field, order);
	}
	
	public String getTableName() {

		return tableName;
	}

	Column<T, ?> getColumn(Field field) {

		return this.columnMap.get(field);
	}

	public void setQueryParameter(PreparedStatementQuery query, QueryParameter<T, ?> queryParameter) throws SQLException {

		Column<?, ?> column = queryParameter.getColumn();

		if (column.getQueryParameterPopulator() != null) {

			column.getQueryParameterPopulator().populate(query, 1, column.getParamValue(queryParameter.getValue()));

		} else {

			try {
				column.getQueryMethod().invoke(query, 1, column.getParamValue(queryParameter.getValue()));

			} catch (IllegalArgumentException e) {

				throw new RuntimeException(e);

			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);

			} catch (InvocationTargetException e) {

				throw new RuntimeException(e);
			}
		}
	}

	public void setQueryParameters(PreparedStatementQuery query, HighLevelQuery<T> highLevelQuery, final int startIndex) throws SQLException {

		if(highLevelQuery != null && highLevelQuery.getParameters() != null){
		
			int index = startIndex;

			for (QueryParameter<?, ?> queryParameter : highLevelQuery.getParameters()) {

				Column<?, ?> column = queryParameter.getColumn();

				if (column.getQueryParameterPopulator() != null) {

					column.getQueryParameterPopulator().populate(query, index++, column.getParamValue(queryParameter.getValue()));

				} else {

					try {
						column.getQueryMethod().invoke(query, index++, column.getParamValue(queryParameter.getValue()));

					} catch (IllegalArgumentException e) {

						throw new RuntimeException(e);

					} catch (IllegalAccessException e) {

						throw new RuntimeException(e);

					} catch (InvocationTargetException e) {

						throw new RuntimeException(e);
					}
				}
			}			
		}
	}
}
