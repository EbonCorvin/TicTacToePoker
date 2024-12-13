package com.eboncorvin.tttp.Opponents;

import com.eboncorvin.tttp.Abstracts.AbsHands;
import com.eboncorvin.tttp.Abstracts.AbstractLayer;
import com.eboncorvin.tttp.Msger;

/**
 * This class is the abstract representation of the player's opponent
 *
 */
public abstract class Opponent{
	protected AbstractLayer tl;
	protected Msger mgr;
	
	public Opponent(Msger mgr){
		//ghc=new GOHandCard(130 , false, al.getOpphc();
		//ghc.setEnclose(true);
		tl=new AbstractLayer(2);
		this.mgr=mgr;
		tl.setMsg(this.mgr);
	}
	
	public AbstractLayer getAL(){
		return tl;
	}
	
	/**
	 * Tell the main view what to do without attaching anything
	 */
	public void tellMV(int msgid){
		mgr.runNow(msgid, null);
	}
	
	 /**
	 * Tell the main view what to do with something attached
	 */
	public void tellMV(int msgid, Object attachment){
		mgr.runNow(msgid, attachment);
	}
	
	 /**
	 * Tell the main view what to do with a boat load of object<br/>
	 * Main View can retrieve them by treating the attachment as an Object[]
	 */	
	protected void tellMV(int msgid, Object...attachment){
		mgr.runNow(msgid, attachment);
	}

	/**
	 * Called when TVMain is loaded. Do some initialize here.
	 */
	public abstract void initOpponent();

	/**
	 * Called when the Opponent is told to place Player's card <br/>
	 * 1. Tell MV cards are being placed (c.isPlacing)<br/>
	 * 2. Tell MV the cell to place cards is decided (c.placeCards[cell No])
	 * @param cards The cards to place
	 */
	public abstract void placeCard(AbsHands cards);

	/**
	 * Called when the Opponent is told to select card (Typically after placeCard()) <br/>
	 * 1. Put the cards to tl (tl.setCards2place(AbsHands)) <br/>
	 * 2. Tell MV this is player's turn (c.playerTurn)
	 */
	public abstract void selectCard();

	/**
	 * Called when the Opponent is told that it had drawn cards <br/>
	 * Actually this can be seems as a notification that the player has placed the Opponent's card
	 */
	public abstract void drawCard();

	/**
	 * Call when the Opponent is told that the game is over and it should clean up
	 */
	public abstract void exitCleanup();

	/**
	 * Called when it is player's turn<br/>
	 * It is called when the Opponent tell MV it is player's turn <br/>
	 * Typically right after Opponent has selected cards for player to place
	 */
	public abstract void playerTurn();

	/**
	 * Called when the Opponent is told that it is opponent's (Player) turn <br/>
	 * Actually this method is for Hot-seat mode only<br/>
	 * It is not useful by now
	 */
	public abstract void oppTurn();
}
