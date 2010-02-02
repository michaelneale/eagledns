package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.validation.StringFormatValidator;

public class PrimitiveIntegerPopulator extends IntegerPopulator{

	public PrimitiveIntegerPopulator() {
		super();
	}

	public PrimitiveIntegerPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public PrimitiveIntegerPopulator(String populatorID) {
		super(populatorID);
	}

	@Override
	public Class<? extends Integer> getType() {

		return int.class;
	}

	@Override
	public Integer getValue(String value) {

		if(value == null){

			return 0;
		}

		return super.getValue(value);
	}
}
