package com.eboncorvin.tttp.GameObjects;
import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.O;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * This GameObject acts like a mask in the Game world. <br/>
 * It absorts all touch event unless the user touchs the transparent part of the Bitmap
 * @author Sunny
 */
public class GOMask extends GameObject {

	private Bitmap bmp;
	private Bitmap[] bmps;
	
	public GOMask(int objectId, int x, int y){
		this(Misc.getBitmap(objectId,0),x,y);
		bmps=Misc.getObject(objectId);
	}
	
	public GOMask(Bitmap mask, int x, int y) {
		super(x, y, mask.getWidth(), mask.getHeight(), 291);
		// TODO Auto-generated constructor stub
		bmp=mask;//Misc.getBitmap(R.drawable.testmask, 0);
	}

	@Override
	public Bitmap frameUpdate() {
		// TODO Auto-generated method stub
		return bmp;
	}
	
	/**
	 * Display the Bitmap which at the position of the Bitmap sequence
	 * @param index
	 */
	public void gotoFrame(int index){
		if(bmps!=null){
			index=(index<bmps.length)?index:0;
			bmp=bmps[index];
		}
	}

	@Override
	public int isTouched(int X, int Y) {
		// TODO Auto-generated method stub
		int returnVal=super.isTouched(X, Y);
		if(returnVal==-1)
			return -1;
		//This GameObject override isTouch, which the coordination is not scaled yet
		int color=bmp.getPixel((int)(X*O.scaleW), (int)(Y*O.scaleH));
		if(color==Color.TRANSPARENT && !isHidden())
			return -1;
		else
			return returnVal;
		
	}

}
