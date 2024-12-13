package com.eboncorvin.tttp.Opponents;

import java.util.Arrays;

import com.eboncorvin.tttp.Abstracts.AbsCardPool;
import com.eboncorvin.tttp.Abstracts.AbsHandCard;
import com.eboncorvin.tttp.Abstracts.AbsHands;
import com.eboncorvin.tttp.Abstracts.AbstractLayer;
import com.eboncorvin.tttp.Msger;
import com.eboncorvin.tttp.R;
import com.eboncorvin.tttp.GameObjects.GOMask;
import com.eboncorvin.tttp.O.C;

public class OppTutorial extends Opponent {

	private AbsCardPool pool=new AbsCardPool(new byte[]{
			42, 4, 30, 44, 6, 19, 10, 23, 36,	//Player's start hand card
			15, 7, 33, 21, 9, 22, 48, 24, 11,	//Opp's start hand card	
			1 , 2,  3,  7, 8,  9
			
	});
	
	private static int[][] playerMustSelectCards=new int[][]{
		new int[]{36,23,10}
	};
	
	private GOMask mask=new GOMask(R.drawable.tttp_tutorial,0,0);
	private int step=0;
	private AbsHandCard handcard;
	
	private SlidePlayer slider;
	
	public OppTutorial(Msger mgr) {
		super(mgr);
		// TODO Auto-generated constructor stub
		this.tl=new AbstractLayer(pool);
		tl.setMsg(mgr);
	}
	
	private class SlidePlayer implements Runnable{
		private Thread t;
		private boolean pause,isStop;
		
		public SlidePlayer(){
			t=new Thread(this);
			t.start();
		}
		
		public void resume(){
			pause=false;
			t.interrupt();
		}
		
		public void pause(){
			pause=true;
		}
		
		public void stop(){
			isStop=true;
			t.interrupt();
		}
		
		private void checkAutoNext(){
			switch(step){
			case 1:
			case 3:
			case 5:
				pause();
				break;
			}
			TutorialStep();
		}
		
		@Override
		public void run() {
			while(!isStop){
				try {				
					Thread.sleep((pause)?Long.MAX_VALUE:2000);
				} catch (InterruptedException e) {}
				mask.gotoFrame(++step);
				checkAutoNext();					
			}
		}
	}
	
	private void TutorialStep(){
		switch(step){
		case 3:
			tellMV(C.placeCard,4);			
			//tellMV(C.playerTurn);			
			break;
		}
	}

	@Override
	public void initOpponent() {
		// TODO Auto-generated method stub
		this.tellMV(C.showGameObject, mask);
		mask.gotoFrame(0);
		slider=new SlidePlayer();
		handcard=tl.getOpphc();
	}
	
	private AbsHands checkPlayerCards(AbsHands cards){
		int[] mustSelect=playerMustSelectCards[0];
		int[] cardArray=cards.getHands();
		for(int card:mustSelect){
			if(Arrays.binarySearch(cardArray, card)<0){
				int cardInHand=Arrays.binarySearch(tl.getPlayerhc().getCardList(),card);
				if(cardInHand>=0){
					tl.getPlayerhc().selectCard(cardInHand);
					tl.getPlayerhc().commitCard();
				}
				
			}
		}
		return cards;
	}

	@Override
	public void placeCard(AbsHands cards) {
		// TODO Auto-generated method stub
		tl.setCard2place(checkPlayerCards(cards));
		tellMV(C.isPlacing);
		tellMV(C.displayCardMark, cards.getSide(), cards.getCardCount());
		//tellMV(C.showUnclosableMsg, R.string.msg_oppplacingcard);
		if(step==1){
			slider.resume();
		}
	}

	@Override
	public void selectCard() {
		// TODO Auto-generated method stub
		//tellMV(C.showMessage, R.string.msg_oppplacingcard);
		handcard.selectCard(1);
		tl.setCard2place(handcard.commitCard());
		tellMV(C.playerTurn);
	}

	@Override
	public void drawCard() {
		// TODO Auto-generated method stub
		slider.resume();
	}

	@Override
	public void playerTurn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitCleanup() {
		// TODO Auto-generated method stub
		slider.stop();
	}

	@Override
	public void oppTurn() {
		// TODO Auto-generated method stub
		
	}

}
