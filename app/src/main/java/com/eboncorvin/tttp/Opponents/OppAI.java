package com.eboncorvin.tttp.Opponents;

import java.util.Arrays;
import java.util.Random;

import android.os.SystemClock;

import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.Msger;
import com.eboncorvin.tttp.R;
import com.eboncorvin.tttp.Msger.MsgCallBack;
import com.eboncorvin.tttp.O.C;
import com.eboncorvin.tttp.Abstracts.AbsHandCard;
import com.eboncorvin.tttp.Abstracts.AbsHands;
import com.eboncorvin.tttp.Abstracts.AbstractLayer;
import com.eboncorvin.tttp.Engine.GameSave;

public class OppAI extends Opponent{
	private static final int TELLMV_SHOW_UNCLOSABLE = 66;
	private static final int TELLMV_PLACE_CARD = 55;
	//The array make the card placing looks more randomly or "tactically"
	/*private static final int[][] cardPlacxingSequence=new int[][]{
		new int[]{3,1,5,8,6,2,7,0,4},	
		new int[]{6,3,1,8,0,7,2,5,4},
		new int[]{8,5,0,6,2,1,7,3,4},
	};*/
	
	private static final int percent_randomCard=0;	//50%
	private static final int percent_234OfKind=50;	//10% (Actually 50%)
	private static final int percent_fullHouse=60;	//20% (Actually 40%)
	private static final int percent_2pair=80;		//20%

	private AbsHandCard ghc;
	//private int count=0;
	private Msger m;
	private Thread t;
	

	private boolean stillAlive=true;
	//private int sequenceNo=0;

	private Runnable aiThread=new Runnable(){
		
		public void run(){
			while(stillAlive){
				try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {
					System.out.println("Thread waken up");
					if(!stillAlive)
						break;
				}
				if(tl.getCard2place()!=null){
					SystemClock.sleep(1500);
					m.run(TELLMV_PLACE_CARD);
					try {
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException e) {
						System.out.println("result block have been closed");
						if(!stillAlive)
							break;
					}
				}
				m.run(TELLMV_SHOW_UNCLOSABLE);
				selectHands();
				SystemClock.sleep(1500);
				
				tl.setCard2place(ghc.commitCard());
				tellMV(C.hideMessage);
				tellMV(C.playerTurn);
			}
		}	
	};
	
	/**
	 * Since TellMV uses Msger.runNow, which does not go through the thread, <br/>
	 * this Msger is for AI thread to send message to TV
	 */
	private MsgCallBack aiMsger=new MsgCallBack(){
		@Override
		public void doMsg(int StatusCode, Object Attachment) {
			// TODO Auto-generated method stub
			if(StatusCode==TELLMV_PLACE_CARD){		//Time to place card
				tellMV(C.hideMessage);
				tellMV(C.placeCard, selectCardSlot());
				//tellMV();	
				return;
			} 
			if(StatusCode==TELLMV_SHOW_UNCLOSABLE)
				tellMV(C.showUnclosableMsg, R.string.msg_oppselectingcard);
		}
	};
		
	public OppAI(Msger callback) {
		super(callback);
		// TODO Auto-generated constructor stub
		//this.al=al;
		//sequenceNo=(int)Math.floor(Math.random()*cardPlacingSequence.length);
		m=new Msger(aiMsger);
		t=new Thread(aiThread);
		//GameSave gs=new GameSave();
		if(GameSave.isSaveExist()){
			GameSave gs=new GameSave();
			if(gs.fileExist){
				tl=new AbstractLayer(new GameSave());
				//m.run(C.playerTurn);
			} else {
				tellMV(C.showMessage, R.string.msg_filedamaged);
				GameSave.deleteSave();
			}
		}
		ghc=tl.getOpphc();
		//ghc.setCardSort();
		t.start();
	}

	@Override
	public void initOpponent() {
		// TODO Auto-generated method stub
	}

	public void selectCard() {
		tellMV(C.hideMessage);
		t.interrupt();
	}

