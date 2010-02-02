package se.unlogic.standardutils.numbers;

public class Counter {

	private Integer value;

	Counter(Integer value) {
		super();
		this.value = value;
	}

	public Counter() {
		super();
		this.value = 0;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer increment(){

		value++;

		return value;
	}

	public Integer decrement(){

		value--;

		return value;
	}
}
