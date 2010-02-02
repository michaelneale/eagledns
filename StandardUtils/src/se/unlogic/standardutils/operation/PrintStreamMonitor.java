package se.unlogic.standardutils.operation;

import java.io.PrintStream;

public class PrintStreamMonitor extends ProgressMonitor{
	
	private PrintStream printStream;
	
	public PrintStreamMonitor(PrintStream printStream, MonitorValue monitorValue, String prefix, String suffix) {
		super(monitorValue, prefix, suffix);
		this.printStream = printStream;
	}

	@Override
	public void run() {
		printStream.print(prefix + this.getMonitorValue() + suffix);
	}

}
