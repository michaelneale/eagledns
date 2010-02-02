package se.unlogic.standardutils.validation;

import java.util.Arrays;
import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ValidationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1221745166857804542L;
	private final Collection<ValidationError> errors;

	public ValidationException(Collection<ValidationError> errors) {
		super();

		if (errors == null) {
			throw new NullPointerException();
		}

		this.errors = errors;
	}

	public ValidationException(ValidationError... errors) {
		super();

		if (errors == null) {
			throw new NullPointerException();
		}

		this.errors = Arrays.asList(errors);
	}

	public Collection<ValidationError> getErrors() {
		return errors;
	}

	public final Element toXML(Document doc) {
		Element validationException = doc.createElement("validationException");

		for (ValidationError validationError : errors) {
			if (validationError != null) {
				validationException.appendChild(validationError.toXML(doc));
			}
		}

		return validationException;
	}
}
