package game.timestorygame;
import java.util.Vector;

public class Timespot {
	public float startTime;
	public float endTime;
	public String action;
	public int xPos;
	public int yPos;
	
	public Timespot(String act, float sTime,float eTime, int xPosSet, int yPosSet)
	{
		action = act;
		startTime = sTime;
		endTime = eTime;
		xPos = xPosSet;
		yPos = yPosSet;
	}
	
	public String getAction(){
		return action;
	}
	
	public float getStartTime(){
		return startTime;
	}

	public void setEndTime(float setEndTime){
		endTime = setEndTime;
	}
	
	public float getEndTime(){
		return endTime;
	}
	
	
}
