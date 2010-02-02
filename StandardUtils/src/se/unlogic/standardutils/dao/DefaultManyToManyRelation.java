package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.unlogic.standardutils.dao.annotations.DAOPopulate;
import se.unlogic.standardutils.dao.annotations.ManyToMany;
import se.unlogic.standardutils.dao.annotations.PrimaryKey;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;


public class DefaultManyToManyRelation<LocalType,RemoteType> implements ManyToManyRelation<LocalType, RemoteType> {

	private final Field field;
	private final Field localKeyField;
	private final Field remoteKeyField;
	
	private final String linkTable;
	private final String localLinkTableColumnName;
	private final String remoteKeyColumnName;
	private final String remoteLinkTableColumnName;
	
	private String linkTableLinkSQL;
	private String linkTableDeleteSQL;
	private String linkTableInsertSQL;
	
	private Column<LocalType,?> localColumn;
	private Column<RemoteType, ?> remoteColumn;
	
	private final AnnotatedDAOFactory daoFactory;
	private AnnotatedDAO<RemoteType> annotatedDAO;
	private final Class<LocalType> beanClass;
	private final Class<RemoteType> remoteClass;
	
	private boolean initialized;
	
	public DefaultManyToManyRelation(Class<LocalType> beanClass, Class<RemoteType> remoteClass, Field field, AnnotatedDAOFactory daoFactory, DAOPopulate daoManaged) {
		super();
		this.beanClass = beanClass;
		this.remoteClass = remoteClass;
		this.field = field;
		this.daoFactory = daoFactory;


		//Validate linkTable name
		ManyToMany localAnnotation = field.getAnnotation(ManyToMany.class);
		
		this.linkTable = localAnnotation.linkTable();
				
		//Get remote field
		Field matchingRemoteField = null;
		
		Field[] fields = remoteClass.getDeclaredFields();

		for(Field remoteField : fields){

			if(ReflectionUtils.getGenericlyTypeCount(remoteField) == 1 && ReflectionUtils.getGenericType(remoteField).equals(beanClass) && remoteField.isAnnotationPresent(DAOPopulate.class) && remoteField.isAnnotationPresent(ManyToMany.class) && remoteField.getAnnotation(ManyToMany.class).linkTable().equals(linkTable)){

				matchingRemoteField = remoteField;

				break;
			}
			
		}

		if(matchingRemoteField == null){

			throw new RuntimeException("Unable to to find corresponding @ManyToMany field in " + remoteClass + " while parsing field " + field.getName() + " in " + beanClass);
		}
		
		//Get remote annotation
		ManyToMany remoteAnnotation = matchingRemoteField.getAnnotation(ManyToMany.class);
		
		//Get local key field
		this.localKeyField = getKeyField(localAnnotation, beanClass, field);
		
		//Get local localColumn name
		this.localLinkTableColumnName = getColumnName(daoManaged, localKeyField);
		
		//Get remote key field
		remoteKeyField = getKeyField(remoteAnnotation, remoteClass, matchingRemoteField);
				
		//Get remote localColumn name
		this.remoteLinkTableColumnName = getColumnName(remoteKeyField.getAnnotation(DAOPopulate.class), remoteKeyField);
		
		String remoteColumnName = remoteKeyField.getAnnotation(DAOPopulate.class).columnName();
		
		//Get remote table localColumn name
		if(!StringUtils.isEmpty(remoteColumnName)){
			
			this.remoteKeyColumnName = remoteColumnName;
			
		}else{
		
			this.remoteKeyColumnName = remoteKeyField.getName();			
		}
		
		this.linkTableDeleteSQL = "DELETE FROM " + linkTable + " WHERE " + localLinkTableColumnName + " = ?";
		this.linkTableInsertSQL = "INSERT INTO " + linkTable + " (" + localLinkTableColumnName + "," + remoteLinkTableColumnName + ") VALUES (?,?)";
		
	}
	
	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToManyRelation#setValue(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	public void setValue(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{
				
