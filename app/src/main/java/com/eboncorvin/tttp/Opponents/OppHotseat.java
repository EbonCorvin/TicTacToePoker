package com.eboncorvin.tttp.Opponents;

import com.eboncorvin.tttp.Abstracts.AbsHands;
import com.eboncorvin.tttp.Msger;
import com.eboncorvin.tttp.O.C;

public class OppHotseat extends Opponent{
	//private AbsHandCard  playerhc;
	
	public OppHotseat(Msger callback) {
		super(callback);
		// TODO Auto-generated constructor stub
		//playerhc=tl.getOpphc();
	}

	//private AbsHandCard  playerhc;
	
	private void handlePassPhone(){
		tellMV(C.isPlacing);
		tellMV(C.hideMessage);
		tl.swapHC();
		tellMV(C.swapCoin);
	}

	@Override
	public void initOpponent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void placeCard(AbsHands cards) {
		// TODO Auto-generated method stub
		//tl.playerhand.setEnclose(true);
		tellMV(C.isPlacing);
		tellMV(C.changePhaseIndicator, false);
		tellMV(C.showPassPhone);
		tl.setCard2place(cards);
		tellMV(C.displayCardMark, cards.getSide(), cards.getCardCount());
		//cp.displayCardmark(cards.getSide(), cards.getCardCount()-1);
	}
	
	@Override
	public void selectCard() {
		// TODO Auto-generated method stub
		tellMV(C.changePhaseIndicator, true);
	}

	@Override
	public void exitCleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerTurn() {	//ah=The card opp passed to player
		// TODO Auto-generated method stub
		oppTurn();	//They are acutally doing the same thing
	}

	@Override
	public void oppTurn() {	//ah=The card player passed to opp to let him place
		// TODO Auto-generated method stub
		this.handlePassPhone();
	}

	@Override
	public void drawCard() {
		// TODO Auto-generated method stub
	}

}
