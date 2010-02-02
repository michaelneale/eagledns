package se.unlogic.standardutils.populators;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class DatePopulator extends BaseTypePopulator<Date> {

	private final SimpleDateFormat dateFormat;

	public DatePopulator(SimpleDateFormat dateFormat) {

		super();

		this.dateFormat = dateFormat;
	}

	public DatePopulator(String populatorID, SimpleDateFormat dateFormat) {

		super(populatorID);

		this.dateFormat = dateFormat;
	}

	public DatePopulator(String populatorID, SimpleDateFormat dateFormat, StringFormatValidator formatValidator) {

		super(populatorID,formatValidator);
		this.dateFormat = dateFormat;
	}

	public Class<? extends Date> getType() {

		return Date.class;
	}

	public Date getValue(String value) {

		try {
			java.util.Date utilDate = this.dateFormat.parse(value);
			
			return new Date(utilDate.getTime());
			
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return DateUtils.isValidDate(this.dateFormat, value);
	}
}
