package com.eboncorvin.tttp.Abstracts;

import com.eboncorvin.tttp.Misc;

import java.util.ArrayList;
import java.util.Random;

public class AbsCardPool{
	// Represent a deck of poker card, also provides function to draw cards and shuffle deck.
	
	//Field
	private ArrayList<Integer> cards=new ArrayList<Integer>();
	//A ArrayList uses to save cards which exists in the card pool
	
	//Constructor
	public AbsCardPool(){
		//Create a new card pool
		for(int i=1;i<=52;i++){
			cards.add(i);
		}
		//Shuffle the card pool 
		ArrayList<Integer> newcard=new ArrayList<Integer>();
		
		Random r=new Random();		
		for(int i=0;i<3;i++){
			while(cards.size()>0){
				newcard.add(cards.remove(r.nextInt(cards.size())));
			}
			cards.addAll(newcard);
			newcard.clear();
			Misc.output(i+"th shuffling");
		}
	}
	
	public AbsCardPool(byte[] poolArray){
		for(int i:poolArray){
			cards.add(i);
		}
	}

	public byte[] getByteArray(){
		byte[] data=new byte[cards.size()];
		int i=0;
		while(i<cards.size())
			data[i]=cards.get(i++).byteValue();
		return data;
	}

	

	//Method

	//A ArrayList uses to save cards which exists in the card pool
	
	public ArrayList<Integer> getCards() {
		return cards;
	}

	public int[] drawCard(int Number){
		//Draw a number of cards and return them in a integer array
		if(Number > cards.size())
			return new int[5];
		int[] returncard=new int[Number];
		for(int i=0;i<Number;i++){
			returncard[i]=cards.remove(0);
		}
		Misc.output("Card in the cardpool: "+cards.size());
		return returncard;
	}

	public int getRemaining(){
		//Return the number of card remaining in the card pool
		return cards.size();
	}

	/**
	 * Peek at the card at the specified position
	 * @param position
	 * @return
	 */
	public int getCard(int position){
		//Return the card in the specified position
		if(position<cards.size()){
			return cards.get(position);
		} else {
			return 0;//53=no card at that position
		}

	}
}

