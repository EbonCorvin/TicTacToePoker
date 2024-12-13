package com.eboncorvin.tttp.Scenes;

import java.lang.reflect.Array;

import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.Msger;
import com.eboncorvin.tttp.O;
import com.eboncorvin.tttp.R;
import com.eboncorvin.tttp.Engine.GameMode;
import com.eboncorvin.tttp.Engine.GameSave;
import com.eboncorvin.tttp.Engine.Setting;
import com.eboncorvin.tttp.Engine.SoundSystem;
import com.eboncorvin.tttp.Engine.StatManager;
import com.eboncorvin.tttp.GameObjects.GOBoard;
import com.eboncorvin.tttp.GameObjects.GOHandCard;
import com.eboncorvin.tttp.GameObjects.GOMsgWindow;
import com.eboncorvin.tttp.GameObjects.GOResult;
import com.eboncorvin.tttp.GameObjects.GameObject;
import com.eboncorvin.tttp.GameObjects.ImgPremake;
import com.eboncorvin.tttp.GameObjects.GOMsgWindow.DialogStyle;
import com.eboncorvin.tttp.GameObjects.GOMsgWindow.MsgButton;
import com.eboncorvin.tttp.O.C;
import com.eboncorvin.tttp.O.S;
import com.eboncorvin.tttp.Opponents.OppWifi;
import com.eboncorvin.tttp.Opponents.Opponent;
import com.eboncorvin.tttp.Abstracts.AbsHands;
import com.eboncorvin.tttp.Abstracts.AbstractLayer;
import android.content.Context;
import android.view.KeyEvent;

public class TVMain extends TouchView implements Msger.MsgCallBack{
	
	private SoundSystem ss;
	private AbstractLayer layer;
	
	private GOHandCard playerhand, opponenthand;
	private GOBoard gob;
	private GOResult resultb;
	private GOMsgWindow window=new GOMsgWindow();
	//private GameObject msgblock=new GameObject(R.drawable.msgblock, -1, 0, 200,  140, 24);
	private GameObject steparrow=new GameObject(R.drawable.arrow, 	50, 0, 480,114 ,-1);
	private GameObject cardmark=new GameObject(570, 315, 55, 75, -1);
	//private GameObject gamehelp=new GameObject(R.drawable.gamehelp,	-1,	0,	0,	0, -1);
	private GameObject viewboardbtn=new GameObject(R.drawable.help, 	20,	250,	50);
	private GameObject sortBtn=new GameObject(R.drawable.sort, -1, 0, 125, 365, 53);
	
	private GameObjectAdapter objlist=new GameObjectAdapter();

	private int winner=-1;
	private int opptype;
	private int playercolor=(Setting.getSetting().Color)?0:1;
	private int oppcolor=(!Setting.getSetting().Color)?0:1;
	
	private Opponent opp;
	private boolean turn=true;	//True=Player, False=Opponent
	private boolean placing=false, gameover=false, viewing=false, freeview=false;
	private boolean exiting=false;
	//Trigger of help
	//private boolean selectNplace=false, compare=false, viewboard=false;

	private Msger actHandle;
	
	private GOMsgWindow.ButtonCallback winCallback=new GOMsgWindow.ButtonCallback() {
		@Override
		public void callbackAction(int messageID, MsgButton button) {
			// TODO Auto-generated method stub
			 switch(messageID){
			 case R.string.ask_savegame:
				 if(button==MsgButton.OK)
					 exitCleanUp();
				 break;
			 case R.string.msg_passphone:
				 opp.oppTurn();
				 break;
			 }
		}
	};

	public TVMain(Context context, Msger cth, int gameMode, GameMode gm) {
		super(context, 5);
		super.setAdapter(objlist);
		this.actHandle=cth;
		opp=gm.getOpp(new Msger(this));
		opptype=gm.oppType;
		layer=opp.getAL();
		initElement();
		if(!gm.playerGo1st){
			turn=false;
			opp.selectCard();
		}
	}
	
	private void initElement(){
		ss=SoundSystem.getSoundSystem();
		resultb=new GOResult(layer);
		playerhand=new GOHandCard(129,true, layer.getPlayerhc());
		opponenthand=new GOHandCard(-1,false, layer.getOpphc());
		opponenthand.setEnclose(true);		
		gob=new GOBoard(136);
		objlist.add(gob);
		objlist.add(playerhand);
		objlist.add(opponenthand);
		objlist.add(R.drawable.coin,		-1, playercolor, 20, 415,  	44);
		objlist.add(R.drawable.coin,		-1, oppcolor, 730, 15,  	-1);
		objlist.add(R.drawable.gamebtn,	-1, 0, 20, 350, 12);	
		objlist.add(R.drawable.statustext, 	515,	110,	-1);
		objlist.add(cardmark);
		objlist.add(steparrow);
		objlist.add(viewboardbtn);
		objlist.add(sortBtn);
		//objlist.add(msgblock);
		objlist.add(resultb);
		//objlist.add(gamehelp);
		//*objlist.add(new GOMask());
		objlist.add(window);

		//gamehelp.setHidden(false);
		cardmark.setHidden(true);
		cardmark.setSequence(ImgPremake.getCardMark());
		//msgblock.setHidden(true);
		resultb.setHidden(true);
		opp.initOpponent();
	}

