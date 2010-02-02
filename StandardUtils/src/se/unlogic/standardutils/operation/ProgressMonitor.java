package se.unlogic.standardutils.operation;

import java.util.TimerTask;

public abstract class ProgressMonitor extends TimerTask {

	protected ProgressMeter progressMeter;
	protected MonitorValue monitorValue;
	protected String prefix = "";
	protected String suffix = "";
	
	protected void setProgressMeter(ProgressMeter progressMeter){
		this.progressMeter = progressMeter;
	}
			
	public ProgressMonitor(MonitorValue monitorValue, String prefix, String suffix){
		this.monitorValue = monitorValue;
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	protected long getMonitorValue(){
		if(monitorValue == MonitorValue.CurrentPosition){
			return progressMeter.getCurrentPosition();
		}else if(monitorValue == MonitorValue.PercentComplete){
			return progressMeter.getPercentComplete();
		}else if(monitorValue == MonitorValue.PercentRemaining){
			return progressMeter.getPercentRemaining();
		}else if(monitorValue == MonitorValue.TimeSpent){
			return progressMeter.getTimeSpent();
		}else{
			return 0;
		}
	}
}
