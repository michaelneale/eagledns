package se.unlogic.standardutils.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@Deprecated
public class ConnectionHandler {
	private DataSource datasource = null;
	
	public ConnectionHandler(String datasource) throws NamingException{
		setDataSource(datasource);
	}
	
	public ConnectionHandler(DataSource datasource){
		setDataSource(datasource);
	}	
	
	public void setDataSource(DataSource ds){
		this.datasource = ds;
	}
	
	public void setDataSource(String datasource) throws NamingException{
		Context initContext = new InitialContext();
		Context envContext  = (Context)initContext.lookup("java:/comp/env");
		this.datasource = (DataSource)envContext.lookup(datasource);
	}
	
	public DataSource getDataSource(){
		return this.datasource;
	}
	
	public Connection getConnection() throws SQLException{
		return datasource.getConnection();
	}
	
	public String toString(){
		return this.datasource.toString();
	}
}
