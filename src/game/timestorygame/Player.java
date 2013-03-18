package game.timestorygame;

import java.nio.FloatBuffer;

import android.R.string;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

public class Player extends TimeObject{
	
	//actions
	final int indxStand = 0;
	final int indxWalk = 1;
	final int indxJump = 2;
	
	float walkspeed = 0.5f;
	
	double douXDiff;
	
	float fltPreviousXPos;
	float fltPreviousYPos;
	
	String[] actionSet = {
			"stand",
			"walk"
	};
	
	public Player(Context context, float x, float y, float depth, String name, String startAction) {
		super(context, x, y, name);
		
		strCurrentAction = startAction;
		
		float[] setDrawPositionData = {
            // X, Y, Z,
			-2.0f,  3.0f, 0.0f,
			-2.0f, -1.0f, 0.0f,
			 1.0f,  3.0f, 0.0f, 
			-2.0f, -1.0f, 0.0f, 				
			 1.0f, -1.0f, 0.0f,
			 1.0f,  3.0f, 0.0f
	    };
		
	    float[] setDrawColorData = {
            // R, G, B, A
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f
	    };

	    float[] setDrawNormalData = {
			0.0f, 0.0f, 1.0f,				
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,				
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f
		};
		
	    drawPositionData = setDrawPositionData;
	    drawColorData = setDrawColorData;
	    drawNormalData = setDrawNormalData;
		
		//float[] setChangePositionData = {x,y,depth};
	    //changePositionData = setChangePositionData;
	    
	    changeDrawPositionData(x,y,depth);
	    
	    //make sure they match constants(I think)
		Integer[] setSpriteSheets = {
			R.drawable.stand,
			R.drawable.walk
		};
		intSpriteSheets = setSpriteSheets;
		
		conContext = context;
	    
		changePositionData[0] = x;
		changePositionData[1] = y;
		changePositionData[2] = depth;
		
	    setDrawPositionData = null;
	    setDrawColorData = null;
	    setDrawNormalData = null;
	    
	    
	    prepareAnimationData();
		//Bitmap imgImage = loadSpriteSheet(intSpriteSheets[indxStand]);
	    if(strCurrentAction == "stand"){
	    	setAnimation(indxStand);
	    }
	    else if(strCurrentAction == "walk"){
	    	setAnimation(indxWalk);	    	
	    }
	    timeline.add(new Timespot(strCurrentAction,(float)Level.lonTime,-1,(int)xPos,(int)yPos));
	    
    	/*aniAnimation.Initialise(context.getResources().getDisplayMetrics().widthPixels/2, 
								context.getResources().getDisplayMetrics().heightPixels/2, 
								aniAnimation.getSpriteSheetData(indxStand,0), 
								aniAnimation.getSpriteSheetData(indxStand,1),
								30, 
								aniAnimation.getSpriteSheetData(indxStand,2),
								aniAnimation.getSpriteSheetData(indxStand,3));*/
    	
    	
	}
	
	@Override
	public void prepareAnimationData(){
		//1.5008431703204047217537942664418
		
		//width/1.5,height/1.5,#rows,#columns
		aniAnimation.addSpriteSheetData(new Integer[]{593,1661,14,10});					//stand
		aniAnimation.addSpriteSheetData(new Integer[]{248,750,9,6});						//walk
	}
	
	public void Update(long GameTime, float xDiff)
	{
		if(Level.lonTime <= 1){
			if(strCurrentAction != timeline.getFirst().getAction()){
				strCurrentAction = timeline.getFirst().getAction();
			}
			else{
				return;
			}
		}
		aniAnimation.Update(GameTime, xDiff);
		douXDiff = xDiff;
		
		if(strCurrentAction.equals("walk")){
			if(intCurrentAnim!=indxWalk){
				setAnimation(indxWalk);
			}
			changeDrawPositionData(xDiff/50, 0.0f, 0.0f);
		}
		else if(strCurrentAction.equals("stand")){
			if(intCurrentAnim!=indxStand){
				setAnimation(indxStand);
			}
		}
	}

	public void walkFunction(){
		xPos = xPos + walkspeed;
	}
	
	@Override
	public void passTouchEvent(MotionEvent e, boolean boolSelected){
		//aniAnimation.Update(System.currentTimeMillis(), 1);
		
		float fltXPos = e.getX();
		float fltYPos = e.getY();
		float dx = fltPreviousXPos;
		float dy = fltPreviousYPos;
		
        switch (e.getAction()) {
	    	case MotionEvent.ACTION_DOWN:
	    		dx = 0;
	    		dy = 0;
	    		
	    		fltPreviousXPos = fltXPos;
	    		fltPreviousYPos = fltYPos;
	    		
        		if(boolSelected){
        			this.actionDisplay();
        		}
	    		
	        case MotionEvent.ACTION_UP:
        		dx = 0;
        		dy = 0;
        		
        		fltPreviousXPos = fltXPos;
        		fltPreviousYPos = fltYPos;
        		
	        case MotionEvent.ACTION_MOVE:
		        dx = fltXPos - fltPreviousXPos;
		        dy = fltYPos - fltPreviousYPos;
		        
		        fltPreviousXPos = fltXPos;
		        fltPreviousYPos = fltYPos;
        }
        
        if(dx < 0)checkTimeline();
        //System.out.println(Level.lonTime+" "+timeline.getLast().getStartTime());
        this.Update(System.currentTimeMillis(), dx);
        
	}
}
