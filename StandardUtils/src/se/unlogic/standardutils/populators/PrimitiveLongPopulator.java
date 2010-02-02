package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.validation.StringFormatValidator;


public class PrimitiveLongPopulator extends LongPopulator {

	public PrimitiveLongPopulator() {
		super();
	}

	public PrimitiveLongPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public PrimitiveLongPopulator(String populatorID) {
		super(populatorID);
	}

	@Override
	public Class<? extends Long> getType() {

		return long.class;
	}

	@Override
	public Long getValue(String value) {

		if(value == null){
			return 0l;
		}

		return super.getValue(value);
	}
}
