package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;


public class StringURLPopulator extends BaseTypePopulator<String> implements TypePopulator<String> {

	public StringURLPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public StringURLPopulator(String populatorID) {
		super(populatorID);
	}

	public StringURLPopulator(){
		super("url");
	}

	public Class<? extends String> getType() {
		return String.class;
	}

	public String getValue(String value) {
		return value;
	}

	@Override
	public boolean validateDefaultFormat(String value) {
		return StringUtils.isValidURL(value);
	}

}
