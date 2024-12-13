package com.eboncorvin.tttp.Abstracts;

import com.eboncorvin.tttp.Misc;

import java.util.Arrays;

public class AbsHands{
	public static AbsHands Empty=new AbsHands(new int[5],false);
	private static int[] pairstat=new int[9];
	private int[] hands;
	private int rank=0, score=0, cardCount;
	private boolean side;
	
	public AbsHands(int[] CardArray, boolean Side){
		if(CardArray==null)
			throw new NullPointerException("The array given pointed to NULL!");
		/*if(CardArray.length!=5)
			throw new IllegalArgumentException("The length of the input array is not vaild!");
		 int count=0;
		 for(int i:CardArray){
			 if(i!=0)
				 count++;
			 else
				 break;
		 }*/
		 hands=CardArray;
		 //System.arraycopy(CardArray, 0, hands, 0, count);
		 Misc.sortIntArray(hands,true);
		 this.side=Side;
		 this.cardCount=hands.length;
		 computeScore(this);
	}
	
	public AbsHands(byte[] data){
		side=(data[0]==1);
		Misc.output(side);
		int count=0;
		for(int i=1;i<=5;i++){
			if(data[i]==0)
				break;
			count=i;
		}
		hands=new int[count];
		for(int i=0;i<count;i++){
			hands[i]=data[i+1];
		}
		cardCount=count;
		computeScore(this);
	}

	/**
	 * Get a byte array which represent the cards it has.
	 * @return
	 */
	public byte[] getByteArray(){
		byte[] data=new byte[6];
		data[0]=(byte) (side?0x1:0x0);
		int i=1;
		for(int c:hands)
			data[i++]=(byte) c;
		return data;
	}
	
	/**
	 * If High Card, set the score to the highest card in the hand <br/>
	 * If Flush, set the score to the suit of the card <br/> 
	 * If Other, sum the number of the score. <br/>
	 * @param h
	 */
	private static void getScore(AbsHands h){
		int[] cardArray=h.getHands();
		if(cardArray.length==0)
			return;
		switch(h.rank){
		case 0:
			int[] pcn=Misc.sortIntArray(getPureCardNumber(cardArray), true);
			h.score=pcn[pcn.length-1];
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 6:
			int[] a=AbsHands.getPureCardNumber(cardArray);
			int sum=0;
			for(int i:a)
				sum+=i;
			h.score=sum;
			Misc.output("Computed Score: "+sum);
			break;
		case 5:
			int score=cardArray[0]/13;
			if(cardArray[0]%13==0)
				score--;
			h.score=score;
			break;
		}
	}

	public int getRank() {
		return rank;
	}

	public int[] getHands() {
		return hands;
	}

	public int getCardCount() {
		return cardCount;
	}

	public boolean getSide() {
		return side;
	}

	public static int[] getPairstat() {
		int[] x=pairstat;
		pairstat=new int[9];
		return x;
	}
	
	public static int[] getCardList(AbsHands h){
		return h.hands;
	}
	
	public static int[] getPureCardNumber(int[] IntArray){
		int[] newint=new int[IntArray.length];
		int count=0;
		for(int i:IntArray){
			if(i==0)
				newint[count++]=0;
			else
				newint[count++]=(i%13==0)?13:i%13;
		}
		Misc.output(Arrays.toString(newint));
		//Misc.SortIntArray(newint, true);
		return newint;
	}
	
	public static int[] getPureCardSuit(int[] IntArray){
		int[] newint=new int[IntArray.length];
		int count=0;
		for(int i:IntArray){
			if(i==0)
				break;		
			newint[count]=i/13;
			if(i%13==0){
				newint[count]--;
			}
			count++;
		}
		return newint;
	}
	
	private static boolean checkIfSame(int[] array){
		int[] tmp=new int[array.length];
		Arrays.fill(tmp, array[0]);
		return (Arrays.equals(tmp, array));
	}
	
	/**
	 * Compute the score and rank of the input Hands, and fill to the object <br/>
	 * Rank is the type of a hands, from 0 (High Card) to 7 (Four of a kind)
	 * @param h
	 */
	public static void computeScore(AbsHands h){
		int[] cardArray=h.hands;
		int[] cardNum=getPureCardNumber(cardArray);
		int[] cardSuit=getPureCardSuit(cardArray);

		int Rank=0;
		if(cardArray.length==2){	//Pair
			if(cardNum[0]==cardNum[1]){
				Rank=1;
			}
		}
		
		if(cardArray.length==4){	//2Pairs
			int[] chk2p=cardNum;
			Misc.sortIntArray(chk2p, true);
			if(chk2p[0]-chk2p[1]==0 && chk2p[2]-chk2p[3]==0){
				Rank=2;
			}
		}
		
		if(cardArray.length==3){ //3kind
			if(checkIfSame(cardNum)){
				Rank=3;
				cardArray=new int[1];
			}
		}
		
		if(cardArray.length==5){ //Straight
			int count=0;
			int[] pcn=Misc.sortIntArray(cardNum, true);
			for(int i:pcn){
				if(i!=pcn[0]+count)
					break;
				count++;
			}
			if(count==5){
				Rank=4;
			}
		}
		
		if(cardArray.length==5){	//Flush
			if(checkIfSame(cardSuit)){
				Rank+=5;	//Straight (4) + Flush (5) = 0 (Royal Flush)
				cardArray=new int[1];
			}
		}
		
		if(cardArray.length==5){	//Full House
			int[] chkfh=Misc.sortIntArray(cardNum, true);
			/*int count=0;	//count how many different number of card the hand has
			int count2=0;
			int curNum=-1;
			for(int i=0;i<5;i++){
				if(chkfh[i]!=curNum && )
			}*/
			int sum=0;
			for(int i:chkfh){
				sum+=i;
			}
			if(sum==(chkfh[0]*3+chkfh[4]*2) || sum==(chkfh[0]*2+chkfh[4]*3)){
				Rank=6;
				cardArray=new int[1];
			}
		}
		
		if(cardArray.length==4){		//Four of a kind
			if(checkIfSame(cardNum)){
				Rank=7;
				cardArray=new int[1];
			}
		}
		
		h.rank=Rank;
		getScore(h);
		if(h.side)
			pairstat[Rank==9?8:Rank]++;	//Since pairstat doesn't have index 9, change 9 (Royal Flush) to 8
	}
	
	/**
	 * Compare 2 hands. <br/>
	 * Compare the rank first, (if same rank) then the score.
	 * @param A
	 * @param B
	 * @return The higher AbsHands
	 */
	public static AbsHands CompareScore(AbsHands A, AbsHands B){
		Misc.output("Card To Place: Rank="+A.rank+" Score="+A.score);
		Misc.output("Card In Slot: Rank="+B.rank+" Score="+B.score);
		if(A.rank==B.rank){
			/*if(A.rank==0 || A.rank==5){
				int[] arrayA=Misc.SortIntArray(getPureCardNumber(A.hands), false);
				int[] arrayB=Misc.SortIntArray(getPureCardNumber(B.hands), false);
				int i=0;
				while(true){
					if(arrayA.length==i && arrayB.length==i){
						if(A.score>B.score)
							return A;
						else
							return B; //xxxx
					}
					if(arrayA.length==i)
						return B;
					if(arrayB.length==i)
						return A;
					if(arrayA[i]>arrayB[i])
						return A;
					if(arrayB[i]>arrayA[i])
						return B;
					i++;
				}
			}*/
			if(A.score>B.score)
				return A;
			else
				return B;
		}
		if(A.rank>B.rank)
			return A;
		else
			return B;
	}
}
