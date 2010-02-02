package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.validation.StringFormatValidator;

public abstract class BaseTypePopulator<T> implements TypePopulator<T> {

	private final String populatorID;
	private final StringFormatValidator formatValidator;

	public BaseTypePopulator(String populatorID, StringFormatValidator formatValidator) {
		super();
		this.populatorID = populatorID;
		this.formatValidator = formatValidator;
	}

	public BaseTypePopulator(String populatorID) {
		super();
		this.populatorID = populatorID;
		this.formatValidator = null;
	}

	public BaseTypePopulator() {
		super();
		this.populatorID = null;
		this.formatValidator = null;
	}

	public String getPopulatorID() {
		return populatorID;
	}

	public final boolean validateFormat(String value) {

		if(formatValidator == null){

			return this.validateDefaultFormat(value);

		}else{

			return this.formatValidator.validateFormat(value);
		}
	}

	protected abstract boolean validateDefaultFormat(String value);
}
