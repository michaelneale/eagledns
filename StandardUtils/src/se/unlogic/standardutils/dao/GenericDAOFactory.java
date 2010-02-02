package se.unlogic.standardutils.dao;

@Deprecated
public interface GenericDAOFactory {

	public <T> GenericDAO<Integer,T> getDAO(Class<T> beanClass);
}
