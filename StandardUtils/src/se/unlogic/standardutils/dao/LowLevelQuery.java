package se.unlogic.standardutils.dao;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a SQL query and is used together with the {@link AnnotatedDAO} class.<p>
 * 
 * A low level query lets you write the SQL and set any parameters in contrast to the {@link HighLevelQuery}.<p>
 * 
 * The SQL query is written like a normal prepared statement with '?' chars representing the parameters to be set.<p>
 * 
 * The parameters will be set on the prepared statement using the "class -> method mapping" in the {@link PreparedStatementQueryMethods} class.<br>
 * Basically this means that any parameter that has a set method matching it's type in the {@link PreparedStatement} interface will work.<p>
 * 
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 */
public class LowLevelQuery extends RelationQuery{

	private String sql;
	private List<Object> parameters;

	public String getSql() {

		return sql;
	}

	public void setSql(String sql) {

		this.sql = sql;
	}

	public List<Object> getParameters() {

		return parameters;
	}

	public void setParameters(List<Object> parameters) {

		this.parameters = parameters;
	}

	public void addParameter(Object parameter){

		if(this.parameters == null){

			this.parameters = new ArrayList<Object>();
		}

		this.parameters.add(parameter);
	}

	public void addParameters(Object... parameters){

		if(this.parameters == null){

			this.parameters = new ArrayList<Object>();
		}

		this.parameters.addAll(Arrays.asList(parameters));
	}

	public void addParameters(List<?> parameters){

		if(this.parameters == null){

			this.parameters = new ArrayList<Object>();
		}

		this.parameters.addAll(parameters);
	}
}
