package game.timestorygame;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.opengl.GLES20;

class Level{
	protected long lonGameTime;
	static long lonTime = 0;
	
	protected double douXDiff = 0;
	protected double douXCurrent;
	protected double douXOld;
	
	float fltPreviousMouseX;
	float fltPreviousMouseY;
	
	ArrayList<TimeObject> objList = new ArrayList<TimeObject>(1);
	ArrayList<Light> lightList = new ArrayList<Light>(1);
	//private ArrayList<GraphicObject> _graphics = new ArrayList<GraphicObject>();
	//private GraphicObject _currentGraphic = null;
	
	public long getlonTime()
	{
		return lonTime;
	}
	
	public void setlonTime(long set)
	{
		lonTime = set;
	}
	
    public Level(Context context) {
    	//must add objects by z depth highest z depth gets added last
    	Player plaPlayer = new Player(context,  -8.0f, 0.0f, -7.002f,	"plamachine", "walk");
    	Player testPlayer = new Player(context, -6.0f, -0.5f, -5.001f, "testmachine", "walk");
    	//Player testPlayer2 = new Player(context, -3.0f, 1.0f, -7.0f, "testmachine2", "stand");
    	
    	//Light light1 = new Light(context, 1.0f, 0.0f, -6.9f,"light",3.0f,1.0f,1.0f,1.0f,1.0f);
    	/*Light light2 = new Light(context, -1.0f, 1.0f, -6.9f,"light",3.0f,1.0f,1.0f,1.0f,1.0f);
    	Light light3 = new Light(context, -4.0f, 1.0f, -6.9f,"light",3.0f,1.0f,1.0f,1.0f,1.0f);*/
    	
    	objList.add(plaPlayer);
    	objList.add(testPlayer);
    	//objList.add(testPlayer2);
    	
    	
    	//lightList.add(light1);
    	/*lightList.add(light2);
    	lightList.add(light3);*/
    	
    }
    
    public ArrayList<Light> getLights(){
    	return lightList;
    }
    
    
    public ArrayList<float[][]> createDrawObject(){
    	ArrayList<float[][]> fltDrawObj = new ArrayList<float[][]>(1);
    	
    	for(int i = 0; i < objList.size();i++){
    		fltDrawObj.add(objList.get(i).constructTimeObjectDraw());
    	}
    	
    	return fltDrawObj;
    }

    public ArrayList<Integer[]> getImageIndex(){
    	ArrayList<Integer[]> intImageIndex = new ArrayList<Integer[]>(1);
    	
    	for(int i = 0; i < objList.size();i++){
    		intImageIndex.add(objList.get(i).getImageData());
    	}
    	
    	return intImageIndex;
    }
    
    public ArrayList<Integer> getCurrentAnimations(){
    	ArrayList<Integer> intCurrentAnims = new ArrayList<Integer>(1);
    	
    	for(int i = 0; i < objList.size();i++){
    		intCurrentAnims.add(objList.get(i).getCurrentAnimation());
    	}
    	
    	return intCurrentAnims;
    }
    
    public Float[] createDrawLight(){
    	/*ArrayList<float[]> fltLightData = new ArrayList<float[]>(1);
    	
    	for(int i = 0; i < lightList.size();i++){
    		fltLightData.add(lightList.get(i).constructLightObjectDraw());
    	}
    	return fltLightData;*/
    	
    	ArrayList<Float> fltLightData = new ArrayList<Float>(1);
    	
    	float[] construct;
    	for(int i = 0; i < lightList.size();i++){
    		construct = lightList.get(i).constructLightObjectDraw();
    		for(int j = 0; j < construct.length; j++){
    			fltLightData.add(construct[j]);
    		}
    	}
    	
    	Float[] convert = new Float[fltLightData.size()];
    	fltLightData.toArray(convert);
    	return convert;
    }    
    
    
	public void passTouchEvents(MotionEvent e, int index){
		
		float fltXPos = e.getX();
		float dx = fltPreviousMouseX;
		
        switch (e.getAction()) {
	    	case MotionEvent.ACTION_DOWN:
	    		dx = 0;
	    		fltPreviousMouseX = fltXPos;
	    		
	        case MotionEvent.ACTION_UP:
        		dx = 0;
        		fltPreviousMouseX = fltXPos;
        		
	        case MotionEvent.ACTION_MOVE:
		        dx = fltXPos - fltPreviousMouseX;
		        fltPreviousMouseX = fltXPos;
        }
		
        lonTime += dx;
        if(lonTime < 0)lonTime = 0;
		
		boolean selected = false;
    	for(int i = 0; i < objList.size();i++){
    		//objList.get(i).updateTime(lonTime);
    		
    		if(index == i)selected = true;
    		objList.get(i).passTouchEvent(e,selected);
    		selected = false;
    	}
	}
}