package game.timestorygame;

import android.content.Context;
import android.view.MotionEvent;

public class Light extends TimeObject{
	
	String lightVertexShader;
	String lightFragmentShader;
	
	public Light(Context context, float x, float y, float depth, String name, float lightSize, float R, float G, float B, float A){
		super(context, x, y, name);
		
		setLightVertexShader(lightSize);
		setLightFragmentShader(R,G,B,A);
		
		float[] setDrawPositionData = {
				x,
				y,
				depth
		};
		
		drawPositionData = setDrawPositionData; 
		setDrawPositionData = null;
	}

	public void setLightVertexShader(float lightSize){
		lightVertexShader = 
	    		  "uniform mat4 u_MVPMatrix;						\n"
	    		+ "attribute vec4 a_Position;						\n"
	    		+ "void main(){										\n"
	    		+ "		gl_Position = u_MVPMatrix * a_Position;		\n"
	    		+ "		gl_PointSize = "+lightSize+";				\n"
	    		+ "}";
	}
	
	public String getLightVertexShader(){
		return lightVertexShader;
	}
	
	public void setLightFragmentShader(float R, float G, float B, float A){
		lightFragmentShader = 
	              "void main()                    						\n"
	            + "{                              						\n"
	            + "   gl_FragColor = vec4("+R+","+G+","+B+","+A+");		\n"
	            + "}                              						\n";
	}
	
	public String getLightFragmentShader(){
		return lightFragmentShader;
	}
	
	public float[] constructLightObjectDraw(){
		return drawPositionData;
	}
	
	@Override
	public void prepareAnimationData() {}
	
	@Override
	public void passTouchEvent(MotionEvent e, boolean boolSelected) {}
	
	
}
