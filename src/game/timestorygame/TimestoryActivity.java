package game.timestorygame;

import java.util.ArrayList;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;


public class TimestoryActivity extends Activity{
	
	//private GLSurfaceView mGLView;
	private TimeStorySurfaceView mGLView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //mGLView = new GLSurfaceView(this);
        //mGLView.setEGLContextClientVersion(2);
        //mGLView.setRenderer(new TimestoryRenderer(this));
        
        mGLView = new TimeStorySurfaceView(this);
        setContentView(mGLView);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
}

class TimeStorySurfaceView extends GLSurfaceView {

	 	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	    private TimestoryRenderer levRenderer;
	    
	    public TimeStorySurfaceView(Context context){
	        super(context);
	        
	        setEGLContextClientVersion(2);
	        levRenderer = new TimestoryRenderer(context);
	        setRenderer(levRenderer);
	        
	        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	    }
	    
	    @Override 
	    public boolean onTouchEvent(MotionEvent e) {
	        // MotionEvent reports input details from the touch screen
	        // and other input controls. In this case, you are only
	        // interested in events where the touch position changed.
	    	requestRender();
	    	levRenderer.passTouchEvents(e);
	    
	        return true;
	    } 
	
}

