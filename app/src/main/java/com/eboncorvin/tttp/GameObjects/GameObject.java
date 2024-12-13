package com.eboncorvin.tttp.GameObjects;

import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.O;
import com.eboncorvin.tttp.R;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

public class GameObject {
	public final static int seInterval=70;
	public final static int seDivider=seInterval/10;
	private boolean seInEffect=false;	//Indicate if special effect is in effect
	private Rect sNpInSE=new Rect();
	private Rect sizeNpos=new Rect();
	private Rect originSP=new Rect();
	private Point SEoffset=new Point();

	private boolean hidden=false;
	
	private Bitmap[] Sequence;
	private int frame, Interval=-1, totalframe, actioncode=-1, SEType;
	private long lasttimedrawn=0, elasttimedrawn=0;
	private Bitmap cached;
	
	//---------Getter and setter declaration end--------------
	/**
	 * This constructor is for object that display a static bitmap <br/>
	 * @param bitmap
	 * @param x
	 * @param y
	 * @param ActionCode
	 */
	public GameObject(Bitmap bitmap, int x, int y, int ActionCode) 
			throws IllegalArgumentException{
		if(bitmap==null)
			throw new NullPointerException("The bitmap pointer points to nothing");
		int width=bitmap.getWidth();
		int height=bitmap.getHeight();
		this.originSP.set(x, y, x+width, y+height);
		setSizeNpos(originSP);
		/*x=(int) (x/O.scaleW);
		y=(int) (y/O.scaleH);
		width=(int) (width/O.scaleW);
		height=(int) (height/O.scaleH);
		this.sizeNpos.set(x, y, x+width, y+height);*/
		this.actioncode=ActionCode;
		cached=bitmap;
		lasttimedrawn=System.currentTimeMillis();
	}

	/**
	 * This constructor is for object that is registered in ImagePreload. <br/>
	 * It is animated if the game object contains multiple frames 
	 * @param ObjectID
	 * @param Interval
	 * @param InitFrame
	 * @param x
	 * @param y
	 * @param ActionCode
	 * @throws IllegalArgumentException
	 */
	public GameObject(int ObjectID, int Interval, int InitFrame, int x, int y, int ActionCode) 
		throws IllegalArgumentException{
		this(Misc.getObject(ObjectID)[0], x, y, ActionCode);
		if(ObjectID==-1)
			return;
		setSequence(Misc.getObject(ObjectID));
		if(totalframe<=InitFrame || InitFrame<0)
			throw new IllegalArgumentException("Wrong initial Frame Number!");
		this.frame=InitFrame;
		this.Interval=Interval;
		cached=Sequence[frame];
	}

	/**
	 * This constructor defines a canvas for drawing with Android API <br/>
	 * Typically this constructor is for sub-class
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param actioncode
	 */
	public GameObject(int x, int y, int width, int height, int actioncode){	
		this(Misc.getBitmap(R.drawable.empty, 0), x, y, actioncode);
		this.originSP.set(x, y, x+width, y+height);
		setSizeNpos(originSP);
		/*x=(int) (x/O.scaleW);
		y=(int) (y/O.scaleH);
		width=(int) (width/O.scaleW);
		height=(int) (height/O.scaleH);
		this.sizeNpos.set(x, y, x+width, y+height);*/
	}

	
	/**
	 * Use object id to create game object, but do not play it as animation
	 * @param ObjectID
	 * @param x
	 * @param y
	 * @param ActionCode
	 */
	public GameObject(int ObjectID, int x, int y, int ActionCode){
		this(ObjectID, -1, 0, x, y, ActionCode);
	}

	//----------Here is the getter and setter for fields------------	
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/*public void setCxached(Bitmap cached) {
		this.cached = cached;
	}*/

	public void setSequence(Bitmap[] sequence) {
		Sequence = sequence;
		totalframe=Sequence.length;
	}

	public int getCurrentFrame() {
		return frame;
	}

	public void goToAndStop(int FrameNo){
		if(FrameNo>=this.totalframe)
			 throw new IllegalArgumentException("The frame No to stop is out of range!");
		frame=FrameNo;
		cached=Sequence[frame];
	}

	/**
	 * Get the original size and position of this GameObject <br/>
	 * Which the value is not scaled yet.
	 * @return
	 */
	public Rect getOriginSP() {
		return originSP;
	}

	/**
	 * Get the drawing size and position of this GameObject <br/>
	 * The return Rectangle represents the real position on the phone screen <br/>
	 * which maybe scaled.
	 * @return
	 */
	public Rect getDrawPosition() {
		if(seInEffect)
			return sNpInSE;
		else
			return sizeNpos;
	}

