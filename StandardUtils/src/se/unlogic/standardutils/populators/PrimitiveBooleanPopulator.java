package se.unlogic.standardutils.populators;


public class PrimitiveBooleanPopulator extends BaseTypePopulator<Boolean> implements TypePopulator<Boolean> {

	public Boolean getValue(String value) {

		if(value == null || value.equalsIgnoreCase("false")){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return true;
	}

	public Class<? extends Boolean> getType() {

		return boolean.class;
	}
}
