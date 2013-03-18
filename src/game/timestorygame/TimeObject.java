package game.timestorygame;
import java.util.*;
import java.lang.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLU;
import android.util.Log;
import android.view.MotionEvent;

abstract public class TimeObject{
	float xPos;
	float yPos;
	String name;

	LinkedList<Timespot> timeline= new LinkedList<Timespot>();
	
	AnimationClass aniAnimation;
	//long lonTime;
	
	int intCurrentAnim;
	Integer[] intSpriteSheets;
	
	String strCurrentAction;
	
    float[] drawPositionData;
    float[] drawColorData;
    float[] drawNormalData;
	
    String[] actionSet;
    
    float[] mViewMatrix = new float[16];
    
    float[] changePositionData = new float[4];
    
    Context conContext;
    
	public TimeObject(Context context, float x, float y, String name)
	{
		xPos = x;
		yPos = y;
		this.name = name;
		
		conContext = context;
		
		aniAnimation = new AnimationClass();
		//timeline.add(new Timespot("stand",x,frame,position));
	}
	
	public float[][] constructTimeObjectDraw(){
		
		float[][] drawData =	{
									drawPositionData,
									drawColorData,
									drawNormalData,
									aniAnimation.getTextureCoord(),
									changePositionData
								};
		
		return drawData;
	}
	
	/*public void updateTime(long lonTime){
		this.lonTime = lonTime;
	}*/
	
	public void changeDrawPositionData(float x, float y, float depth){
		changePositionData[0] = changePositionData[0]+x;
		changePositionData[1] = changePositionData[1]+y;
		changePositionData[2] = changePositionData[2]+depth;
		/*for(int i = 0; i < drawPositionData.length; i++){
			switch(i % 3){
				case 0: drawPositionData[i] = drawPositionData[i] + x;
				case 1: drawPositionData[i] = drawPositionData[i] + y;
				case 2: drawPositionData[i] = drawPositionData[i] + depth;
			}
		}*/
	}

	public void changeDrawColorData(float R, float G, float B, float A){
		for(int i = 0; i < drawPositionData.length; i++){
			switch(i % 4){
				case 0: drawColorData[i] = drawColorData[i] + R;
				case 1: drawColorData[i] = drawColorData[i] + G;
				case 2: drawColorData[i] = drawColorData[i] + B;
				case 3: drawColorData[i] = drawColorData[i] + A;
			}
		}
	}
	
	public void setAnimation(int intAnimation){			
		intCurrentAnim = intAnimation;
    	this.aniAnimation.Initialise(	conContext.getResources().getDisplayMetrics().widthPixels/2, 
										conContext.getResources().getDisplayMetrics().heightPixels/2, 
										aniAnimation.getSpriteSheetData(intAnimation,0), 
										aniAnimation.getSpriteSheetData(intAnimation,1),
										30, 
										aniAnimation.getSpriteSheetData(intAnimation,2),
										aniAnimation.getSpriteSheetData(intAnimation,3));
	}
	
	public int getCurrentAnimation(){
		return intCurrentAnim;
	}
	
	public Integer[] getImageData(){
		return intSpriteSheets;
	}
	
	public Bitmap loadSpriteSheet(int Id){
		Bitmap imgSpriteSheet = BitmapFactory.decodeResource(conContext.getResources(),Id);
		return imgSpriteSheet;
	}
	
	public void addTimeSpot(String act, int sTime,int eTime, int xPosSet, int yPosSet){
		timeline.add(new Timespot(act, sTime, eTime, xPosSet, yPosSet));
	}
	
	public Timespot getLatestTimespot(){
		return timeline.get(timeline.size() - 1);
	}
	
	public void actionDisplay(){
		/*for(int i = 0; i < actionSet.length; i++){
			
		}*/
    	if(strCurrentAction =="stand"){
    		strCurrentAction = "walk";
    	}
    	else if(strCurrentAction =="walk") {
    		strCurrentAction = "stand";
    	}
    	
    	timeline.add(new Timespot(strCurrentAction,(float)Level.lonTime,-1,(int)xPos,(int)yPos));
	}
	
	public void checkTimeline(){
		if(timeline.size() == 1)return;
		if(Level.lonTime <= timeline.getLast().getStartTime()){
			strCurrentAction = timeline.get(timeline.size()-2).getAction(); 
			timeline.removeLast();
		}
	}
	
	abstract public void passTouchEvent(MotionEvent e, boolean boolSelected);
	abstract public void prepareAnimationData();

}
