package se.unlogic.standardutils.operation;

import java.util.ArrayList;
import java.util.Timer;

public class ProgressMeter {
	private int start;
	private int finish;
	private int currentPosition;
	private long startTime;
	private long endTime;
	private Timer timer;
	private ArrayList<ProgressMonitor> progressMonitors;
	public ProgressMeter(){};
	
	public ProgressMeter(int start, int finish){
		this.start = start;
		this.finish = finish;
	}
	
	public ProgressMeter(boolean setStartTime){
		if(setStartTime){
			this.setStartTime();
		}
	}

	public ProgressMeter(int start, int finish, int currentPosition){
		this.start = start;
		this.finish = finish;
		this.currentPosition = currentPosition;
	}	
	
	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}
	
	public void incrementCurrentPosition(){
		this.currentPosition++;
	}
	
	public void decrementCurrentPosition(){
		this.currentPosition--;
	}

	public int getFinish() {
		return finish;
	}

	public void setFinish(int finish) {
		this.finish = finish;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}
	
	public int getPercentComplete(){
		if(this.finish > this.start){
			return (int)(((float)(this.currentPosition - this.start)/(float)(this.finish - this.start))*100f);
		}else if(this.finish < this.start){
			return (int)(((float)(this.start - this.currentPosition)/(float)(this.start - this.finish))*100f);
		}else{
			return -1;
		}
	}
	
	public int getPercentRemaining(){
		if(this.finish > this.start){
			if(this.finish - this.currentPosition != 0){
				return (int)(((float)(this.finish - this.currentPosition)/(float)(this.finish - this.start))*100f);
			}else{
				return 0;
			}			
		}else if(this.finish < this.start){
			if(this.currentPosition - this.finish != 0){
				return (int)(((float)(this.currentPosition - this.finish)/(float)(this.start - this.finish))*100f);	
			}else{
				return 0;
			}
		}else{
			return -1;
		}		
	}
	
	public int getIntervalSize(){
		if(this.finish > this.start){
			return this.finish - this.start;
		}else if(this.finish < this.start){
			return this.start - this.finish;
		}else{
			return 0;
		}		
	}
	
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime() {
		this.startTime = System.currentTimeMillis();
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public void setEndTime() {
		this.endTime = System.currentTimeMillis();
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getTimeSpent(){
		if(this.startTime != 0){
			if(this.endTime == 0){
				return System.currentTimeMillis() - this.startTime;				
			}else{
				return this.endTime - this.startTime;
			}
		}else{
			return 0;
		}
	}

	public void addProgressMonitor(ProgressMonitor pm, long period){
		
		if(this.timer == null){
			this.timer = new Timer();
		}
		
		if(this.progressMonitors == null){
			this.progressMonitors = new ArrayList<ProgressMonitor>();
		}
		
		pm.setProgressMeter(this);
		this.timer.schedule(pm, 0, period);
		this.progressMonitors.add(pm);
	}
	
	public ArrayList<ProgressMonitor> getProgressMonitors() {
		return progressMonitors;
	}
	
	public void cancelProgressMonitors(){
		if(this.progressMonitors != null){
			for(ProgressMonitor pm: this.progressMonitors){
				pm.cancel();
			}			
		}
	}
	
	public void finalize(){
		if(this.timer != null){
			timer.cancel();
		}
	}
}
