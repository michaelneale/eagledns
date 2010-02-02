package se.unlogic.standardutils.populators;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;

public class BlobPopulator implements BeanResultSetPopulator<Blob> {

	public static final BlobPopulator POPULATOR = new BlobPopulator();

	public static BlobPopulator getPopulator() {
		return POPULATOR;
	}

	public Blob populate(ResultSet rs) throws SQLException {
		return rs.getBlob(1);
	}
}