	/**
	 * Called when it is player's turn (Card placing phase)
	 */
	private void playerTurn() {
		// TODO Auto-generated method stub
		AbsHands ah=layer.getCard2place();
		turn=!turn;
		changePhaseIndicator(false);
		displayCardmark(!turn, ah.getCardCount());
		placing=true;
		opp.playerTurn();
		/*if(!selectNplace)
			gamehelp.setHidden(false);*/
	}

	/**
	 * Called when both the Opponent and Player place cards to a cell
	 * @param Position
	 */
	private void placeCard(int Position){
		Misc.output("Turn: "+turn);
		int result=layer.placeCard(Position);
		if(result==3){
			window.setMsg(R.string.msg_slotused);
			//showMessage(3, 25);
			return;
		}
		layer.setCard2place(null);
		if(opptype==2 && turn)
			((OppWifi)opp).notifyCardPlacing(Position);
		ss.PlaySound(S.SelectCard, 0);
		//Result that > 9 can be 10/11/20/21, which means win by 5 coins or by 3 coins in a line
		if(result==2 || result>9)
			displayResult(Position);
		if(result>9){
			winner=result;
			gameover=true;
		}
		if(opptype==2 && !turn)
			return;
		playerhand.setEnclose(false);
		placing=false;
		cardmark.setHidden(true);
		//msgblock.setHidden(true);
		layer.drawCard(2,(opptype==1)?false:!turn);
		/*rdcount++;	//Both side draw 2 cards if a round is finished (Both selected and placed)
		if(rdcount==2){
			layer.addCard(2);
			rdcount=0;
		}*/
		//Close the result window before starting the Opponent's turn
		if(!turn){
			if(!(result==2 || result>9))
				opp.selectCard();
		}else{
			opp.drawCard();
			changePhaseIndicator(true);
			/*if(!selectNplace){
				selectNplace=true;
				gamehelp.setHidden(true);
			}*/
			if(opptype==0)
				GameSave.writeGameSave(layer);
		}
	}

	/**
	 * Called when the player has finished select cards
	 */
	private void finishSelectCard(){
		if(placing)
			return;		
		if(layer.getSelectedCard()==0){
			window.setMsg(R.string.msg_shouldselectcard);
			//showMessage(4, 25);
			return; 
		}
		turn=!turn;
		AbsHands selected=playerhand.CommitCard();
		displayCardmark(turn,selected.getCardCount());
		opp.placeCard(selected);
	}

	/**
	 * Called when the game is over
	 */
	private void gameOver(){
		//Handle an over game here
		Misc.output("Game Finished "+winner);
		if(opptype==0)
			GameSave.deleteSave();
		//int type=(winner>=10)?0:1;
		opponenthand.setEnclose(false);
		resultb.showWinner((winner % 10==0)?true:false);
		StatManager.writeWinLose(opptype, (winner % 10==0));
		steparrow.setObjectPos(-1, 234);
		viewing=!viewing;
		viewboardbtn.goToAndStop((viewing?1:0));
		opp.exitCleanup();
	}

	private void exitCleanUp(){
		if(exiting){
			Misc.output("Already started exiting");
			return;
		}
		exiting=true;
		opp.exitCleanup();
		AbstractLayer.writeStat();
		resultb.freeupMemory();
		actHandle.run(C.showMenuView);
	}

	/**
	 * Change the phase indicator
	 * @param phase - True for select cards, False for place cards
	 */
	private void changePhaseIndicator(boolean phase){	//true=select card, false=place card
		if(phase){
			steparrow.setObjectPos(-1, 114);
			//steparrow.PushUp(0);
		} else{
			steparrow.setObjectPos(-1, 154);
			//steparrow.DropDown(200);
		}
	}

	/**
	 * Display the "Card to place", which indicate the number of cards to be placing
	 * @param side
	 * @param cardno
	 */
	private void displayCardmark(boolean side, int cardno){
		cardmark.goToAndStop((side?playercolor*5:oppcolor*5)+(cardno)-1);
		cardmark.setHidden(false);
	}

	private void displayResult(int Position){
		ss.PlaySound(S.CoinDrop, 0);
		resultb.showResult(Position, true);
		boolean winside=(opptype==1)?turn:true;
		if(layer.compareCard(Position)==winside)
			ss.PlaySound(S.CompareWin ,0);
		else
			ss.PlaySound(S.CompareLose ,0);
	}
	
