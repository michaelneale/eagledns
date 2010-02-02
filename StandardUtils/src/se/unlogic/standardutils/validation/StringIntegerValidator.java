package se.unlogic.standardutils.validation;

import se.unlogic.standardutils.numbers.NumberUtils;


public class StringIntegerValidator extends StringNumberValidator<Integer> {

	public StringIntegerValidator() {
		super(null, null);
	}

	public StringIntegerValidator(Integer minValue,Integer maxValue) {
		super(minValue,maxValue);
	}

	public boolean validateFormat(String value) {

		Integer numberValue = NumberUtils.toInt(value);

		if(numberValue == null){

			return false;

		}else if(maxValue != null && minValue != null){

			return numberValue <= maxValue && numberValue >= minValue;

		}else if(maxValue != null){

			return numberValue <= maxValue;

		}else if(minValue != null){

			return numberValue >= minValue;
		}
		else{
			return true;
		}
	}
}
