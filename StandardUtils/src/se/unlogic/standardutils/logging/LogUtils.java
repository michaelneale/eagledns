package se.unlogic.standardutils.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LogUtils {


	/**
	 * A method for triggering the LogFactory.release method on the commons logging library (http://commons.apache.org/logging/) without having compile time dependency to the library itself.
	 * Trigger of the release method can be nessecary sometimes  in order to avoid memory leaks, see http://wiki.apache.org/jakarta-commons/Logging/UndeployMemoryLeak for more information.
	 */
	public static void releaseCommonsLogging(){

		try {
			Class<?> logFactoryClass = Class.forName("org.apache.commons.logging.LogFactory");

			Method releaseMethod = logFactoryClass.getMethod("release", ClassLoader.class);

			releaseMethod.invoke(null,Thread.currentThread().getContextClassLoader());

		} catch (ClassNotFoundException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {}
	}
}
