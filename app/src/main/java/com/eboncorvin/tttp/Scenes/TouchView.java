package com.eboncorvin.tttp.Scenes;

import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.O;
import com.eboncorvin.tttp.R;
import com.eboncorvin.tttp.Engine.Setting;
import com.eboncorvin.tttp.GameObjects.GameObject;
import android.content.Context;
import android.graphics.*;
import android.view.*;

public abstract class TouchView extends SurfaceView
		implements SurfaceHolder.Callback, Runnable, View.OnTouchListener, View.OnKeyListener{
	
	private static Rect bgrect;
	private boolean changingSecne=false, IsDestroying=false;
	private Bitmap background;	
	private long changestart=0;
	private Setting gs;
	private GameObjectAdapter objlist;
	
	public TouchView(Context context, int BgFrame){
		super(context);
		bgrect=new Rect(0, 0, (int)(800/O.scaleW), (int)(480/O.scaleH));
		// TODO Auto-generated constructor stub
		this.setOnTouchListener(this);
		this.getHolder().addCallback(this);
		this.setOnKeyListener(this);
		if(BgFrame==5)
			background=Misc.getBitmap(R.drawable.ingamebg, 0);
		else
			background=Misc.getBitmap(R.drawable.gametable, BgFrame);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.requestFocus();
		gs=Setting.getSetting();
	}

	public Setting getGameSetting() {
		return gs;
	}
	
	public boolean isChangingSecne() {
		return changingSecne;
	}
	
	public void setAdapter(GameObjectAdapter adapter){
		this.objlist=adapter;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!IsDestroying){
			redraw();
			//if(utime<50l){
				try {
					Thread.sleep(50l);
				} catch (InterruptedException e) {

				}
			//}
		}
		Misc.output("Drawing loop stopped");
	}
	
	private void redraw(){
		//long timedrawn=System.currentTimeMillis();
		//long drawtime=0;
		
		Canvas c=getHolder().lockCanvas(null);
		if(c!=null){	//!=null means the device has lock the surface successfully
			
			c.drawBitmap(background, null, bgrect, null);
			for(GameObject o : objlist){
				try{
					if(o.isHidden())
						continue;
					c.drawBitmap(o.frameUpdate(), null, o.getDrawPosition(), null);
				} catch(Exception e){
					Misc.output("Drawing Error: "+e.toString());
					e.printStackTrace();	
				}				
			}
			//drawtime=System.currentTimeMillis()-timedrawn;
			//c.drawText(String.valueOf(drawtime), 100, 100, GOStat.pt);
			getHolder().unlockCanvasAndPost(c);
			this.postInvalidate();
		}
		if(changingSecne){	//We allow only the secne done its change within 1 secord
			if(!objlist.get(0).isIneffect() || System.currentTimeMillis()-changestart>5000){
				changingSecne=false;
				objlist.clear();
				SecneChangedCallBack();
			}
		}
		//return drawtime;
	}

	/**
	 * Create a snapshot of the screen for scene changing
	 * @param direction
	 * @return
	 */
	private Bitmap CreateStaticSnapshot(int direction){
		//Bitmap bm=ImagePremake.SecneChange[direction];
		Bitmap bm=Misc.getUnscaledBmp(800, 480, Bitmap.Config.RGB_565);
		Canvas c=new Canvas(bm);//sadasd
		c.drawBitmap(background,0,0, null);
		for(GameObject o : objlist){
			try{					
				if(o.isHidden())
					continue;
				c.drawBitmap(o.frameUpdate(),  null, o.getOriginSP(), null);
			} catch(Exception e){
				Misc.output("Drawing Error: "+e.getMessage());
			}
		}
		return bm;
	}

	protected void ChangeScene(int direction, int BgID){
		Bitmap bm=CreateStaticSnapshot(direction);
		
		objlist.clear();
		background=Misc.getBitmap(R.drawable.gametable, BgID);
		objlist.add(background,	0,	0,	-1);
		objlist.add(R.drawable.carpet1,	0,	0,	-1);
		objlist.add(bm,			0,	0,	-1);
		//objlist.get(2).setSizeNpos(bgrect);
		switch(direction){
			case 0:
				//objlist.get(0).y=0;
				objlist.get(1).setObjectPos(0, 480);
				objlist.get(2).setObjectPos(0, 960);
				
				objlist.get(0).DropDown(-960);
				objlist.get(1).DropDown(-480);
				objlist.get(2).DropDown(0);
				break;
			case 1:
				//objlist.get(0).y=0;
				objlist.get(1).setObjectPos(0, -480);
				objlist.get(2).setObjectPos(0, -960);
				
				objlist.get(0).PushUp(960);
				objlist.get(1).PushUp(480);
				objlist.get(2).PushUp(0);
				break;
			case 2:
				//objlist.get(0).x=0;
				objlist.get(1).setObjectPos(-800, 0);
				objlist.get(2).setObjectPos(-1600, 0);
				
				objlist.get(0).SlideLeft(1600);
				objlist.get(1).SlideLeft(800);
				objlist.get(2).SlideLeft(0);
				break;
			case 3:
				//objlist.get(0).x=0;
				objlist.get(1).setObjectPos(800, 0);
				objlist.get(2).setObjectPos(1600, 0);
				
				objlist.get(0).SlideRight(-1600);
				objlist.get(1).SlideRight(-800);
				objlist.get(2).SlideRight(0);
				break;
		}
		changingSecne=true;
		changestart=System.currentTimeMillis();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Misc.output("Changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		IsDestroying=false;
		Misc.output("Draw Thread Created");
		new Thread(this).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		IsDestroying=true;
		Misc.output("Stopping Drawing Loop");
	}

	public boolean onKey (View v, int keyCode, KeyEvent event){
		if(changingSecne)
			return true;
		if(event.getAction()==KeyEvent.ACTION_UP){	//Only Handle Key when the key is pressed.
			doKeyAction(keyCode);		
			return true;
		}
		return false;
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		if(IsDestroying)
			return true;
		if(changingSecne)
			return true;
		if(event.getAction()!=MotionEvent.ACTION_UP)	//We process only CLICK event
			return true;
		
		int x=(int) event.getX();
		int y=(int) event.getY();
		int actioncode=-1;
		for(GameObject o:objlist){
			int c=o.isTouched(x, y);
			if(c!=-1)
				actioncode=c;
		}
		if(actioncode==-1)
			return true;
		if(actioncode<128)
			doTouchAction(actioncode);
		if(actioncode>128){
			doTouchAction(actioncode, (int)(x*O.scaleW), (int)(y*O.scaleH));
		}
		return true;
	}
	
	//-----------------------------------------------
	/**
	 * Will be called when the user touch an object with Action Code smaller than 128
	 * @param ActionCode Action Code of the touched object, use to identify the object.
	 */
	protected abstract void doTouchAction(int ActionCode);
	
	/**
	 * Will be called when the user touch an object with Action Code greater than 128 <br/>
	 * The X & Y are the coordination where user touch the screen. <br/>
	 * The values are scaled down, to the positions where the user touch a 800x480 screen.
	 * @param ActionCode Action Code of the touched object, use to identify the object.
	 * @param X	X axis of the touched point 
	 * @param Y Y axis of the touched point 
	 */
	protected abstract void doTouchAction(int ActionCode, int X, int Y);

	/**
	 * Will be called when the user pressed a key.
	 * @param KeyCode The keycode of the key pressed.
	 */
	protected abstract void doKeyAction(int KeyCode);
	
	/**
	 * Will be called after the view finished its secne changing
	 */
	protected abstract void SecneChangedCallBack();
}