	/**
	 * This method handles message from Opponents
	 */
	@Override
	public void doMsg(int StatusCode, Object Attachment) {
		// TODO Auto-generated method stub
		switch(StatusCode){
		case C.changePhaseIndicator:
			 this.changePhaseIndicator((Boolean)Attachment);
			 break;
		case C.displayCardMark:
			this.displayCardmark((Boolean)Array.get(Attachment, 0)
					, (Integer)Array.get(Attachment, 1));
			break;
		case C.hideMessage:
			//msgblock.setHidden(true);
			resultb.setHidden(true);
			window.setHidden(true);
			break;
		case C.isNotPlacing:
			this.placing=false;
			break;
		case C.isPlacing:
			this.placing=true;
			break;
		case C.placeCard:
			this.placeCard((Integer)Attachment);
			break;
		case C.playerTurn:
			this.playerTurn();
			break;
		case C.showMessage:
			window.setMsg((Integer)Attachment);
			//this.showMessage((Integer)Attachment, 25);
			break;
		case C.showPassPhone:
			window.setMsg(R.string.msg_passphone, DialogStyle.OKonly
					, winCallback);
			//this.showMessage(, 24);
			playerhand.setEnclose(true);
			break;
		case C.showUnclosableMsg:
			steparrow.setObjectPos(-1, 194);
			//this.showMessage((Integer)Attachment, -1);
			Misc.output(Attachment);
			window.setMsg((Integer) Attachment, GOMsgWindow.DialogStyle.NoButton, null);
			break;
		case C.swapCoin:
			objlist.get(3).goToAndStop(turn?playercolor:oppcolor);
			objlist.get(4).goToAndStop(!turn?playercolor:oppcolor);
			break;
		case C.socketError:
			window.setMsg(R.string.inf_disconnected);
			break;
		case C.showGameObject:
			//The z-index of all object added by msger must be lower than window
			objlist.add(objlist.indexOf(window), (GameObject) Attachment);
			break;
		case C.removeGameObject:
			objlist.remove(Attachment);
			break;
		}
	}

	@Override
	protected void doTouchAction(int ActionCode) {
		// TODO Auto-generated method stub
		if(ActionCode==44){	//Debug button, will be removed when releasing
			opponenthand.setEnclose(false);
			return;
		}
		
		if(ActionCode==24){		//User clicked on "Pass phone" msg
			opp.oppTurn();	//Hotseat only
			return;
		}
		if(ActionCode==45){		//Clicked on comparison result
			Misc.output("Result touched");
			if(viewing){
				resultb.setHidden(true);
		//		gamehelp.setHidden(true); 
				return;
			}
			if(gameover){
				if(!freeview){
					freeview=true;
					gameOver();
				}
				return;
			}
			if(placing ^ !turn){ //if(Placing==true XOR Turn==false)
				opp.selectCard();
			}
			ActionCode=25;
		}
		if(ActionCode==25){		//User clicked on msg that just a warning on game play
			//msgblock.setHidden(true);
			resultb.setHidden(true);
		//	gamehelp.setHidden(true);
			return;
		}
		
		if(ActionCode==53){
			playerhand.changeCardSort();
			sortBtn.goToAndStop((sortBtn.getCurrentFrame()==0)?1:0);
		}
	
		//If msgblock is showing, disable other buttons
		if(!resultb.isHidden() || gameover)	
			return;
		
		if(ActionCode==50){		//Clicked on "View Board" button
			/*if(!viewboard){
				gamehelp.goToAndStop(3);
				gamehelp.setHidden(false);
			}*/
			viewing=!viewing;
			viewboardbtn.goToAndStop((viewing?1:0));
			return;
		}
			
		if(ActionCode==12){		//User clicked on "Finish" button
			if(viewing)
				return;
			if(placing)
				return;
			ss.PlaySound(S.BtnPressed, 0);
			finishSelectCard();
		}
	}

	@Override
	protected void doTouchAction(int ActionCode, int X, int Y){
		if(!resultb.isHidden())	//If msgblock is showing, disable other buttons
			return;
		
		if(!window.isHidden()){
			window.handleTouch(X, Y);
			return;
		}
		
		switch(ActionCode){
		case 129:
			if(placing)
				return;
			if(playerhand.handleSelection(X,Y))
				ss.PlaySound(O.S.SelectCard, 0);
			break;
		case 136:
			int i=gob.getCell(X, Y);
			if(viewing){
				/*if(!viewboard){
					viewboard=true;
					gamehelp.setHidden(true);
				}*/
				AbsHands[] h=layer.getCardSlot(i);
				if(h!=null){
					if(h[0].getSide()==turn || layer.getCoinSlot(i)!=0x0 || gameover)
						resultb.showResult(i, false);
				}
				return;
			}
			if(placing)
				placeCard(i);
			break;
		}
	}

	@Override
	protected void doKeyAction(int KeyCode) {
		// TODO Auto-generated method stub
		if(KeyCode==KeyEvent.KEYCODE_BACK){
			/*if(!gamehelp.isHidden()){
				gamehelp.setHidden(true);
				return;
			}*/
			if(!window.isHidden())
				return;
			window.setMsg(R.string.ask_savegame, DialogStyle.OKCancel, winCallback);					
			//DisplayAlert(R.string.ask_savegame, this);
		}
	}

	/*@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		if(which==DialogInterface.BUTTON_POSITIVE)
			exitCleanUp();
		else
			resultb.setActioncode(25);
	}*/

	@Override
	protected void SecneChangedCallBack() {
		// TODO Auto-generated method stub
		
	}
}