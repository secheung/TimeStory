package game.timestorygame;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;

public class TimestoryRenderer implements GLSurfaceView.Renderer {
	
	Context mActivityContext;
	
	private FloatBuffer mSquarePositions;
	private FloatBuffer mSquareColors;
	private FloatBuffer mSquareNormals;
	private FloatBuffer mLightData;
	
	private int viewportWidth;
	private int viewportHeight;
	
	private final int mBytesPerFloat = 4;
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	
	private int mMVPMatrixHandle;
	private int mMVMatrixHandle;
	
	private int mPositionHandle;
	private int mColorHandle;
	private int mNormalHandle;
	
	private int mLightPosHandle;
	
	private float[] mModelMatrix = new float[16];
	private float[] mMVMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	
	//private ArrayList<float[]> mMVMatrices = new ArrayList<float[]>();
	private ArrayList<float[]> projectedPoints = new ArrayList<float[]>();
	
	private float[] mLightModelMatrix = new float[16];
	private final float[] mLightPosInWorldSpace = new float[4];
	private final float[] mLightPosInEyeSpace = new float[4];
	
	private final int mPositionDataSize = 3;
	private final int mColorDataSize = 4;
	private final int mNormalDataSize = 3;
	private final int mLightDataSize = 3;
	
	private int mPerVertexProgramHandle;
	
	private int mPointProgramHandle;
	
	private FloatBuffer mSquareTextureCoordinates;
	private int mTextureUniformHandle;
	private int mTextureCoordinateHandle;
	private final int mTextureCoordinateDataSize = 2;
	private int mTextureDataHandle;
	
	ArrayList<Integer[]> intTextures = new ArrayList<Integer[]>(1);
	ArrayList<Integer> intLights = new ArrayList<Integer>(1);
	
	ArrayList<Integer> intLightProgramHandler = new ArrayList<Integer>(1);
	
	Level level;
	
	
	public TimestoryRenderer(final Context activityContext)
	{
		mActivityContext = activityContext;
		level = new Level(activityContext);
		
	}
	
	public void passTouchEvents(MotionEvent e){
		
		float x = e.getX(), y = viewportHeight - e.getY();
		float closestdepth = -1;
		int passIndex = -1;
		
		for(int i = 0; i < projectedPoints.size();i++){
			if(x > projectedPoints.get(i)[0] && x < projectedPoints.get(i)[0]+projectedPoints.get(i)[2] && y < projectedPoints.get(i)[1] && y > projectedPoints.get(i)[1]-projectedPoints.get(i)[3]){
				if(projectedPoints.get(i)[4] >= closestdepth){
					closestdepth = projectedPoints.get(i)[4];
					passIndex = i;
				}
			}
		}
		
		level.passTouchEvents(e, passIndex);
		//System.out.println(passIndex);
		
		//System.out.println(x+" "+y);
		//System.out.println(projectedPoints.get(0)[0]+" "+(projectedPoints.get(0)[0]+projectedPoints.get(0)[2])+" "+projectedPoints.get(0)[1]+" "+(projectedPoints.get(0)[1]-projectedPoints.get(0)[3])+" "+projectedPoints.get(0)[4]);
		//System.out.println(projectedPoints.get(1)[0]+" "+(projectedPoints.get(1)[0]+projectedPoints.get(1)[2])+" "+projectedPoints.get(1)[1]+" "+(projectedPoints.get(1)[1]-projectedPoints.get(1)[3])+" "+projectedPoints.get(1)[4]);
		
		/*
		System.out.println(projectedPoints.get(0)[0]+" "+projectedPoints.get(0)[1]+" "+projectedPoints.get(0)[2]);
		System.out.println(projectedPoints.get(0)[3]+" "+projectedPoints.get(0)[4]+" "+projectedPoints.get(0)[5]);
		System.out.println(projectedPoints.get(0)[6]+" "+projectedPoints.get(0)[7]+" "+projectedPoints.get(0)[8]);
		*/
		/*
		System.out.println(projectedPoints.get(1)[0]+" "+projectedPoints.get(1)[1]+" "+projectedPoints.get(1)[2]);
		System.out.println(projectedPoints.get(1)[3]+" "+projectedPoints.get(1)[4]+" "+projectedPoints.get(1)[5]);
		System.out.println(projectedPoints.get(1)[6]+" "+projectedPoints.get(1)[7]+" "+projectedPoints.get(1)[8]);
		*/
	}


