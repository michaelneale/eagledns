package se.unlogic.standardutils.validation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.unlogic.standardutils.xml.XMLUtils;

public class ValidationError {
	private String fieldName;
	private ValidationErrorType validationErrorType;
	private String messageKey;
	
	public ValidationError(String fieldName, ValidationErrorType validationErrorType, String messageKey) {
		super();
		this.fieldName = fieldName;
		this.validationErrorType = validationErrorType;
		this.messageKey = messageKey;
	}
	
	public ValidationError(String fieldName, ValidationErrorType validationErrorType) {
		super();
		this.fieldName = fieldName;
		this.validationErrorType = validationErrorType;
	}	

	public ValidationError(String messageKey) {
		super();
		
		this.messageKey = messageKey;
	}

	public String getFieldName() {
		return fieldName;
	}

	public ValidationErrorType getValidationErrorType() {
		return validationErrorType;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public final Node toXML(Document doc) {
		Element validationError = doc.createElement("validationError");
		
		if(this.fieldName != null){
			validationError.appendChild(XMLUtils.createCDATAElement("fieldName", fieldName, doc));
		}
		
		if(this.validationErrorType != null){
			validationError.appendChild(XMLUtils.createCDATAElement("validationErrorType", validationErrorType.toString(), doc));
		}
		
		if(this.messageKey != null){
			validationError.appendChild(XMLUtils.createCDATAElement("messageKey", messageKey, doc));
		}
		
		return validationError;
	}
}
