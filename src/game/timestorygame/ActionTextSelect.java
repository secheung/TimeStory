package game.timestorygame;

import android.content.Context;
import android.view.MotionEvent;

public class ActionTextSelect extends TimeInvarObj{
	float xPos;
	float yPos;
	String name;
	
	int intCurrentAnim;
	Integer[] intSpriteSheets;
	
	String strActionDisplay;
	
    /*float[] drawPositionData;
    float[] drawColorData;
    float[] drawNormalData;
    
    float[] mViewMatrix = new float[16];
    float[] changePositionData = new float[4];*/
    
    Context conContext;
	
	public ActionTextSelect(Context context, float x, float y, String name, String action) {
		super(context, x, y, name);
		strActionDisplay = action;
		
	}

	@Override
	public void passTouchEvent(MotionEvent e, boolean boolSelected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareAnimationData() {
		// TODO Auto-generated method stub
		
	}

}
