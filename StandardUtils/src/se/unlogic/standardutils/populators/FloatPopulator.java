package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class FloatPopulator extends BaseTypePopulator<Float> implements TypePopulator<Float> {

	public FloatPopulator() {
		super();
	}

	public FloatPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public FloatPopulator(String populatorID) {
		super(populatorID);
	}

	public Float getValue(String value) {

		return Float.valueOf(value);
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return NumberUtils.isFloat(value);
	}

	public Class<? extends Float> getType() {

		return Float.class;
	}
}
