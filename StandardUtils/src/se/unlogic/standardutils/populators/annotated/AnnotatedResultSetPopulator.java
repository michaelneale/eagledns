package se.unlogic.standardutils.populators.annotated;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.unlogic.standardutils.annotations.NoAnnotatedFieldsFoundException;
import se.unlogic.standardutils.annotations.UnsupportedFieldTypeException;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.dao.ResultSetField;
import se.unlogic.standardutils.dao.annotations.DAOPopulate;
import se.unlogic.standardutils.dao.annotations.ManyToMany;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.OneToOne;
import se.unlogic.standardutils.populators.TypePopulator;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;


public class AnnotatedResultSetPopulator<T> implements BeanResultSetPopulator<T>{

	protected Class<T> beanClass;
	protected ArrayList<ResultSetField> resultSetFields = new ArrayList<ResultSetField>();

	public AnnotatedResultSetPopulator(Class<T> beanClass) throws UnsupportedFieldTypeException{
		this(beanClass, (List<TypePopulator<?>>)null);
	}

	public AnnotatedResultSetPopulator(Class<T> beanClass, TypePopulator<?>... populators) throws UnsupportedFieldTypeException{
		this(beanClass,Arrays.asList(populators));
	}

	@SuppressWarnings("unchecked")
	public AnnotatedResultSetPopulator(Class<T> beanClass, List<TypePopulator<?>> populators) throws UnsupportedFieldTypeException{

		this.beanClass = beanClass;

		//cache fields
		List<Field> fields = ReflectionUtils.getFields(beanClass);

		for(Field field : fields){

			DAOPopulate annotation = field.getAnnotation(DAOPopulate.class);

			if(annotation != null && (!field.isAnnotationPresent(OneToOne.class) && !field.isAnnotationPresent(OneToMany.class) && !field.isAnnotationPresent(ManyToOne.class) && !field.isAnnotationPresent(ManyToMany.class) )){

				if(Modifier.isFinal(field.getModifiers())){

					throw new UnsupportedFieldTypeException("The annotated field " + field.getName() + " in class " + beanClass + " is final!", field, annotation.getClass(), beanClass);

				}

				Method resultSetMethod = ResultSetField.RESULTSET_METHODS.get(field.getType());

				TypePopulator<?> typePopulator = null;

				if(resultSetMethod == null){

					if(populators != null){
						typePopulator = this.getPopulator(populators, field, annotation);
					}

					if(typePopulator == null){
						throw new UnsupportedFieldTypeException("The annotated field " + field.getName() + " in class " + beanClass + " is of unsupported type " + field.getType(), field, annotation.annotationType() , beanClass);
					}
				}

				ReflectionUtils.fixFieldAccess(field);

				if(!StringUtils.isEmpty(annotation.columnName())){

					this.resultSetFields.add(new ResultSetField(field,resultSetMethod,annotation.columnName(),typePopulator));

				}else{

					this.resultSetFields.add(new ResultSetField(field,resultSetMethod,field.getName(),typePopulator));
				}
			}
		}

		if(this.resultSetFields.isEmpty()){
			throw new NoAnnotatedFieldsFoundException(beanClass,DAOPopulate.class);
		}
	}

	private TypePopulator<?> getPopulator(List<TypePopulator<?>> populators, Field field, DAOPopulate annotation) {

		String populatorID = annotation.populatorID();

		Object clazz = field.getType();

		for(TypePopulator<?> populator : populators){

			if(clazz.equals(populator.getType())){

				if((StringUtils.isEmpty(populatorID) && populator.getPopulatorID() == null) || populatorID.equals(populator.getPopulatorID())){

					return populator;
				}
			}
		}

		return null;
	}

	public T populate(ResultSet rs) throws SQLException, BeanResultSetPopulationException {

		ResultSetField currentField = null;

		try {
			T bean = beanClass.newInstance();

			for(ResultSetField resultSetField : this.resultSetFields){

				currentField = resultSetField;

				if(currentField.getResultSetMethod() != null){

					Object value = resultSetField.getResultSetMethod().invoke(rs, resultSetField.getAlias());

					if(rs.wasNull() && !resultSetField.getBeanField().getType().isPrimitive()){

						resultSetField.getBeanField().set(bean, null);

					}else{

						resultSetField.getBeanField().set(bean, value);
					}

				}else{

					String value = rs.getString(currentField.getAlias());

					if(value != null || currentField.getTypePopulator().getType().isPrimitive()){

						resultSetField.getBeanField().set(bean, currentField.getTypePopulator().getValue(value));
					}else{
						resultSetField.getBeanField().set(bean, null);
					}
				}
			}

			return bean;

		} catch (InstantiationException e) {

			throw new BeanResultSetPopulationException(currentField,e);

		} catch (IllegalAccessException e) {

			throw new BeanResultSetPopulationException(currentField,e);

		} catch (IllegalArgumentException e) {

			throw new BeanResultSetPopulationException(currentField,e);

		} catch (InvocationTargetException e) {

			throw new BeanResultSetPopulationException(currentField,e);
		}
	}
}
