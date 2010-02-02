package se.unlogic.standardutils.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class UnsupportedFieldTypeException extends RuntimeException {

	private static final long serialVersionUID = -6723843186067887845L;
	private final Class<?> beanClass;
	private final Class<? extends Annotation> annotation;
	private final Field field;

	public UnsupportedFieldTypeException(String message, Field field, Class<? extends Annotation> annotation, Class<?> beanClass) {
		super(message);

		this.beanClass = beanClass;
		this.annotation = annotation;
		this.field = field;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public Field getField() {
		return field;
	}

	public Class<? extends Annotation> getAnnotation() {
		return annotation;
	}
}