	/**
	 * Scan the hand card and find all possible poker hand pair. <br/>
	 * index - Description<br/>
	 * 0		unusable cards<br/>
	 * 	-0			leftover cards of 2/3/4 of kinds	<br/>
	 * 	-1			leftover cards of straigth<br/>
	 * 1		NOT USED <br/>
	 * 2~4		2/3/4 of kind combo <br/>
	 * 5		Straigth<br/>
	 * @return An array which contains the found poker hand pair <br/>
	 */
	private int[][][] getUsableHands(){
		int cardCount=ghc.getCardCount();
		int[] cardlist=ghc.getCardList();
		int[][][] result=new int[7][][];
		
		result[0]=new int[2][];		//Initize the array that for storing leftover cards
		
		int[][] unusable=new int[2][];
		unusable[0]=new int[cardCount];
		unusable[1]=new int[cardCount];
		
		for(int i=0;i<cardCount;i++){
			unusable[0][i]=i;
		}
		unusable[1]=Arrays.copyOf(unusable[0], cardCount);
		
		int[] purecardno=AbsHands.getPureCardNumber(cardlist);
		
		//This array copy the pure number of the cards of the hand card
		int[] existchk=Arrays.copyOf(purecardno, cardCount);
		Arrays.sort(existchk);
		
		for(int i=4;i>=2;i--){	//i=? of kind, j=2 To A, k=card in the hand 
			//For example, in 9 cards, there would only 2 4-of-kind, 3 3-of-kind and 4 4-of-kind
			result[i]=new int[cardCount/i][];
			//The number of pairs found in this X-of-a-kind searching
			int count=0;
			for(int j=1;j<=13;j++){
				//If the number of card does not exist in the hand card, doesn't waste time to find pairs
				if(Arrays.binarySearch(existchk, j)<0){
					continue;
				}
				//Count how many card of same number is found 
				int count2=0;
				int[] tempa=new int[i];
				for(int k=0;k<cardlist.length;k++){	//Scan the handcard array
					//In the hand card array, 0 = no any card in this position
					if(purecardno[k]==0){
						continue;
					}
					if(purecardno[k]==j){
						//If the number of cards is already equal to the X-of-a-kind currently searching... 
						//For example, the program is currently searching for 2-of-a-kind, but there are 3 cards have the same number
						if(count2==i){
							//I only want the AI to keep to highest rank of X-of-a-kind, so discard the cards and break.
							count2=0;
							tempa=null;
							break;
						}
						//Save the position of the card to the array
						tempa[count2++]=k;
					}
				}
				//If the number of cards found equal to the number currently searching, add to the final result array
				if(count2==i){
					Misc.output(Arrays.toString(tempa));
					result[i][count++]=tempa;
					for(int s:tempa){
						unusable[0][s]=-1;
					}
				}
			}
			if(count==0){
				System.out.println("No hands found for "+i+" of Kind");
				result[i]=null;
			}
		}
		//Only find Straight pairs if there are at least 5 cards.
		if(cardCount>=5){
			int[][] straight=new int[cardCount/5][];
			int count2=0;
			//i is the start number of the straight hand
			//i>4 because it will not be enough cards for a straight pair if the number start from 1~4
			//4,3,2,1 - 3,2,1 - 2,1 - 1
			for(int i=13;i>4;i--){
				//If the card of the start number is not found, skip this number
				if(Arrays.binarySearch(existchk, i)<0){
					continue;
				}
				int count=0;
				int[] card=new int[5];
				//Find the remaining 4 cards needed to make a straight pair
				for(int k=0;k<=4;k++){
					int target=i-k;
					//If next number is not found, then it is not possible to make a straight anymore
					if(Arrays.binarySearch(existchk, target)<0)
						break;
					for(int j=0;j<purecardno.length;j++){
						if(purecardno[j]==target){
							card[count++]=j;
							break;
						}
					}
				}
				Misc.output(count);
				if(count==5){
					for(int c:card){
						unusable[1][c]=-1;
						purecardno[c]=-1;
					}
					straight[count2++]=card;
					//Misc.output(Arrays.toString(card));
					//Misc.output(Arrays.toString(unusable[1]));
				}
			}
			//Only add the straight array to the final result array if at least 1 pair is found
			if(count2!=0)
				result[5]=straight;
		}
		//Copy to leftover cards (Which cannot be used for making any hand) to the final result array
		//unusable[0] is leftover of X-of-a-kind, unusable[1] is leftover of straight
		for(int c=0;c<2;c++){
			unusable[c]=Misc.sortIntArray(unusable[c], false);
			int count=0;
			//-1 = Cards that are used, and since the array is sorted, the loop can be broken here
			for(int i:unusable[c]){
				if(i==-1)
					break;
				count++;
			}
			Misc.output(Arrays.toString(unusable[c]) + " Count4: "+count);
			result[0][c]=new int[count];
			System.arraycopy(unusable[c], 0, result[0][c], 0, count);		
	
		}	
		return result;
	}