	/**
	 * Move the object to specified values <br/>
	 * Provide -1 if X or Y is no needed to be changed
	 * @param X X-axis to move
	 * @param Y Y-axis to move
	 */
	public void setObjectPos(int X, int Y){
		X=(X==-1)?sizeNpos.left:(int) (X/O.scaleW);
		Y=(Y==-1)?sizeNpos.top: (int) (Y/O.scaleH);
		sizeNpos.offsetTo(X, Y);
	}

	/**
	 * Move the object to a position on the stage, also resize the object<br/>
	 * This setter will handle the scaling on different phone screen
	 * @param snp
	 */
	public void setSizeNpos(Rect snp) {
		int x=(int) (snp.left/O.scaleW);
		int y=(int) (snp.top/O.scaleH);
		int width=(int) (snp.width()/O.scaleW);
		int height=(int) (snp.height()/O.scaleH);		
		this.sizeNpos.set(x, y, x+width, y+height);
	}

	/**
	 * Is this object is moving (By motion animation)
	 * @return
	 */
	public boolean isIneffect() {
		return seInEffect;
	}

	public void setActioncode(int actioncode) {
		this.actioncode = actioncode;
	}

	public Bitmap frameUpdate(){	//-1=Animation Disabled
		if(Interval!=-1) {
			if(System.currentTimeMillis()-lasttimedrawn>=Interval){
				frame++;
				if(frame==totalframe){
					frame=0;
				}		
				cached=Sequence[frame];
				lasttimedrawn=System.currentTimeMillis();
			}
		}
		
		if(seInEffect){
			if(System.currentTimeMillis()-elasttimedrawn>=seInterval){
				doSE();
				elasttimedrawn=System.currentTimeMillis();
			}
		}	
		if(cached==null)
			throw new NullPointerException("No bitmap is returned!");
		return cached;
	}
	
	/**
	 * Check if the object is being touched <br/>
	 * The object is not considered been touched if <br/>
	 * Object is hidden and Object is moving
	 * @param X
	 * @param Y
	 * @return
	 */
	public int isTouched(int X, int Y){
		if(hidden)
			return -1;
		if(seInEffect)
			return -1;
		if(sizeNpos.contains(X, Y))
			return actioncode;
		return -1;
	}

	private void doSE(){
		sNpInSE.offset(SEoffset.x, SEoffset.y);
		switch(SEType){
		case 1:	
			seInEffect=!sNpInSE.contains(sizeNpos.left, sizeNpos.top);
			break;
		case 0:
			seInEffect=!sNpInSE.contains(sizeNpos.right, sizeNpos.top);
			break;
		case 2:
			seInEffect=!sNpInSE.contains(sizeNpos.left, sizeNpos.bottom);
			break;
		}
			
	}
	
	private void prepareXmotive(int SrcX){
		if(seInEffect)
			return;
		seInEffect=true;
		if(SrcX % seDivider!=0)
			SrcX-=(SrcX%seDivider);
		sNpInSE.set(sizeNpos);
		sNpInSE.offsetTo(SrcX, sizeNpos.top);
		SEoffset.set(-(SrcX-sizeNpos.left)/seDivider, 0);
	}

	private void prepareYmotive(int SrcY){
		if(seInEffect)
			return;
		seInEffect=true;
		if(SrcY % seDivider!=0)
			SrcY-=(SrcY%seDivider);
		sNpInSE.set(sizeNpos);
		sNpInSE.offsetTo(sizeNpos.left, SrcY);
		SEoffset.set(0, -(SrcY-sizeNpos.top)/seDivider);
	}
	
	public void DropDown(int SrcY){	//G -> UG
		if(SrcY>sizeNpos.top)
			throw new IllegalArgumentException("The Y asix to drop from must be smaller than Y!");
		SEType=2;
		prepareYmotive(SrcY);
	}
	
	public void PushUp(int SrcY){	//G -> 1F
		if(SrcY<sizeNpos.top)
			throw new IllegalArgumentException("The Y asix to rise from must be greater than Y!");
		SEType=1;
		prepareYmotive(SrcY);
	}
	
	public void SlideLeft(int SrcX){  //<----
		if(SrcX<sizeNpos.left)
			throw new IllegalArgumentException("The X asix to pull from must be greater than X!");
		SEType=1;
		this.prepareXmotive(SrcX);
	}
	
	public void SlideRight(int SrcX){	//---->
		if(SrcX>sizeNpos.left)
			throw new IllegalArgumentException("The X asix to pull from must be smaller than X!");
		SEType=0;
		this.prepareXmotive(SrcX);
	}
}
