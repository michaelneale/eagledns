package se.unlogic.standardutils.validation;


public abstract class StringNumberValidator<T extends Number> implements StringFormatValidator {

	protected final T maxValue;
	protected final T minValue;

	public StringNumberValidator(T minValue, T maxValue) {
		super();
		this.maxValue = maxValue;
		this.minValue = minValue;
	}
}