	private void selectHands(){
		int[][][] possibleHands=getUsableHands();
		Misc.output("Start outputting possible hands");
		//The following code is for debug purpose, can be removed later for better performance
		for(int[][] i:possibleHands){
			if(i==null){
				Misc.output("index is null");
				continue;
			}
			for(int[] j:i){
				if(j==null){
					Misc.output("item is null");
					continue;
				}
				Misc.output(Arrays.toString(j));
			}
		}
		//Get the real number a kind of hand the program found (Since not all index of the hands array are filled with data)
		//See the description of getUsableHands for the meaning of 2 to 5
		int[] handcount=new int[6];
		int handsum=0;
		int[] select=null;
		for(int i=2;i<=5;i++){
			if(possibleHands[i]==null)
				handcount[i]=0;
			else {
				for(int[] ia:possibleHands[i]){
					if(ia!=null)
						handcount[i]++;	
				}
			}
			handsum+=handcount[i];
		}
		//Random decide the hand AI should select
		int decision=(int) Math.floor(Math.random()*100+1);
		//If there is a pair of straight, choose the straight pair immediately without thinking
		if(handcount[5]!=0){
			select=possibleHands[5][0];
			decision=-1;
		}
		
		System.out.println("Decision of AI: "+decision);
		
		//Pre-process of the decision, to prevent error
		if(decision<=percent_234OfKind && decision!=-1){	//Random Card
			if(possibleHands[0][0].length<2){	//At least reserve 2 unusable card
				System.out.println("Change of mind coz no enough unusable card (only 1 card)");
				//Change to select 2pair
				decision=percent_2pair;
			}
		}
		
		if(decision>=percent_234OfKind){
			//if AI decide not to select random card, do the following checking
			//If no any possible hand found, change to select random card
			if(handsum==0){
				System.out.println("Change of mind coz no hand found");
				decision=percent_randomCard;
			} else {
				if(decision>=percent_2pair){
					if(handcount[2]<2){
						System.out.println("Change of mind coz no enough pair");
						decision=percent_fullHouse;
					}
				}
				//Full house are made from pair and 3 of kinds
				if(decision>=percent_fullHouse && decision<=percent_2pair){	//Try to make a Full House
					if(handcount[2]==0 || handcount[3]==0){
						System.out.println("Change of mind coz no enough 2 and 3 of kind");
						decision=percent_234OfKind;
					}
				}
			}
		}
		
		//Process of the decision		
		if(decision>=percent_2pair){			//2 pair
			select=new int[4];
			System.arraycopy(possibleHands[2][0], 0, select, 0, 2);
			System.arraycopy(possibleHands[2][1], 0, select, 2, 2);
		} else if(decision>=percent_fullHouse){		//Full House
			select=new int[5];
			System.arraycopy(possibleHands[3][0], 0, select, 0, 3);
			System.arraycopy(possibleHands[2][0], 0, select, 3, 2);
		} else if(decision>=percent_234OfKind){		//pair and 3 or 4 of kind
			select=possibleHands[(handcount[4]!=0)?4:(handcount[3]!=0)?3:2][0];
		} else if(decision>=percent_randomCard){	//Draw 2~3 card randomly
			int a=(int) Math.floor(Math.random()*3+2);
			Misc.output("Number of card to select: "+a);
			if(possibleHands[0][0].length<a)
				a=possibleHands[0][0].length;
			for(int c=0;c<a;c++)
				ghc.selectCard(possibleHands[0][0][c]);
		}
		if(select!=null){
			for(int i:select)
				ghc.selectCard(i);
		}
	}

	@Override
	public void placeCard(AbsHands cards) {
		// TODO Auto-generated method stub
		tl.setCard2place(cards);
		tellMV(C.isPlacing);
		//tellMV(C.displayCardMark, cards.getSide(), cards.getCardCount());
		tellMV(C.showUnclosableMsg, R.string.msg_oppplacingcard);
		t.interrupt();
	}
	
	/**
	 * The retard AI to select card slot <br/>
	 * a. Find available card slots (Empty slot) <br/>
	 * b. Find available card slots (Where AI's cards are placed) <br/>
	 * c.  Find AI's card slots where number is greater than player's cards <br/><br/>
	 * 1. If no a & no c - Random select from b <br/>
	 * 2. If have c - Place at c <br/>
	 * 2a. Otherwise, Random select from a <br/>
	 * @return
	 */
	private int selectCardSlot(){
		//Available finding part
		byte[] stat=tl.getPlacer().getBriefState();
		int c2pCount=tl.getCard2place().getCardCount();
		Misc.output(c2pCount);
		Misc.output(tl.getCard2place().getSide());
		Misc.output(Arrays.toString(stat));
		int moreCardPlace=-1;
		int empty=0;
		int[] emptyplace=new int[9];
		int aicard=0;
		int[] aicardplace=new int[9];
		
		//Find available card slots of the board
		for(int i=0;i<9;i++){
			byte b=stat[i];
			if(b==-1 || b==127){
				//Completely empty slot
				if(b==-1){
					emptyplace[empty++]=i;
				}
				continue;
			}
			//Slots where Opponent's cards are placed
			if(b>=9){
				aicardplace[aicard++]=i;
				//Get the number of cards by performing AND to the card slot number
				if((b & 7)>=c2pCount){
					moreCardPlace=i;
				}
			}
		}
		
		//Decision marking part
		//If no empty slot on the board, then random select a slot where AI's cards are placed
		if(moreCardPlace==-1 && empty==0){
			return aicardplace[new Random().nextInt(aicard)];
		}
		//If it is found that there is a slot where no of AI's cards is greater than player's cards, place there.
		if(moreCardPlace!=-1)
			return moreCardPlace;
		else //Otherwise, random select an empty space
			return emptyplace[new Random().nextInt(empty)];
	}

	@Override
	public void exitCleanup() {
		// TODO Auto-generated method stub
		stillAlive=false;
		t.interrupt();
	}

	@Override
	public void playerTurn() {
		// TODO Auto-generated method stub
	}

	@Override
	public void oppTurn() {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void drawCard() {
		// TODO Auto-generated method stub
	}

}