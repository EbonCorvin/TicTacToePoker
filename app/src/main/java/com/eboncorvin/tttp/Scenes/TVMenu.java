package com.eboncorvin.tttp.Scenes;


import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.Msger;
import com.eboncorvin.tttp.O;
import com.eboncorvin.tttp.R;
import com.eboncorvin.tttp.Engine.GameMode;
import com.eboncorvin.tttp.Engine.GameSave;
import com.eboncorvin.tttp.Engine.Setting;
import com.eboncorvin.tttp.Engine.SoundSystem;
import com.eboncorvin.tttp.Engine.StatManager;
import com.eboncorvin.tttp.GameObjects.GOMsgWindow;
import com.eboncorvin.tttp.GameObjects.GOStat;
import com.eboncorvin.tttp.GameObjects.GameObject;
import com.eboncorvin.tttp.GameObjects.GOMsgWindow.DialogStyle;
import com.eboncorvin.tttp.GameObjects.GOMsgWindow.MsgButton;
import com.eboncorvin.tttp.O.C;
import com.eboncorvin.tttp.O.S;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

public class TVMenu extends TouchView implements DialogInterface.OnClickListener{
	private int sceneID=C.Menu, mode=0, coinType;
	private Setting gs;
	/**
	 * This msger connects with ActEntry
	 */
	private Msger cth;
	private SoundSystem ss;
	private boolean tutorial=false, whoGo1st=true;
	private GameObject soundswitch=new GameObject(R.drawable.sound,	-1, 0, 650, 30, 127);
	private GameObjectAdapter objlist=new GameObjectAdapter();
	private GOMsgWindow window=new GOMsgWindow();
	
	private GOMsgWindow.ButtonCallback windowCallback=new GOMsgWindow.ButtonCallback(){
		@Override
		public void callbackAction(int messageID, MsgButton button) {
			// TODO Auto-generated method stub
			Misc.output(messageID);
			switch(messageID){
			case R.string.ask_exit:
				if(button==MsgButton.OK)
					cth.run(C.exitGame);
				break;
			case R.string.msg_loadsave:
				if(button==MsgButton.Cancel){
					Misc.output("Should display confirm delete");
					window.setMsg(R.string.msg_deleteornot, DialogStyle.OKCancel, this);
				} else {
					EnterGame();
				}
				break;
			case R.string.msg_deleteornot:
				if(button==MsgButton.OK){
					GameSave.deleteSave();
					window.setHidden(true);
					StatManager.writeWinLose(0, false);
					mode=0;
					sceneID=10;
					ChangeScene(0, C.Start);
				}
				break;
			}

		}
	};

	public TVMenu(Context context, Msger cth) {
		super(context, C.Menu);
		this.setAdapter(objlist);
		ss=SoundSystem.getSoundSystem();
		gs=this.getGameSetting();
		this.cth=cth;
		SecneChangedCallBack();
		coinType=gs.Color?0:1;
	}

	private void displaySetting(){
		if(this.isChangingSecne())
			return;
		soundswitch.goToAndStop(gs.EnableSound?0:1);
		if(this.sceneID!=O.C.Option)
			return;
		objlist.get(4).setObjectPos(300+10+((gs.EnableSound)?0:150), -1);
		objlist.get(5).setObjectPos(300+10+((gs.size)?0:150), -1);
		objlist.get(6).setObjectPos(300+10+((gs.Color)?0:150), -1);
		objlist.get(4).goToAndStop(gs.Color?0:1);
		objlist.get(5).goToAndStop(gs.Color?0:1);
		objlist.get(6).goToAndStop(gs.Color?0:1);
	}

	private void EnterGame(){
		cth.run(C.showMainView, new GameMode(tutorial, whoGo1st, mode));
	}

	private void startGameClicked(int ac){
		if(ac==30){
			sceneID=C.Menu;
			ChangeScene(1, C.Menu);
			return;
		}
		if(ac==8){
			cth.run(C.showBluetoothView);
			//cth.run(C.showBluetoothView);
			//Do something to connect to  server
			return;
		}
		if(ac==0){
			if(GameSave.isSaveExist()){
				window.setMsg(R.string.msg_loadsave, DialogStyle.OKCancel
						, windowCallback);
				return;
			}
		}
		if(ac==9){
			mode=9;
			//whoGo1st=false;
			EnterGame();
			return;
		}
		mode=ac;
		sceneID=10;
		ChangeScene(0, C.Start);
	}
	
	private boolean optionClicked(int ac){
		switch(ac){
			case 5:
				gs.EnableSound=!gs.EnableSound;
				break;
			case 6:
				gs.size=!gs.size;
				break;
			case 7:
				gs.Color=!gs.Color;
				break;
			case 30:
				Setting.setSetting(gs);
				coinType=gs.Color?0:1;
				sceneID=C.Menu;
				ChangeScene(3, C.Menu);
				return false;
		}
		displaySetting();
		ss.PlaySound(O.S.CoinDrop, 0);
		return true;
	}
	
	private boolean gameConfigClicked(int code){
		switch(code){
		case 6:
			whoGo1st=!whoGo1st;
			break;
		case 7:
			tutorial=!tutorial;
			break;
		case 8:
			sceneID=30;
			ChangeScene(0, 4);
			return false;
		}
		objlist.get(5).setObjectPos(whoGo1st?310:475, -1);
		objlist.get(6).setObjectPos(tutorial?310:475, -1);
		ss.PlaySound(S.CoinDrop, 0);
		return true;
	}
	
