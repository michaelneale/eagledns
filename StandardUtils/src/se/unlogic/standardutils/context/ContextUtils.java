package se.unlogic.standardutils.context;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ContextUtils {

	public static boolean isBound(String resource) throws NamingException {
		Context initial = new InitialContext();
		try {
			initial.lookup(resource);
			return true;
		} catch(NamingException e) {
			return false;
		}
	}
}
