package se.unlogic.standardutils.xml;

import java.util.List;

public class ClassXMLInfo {

	private String elementName;
	private List<FieldXMLInfo> fields;

	public ClassXMLInfo(String elementName, List<FieldXMLInfo> field) {
		super();
		this.elementName = elementName;
		this.fields = field;
	}

	public String getElementName() {
		return elementName;
	}

	public List<FieldXMLInfo> getFields() {
		return fields;
	}
}