	@Override
	protected void doTouchAction(int ActionCode) {
		if(ActionCode==127){
			gs.EnableSound=!gs.EnableSound;
			Setting.setSetting(gs);
			displaySetting();
			return;
		}
		switch(sceneID){
			case C.Menu:		//Menu
				if(ActionCode==76){
					int cur=Setting.getLanguage();
					Setting.setLanguage((cur==0)?1:0);
					cth.run(C.showMenuView);
					return;
				}
				if(ActionCode==30){
					window.setMsg(R.string.ask_exit, 
							GOMsgWindow.DialogStyle.OKCancel, windowCallback);
					return;
				}
				sceneID=ActionCode;
				ChangeScene(ActionCode, ActionCode);
				break;
			case C.Start:		//Start
				startGameClicked(ActionCode);
				break;
			case C.Option:		//Option
				if(optionClicked(ActionCode))
					return;
				break;
			case C.Stat:		//Stat
				if(ActionCode==60){
					((GOStat)objlist.get(0)).switchView();
					return;
				}
				sceneID=C.Menu;
				ChangeScene(2, C.Menu);
				break;
			case C.About:		//About
				if(ActionCode==30){
					sceneID=C.Menu;
					ChangeScene(0, C.Menu);
				}
				break;
			case 10:		//Game Config
				if(ActionCode==30){
					sceneID=C.Start;
					ChangeScene(1, C.Start);
					break;
				}
				if(gameConfigClicked(ActionCode))
					return;
				break;
		}
		ss.PlaySound(O.S.BtnPressed, 0);
	}

	@Override
	protected void doTouchAction(int ActionCode, int X, int Y) {
		if(ActionCode==234)
			window.handleTouch(X, Y);
	}

	@Override
	protected void doKeyAction(int KeyCode) {
		// TODO Auto-generated method stub
		if(KeyCode==KeyEvent.KEYCODE_BACK){
			if(!window.isHidden())
				return;
			doTouchAction(30);
		}
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		if(arg1==DialogInterface.BUTTON_POSITIVE)
			cth.run(C.exitGame);
	}
	@Override
	protected void SecneChangedCallBack() {
		// TODO Auto-generated method stub
		switch(sceneID){
			case C.Menu:		//Menu
				objlist.add(R.drawable.testtitle, 		-1,	0, 185, 81, -1);
				objlist.add(R.drawable.menuitem, 	-1, 3, 400, 150, C.Stat); //Stat
				objlist.add(R.drawable.menuitem, 	-1, 6, 550, 200, C.About); //About
				objlist.add(R.drawable.menuitem, 	-1,	0, 100, 150, C.Start); //Start
				objlist.add(R.drawable.menuitem, 	-1, 2, 250, 200, C.Option); //Option
				objlist.add(R.drawable.language,	-1, 0, 650, 80, 76); 
				/*GOMsgWindow gmw=new GOMsgWindow();
				objlist.add(gmw);
				gmw.setMsg(R.string.ifo_loadingerror);*/
				//objlist.add((I.language,		-1, 0, 650, 400, 123);
				break;
			case C.Start:		//Start Game
				objlist.add(R.drawable.gamemode,	-1, 0, 100, 175, 0);
				objlist.add(R.drawable.gamemode,	-1, 1, 220, 175, 1);
				//objlist.add((I.modeselect,	-1, 2, 340, 175, 2);
				objlist.add(R.drawable.gamemode,	-1, 3, 340, 175, 8);
				objlist.add(R.drawable.gamemode,	-1, 4, 460, 175, 9);				
				objlist.add(R.drawable.menuitem,	-1, 5, 610, 200, 30);
				break;
			case C.Option:		//Option
				objlist.add(R.drawable.configtable,		-1, 0, 100, 100, 5);
				objlist.add(R.drawable.configtable,		-1, 1, 100, 163, 6);
				objlist.add(R.drawable.configtable,		-1, 2, 100, 226, 7);
				//objlist.add(R.drawable.configtable,		-1, 3, 100, 289, 8);
				objlist.add(R.drawable.menuitem,		-1, 5, 610, 200, 30);
				objlist.add(R.drawable.coin,			-1, coinType, 470, 110, -1);
				objlist.add(R.drawable.coin,			-1, coinType, 470, 173, -1);
				objlist.add(R.drawable.coin,			-1, coinType, 470, 236, -1);		
				displaySetting();
				break;
			case C.Stat:		//Stat.
				objlist.add(new GOStat());
				objlist.add(R.drawable.menuitem,	-1, 5, 610, 200, 30);
				break;
			case C.About:		//About Us
				objlist.add(R.drawable.aboutpage,		-1, 0, 100, 40, -1);
				objlist.add(R.drawable.menuitem,	-1, 5, 610, 200, 30);
				break;
			case 30:
				EnterGame();
				break;
			case 10:	//Game Config
				objlist.add(R.drawable.gamesetting,	-1, 0, 100, 70, 5);	//Mode
				objlist.add(R.drawable.gamesetting,	-1, 1, 100, 133, 6);	//Order
				objlist.add(R.drawable.gamesetting,	-1, 3, 100, 196, 7);	//Tutorial
				//objlist.add(R.drawable.gamemode,	-1, 0, 100, 226, 6); 
				objlist.add(R.drawable.gamesetting,	-1, 2, 100, 293, 8);
				objlist.add(R.drawable.coin,			-1, coinType, 310, 80, -1);
				objlist.add(R.drawable.coin,			-1, coinType, 310, 143, -1);
				objlist.add(R.drawable.coin,			-1, coinType, 475, 206, -1);
				objlist.add(R.drawable.menuitem,	-1, 5, 610, 200, 30);
				break;
			case 94:
				break;
		}
		objlist.add(window);
		objlist.add(soundswitch);
		displaySetting();
	}
}
