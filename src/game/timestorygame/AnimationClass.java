package game.timestorygame;


import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class AnimationClass {
	private int XPos;
	private int YPos;
	//private Rect FrameRect;
	private int FPS;
	private int NumOfFrames;
	private int CurrentFrame;
	private long FrameTimer;
	private int SpriteHeight;
	private int SpriteWidth;
	
	private int rows;
	private int columns;
	private int intCurrentRow;
	private int intCurrentColumn;
	
	private ArrayList<Integer[]> spriteSheetData = new ArrayList<Integer[]>(1);
	
	//left column = 1/# of columns on sprite sheet
	//right column = 1/# of rows on sprite sheet
    /*float[] texCoord = {
	         0.0f, 0.0f,
	         0.0f, 0.07f,
	         0.1f, 0.0f,
	         0.0f, 0.07f,
	         0.1f, 0.07f,
	         0.1f, 0.0f
	};*/
	float[] texCoord;
	
	public AnimationClass()
	{
		//FrameRect = new Rect(0,0,0,0);
		FrameTimer = 0;
		CurrentFrame = 0;
	}
	
	public void setXPos(int set)
	{
		XPos = set;
	}
	
	public int getXPos()
	{
		return XPos;
	}
	
	public void setYPos(int set)
	{
		YPos = set;
	}
	
	public int getYPos()
	{
		return YPos;
	}
	
	public void addSpriteSheetData(Integer[] data){
		spriteSheetData.add(data);
	}

	public int getSpriteSheetData(int intAnimIndex, int intAnimData){
		return spriteSheetData.get(intAnimIndex)[intAnimData];
	}
	
	public float[] getTextureCoord(){
		/*float[] texCoord = {
				FrameRect.left,FrameRect.top,
				FrameRect.left,FrameRect.bottom,
				FrameRect.right,FrameRect.top,
				FrameRect.left,FrameRect.bottom,
				FrameRect.right,FrameRect.bottom,
				FrameRect.right, FrameRect.top
		};*/
		
		
		return texCoord; 
	}
	
	public void Initialise(int xPos, int yPos, int SheetWidth, int SheetHeight, int theFPS, int setRows, int setColumns)
	{
		SpriteHeight = SheetHeight/setRows;
		SpriteWidth = SheetWidth/setColumns;
		
		/*FrameRect.top = 0;
		FrameRect.bottom = SpriteHeight;
		FrameRect.left = 0;
		FrameRect.right = SpriteWidth;*/
		
		FPS = 1000/theFPS;
		NumOfFrames = setRows*setColumns;
		
		XPos = xPos;
		YPos = yPos;
		
		rows = setRows;
		columns = setColumns;
		
		intCurrentRow = 0;
		intCurrentColumn = 0;
		
		float[] setTexCoord = {
			0.0f, 			0.0f,
			0.0f, 			(1.0f/rows),
			(1.0f/columns), 0.0f,
			0.0f, 			(1.0f/rows),
			(1.0f/columns), (1.0f/rows),
			(1.0f/columns),	0.0f
		};
		
		texCoord = setTexCoord;
		setTexCoord = null;
	}
	
	public void Update(long GameTime, double TimeChange)
	{
		int row;
		int col;
		int x;
		int y;
		
		FPS = (int) (1000/(FPS + TimeChange));
		if(GameTime > FrameTimer + FPS)
		{
			FrameTimer = GameTime;
			CurrentFrame = (int) (CurrentFrame + TimeChange);
			
			if(CurrentFrame >= NumOfFrames)
			{
				CurrentFrame = 1;
			}
			else if(CurrentFrame <= 0)
			{
				CurrentFrame = NumOfFrames - 1;
			}
			
			intCurrentRow = ((int)CurrentFrame / columns);
			intCurrentColumn = ((int)CurrentFrame % columns);
			
			texCoord[0] = (1.0f/columns)*intCurrentColumn;
			texCoord[1] = (1.0f/rows)*intCurrentRow;
			
			texCoord[2] = (1.0f/columns)*intCurrentColumn;
			texCoord[3] = (1.0f/rows)*intCurrentRow + (1.0f/rows);
			
			texCoord[4] = (1.0f/columns)*intCurrentColumn + (1.0f/columns);
			texCoord[5] = (1.0f/rows)*intCurrentRow;
			
			texCoord[6] = (1.0f/columns)*intCurrentColumn;
			texCoord[7] = (1.0f/rows)*intCurrentRow + (1.0f/rows);
			
			texCoord[8] = (1.0f/columns)*intCurrentColumn + (1.0f/columns);
			texCoord[9] = (1.0f/rows)*intCurrentRow + (1.0f/rows);
			
			
			texCoord[10] = (1.0f/columns)*intCurrentColumn + (1.0f/columns);
			texCoord[11] = (1.0f/rows)*intCurrentRow;
			
			
			row = ((int)CurrentFrame / columns);
			col = ((int)CurrentFrame % columns);
			x = SpriteWidth * col;
			y = SpriteHeight * row;
			
			/*FrameRect.left = x;
			FrameRect.top = y;
			FrameRect.right = x + SpriteWidth;
			FrameRect.bottom = y + SpriteHeight;*/
		}
	}
	
	public void draw()
	{
		/*Rect dest = new Rect(getXPos(), getYPos(), getXPos() + SpriteWidth, getYPos() + SpriteHeight);
		Canvas store = new Canvas();
		store.drawBitmap(Animation, FrameRect,dest, null);
		
		Bitmap imgTexture = null;
		store.setBitmap(imgTexture);
		
		loadTexture(imgTexture);*/
	}
	

}
