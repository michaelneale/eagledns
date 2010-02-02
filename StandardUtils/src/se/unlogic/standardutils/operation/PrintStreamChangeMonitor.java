package se.unlogic.standardutils.operation;

import java.io.PrintStream;

public class PrintStreamChangeMonitor extends PrintStreamMonitor {

	private long previousValue = -1;
	
	public PrintStreamChangeMonitor(PrintStream printStream, MonitorValue monitorValue, String prefix, String suffix) {
		super(printStream, monitorValue, prefix, suffix);
	}

	@Override
	public void run() {
		if(this.getMonitorValue() != previousValue){
			previousValue = this.getMonitorValue();
			super.run();
		}
	}
}
