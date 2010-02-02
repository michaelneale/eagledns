package se.unlogic.standardutils.validation;

import se.unlogic.standardutils.numbers.NumberUtils;


public class StringFloatValidator extends StringNumberValidator<Float> {

	public StringFloatValidator() {
		super(null, null);
	}

	public StringFloatValidator(Float minValue, Float maxValue) {
		super(minValue,maxValue);
	}

	public boolean validateFormat(String value) {

		Float numberValue = NumberUtils.toFloat(value);

		if(numberValue == null){

			return false;

		}else if(maxValue != null && minValue != null){

			return numberValue <= maxValue && numberValue > minValue;

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
