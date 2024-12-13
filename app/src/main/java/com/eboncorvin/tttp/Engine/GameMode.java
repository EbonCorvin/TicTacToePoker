package com.eboncorvin.tttp.Engine;

import com.eboncorvin.tttp.Msger;
import com.eboncorvin.tttp.Opponents.OppAI;
import com.eboncorvin.tttp.Opponents.OppHotseat;
import com.eboncorvin.tttp.Opponents.OppTutorial;
import com.eboncorvin.tttp.Opponents.OppWifi;
import com.eboncorvin.tttp.Opponents.Opponent;

public class GameMode {
	public boolean tutorial;
	public boolean playerGo1st;
	public int oppType;
	
	public GameMode(boolean tutorial, boolean playerGo1st, int oppType) {
		super();
		this.tutorial = tutorial;
		this.playerGo1st = playerGo1st;
		this.oppType = oppType;
	}
	
	public Opponent getOpp(Msger callback){
		switch(oppType){
		case 0:
			return new OppAI(callback);
		case 1:
			return new OppHotseat(callback);
		case 2:
			return OppWifi.getOpp(callback);
		case 9:
			return new OppTutorial(callback);
		}
		return null;
	}
}
