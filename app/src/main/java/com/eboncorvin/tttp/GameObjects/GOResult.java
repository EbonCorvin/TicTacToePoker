package com.eboncorvin.tttp.GameObjects;


import com.eboncorvin.tttp.Abstracts.AbsHands;
import com.eboncorvin.tttp.Abstracts.AbstractLayer;
import com.eboncorvin.tttp.Engine.Setting;
import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class GOResult extends GameObject implements Runnable{
	private Bitmap resultb=Misc.getBitmap(R.drawable.comparesult, 0);
	private Bitmap resultgameover=Misc.getBitmap(R.drawable.comparesult, 1);
	private Bitmap[] cachedresult=new Bitmap[9];
	private Bitmap cached;
	/**
	 * Indicate the status of the board <br/>
	 * x = Two sides of cards are drawn <br/>
	 * o = One side of cards is drawn <br/>
	 * \0= No card is drawn
	 */
	private char[] status=new char[9]; //x=two hands is drawn, o=one hands is drawn, '\0'=nothing
	
	private int playercolor=(Setting.getSetting().Color)?0:1;
	private int oppcolor=(!Setting.getSetting().Color)?0:1;
	
	private AbstractLayer al;
	private Canvas c=new Canvas();
	private Thread t;
	private boolean end=false;
	private int pos;
	private boolean drawwhole;

	public GOResult(AbstractLayer layer){
		super(162, 140, 475, 230,  45);
		//resultb=Misc.getBitmap(R.drawable.comparesult, 0);
		al=layer;
		this.setHidden(true);
		t=new Thread(this);
		t.start();
	}
	
	public void showWinner(boolean winside){
		cached=Misc.getUnscaledBmp(475, 230, Bitmap.Config.ARGB_8888);
		cached.eraseColor(Color.TRANSPARENT);
		//resultb=Misc.getBitmap(R.drawable.comparesult, 1);
		c.setBitmap(cached);
		c.drawBitmap(resultgameover, 0, 0, null);
		c.drawBitmap(Misc.getBitmap(R.drawable.coin, (winside)?playercolor:oppcolor),60 ,85 , null);
		//this.setActioncode(99);
		this.setHidden(false);
	}
	
	public void showResult(int Position, boolean drawWhole){
		pos=Position;
		drawwhole=drawWhole;
		t.interrupt();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!end){
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				Misc.output("Be waken up");
				if(end)
					break;
			}
			makeResult(pos, drawwhole);
		}
	}
	
	@Override
	public Bitmap frameUpdate(){
		return cached;	
	}

	private void makeResult(int Position, boolean drawWhole){
		//\0 = No cache found, o = The first hand is drawn, x = 2nd hand is draw, use cache
		if(status[Position]!='x'){
			drawThing(Position);			
		}
		show(Position);		
	}
	
	private void show(int pos){
		cached=cachedresult[pos];
		this.setHidden(false);
	}

	public void freeupMemory(){
		end=true;
		t.interrupt();
		resultb.recycle();
		resultgameover.recycle();
		for(int i=0;i<9;i++){
			if(cachedresult[i]==null)
				continue;
			cachedresult[i].recycle();
		}
		System.gc();
	}
	
	private void drawThing(int position){
		if(cachedresult[position]==null)
			cachedresult[position]=Misc.getUnscaledBmp(475, 230, Bitmap.Config.ARGB_8888);
		c.setBitmap(cachedresult[position]);
		//If nothing is drawn before, draw the window, and the first cards which was placed 
		if(status[position]=='\0'){
			c.drawBitmap(resultb, 0, 0, null);
			c.drawBitmap(Misc.makeHandsImg(al.getCardSlot(position)[0]), 16, 50, null);
			c.drawText(GOStat.getPairname(al.getCardSlot(position)[0].getRank())
					, 20, 175, GOStat.pItem);
		}
		//Furthermore, if the second hand is placed in the slot
		if(al.getCardSlot(position)[1]!=null){
			//Draw the second hand to the window
			AbsHands sec=al.getCardSlot(position)[1];
			c.drawBitmap(Misc.makeHandsImg(sec), 265, 50, null);
			c.drawText(GOStat.getPairname(sec.getRank())
					, 265, 175, GOStat.pItem);
			boolean winner=al.compareCard(position);
			//Second is greater = 325, First is greate = 90
			c.drawBitmap(Misc.getBitmap(R.drawable.coin, winner?playercolor:oppcolor),
					(winner==sec.getSide())?325:49 ,90 , null);
			//x = 2 sides of cards are drawn
			status[position]='x';
		}else
			status[position]='o';
		Misc.output("Draw complete");
	}
}
