package se.unlogic.standardutils.dao.script;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public interface ScriptDAO {

	void executeScript(InputStream inputStream) throws SQLException, IOException;
	
	void executeScript(String script) throws SQLException;

}