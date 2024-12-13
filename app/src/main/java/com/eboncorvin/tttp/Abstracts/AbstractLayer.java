package com.eboncorvin.tttp.Abstracts;

import com.eboncorvin.tttp.Msger;
import com.eboncorvin.tttp.Engine.GameSave;
import com.eboncorvin.tttp.Engine.StatManager;

public class AbstractLayer {
	
	public static AbstractLayer layer;
	
	//A layer between abstract game world and visual presentation of the game
	private AbsCardPool cardpool;
	private AbsHandCard playerhc;
	private AbsHandCard opphc;
	private AbsCardPlacer placer;
	private AbsHands card2place;
	private Msger msg;

	public AbstractLayer(int gamemode){		//Create a new AL with all stuff generated randomly
		this(new AbsCardPool());
	}
	
	//Create a pre-defined AbstractLayer for the tutorial
	public AbstractLayer(AbsCardPool cardpool) {
		this(cardpool, new AbsHandCard(cardpool, true), new AbsHandCard(cardpool, false)
		, new AbsCardPlacer());
	}
	
	public AbstractLayer(AbsCardPool cardpool, AbsHandCard playerhc,
			AbsHandCard opphc, AbsCardPlacer placer){//, AbsHands card2place) {
		this.cardpool = cardpool;
		this.playerhc = playerhc;
		this.opphc = opphc;
		this.placer = placer;
		//this.card2place = card2place;
		layer=this;
	}

	public AbstractLayer(GameSave gs){
		this(gs.getCardpool(), gs.getPlayerhc(), gs.getOpphc()
				, gs.getPlacer());//, gs.getCard2place());
		layer=this;
	}

	public Msger getMsg() {
		return msg;
	}

	public void setMsg(Msger msg) {
		this.msg = msg;
	}

	public AbsCardPool getCardpool() {
		return cardpool;
	}

	public AbsHandCard getPlayerhc() {
		if(playerhc==null)
			playerhc=new AbsHandCard(cardpool,true);
		return playerhc;
	}

	public AbsHandCard getOpphc() {
		if(opphc==null)
			opphc=new AbsHandCard(cardpool,false);
		return opphc;
	}
	
	public void prepareHC(){
		if(opphc==null && playerhc==null){
			playerhc=new AbsHandCard(cardpool, true);
			opphc=new AbsHandCard(cardpool, false);
		}
	}

	/**
	 * Help a side draw a specified number of cards from the card pool
	 * @param Number
	 * @param side True for Player, False for Opponents
	 */
	public void drawCard(int Number, boolean side){
		if(side)
			playerhc.drawCard(Number);
		else
			opphc.drawCard(Number);
	}

	public void swapHC(){	//For hotseat mode
		AbsHandCard.swapHandCards(playerhc, opphc);
	}

	public AbsHands getCard2place() {
		return card2place;
	}

	public void setCard2place(AbsHands card2place) {
		this.card2place = card2place;
	}

	public AbsCardPlacer getPlacer() {
		return placer;
	}

	public AbsHands[] getCardSlot(int slot){
		return placer.getCardSlot(slot);
	}

	public byte getCoinSlot(int position){
		return placer.getCoin()[position];
	}

	public int getSelectedCard(){
		return playerhc.getSelectedNo();
	}

	/**
	 * Place the cards to the tic-tac-toe board, and return value to indicate the status <br/>
	 * 1 = Cards placed in an empty slot <br/>
	 * 3 = The slot is either placed the side's card or a coin is placed in the slot <br/>
	 * 10 / 11 / 20 / 21 = Someone won <br/>
	 * 2 = Cards placed in a slot that has other side's cards, compare the cards.
	 * @param Hands
	 * @param slot
	 * @return status code
	 */
	public int placeCard(int position){
		return placer.placeCard(getCard2place(),position);
	}
	
	public boolean compareCard(int position){
		//return AbsHands.CompareScore(getCardSlot(position), getCard2place()).getSide();
		return (getCoinSlot(position)=='o')?true:false;
	}

	public static void writeStat(){
		StatManager.writePairStat(AbsHands.getPairstat());
	}
}
