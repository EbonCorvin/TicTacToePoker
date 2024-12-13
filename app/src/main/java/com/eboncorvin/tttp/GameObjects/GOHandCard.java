package com.eboncorvin.tttp.GameObjects;


import com.eboncorvin.tttp.Abstracts.AbsHandCard;
import com.eboncorvin.tttp.Abstracts.AbsHands;
import com.eboncorvin.tttp.Engine.Setting;
import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class GOHandCard extends GameObject {
	private AbsHandCard hc;
	private boolean Side=true;	//true=Play, false=Opponent
	private boolean Enclose=false;
	private boolean forceupdate=false;
	private Bitmap cached;
	private int cardwidth=Setting.getSetting().size?30:35;
	private float touchwidth=cardwidth;//(cardwidth/O.scaleW);
	
	public GOHandCard(int ActionCode, boolean Side, AbsHandCard acp){
		super(Side?80:140, Side?405:-16, 650, 101, ActionCode);
		hc=acp;
		this.Side=Side;
	}
	
	public AbsHandCard getHc() {
		return hc;
	}

	public void setHc(AbsHandCard hc) {
		this.hc = hc;
		forceupdate=true;
	}

	public void setEnclose(boolean enclose) {
		Enclose = enclose;
		forceupdate=true;
	}

	public boolean handleSelection(int X, int Y){
		X-=this.getOriginSP().left;
		int no=(int) (X/touchwidth);

		if(no>=hc.getCardCount())
			no=hc.getCardCount()-1;
		return hc.selectCard(no);
	}
	
	/*public void HandleSxelection(int Position){
		hc.selectCard(Position);
	}
	*/
	public AbsHands CommitCard(){
		return hc.commitCard();
	}
	
	public void changeCardSort(){
		hc.setCardSort();
	}
	
	@Override
	public Bitmap frameUpdate(){
		if(hc.isCardChanged() || forceupdate){
			Bitmap bmp=Misc.getUnscaledBmp(650, 101, Bitmap.Config.ARGB_8888);
			bmp.setDensity(Bitmap.DENSITY_NONE);
			Canvas c=new Canvas(bmp);
			int count=0;
			if(!Side){
				c.save();
				c.rotate(180f, 290f, 50f);
			}
			if(!Enclose){
				for(int i:hc.getCardList()){
					if(i==0)
						break;
					if(i<0)
						c.drawBitmap(ImgPremake.getCard(-i), count*cardwidth, 0, ImgPremake.ptr);
					else
						c.drawBitmap(ImgPremake.getCard(i), count*cardwidth, 15, ImgPremake.ptr);
					count++;
				}
			}else{
				hc.getCardList();
				Bitmap cardback=Misc.getBitmap(R.drawable.cardbody, 1);
				for(int i=0;i<hc.getCardCount();i++){
					c.drawBitmap(cardback, count*cardwidth, 5, ImgPremake.ptr);
					count++;
				}
			}
			if(!Side)
				c.restore();
			cached=bmp;
			//hc.setCardChanged();
			forceupdate=false;
		}
		return cached;
	}

}
