package se.unlogic.standardutils.xml;

public class MissingXMLAnnotationException extends RuntimeException {

	private static final long serialVersionUID = -5127582859037405055L;
	private Class<?> clazz;

	public MissingXMLAnnotationException(Class<?> clazz) {

		super("Class " + clazz + " is missing the XMLElement annotation and can therefore not be converted to an element");

		this.clazz = clazz;
	}

	public Class<?> getBeanClass() {
		return clazz;
	}
}
