package com.eboncorvin.tttp.GameObjects;


import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.R;
import android.graphics.*;

public class ImgPremake {
	private static Bitmap[] card;
	private static Bitmap[] cardMark;
	public static Paint ptr = new Paint();
	public static Paint ptb;
	
	public static void initVar(){	
		ptr.setColor(Color.RED);
		ptr.setTextSize(20f);
		ptr.setTextAlign(Paint.Align.CENTER);
		ptr.setAntiAlias(true);
		ptb=new Paint(ptr);
		ptb.setColor(Color.BLACK);
	}
	
	public static Bitmap getCard(int num){
		if(num>52 || num<1)
			return card[0];
		return card[num];
	}
	
	public static Bitmap getCardMark(int num){
		if(num>=10 || num<0)
			return cardMark[10];
		return cardMark[num];
	}
	
	public static Bitmap[] getCardMark(){
		return cardMark;
	}
	
	/**
	 * Create the poker cards.
	 */
	public static void makeCard(){
		card=new Bitmap[53];
		Bitmap cardbody=Misc.getBitmap(R.drawable.cardbody, 0);
		Bitmap[] cardsuit=Misc.getObject(R.drawable.cardsuit);
		Canvas c=new Canvas();
		Paint p;
		//Error preventer
		card[0]=Misc.getUnscaledBmp(70, 96, Bitmap.Config.ARGB_8888);
		c.setBitmap(card[0]);
		c.drawBitmap(cardbody, 0, 0, null);
		c.drawText("Error!", 26, 45, ptr);
		
		for(int i=0;i<4;i++){
			if(i%2==0)
				p=ptr;
			else
				p=ptb;
			for(int j=1;j<=13;j++){
				card[i*13+j]=Misc.getUnscaledBmp(70, 96, Bitmap.Config.ARGB_8888);
				c.setBitmap(card[i*13+j]);
				c.drawBitmap(cardbody, 0, 0, null);
				c.drawBitmap(cardsuit[i], 0, 0, null);
				String cardnumber;
				switch(j+1){
					case 11:
						cardnumber="J";
						break;
					case 12:
						cardnumber="Q";
						break;
					case 13:
						cardnumber="K";
						break;
					case 14:
						cardnumber="A";
						break;
					default:
						cardnumber=String.valueOf(j+1);			
				}
				c.drawText(cardnumber, 15, 45, p);
			}
		}
	}
	
	/**
	 * Make card marks, which are placed on the ttt board to indicate the side and number of the cards
	 */
	public static void makeCardMark(){
		cardMark=new Bitmap[11];
		Bitmap[] xo=Misc.getObject(R.drawable.xomark);
		Bitmap[] cc=Misc.getObject(R.drawable.cardmark);
		Canvas c=new Canvas();			
		//Error preventer
		cardMark[10]=Misc.getUnscaledBmp(55, 75, Bitmap.Config.ARGB_8888);
		c.setBitmap(cardMark[10]);
		c.drawBitmap(cc[0], 0, 0, null);
		c.drawText("Error!", 26, 45, ptr);
		
		for(int j=0;j<=1;j++){
			for(int i=0;i<5;i++){
				cardMark[i+j*5]=Misc.getUnscaledBmp(55, 75, Bitmap.Config.ARGB_8888);
				c.setBitmap(cardMark[i+j*5]);
				c.drawBitmap(cc[i], 0, 0, null);
				c.drawBitmap(xo[j], 13, 38, null);
			}
		}
	}
	
	public static void unloadImage(){
		if(card==null)
			return;
		for(int i=0;i<53;i++){
			card[i].recycle();
			card[i]=null;
		}
		card=null;
		for(int i=0;i<10;i++){
			cardMark[i].recycle();
			cardMark[i]=null; 
		}
		cardMark=null;
		Misc.output("All premade images are unloaded!");
	}
}