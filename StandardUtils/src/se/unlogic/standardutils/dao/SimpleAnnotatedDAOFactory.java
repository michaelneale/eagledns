package se.unlogic.standardutils.dao;

import java.util.HashMap;

import javax.sql.DataSource;

import se.unlogic.standardutils.populators.QueryParameterPopulator;


public class SimpleAnnotatedDAOFactory implements AnnotatedDAOFactory{

	private final DataSource dataSource;
	private final HashMap<Class<?>, AnnotatedDAO<?>> daoMap = new HashMap<Class<?>, AnnotatedDAO<?>>();
	
	public SimpleAnnotatedDAOFactory(DataSource dataSource) {

		super();
		this.dataSource = dataSource;
	}
	
	public SimpleAnnotatedDAOFactory() {

		super();
		this.dataSource = null;
	}	
	
	@SuppressWarnings("unchecked")
	public synchronized <T> AnnotatedDAO<T> getDAO(Class<T> beanClass) {

		AnnotatedDAO<T> dao = (AnnotatedDAO<T>) this.daoMap.get(beanClass);

		if(dao == null){

			dao = new AnnotatedDAO<T>(dataSource, beanClass, this);
			this.daoMap.put(beanClass, dao);
		}

		return dao;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T> AnnotatedDAO<T> getDAO(Class<T> beanClass, BeanResultSetPopulator<T> populator,	QueryParameterPopulator<?>... queryParameterPopulators) {

		AnnotatedDAO<T> dao = (AnnotatedDAO<T>) this.daoMap.get(beanClass);

		if(dao == null){

			dao = new AnnotatedDAO<T>(dataSource, beanClass, this, populator, queryParameterPopulators);
			this.daoMap.put(beanClass, dao);
		}

		return dao;
	}	

	public DataSource getDataSource() {
	
		return dataSource;
	}

	public void addDAO(Class<?> beanClass, AnnotatedDAO<?> daoInstance) {
		daoMap.put(beanClass, daoInstance);
	}
}