	protected String getVertexShader(){
		 final String vertexShader =
					"uniform mat4 u_MVPMatrix;      	\n"		// A constant representing the combined model/view/projection matrix.
				  + "uniform mat4 u_MVMatrix;      		\n"		// A constant representing the combined model/view matrix.

				  + "attribute vec4 a_Position;     	\n"		// Per-vertex position information we will pass in.
				  + "attribute vec4 a_Color;        	\n"		// Per-vertex color information we will pass in.
				  + "attribute vec3 a_Normal;       	\n"		// Per-vertex normal information we will pass in.
				  + "attribute vec2 a_TexCoordinate;	\n"		// Per-vertex texture coordinate information we will pass in.
				  + "attribute vec3 a_LightPos;			\n"	
				  
				  + "varying vec3 v_Position;       	\n"		// This will be passed into the fragment shader.
				  + "varying vec4 v_Color;          	\n"		// This will be passed into the fragment shader.
				  + "varying vec3 v_Normal;         	\n"		// This will be passed into the fragment shader.
				  + "varying vec2 v_TexCoordinate;		\n"		// This will be passed into the fragment shader.
				  + "varying vec3 v_LightPos;       	\n"		// This will be passed into the fragment shader.
				// The entry point for our vertex shader.  
				  + "void main()                                                \n" 	
				  + "{                                                          \n"
				// Transform the vertex into eye space.
				  + "   v_Position = vec3(u_MVMatrix * a_Position);             \n"
				// Pass through the color.
				  + "   v_Color = a_Color;                                      \n"
				// Transform the normal's orientation into eye space.
				  + "   v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));      \n"
				// Pass through the texture coordinate.//
				  +"	v_TexCoordinate = a_TexCoordinate;						\n"
				  +"	v_LightPos = vec3(u_MVMatrix * vec4(a_LightPos, 0.0));								\n"				  
				// gl_Position is a special variable used to store the final position.
				// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
				  + "   gl_Position = u_MVPMatrix * a_Position;                 \n"      		  
				  + "}                                                          \n";

		
		 
		 return vertexShader;
	}
	
	public String getFragmentShader(){
		final String fragmentShader =
				"precision mediump float;       	\n"		// Set the default precision to medium. We don't need as high of a 
															// precision in the fragment shader.
				+ "varying vec3 v_LightPos;       	\n"	    // The position of the light in eye space.
				+ "uniform sampler2D u_Texture;		\n"		// input Texture
				
				+ "varying vec3 v_Position;			\n"		// Interpolated position for this fragment.
				+ "varying vec4 v_Color;          	\n"		// This is the color from the vertex shader interpolated across the 
															// triangle per fragment.
				+ "varying vec3 v_Normal;         	\n"		// Interpolated normal for this fragment.				
				+ "varying vec2 v_TexCoordinate;	\n"		// This will be passed into the fragment shader.
				
				// The entry point for our fragment shader.
				+ "void main()                    													\n"
				+ "{                              													\n"
				// Will be used for attenuation.
				+ "		float distance = length(v_LightPos - v_Position);                  			\n"
				// Get a lighting direction vector from the light to the vertex.
				+ "		vec3 lightVector = normalize(v_LightPos - v_Position);             			\n"
				// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
				// pointing in the same direction then it will get max illumination.
				+ "		float diffuse = max(dot(v_Normal, lightVector), 0.0);              			\n" 	  		  													  
				// Add attenuation. 
				//+ "		diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance )));  					\n"
				+ "		diffuse = diffuse*(1.0 / (0.20 * distance ));								\n"
				// Multiply the color by the diffuse illumination level and texture value to get final output color.
				+ "		gl_FragColor = texture2D(u_Texture, v_TexCoordinate);				\n"
				+ "}                                                                     			\n";
		/*final String fragmentShader = 
						"precision mediump float;       \n"     	// Set the default precision to medium. We don't need as high of a
																	// precision in the fragment shader.
						+ "varying vec4 v_Color;          \n"    	// This is the color from the vertex shader interpolated across the
						                							// triangle per fragment.
						+ "void main()                    \n"     	// The entry point for our fragment shader.
						+ "{                              \n"
						+ "   gl_FragColor = v_Color;     \n"     	// Pass the color directly through the pipeline.
						+ "}                              \n";*/				
		
		return fragmentShader;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background clear color to black.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
	    Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, -0.5f, 0.0f, 0.0f, -5.0f, 0.0f, 1.0f, 0.0f);
		
		final String vertexShader = getVertexShader();
		final String fragmentShader = getFragmentShader();
	    
