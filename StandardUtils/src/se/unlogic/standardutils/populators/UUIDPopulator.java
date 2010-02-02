package se.unlogic.standardutils.populators;

import java.sql.SQLException;
import java.util.UUID;

import se.unlogic.standardutils.dao.querys.PreparedStatementQuery;
import se.unlogic.standardutils.validation.StringFormatValidator;


public class UUIDPopulator extends BaseTypePopulator<UUID> implements QueryParameterPopulator<UUID> {


	public UUIDPopulator() {
		super();
	}

	public UUIDPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public UUIDPopulator(String populatorID) {
		super(populatorID);
	}

	@Override
	protected boolean validateDefaultFormat(String value) {

		try {
			UUID.fromString(value);

			return true;

		} catch (IllegalArgumentException e) {}

		return false;
	}

	public Class<? extends UUID> getType() {

		return UUID.class;
	}

	public UUID getValue(String value) {

		return UUID.fromString(value);
	}

	public void populate(PreparedStatementQuery query, int paramIndex, Object bean) throws SQLException {
		query.setString(paramIndex, ((UUID) bean).toString());
		
	}

}