		try {
			if(!initialized){
				init();	
			}
			
			CustomQueryParameter<LocalType> queryParameter = new CustomQueryParameter<LocalType>(localColumn, bean);
			
			if(queryParameter.getParamValue() != null){
			
				field.set(bean, annotatedDAO.getAll(linkTableLinkSQL, queryParameter, connection, relationQuery));				
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToManyRelation#add(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	@SuppressWarnings("unchecked")
	public void add(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{
		
		//TODO cascading add like OneToMany relations
		
		try {
			if(!initialized){
				init();	
			}
			
			//Check if there are any relations to set 
			List<RemoteType> remoteBeans = (List<RemoteType>) field.get(bean);
			
			if(remoteBeans == null){
				
				return;
			}
			
			for(RemoteType remoteBean : remoteBeans){
			
				//Set new relations
				UpdateQuery insertQuery;
				
				insertQuery = new UpdateQuery(connection, false, linkTableInsertSQL);
				
				setQueryParameter(insertQuery, this.localColumn, bean, 1);
				setQueryParameter(insertQuery, this.remoteColumn, remoteBean, 2);
				
				insertQuery.executeUpdate();
			}
			
		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);
			
		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}	
	
	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToManyRelation#update(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	@SuppressWarnings("unchecked")
	public void update(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{
		
		//TODO cascading add like OneToMany relations
		
		try {
			if(!initialized){
				init();	
			}
			
			//Clear old relations
			UpdateQuery deleteQuery;
			
			deleteQuery = new UpdateQuery(connection, false, linkTableDeleteSQL);
			
			setQueryParameter(deleteQuery, this.localColumn, bean, 1);
			
			deleteQuery.executeUpdate();
			
			//Check if there are any new relations to set 
			List<RemoteType> remoteBeans = (List<RemoteType>) field.get(bean);
			
			if(remoteBeans == null){
				
				return;
			}
			
			for(RemoteType remoteBean : remoteBeans){
			
				//Set new relations
				UpdateQuery insertQuery;
				
				insertQuery = new UpdateQuery(connection, false, linkTableInsertSQL);
				
				setQueryParameter(insertQuery, this.localColumn, bean, 1);
				setQueryParameter(insertQuery, this.remoteColumn, remoteBean, 2);
				
				insertQuery.executeUpdate();
			}
			
		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);
			
		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}
	
	private void init() {

		if(annotatedDAO == null){
			annotatedDAO = this.daoFactory.getDAO(remoteClass);
		}
		
		if(this.linkTableLinkSQL == null){
			this.linkTableLinkSQL = "SELECT " + annotatedDAO.getTableName() + ".* FROM " + annotatedDAO.getTableName() + " INNER JOIN " + linkTable + " ON (" + annotatedDAO.getTableName() + "." + remoteKeyColumnName + "=" + linkTable + "." + remoteLinkTableColumnName + ") WHERE " + linkTable + "." + localLinkTableColumnName + " = ?";
		}
		
		if(this.localColumn == null){
			this.localColumn = this.daoFactory.getDAO(beanClass).getColumn(this.localKeyField);
		}
		
		if(this.remoteColumn == null){
			this.remoteColumn = this.annotatedDAO.getColumn(remoteKeyField);
		}
		
		this.initialized = true;
	}	
	
	private static <Type> void setQueryParameter(UpdateQuery query, Column<Type,?> column, Type bean, int index) throws SQLException{
		
		if(column.getQueryParameterPopulator() != null){

			column.getQueryParameterPopulator().populate(query, index, column.getBeanValue(bean));

		}else{

			try {
				column.getQueryMethod().invoke(query, index, column.getBeanValue(bean));

			} catch (IllegalArgumentException e) {

				throw new RuntimeException(e);

			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);

			} catch (InvocationTargetException e) {

				throw new RuntimeException(e);
			}
		}		
	}
	
	public static Field getKeyField(ManyToMany manyToManyAnnotation, Class<?> clazz, Field annotatedField){
		
		if(!StringUtils.isEmpty(manyToManyAnnotation.keyField())){
			
			try {		
				Field keyField = clazz.getDeclaredField(manyToManyAnnotation.keyField());
							
				DAOPopulate keyDAOPopulate = keyField.getAnnotation(DAOPopulate.class);
				
				if(keyDAOPopulate == null){
					
					throw new RuntimeException("Specified keyField " + manyToManyAnnotation.keyField() + " for @ManyToMany annotation for field " + annotatedField.getName() + "  in " + clazz + " is missing the @DAOPopulate annotation");
				}
				
				return keyField;
			
				
			} catch (SecurityException e) {

				throw new RuntimeException("Unable to find specified keyField " + manyToManyAnnotation.keyField() + " for @ManyToMany annotation for field " + annotatedField.getName() + "  in " + clazz);
				
			} catch (NoSuchFieldException e) {

				throw new RuntimeException("Unable to find specified keyField " + manyToManyAnnotation.keyField() + " for @ManyToMany annotation for field " + annotatedField.getName() + "  in " + clazz);
			}
			
		}else{
			
			ArrayList<Field> keyFields = getKeyFields(clazz);
			
			if(keyFields.size() == 0){
			
				throw new RuntimeException("Unable to find any @PrimaryKey annotated fields in " + clazz);
				
			}else if(keyFields.size() > 1){
				
				throw new RuntimeException("keyField needs to be specified for @ManyToMany annotated field " + annotatedField.getName() + " in " + clazz + " since the class contains multiple @PrimaryKey annotated fields");
			}
			
			return keyFields.get(0);
		}		
	}
	
	public static ArrayList<Field> getKeyFields(Class<?> clazz){
		
		ArrayList<Field> keyFields = new ArrayList<Field>();
		
		Field[] fields = clazz.getDeclaredFields();
		
		for(Field field : fields){
			
			if(field.isAnnotationPresent(PrimaryKey.class) && field.isAnnotationPresent(DAOPopulate.class)){
				
				keyFields.add(field);
			}
		}
		
		return keyFields;
	}
	
	private static String getColumnName(DAOPopulate manyToManyDAOPopulate, Field keyField){

		String keyColumnName;
		
		if(!StringUtils.isEmpty(manyToManyDAOPopulate.columnName())){
			
			return manyToManyDAOPopulate.columnName();

		}else if(!StringUtils.isEmpty(keyColumnName = keyField.getAnnotation(DAOPopulate.class).columnName())){
			
			return keyColumnName;
			
		}else{
			
			return keyField.getName();
		}
	}
	
	public static <LT,RT> ManyToManyRelation<LT, RT> getGenericInstance(Class<LT> beanClass, Class<RT> remoteClass, Field field, AnnotatedDAOFactory daoFactory, DAOPopulate daoManaged){

		return new DefaultManyToManyRelation<LT,RT>(beanClass,remoteClass,field,daoFactory,daoManaged);
	}	
}
