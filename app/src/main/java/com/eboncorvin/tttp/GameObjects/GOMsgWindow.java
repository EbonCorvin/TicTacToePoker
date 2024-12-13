package com.eboncorvin.tttp.GameObjects;


import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.R;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.SparseArray;
/**
 * Default actioncode : 234<br/>
 * Insert / in the message to insert a new line
 * @author Sunny
 *
 */ 
public class GOMsgWindow extends GameObject {
	
	private static SparseArray<Bitmap> windows=new SparseArray<Bitmap>();
	private static SparseArray<Rect> rects=new SparseArray<Rect>();
	private static Bitmap[] dialogButton;
	private static Paint pt;
	private static Paint framept;
	private static Resources res;
	private static Bitmap windowBg;
	private static int charwidth;
	private Rect okbtnpos=new Rect();
	private Rect cancelbtnpos=new Rect();
	private ButtonCallback curCallback;
	private DialogStyle curWinStyle;
	private int curMsgID=-1;
	private int xInWorld;
	private int yInWorld;	
	
	private ButtonCallback defaultCallback=new ButtonCallback(){
		@Override
		public void callbackAction(int MessageID, MsgButton button) {
			// TODO Auto-generated method stub
			GOMsgWindow.this.setHidden(true);
		}
	};
	
	public GOMsgWindow() {
		super(0, 0, 100, 100, 234);
		// TODO Auto-generated constructor stub
		windowBg=Misc.getBitmap(R.drawable.mw_bg, 0);
		windows.put(-1, windowBg);
		rects.put(-1, new Rect());
		this.setHidden(true);
	}

	public void handleTouch(int X, int Y){
		//X=(int) ((X*O.scaleW)-xInWorld);
		//Y=(int) ((Y*O.scaleH)-yInWorld);
		X=(int) (X-xInWorld);
		Y=(int) (Y-yInWorld);		
		if(curWinStyle==DialogStyle.NoButton)	
			return;
		else {
			if(okbtnpos.contains(X, Y)){
				this.setHidden(true);
				curCallback.callbackAction(curMsgID, MsgButton.OK);
			} else if(curWinStyle==DialogStyle.OKCancel && cancelbtnpos.contains(X, Y)){
				this.setHidden(true);	
				curCallback.callbackAction(curMsgID, MsgButton.Cancel);
			}
		}
	}
	
	public static void init(Resources res){
		GOMsgWindow.res=res;
		pt=new Paint();
		pt.setColor(Color.WHITE);
		pt.setTextSize(32);
		pt.setTextAlign(Align.CENTER);
		pt.setAntiAlias(true);
		//pt.setTypeface(Typeface.SERIF);
		framept=new Paint(pt);
		framept.setColor(Color.BLACK);
		framept.setStyle(Paint.Style.STROKE);
		framept.setStrokeWidth(2);
		charwidth=(res.getConfiguration().locale.getDisplayLanguage().equals("Chinese"))?32:16;
		dialogButton=Misc.getObject(R.drawable.gamebtn);
		
		exitCleanup();
	}
	
	public static void exitCleanup(){
		for(int i=0;i<windows.size();i++){
			windows.valueAt(i).recycle();
		}
		rects.clear();
		windows.clear();
		Misc.output("All made windows are unloaded!");
	}
	
	private void calculateBtnPos(int messageID, DialogStyle style){
		Rect r=rects.get(messageID);
		calculateBtnPos(r.width(), r.height(), style);
	}
	
	private void calculateBtnPos(int width, int height, DialogStyle style){
		if(style==DialogStyle.OKonly){
			okbtnpos.set(width/2-45, height-50, width/2-45+90, height-50+45);
		} else if(style==DialogStyle.OKCancel){
			okbtnpos.set(width/2-100, height-50, width/2-100+90, height-50+45);
			cancelbtnpos.set(width/2, height-50, width/2+90, height-50+45);
		}
	}
	
	public void setMsg(int messageID, DialogStyle style, ButtonCallback callback){
		Misc.output(windows.indexOfKey(messageID));		
		 if(windows.indexOfKey(messageID)<0){
			  try {
				String msg=res.getString(messageID);
				Bitmap b=makeMsg(msg, style);
				windows.put(messageID, b);
				Rect r=new Rect();
				r.top=(480-b.getHeight())/2;	//800x480 is the base resolution of the game
				r.left=(800-b.getWidth())/2;	//The engine will adjust its pos and size
				r.right=r.left+b.getWidth();	//when drawing.
				r.bottom=r.top+b.getHeight();
				rects.put(messageID, r);
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				Misc.output("String not found");
				return;
			}
		 }
		 calculateBtnPos(messageID, style);
		 Rect r=rects.get(messageID);
		 curCallback=(callback==null)?defaultCallback:callback;
		 curWinStyle=style;
		 curMsgID=messageID;
		 this.setSizeNpos(r);
		 xInWorld=r.left;
		 yInWorld=r.top;
		 this.setHidden(false);
	}
	
	/**
	 * Display a messagebox with only a OK button provided
	 * @param messageID
	 */
	public void setMsg(int messageID){
		setMsg(messageID, DialogStyle.OKonly, null);
	}
	
	public Bitmap makeMsg(String msg, DialogStyle style){
		String[] s=msg.split("/");
		int maxlen=0;
		for(String str:s){
			if(str.length()>maxlen)
				maxlen=str.length();
		}
		int width=maxlen*charwidth+40;
		int height=s.length*32+40;
		if(style!=DialogStyle.NoButton)
			height+=50;
		Bitmap result=Misc.getUnscaledBmp(width, height, Config.ARGB_8888);
		Canvas canvas=new Canvas(result) ;
		canvas.drawARGB(154, 0, 0, 0);
		canvas.drawRect(0, 0, result.getWidth(),result.getHeight(), framept);
		int i=1;
		int textLeft=result.getWidth()/2;
		for(String str:s){
			canvas.drawText(str, textLeft, 10+(i++)*32, pt);
			//canvas.drawText(str, textLeft, 10+(i++)*32, framept);			
		}
		calculateBtnPos(width, height, style);
		if(style!=DialogStyle.NoButton){
			canvas.drawBitmap(dialogButton[1], null, okbtnpos, null);
			if(style==DialogStyle.OKCancel){
				canvas.drawBitmap(dialogButton[2], null, cancelbtnpos, null);
			}
		}
		return result;
	}

	@Override
	public Bitmap frameUpdate() {
		// TODO Auto-generated method stub
		return windows.get(curMsgID);
	}
	
	public interface ButtonCallback{
		/**
		 * Call when a button in the window is clicked<br/>
		 * No need to close the window, it will close itself
		 * @param messageID The message that cause the event
		 * @param button The button user clicked in response to the message
		 */
		public void callbackAction(int messageID, MsgButton button);
	}
	
	public enum DialogStyle{
		OKonly, OKCancel, NoButton
	}
	
	public enum MsgButton{
		OK, Cancel
	}

}
