package se.unlogic.standardutils.timer;

import java.util.TimerTask;

public class RunnableTimerTask extends TimerTask {

	private final Runnable runnable;

	public RunnableTimerTask(Runnable runnable) {
		super();
		this.runnable = runnable;
	}

	@Override
	public void run() {
		this.runnable.run();
	}
}
