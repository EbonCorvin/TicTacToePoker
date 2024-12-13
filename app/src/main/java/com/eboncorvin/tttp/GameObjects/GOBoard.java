package com.eboncorvin.tttp.GameObjects;

import com.eboncorvin.tttp.Abstracts.AbsCardPlacer;
import com.eboncorvin.tttp.Abstracts.AbsHands;
import com.eboncorvin.tttp.Abstracts.AbstractLayer;
import com.eboncorvin.tttp.Engine.Setting;
import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.R;

import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

public class GOBoard extends GameObject {

	//private static Bitmap tttboard=;
	private AbsCardPlacer acd;
	private Bitmap cache;
	private Canvas c;

	private int playercolor=(Setting.getSetting().Color)?0:1;
	private int oppcolor=(!Setting.getSetting().Color)?0:1;
	
	private Rect r=this.getOriginSP();
	
	public GOBoard(int ActionCode) {
		super(170, 90, 300, 300, ActionCode);
		acd=AbstractLayer.layer.getPlacer();
		cache=Misc.getUnscaledBmp(300, 300, Bitmap.Config.ARGB_8888);
		c=new Canvas(cache);
	}
	
	@Override
	public Bitmap frameUpdate(){
		if(acd.isDeskChanged()){
			Misc.output(Arrays.toString(acd.getBriefState()));
			cache.eraseColor(Color.TRANSPARENT);
			c.drawBitmap(Misc.getBitmap(R.drawable.tttboard, 0), 0, 0, null);
			int count=0;
			for(AbsHands[] a:acd.getTheWholeCardslot()){
				if(a!=null){ 
					int coin=0;
					if(a[0].getSide())
						coin=playercolor*5;
					else
						coin=oppcolor*5;
					c.drawBitmap(ImgPremake.getCardMark(coin+a[0].getCardCount()-1)
							, (99*(count % 3))+(27),(99*(count / 3))+(22) , null);
				}
				count++;
			}
			count=0;
			for(byte a:acd.getCoin()){
				if(a!='\0'){ 
					c.drawBitmap(Misc.getBitmap(R.drawable.coin, (a=='o')?playercolor:oppcolor)
							, (99*(count % 3))+(50),(99*(count / 3))+(35) , null);
				}
				count++;
			}
			acd.setDeskChanged(false);
		}
		return cache;
	}

	public int getCell(int X, int Y){
		return (X-r.left)/(r.width()/3)+(Y-(r.top))/(r.height()/3)*3;
	}
}
