package com.eboncorvin.tttp.Abstracts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.O.C;

public class AbsCardPlacer{
	private static final int PLACECARD_PLACED_OK = 1;
	private static final int PLACECARD_INVALID = 3;
	private AbsHands[][] cardslot = new AbsHands[9][];
	private byte[] coin = new byte[9];
	private boolean changed=true;
	private short coinO=0, coinX=0;
	private int gametype=-1;
	
	public AbsCardPlacer(){
		//This constructor has nothing to do
	}
	
	public AbsCardPlacer(byte[] data){
		ByteBuffer bb=ByteBuffer.wrap(data);
		//Misc.output(Arrays.toString(data));
		bb.get(coin);
		//Misc.output(Arrays.toString(coin));
		while(true){
			int slotn=bb.get();
			Misc.output(slotn + " " + (slotn==0xAA));
			if(slotn==(byte) 0xAA)
				break;
			cardslot[slotn]=new AbsHands[2];
			byte[] card=new byte[6];
			bb.get(card);
			Misc.output(Arrays.toString(card));			
			cardslot[slotn][0]=new AbsHands(card);
			if(bb.get()==(byte) 0xFF){
				byte[] card2=new byte[6];
				bb.get(card2);
				cardslot[slotn][1]=new AbsHands(card2);				
			}
		}
		for(byte b:coin){
			if((char)b=='o')
				coinO++;
			else if((char)b=='x')
				coinX++;
		}
		Misc.output("O="+coinO+" X="+coinX);
	}
	
	/**
	 * Get a brief status of the card placer, written for AI to make decision
	 * @return A byte array with 9 slots<br/>
	 * -1 = empty <br/>
	 * 127 = has coin <br/>
	 * 1~5 = player side <br/>
	 * 9~13 = opponent side
	 */
	public byte[] getBriefState(){
		byte[] status=new byte[9];
		int i=0;
		for(AbsHands[] ah:cardslot){
			if(ah==null){
				status[i++]=-1;
			} else {
				if(ah[1]!=null){
					status[i++]=127;
				} else {
					status[i++]=(byte) ((ah[0].getSide()?0:8) | ah[0].getCardCount());
				}
			}
		}
		return status;
	}

	public byte[] getByteArray(){
		/*
		 * Format: coin(9 bytes)-cardslot(slotnum,place,card sequence);
		 * Ex: 0 0 0 0 0 0 0 0 0  0 0 52 51 50 49 0 1 48 47 46 0 0 0....
		 */
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try {
			baos.write(coin);
			for(int i=0;i<9;i++){
				if(cardslot[i]==null)
					continue;
				baos.write(i);
				baos.write(cardslot[i][0].getByteArray());
				if(cardslot[i][1]!=null){
					baos.write(0xFF);	
					baos.write(cardslot[i][1].getByteArray());
				} else 
					baos.write(0xEE);					
			}
			baos.write(0xAA);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return baos.toByteArray();
	}

	public boolean isDeskChanged() {
		return changed;
	}

	public void setDeskChanged(boolean deskChanged) {
		changed = deskChanged;
	}

	public AbsHands[] getCardSlot(int slot){
		return cardslot[slot];
	}

	public AbsHands[][] getTheWholeCardslot() {
		return cardslot;
	}

	public byte[] getCoin() {
		return coin;
	}

	public void setGametype(int gametype) {
		//this.gametype = gametype;
	}

	/**
	 * Place the cards to the tic-tac-toe board, and return value to indicate the status <br/>
	 * 1 = Cards placed in an empty slot <br/>
	 * 3 = The slot is either placed the side's card or a coin is placed in the slot <br/>
	 * 10 / 11 / 20 / 21 = Someone won <br/>
	 * 2 = No one win by now
	 * @param Hands
	 * @param slot
	 * @return status code
	 */
	public int placeCard(AbsHands Hands, int slot){
		if(slot<0 || slot>=9)
			return PLACECARD_INVALID;
		if(cardslot[slot]==null){				//Place in empty slot
			cardslot[slot]=new AbsHands[2];
			cardslot[slot][0]=Hands;
			setDeskChanged(true);
			return PLACECARD_PLACED_OK;
		}
		if(coin[slot]!='\0')
			return PLACECARD_INVALID;
		if(cardslot[slot][0].getSide()!=Hands.getSide()){	//Compare
			cardslot[slot][1]=Hands;
			AbsHands Winner=AbsHands.CompareScore(Hands, cardslot[slot][0]);
			coin[slot]=(byte) ((Winner.getSide())?'o':'x');
			setDeskChanged(true);
			if(Winner.getSide())
				coinO++;
			else
				coinX++;
			return checkWinner();
		}
		return 3;	// If either the slot is not empty nor the side is the same with hands
	}
	
	/**
	 * Check the status of the ttt board to find the winner <br/>
	 * 10 / 11 = O / X won by earning 5 coins <br/>
	 * 20 / 21 = O / X won by earning 3 coins in a line <br/>
	 * 2       = No one win by now
	 * @return
	 */
	private int checkWinner(){
		if(gametype!=C.CoinMode){
			if(coinO==5)
				return 10;
			if(coinX==5)
				return 11;
		}
		if(gametype!=C.TTTMode){
			byte winner='s';
			//Check Row
			for(int i=0;i<=6;i+=3){
				byte c=coin[i];
				if(c=='\0')
					continue;
				if(coin[i+1]==c && coin[i+2]==c)
					winner=c;
			}
			//Check Col
			for(int i=0;i<=2;i++){
				byte c=coin[i];
				if(c=='\0')
					continue;
				if(coin[i+3]==c && coin[i+6]==c)
					winner=c;
			}
			//Check Slash
			byte c=coin[4];
			if((coin[2]==c && coin[6]==c) || (coin[0]==c && coin[8]==c))
				winner=c;
			if(winner!='s' && winner!='\0')
				return ((winner=='o')?20:21);
		}
		return 2;
	}
}
