package se.unlogic.standardutils.populators;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class TimeStampPopulator extends BaseTypePopulator<Timestamp> {

	private final SimpleDateFormat dateFormat;

	public TimeStampPopulator(SimpleDateFormat dateFormat) {

		super();

		this.dateFormat = dateFormat;
	}

	public TimeStampPopulator(String populatorID, SimpleDateFormat dateFormat) {

		super(populatorID);

		this.dateFormat = dateFormat;
	}

	public TimeStampPopulator(String populatorID, SimpleDateFormat dateFormat, StringFormatValidator formatValidator) {

		super(populatorID,formatValidator);
		this.dateFormat = dateFormat;
	}

	public Class<? extends Timestamp> getType() {

		return Timestamp.class;
	}

	public Timestamp getValue(String value) {

		try {
			return new Timestamp(this.dateFormat.parse(value).getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return DateUtils.isValidDate(this.dateFormat, value);
	}
}
