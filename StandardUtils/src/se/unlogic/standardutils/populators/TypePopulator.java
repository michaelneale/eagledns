package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.validation.StringFormatValidator;

public interface TypePopulator<T> extends StringFormatValidator{

	public abstract T getValue(String value);

	public abstract Class<? extends T> getType();

	public abstract String getPopulatorID();
}
