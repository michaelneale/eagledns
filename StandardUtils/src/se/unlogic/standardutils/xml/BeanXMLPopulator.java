package se.unlogic.standardutils.xml;

import org.w3c.dom.Document;

import se.unlogic.standardutils.validation.ValidationException;

public interface BeanXMLPopulator<Type> {
	public Type populate(Document doc) throws ValidationException;
}
