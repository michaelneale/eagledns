package se.unlogic.standardutils.dao;




public interface AnnotatedDAOFactory {

	public <T> AnnotatedDAO<T> getDAO(Class<T> beanClass);
}
