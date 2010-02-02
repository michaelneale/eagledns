package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import se.unlogic.standardutils.dao.annotations.DAOPopulate;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.reflection.ReflectionUtils;


public class DefaultOneToManyRelation<LocalType,RemoteType> implements OneToManyRelation<LocalType, RemoteType> {

	private final Field field;
	private Field remoteField;
	private final AnnotatedDAOFactory daoFactory;
	private AnnotatedDAO<RemoteType> annotatedDAO;
	private QueryParameterFactory<RemoteType, LocalType> queryParameterFactory;
	private final Class<LocalType> beanClass;
	private final Class<RemoteType> remoteClass;
	private boolean initialized;
	
	public DefaultOneToManyRelation(Class<LocalType> beanClass, Class<RemoteType> remoteClass, Field field, AnnotatedDAOFactory daoFactory, DAOPopulate daoManaged) {
		super();
		this.beanClass = beanClass;
		this.remoteClass = remoteClass;
		this.field = field;
		this.daoFactory = daoFactory;
		
		Field[] fields = remoteClass.getDeclaredFields();

		for(Field remoteField : fields){

			if(remoteField.getType().equals(beanClass) && remoteField.isAnnotationPresent(DAOPopulate.class) && remoteField.isAnnotationPresent(ManyToOne.class)){
								
				this.remoteField = remoteField;

				ReflectionUtils.fixFieldAccess(this.remoteField);
				
				break;
			}
		}

		if(this.remoteField == null){

			throw new RuntimeException("Unable to to find corresponding @ManyToOne field in class " + remoteClass + " for @OneToMany annotated field " + field.getName() + " in " + beanClass);
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.OneToManyRelation#setValue(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	public void setValue(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{

		if(!initialized){
			init();
		}

		try {
			HighLevelQuery<RemoteType> query = new HighLevelQuery<RemoteType>();
			
			query.addRelations(relationQuery);
			
			query.addParameter(queryParameterFactory.getParameter(bean));
			
			field.set(bean, annotatedDAO.getAll(query, connection));

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.OneToManyRelation#add(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	@SuppressWarnings("unchecked")
	public void add(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{
	
		if(!initialized){
			init();
		}
		
		try {
			List<RemoteType> remoteBeans = (List<RemoteType>) field.get(bean);
			
			if(remoteBeans != null){
				
				this.fixReferences(remoteBeans, bean);
				
				annotatedDAO.addAll(remoteBeans, connection, relationQuery);
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}	
	
	private void fixReferences(List<RemoteType> remoteBeans, LocalType bean) throws IllegalArgumentException, IllegalAccessException {

		for(RemoteType remoteBean : remoteBeans){
			
			remoteField.set(remoteBean, bean);
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.OneToManyRelation#update(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	@SuppressWarnings("unchecked")
	public void update(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{
	
		if(!initialized){
			init();
		}
		
		try {
			List<RemoteType> remoteBeans = (List<RemoteType>) field.get(bean);
			
			if(remoteBeans == null){
				
				HighLevelQuery<RemoteType> query = new HighLevelQuery<RemoteType>();
				
				query.addRelations(relationQuery);
				
				query.addParameter(queryParameterFactory.getParameter(bean));
				
				annotatedDAO.delete(query, connection);
				
			}else{
				
				this.fixReferences(remoteBeans, bean);
				
				annotatedDAO.deleteWhereNotIn(remoteBeans, connection, queryParameterFactory.getParameter(bean));
				
				annotatedDAO.addOrUpdateAll(remoteBeans, connection, relationQuery);
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
			queryParameterFactory = annotatedDAO.getParamFactory(remoteField, beanClass);
		}
		
		this.initialized = true;
	}	
	
	public static <LT,RT> OneToManyRelation<LT, RT> getGenericInstance(Class<LT> beanClass, Class<RT> remoteClass, Field field, AnnotatedDAOFactory daoFactory, DAOPopulate daoManaged){

		return new DefaultOneToManyRelation<LT,RT>(beanClass,remoteClass,field,daoFactory,daoManaged);
	}
}
