package se.unlogic.standardutils.datatypes;

import java.util.Map.Entry;

public class SimpleEntry<KeyType, ValueType> implements Entry<KeyType,ValueType>{

	public SimpleEntry(KeyType key, ValueType value) {
		super();
		this.key = key;
		this.value = value;
	}

	private final KeyType key;
	private ValueType value;

	public ValueType getValue() {
		return value;
	}
	public ValueType setValue(ValueType value) {

		ValueType oldValue = this.value;

		this.value = value;

		return oldValue;
	}
	public KeyType getKey() {
		return key;
	}
}
