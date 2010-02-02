package se.unlogic.standardutils.annotations;

import java.lang.annotation.Annotation;

public class NoAnnotatedFieldsFoundException extends RuntimeException {

	private static final long serialVersionUID = 5295557583550461676L;
	private final Class<?> beanClass;
	private final Class<? extends Annotation>[] annotations;

	public NoAnnotatedFieldsFoundException(Class<?> beanClass,Class<? extends Annotation>... annotations) {

		super("No annotated fields found in class " + beanClass + " with annotations " + annotations);

		this.beanClass = beanClass;
		this.annotations = annotations;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public Class<? extends Annotation>[] getAnnotations() {
		return annotations;
	}
}
