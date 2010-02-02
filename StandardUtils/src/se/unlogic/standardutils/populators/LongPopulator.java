package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class LongPopulator extends BaseTypePopulator<Long> implements TypePopulator<Long> {

	public LongPopulator() {
		super();
	}

	public LongPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public LongPopulator(String populatorID) {
		super(populatorID);
	}

	public Long getValue(String value) {

		return Long.valueOf(value);
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return NumberUtils.isLong(value);
	}

	public Class<? extends Long> getType() {

		return Long.class;
	}
}
