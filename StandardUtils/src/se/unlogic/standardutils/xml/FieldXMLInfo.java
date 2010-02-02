package se.unlogic.standardutils.xml;

import java.lang.reflect.Field;

public class FieldXMLInfo {

	private String name;
	private Field field;
	private FieldType fieldType;
	private boolean cdata;
	private boolean elementable;
	private boolean list;
	private String childName;

	public FieldXMLInfo(String name, Field field, FieldType fieldType, boolean cdata, boolean elementable, boolean list, String childName) {
		super();
		this.name = name;
		this.field = field;
		this.fieldType = fieldType;
		this.cdata = cdata;
		this.elementable = elementable;
		this.list = list;
		this.childName = childName;
	}

	public String getName() {
		return name;
	}

	public Field getField() {
		return field;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public boolean isCDATA() {
		return cdata;
	}

	public boolean isList() {
		return list;
	}

	public boolean isElementable() {
		return elementable;
	}

	public String getChildName() {
		return childName;
	}
}