	    int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER,vertexShader);
	    int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
	    
	    mPerVertexProgramHandle = createAndLinkProgram(vertexShaderHandle,fragmentShaderHandle, new String[]{"a_Position","a_Color","a_Normal","a_TexCoordinate"});
	    
	    ArrayList<Integer[]> intImageIndexes = level.getImageIndex();
	    for(int i = 0; i < intImageIndexes.size(); i++){
	    	intTextures.add(new Integer[intImageIndexes.get(i).length]);
		    for(int j = 0; j < intImageIndexes.get(i).length; j++){
		    	intTextures.get(i)[j] = loadTexture(intImageIndexes.get(i)[j]);
		    }
	    }
	    
	    ArrayList<Light> intlight = level.getLights();
	    int pointvertexShaderHandle;
	    int pointfragmentShaderHandle;
	    
	    for(int i=0;i < intlight.size();i++){
    		pointvertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER,intlight.get(i).getLightVertexShader());
    		pointfragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER,intlight.get(i).getLightFragmentShader());
	    	intLightProgramHandler.add(createAndLinkProgram(pointvertexShaderHandle,pointfragmentShaderHandle, new String[]{"a_Position"}));
	    }
	    
	}
	
	private int compileShader(int shaderType, String shaderProgram){
		int shaderHandle = GLES20.glCreateShader(shaderType);
		
	    if(shaderHandle != 0)
	    {
	    	// Pass in the shader source.
	    	GLES20.glShaderSource(shaderHandle, shaderProgram);
	    	
	    	// Compile the shader.
	    	GLES20.glCompileShader(shaderHandle);
	    	
	    	// Get the compilation status.
	    	final int[] compileStatus = new int[1];
	    	GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
	    	
	        // If the compilation failed, delete the shader.
	        if (compileStatus[0] == 0)
	        {
	        	Log.e("LessonOpenGLESRenderer", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
	            GLES20.glDeleteShader(shaderHandle);
	            shaderHandle = 0;
	        }
	    	
	    }
	    
	    if (shaderHandle == 0)
	    {
	        throw new RuntimeException("Error creating shader.");
	    }
	    
	    return shaderHandle;
	}
	
	public int createAndLinkProgram(final int vertexShaderProgram, final int fragmentShaderProgram, final String[] attribute){
	    // Create a program object and store the handle to it.
	    int programHandle = GLES20.glCreateProgram();
	    
	    if(programHandle != 0)
	    {
	    	GLES20.glAttachShader(programHandle, vertexShaderProgram);
	    	GLES20.glAttachShader(programHandle, fragmentShaderProgram);
	    	
	    	if(attribute != null)
	    	{
	    		for(int i = 0; i < attribute.length; i++)
	    		{
			    	GLES20.glBindAttribLocation(programHandle, i, attribute[i]);
	    		}
	    	}
	    	
	    	GLES20.glLinkProgram(programHandle);
	    	
	        final int[] linkStatus = new int[1];
	        GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
	     
	        if (linkStatus[0] == 0)
	        {
	        	Log.e("LessonOpenGLESRenderer", "Error compiling shader: " + GLES20.glGetShaderInfoLog(programHandle));
	            GLES20.glDeleteProgram(programHandle);
	            programHandle = 0;
	        }
	    }
	    
	    if (programHandle == 0)
	    {
	        throw new RuntimeException("Error creating program.");
	    }
	    
	    return programHandle;
	}
	
	public int loadTexture(int Id){
		Bitmap imgSpriteSheet = BitmapFactory.decodeResource(mActivityContext.getResources(),Id);
		
		int[] textureHandle = new int[1];
		
		GLES20.glDeleteTextures(1,textureHandle,0);
		GLES20.glGenTextures(1, textureHandle, 0);
		
		if(textureHandle[0] != 0)
		{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;
			
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	        
	        
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, imgSpriteSheet, 0);
	        
	        imgSpriteSheet.recycle();
		}
		
		if(textureHandle[0] == 0)
		{
			throw new RuntimeException("Error loading texture.");
		}
		
		return textureHandle[0];
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		viewportWidth = width;
		viewportHeight = height;

		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;

		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
		
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glUseProgram(mPerVertexProgramHandle);
        
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Normal");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_TexCoordinate");
        
        
        
		ArrayList<float[][]> levelObjects = level.createDrawObject();
		ArrayList<Integer> levelCurrentAnims = level.getCurrentAnimations();
		int dataToload;

		for(int intObjectIndx = 0; intObjectIndx < levelCurrentAnims.size(); intObjectIndx++){
			dataToload = intTextures.get(intObjectIndx)[levelCurrentAnims.get(intObjectIndx)];
			
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + intObjectIndx);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, dataToload);
        	GLES20.glUniform1i(mTextureUniformHandle,intObjectIndx);
        	drawObject(levelObjects.get(intObjectIndx), intObjectIndx);
		}
		
		Float[] lightObjects = level.createDrawLight();
		float[] lightDataPassIn = new float[lightObjects.length];

		for(int i = 0; i < lightObjects.length; i++){
			lightDataPassIn[i] = lightObjects[i].floatValue();
		}
		
		
		for(int i = 0; i < levelObjects.size(); i++){
			//drawObject(levelObjects.get(i),lightDataPassIn, i);
		}
		
		
		/*float[] construct = new float[4];
		for(int i = 0; i < lightObjects.length; i=i+3){
			int get = i == 0 ? 0 : (i/3 - 1);
			mPointProgramHandle = intLightProgramHandler.get(get);
			GLES20.glUseProgram(mPointProgramHandle);
			construct[0] = lightObjects[i].floatValue();
			construct[1] = lightObjects[i+1].floatValue();
			construct[2] = lightObjects[i+2].floatValue();
			construct[3] = 1.0f;
			drawLight(construct);
		}*/
		
	}
	
	//public void drawObject(float[][] data, float[] lightdata, int index){
	public void drawObject(float[][] data, int index){
		float[] positionData = data[0];
		float[] colourData = data[1];
		float[] normalData = data[2];
		float[] textureData = data[3];
		
		float[] changePosData = data[4];
		
		//float[] lightData = lightdata;
		
	    mSquarePositions = ByteBuffer.allocateDirect(positionData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
	    mSquarePositions.put(positionData).position(0);
	    
	    mSquareColors = ByteBuffer.allocateDirect(colourData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
	    mSquareColors.put(colourData).position(0);
	    
	    mSquareNormals = ByteBuffer.allocateDirect(normalData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
	    mSquareNormals.put(normalData).position(0);
	    
	    mSquareTextureCoordinates = ByteBuffer.allocateDirect(textureData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
	    mSquareTextureCoordinates.put(textureData).position(0);

	    //mLightData = ByteBuffer.allocateDirect(lightData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
	    //mLightData.put(lightData).position(0);
	    
	    
	    
		mSquarePositions.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mSquarePositions);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
	 
	    // Pass in the color information
	    mSquareColors.position(0);
	    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false, 0, mSquareColors);
	    GLES20.glEnableVertexAttribArray(mColorHandle);

	    // Pass in the normal information
	    mSquareNormals.position(0);
	    GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false, 0, mSquareNormals);
	    GLES20.glEnableVertexAttribArray(mNormalHandle);

	    // Pass in the texture information
	    mSquareTextureCoordinates.position(0);
	    GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, mSquareTextureCoordinates);
	    GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
	    
	    
	    //Manipulate stuff
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, changePosData[0], changePosData[1], changePosData[2]);
        
        mMVMatrix = new float[16];
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    //Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	    Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	    GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0); 	// Pass in the modelview matrix.

	    float[] set = screenProjection(mMVMatrix,positionData);
	    if(projectedPoints.size() <= index){
	    	projectedPoints.add(set);
	    }
	    else{
	    	projectedPoints.set(index,set);
	    }
	    
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);  // Pass in the combined matrix.
	    
	    
	    // Pass in the light position in eye space.
	    //mLightData.position(0);
	    //GLES20.glVertexAttribPointer(mLightPosHandle, mLightDataSize, GLES20.GL_FLOAT, false, 0, mLightData);
	    //GLES20.glEnableVertexAttribArray(mLightPosHandle);
	    //GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
	    //GLES20.glUniformMatrix3fv(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
	    
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	    mMVMatrix = null;
	    set = null;
	    
	}
	
	private float[] screenProjection(float[] mModelView, float[] PosData){
		int[] viewport = {0,0,viewportWidth,viewportHeight};
		
		float[] pos1 = new float[3];
		float[] pos2 = new float[3];
		float[] pos3 = new float[3];
		
		GLU.gluProject(		PosData[0], PosData[1], PosData[2],
							mModelView, 0,
							mProjectionMatrix, 0,
							viewport, 0,
							pos1, 0);
		
		GLU.gluProject(		PosData[3], PosData[4], PosData[5],
							mModelView, 0,
							mProjectionMatrix, 0,
							viewport, 0,
							pos2, 0);
		
		GLU.gluProject(		PosData[6], PosData[7], PosData[8],
							mModelView, 0,
							mProjectionMatrix, 0,
							viewport, 0,
							pos3, 0);
		
		/*
		float[] set = 	{	pos1[0],pos1[1],pos1[2],
							pos2[0],pos2[1],pos2[2],
							pos3[0],pos3[1],pos3[2]
						};
		*/
		
		float width = pos3[0] - pos1[0];
		float height = pos1[1] - pos2[1];
		float depth = pos3[2];
		
		float[] set = 	{pos1[0],pos1[1],width,height,depth};
		return set;
		
	}
	
	private void drawLight(float[] mLightPosInModelSpace)
	{
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
		
		final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
		final int pointPositionHandle = GLES20.glGetAttribLocation(mPointProgramHandle, "a_Position");
		
		//Pass in the position
		GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);
		
		// Since we are not using a buffer object, disable vertex arrays for this attribute.
		GLES20.glDisableVertexAttribArray(pointPositionHandle);
        
		// Pass in the transformation matrix.
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		
		// Draw the point.
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

	}
}