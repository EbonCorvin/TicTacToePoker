package com.eboncorvin.tttp.GameObjects;


import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.R;
import com.eboncorvin.tttp.Engine.StatManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class GOStat extends GameObject {
	public static Paint pItem=new Paint();
	public static Paint pHeader=new Paint();
	public static Paint pCenterHeader=new Paint();
	private static String[] pairname=new String[9];
	private static String txt_hands;
	private static String txt_time;
	private static String txt_viewhand;
	private static String txt_viewwinlose;
	private static String txt_win;
	private static String txt_lose;
	private static String[] gamemode;
	private boolean view=true;
	
	private Bitmap cached, handstat, winlosestat;
	
	public GOStat() {
		super(129, 105, 566, 330, 60);
		this.setHidden(true);
		getResult();
	}
	
	public static String getPairname(int rank) {
		if(rank<0 || rank>9)
			throw new IllegalArgumentException("There is no such hand rank!");
		return pairname[rank==9?8:rank];
	}

	public static void initPairName(Resources r){
		pItem.setARGB(255, 255, 255, 255);
		pItem.setTextSize(25);
		pItem.setAntiAlias(true);
		pHeader.set(pItem);
		pHeader.setTextSize(34);
		pCenterHeader.set(pHeader);
		pCenterHeader.setTextAlign(Align.CENTER);
		txt_hands=r.getString(R.string.inf_hands);
		txt_time=r.getString(R.string.inf_time);
		txt_viewhand=r.getString(R.string.inf_switchhands);
		txt_viewwinlose=r.getString(R.string.inf_switchwinlose);
		txt_win=r.getString(R.string.inf_win);
		txt_lose=r.getString(R.string.inf_lose);
		
		gamemode=new String[3];
		for(int i=0;i<9;i++){
			try {
				pairname[i]=r.getString(R.string.class.getField("inf_p"+i).getInt(null));
				if(i<4)
					gamemode[i-1]=r.getString(R.string.class.getField("inf_m"+(i-1)).getInt(null));
			} catch(Exception e) {
				
			}
		}
	}
	public void switchView(){
		cached=view?handstat:winlosestat;
		view=!view;
	}

	private void getResult(){
		handstat=Misc.getUnscaledBmp(566, 330, Bitmap.Config.ALPHA_8);
		Canvas c=new Canvas(handstat);
		int[] stat=StatManager.readPairStat();
		c.drawText(txt_hands, 0, 34, pHeader);
		c.drawText(txt_time, 180, 34, pHeader);
		c.drawText("%", 320, 34, pHeader);
		c.drawText(txt_viewwinlose, 283, 300, pCenterHeader);
		int sum=0;
		for(int i:stat)
			sum+=i;
		for(int i=1;i<9;i++){
			c.drawText(pairname[i], 0, i*25+50, pItem);
			c.drawText(String.valueOf(stat[i]), 180, i*25+50, pItem);
			String temp=String.valueOf(Math.floor((double)stat[i]/sum*10000)/100);
			if(temp.equals("NaN"))
				temp="0";
			c.drawText(temp+"%", 320, i*25+50, pItem);
		}
				
		winlosestat=Misc.getUnscaledBmp(566, 330, Bitmap.Config.ALPHA_8);
		c.setBitmap(winlosestat);
		int[][] stat2=StatManager.readWinLose();
		c.drawText(txt_win, 250, 34, pHeader);
		c.drawText(txt_lose, 360, 34, pHeader);
		c.drawText(txt_viewhand, 283, 300, pCenterHeader);
		for(int i=0;i<3;i++){
			c.drawText(gamemode[i], 0, i*25+80, pItem);
			c.drawText(String.valueOf(stat2[i][0]), 250, i*25+80, pItem);
			c.drawText(String.valueOf(stat2[i][1]), 360, i*25+80, pItem);
		}
		cached=winlosestat;
		this.setHidden(false);
	}
	
	@Override
	public Bitmap frameUpdate(){
		return cached;
	}

}
