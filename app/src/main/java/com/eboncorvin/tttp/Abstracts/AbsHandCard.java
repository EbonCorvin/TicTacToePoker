package com.eboncorvin.tttp.Abstracts;

import com.eboncorvin.tttp.Misc;

import java.io.ByteArrayOutputStream;

public class AbsHandCard{
	//Represent the hand cards
	private AbsCardPool cp;
	private int[] cardList=new int[18];
	private int selected=0;
	private int cardCount=0;
	private boolean cardChanged=true;
	private boolean side;
	private boolean sortByNo=true;
	
	/*public AbsHandCard(AbsCardPool acp, int[] cardArray, boolean side){
		if(cardArray.length>18)
			throw new IllegalArgumentException("Too many card in the cardArray!");
		System.arraycopy(cardArray, 0, cardList, 0, cardArray.length);
		this.side=side;
		cp=acp;
	}*/

	public AbsHandCard(AbsCardPool acp, boolean Side){
		//Draw 9 cards in default
		cp=acp;
		drawCard(9);
		this.side=Side;
		//debug();
	}
	
	public AbsHandCard(byte[] data, AbsCardPool acp){
		cp=acp;
		side=(data[0]==1);
		cardCount=data[1];
		for(int i=0;i<cardCount;i++){
			cardList[i]=data[i+2];
		}
	}
	
	public byte[] getByteArray(){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		baos.write(side?1:0);
		baos.write(cardCount);
		for(int n:cardList){
			if(n==0)
				break;
			baos.write(n);
		}
		//Misc.output(Arrays.toString(baos.toByteArray()));
		return baos.toByteArray();
	}



	public void setCardSort(){
		sortByNo=!sortByNo;
		sortCard();
		cardChanged=true;
	}

	public int getCardCount() {
		return cardCount;
	}

	public int[] getCardList() {
		cardChanged=false;
		return cardList;
	}

	public boolean isCardChanged() {
		return cardChanged;
	}

	public void setCardChanged() {
		cardChanged = true;
	}

	public int getSelectedNo() {
		return selected;
	}

	public boolean selectCard(int Position){
		if(selected==5 && cardList[Position]>0)
			return false;
		if(cardList[Position]==0)
			return false;
		cardList[Position]=-cardList[Position];
		selected=selected+((cardList[Position]>0)?-1:1);
		cardChanged=true; 
		return true;
	}

	private void unselectCard(){
		for(int i=0;i<18;i++){
			cardList[i]=Math.abs(cardList[i]);
		}
		selected=0;
	}

	public AbsHands commitCard(){
		int count=0,count2=0;
		int[] cardhand=new int[selected];
		for(int i:cardList){
			 if(i<0){
				  cardhand[count2++]=-i;
				  cardList[count]=0;
				  selected--;
			 }
			 count++;
		}
		AbsHands a=new AbsHands(cardhand, side);
		cardChanged=true;
		//selected=0;
		cardCount-=count2;
		sortCard();
		return a;
	}

	public void debug(){
		//cardList=new int[]{	1,14,	27,	40};
		cardList=new int[]{	1,	14,	27,	2,	15,
							28, 3, 16, 29, 
							4, 17, 30, 5, 18,31,6,19,32};
		cardCount=cardList.length;
	}
	
	private void sortCard(){
		unselectCard();
		if(sortByNo)
			sortCardByNumber();
		else
			cardList=Misc.sortIntArray(cardList, false);		
	}

	private void sortCardByNumber(){
		int[] tmp=AbsHands.getPureCardNumber(cardList);
		int[] result=new int[18];
		int count=0;
		for(int i=13;i>0;i--){
			for(int j=0;j<tmp.length;j++){
				int testsubject=Math.abs(tmp[j]);
				if(testsubject==0)
					continue;
				if(testsubject==i)
					result[count++]=cardList[j];
			}
		}
		//Misc.output(Arrays.toString(result));
		cardList=result;
	}

	public void drawCard(int Number){
		System.arraycopy(cp.drawCard(Number), 0, cardList, cardCount, Number);
		cardCount+=Number;
		sortCard();
		cardChanged=true;
	}

	public static void swapHandCards(AbsHandCard a, AbsHandCard b){	
		if(a==null || b==null)	//Design for Hotseat mode
			return;
		int[] temp=a.cardList;
		a.cardList=b.cardList;
		b.cardList=temp;
		int t=a.cardCount;
		a.cardCount=b.cardCount;
		b.cardCount=t;
		a.side=!a.side;
		b.side=!b.side;
		a.cardChanged=true;
		b.cardChanged=true;
	}
}
