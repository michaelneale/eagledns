package se.unlogic.standardutils.exec;

import java.io.IOException;

/**
 * Utility class for executing processes and handling the output from them.
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 *
 */
public class ExecUtils {

	/**
	 * Executes the given command and waits for the resulting processes to terminate and all data written to standard out and error out to be handled.<p>
	 * 
	 * All data written to standard out is piped to System.out with the given prefix<p>
	 * 
	 * All data written to error out is piped to System.err with the given prefix<p>
	 * 
	 * @param command the command to executed
	 * @return the exit value of the process. By convention, 0 indicates normal termination.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int execAndWait(String command) throws IOException, InterruptedException {

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command);

		StreamPrinter errorOutHandler = new StreamPrinter(proc.getErrorStream(), null, System.err);
		StreamPrinter stdOutHandler = new StreamPrinter(proc.getInputStream());

		errorOutHandler.start();
		stdOutHandler.start();

		return waitForProcessAndStreams(proc,stdOutHandler,errorOutHandler);
	}

	/**
	 * Executes the given command and waits for the resulting processes to terminate and all data written to standard out and error out to be handled.<p>
	 * 
	 * All data written to standard out is piped to System.out with the given prefix<p>
	 * 
	 * All data written to error out is piped to System.err with the given prefix<p>
	 * 
	 * @param command the command to executed
	 * @param stdOutHandler the {@link StreamHandler} to handle all output the process writes on standard out
	 * @param errorOutHandler the {@link StreamHandler} to handle all output the process writes on error out
	 * @return the exit value of the process. By convention, 0 indicates normal termination.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int execAndWait(String command, StreamHandler stdOutHandler, StreamHandler errorOutHandler) throws IOException, InterruptedException {

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command);

		errorOutHandler.setIs(proc.getErrorStream());
		stdOutHandler.setIs(proc.getInputStream());

		errorOutHandler.start();
		stdOutHandler.start();

		return waitForProcessAndStreams(proc,stdOutHandler,errorOutHandler);
	}

	private static int waitForProcessAndStreams(Process proc, StreamHandler stdOutHandler, StreamHandler errorOutHandler) throws InterruptedException {

		int exitVal = proc.waitFor();


		synchronized (errorOutHandler) {

			if(errorOutHandler.isAlive()){
				errorOutHandler.join();
			}
		}

		synchronized (stdOutHandler) {

			if(stdOutHandler.isAlive()){
				stdOutHandler.join();
			}
		}

		return exitVal;
	}
}
