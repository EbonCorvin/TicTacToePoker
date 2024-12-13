package com.eboncorvin.tttp;

public final class O {
	/**
	 * Constants of image code
	 */
	/*public class I{	//Image
		public final static int Title=0;
		public final static int Menubuttons=1;
		public final static int GamingTable=2;
		public final static int Coin=3;
		
		public final static int ConfigTable=4;
		public final static int AboutPage=5;
		public final static int GameMode=6;
		public final static int XOMark=7;

		public final static int tttboard=8;
		public final static int CardMark=9;
		public final static int CardBody=10;
		public final static int Deck=11;
		
		public final static int Carpet=12;
		//final static int AboutTable=13;
		public final static int modeselect=13;
		public final static int CardSuit=14;
		public final static int Empty=15;
		
		public final static int MsgBlock=16;
		public final static int GameBtn=17;
		public final static int TurnStep=18;
		public final static int Arrow=19;
		
		public final static int Result=20;
		public final static int InGameBg=21;
		public final static int helpbtn=22;
		public final static int gamehelp=23;
		
		public final static int language=24;
		public final static int soundswitch=25;
		public final static int mwbg=26;
	}*/
	
	/**
	 * Constants of sound code
	 */
	public class S{	//Sound
		public final static int CoinDrop=0;
		public final static int BtnPressed=1;
		public final static int CompareWin=2;
		public final static int CompareLose=3;
		public final static int SelectCard=4;
	}
	
	/**
	 * Constant of achievement
	 */
	public class A{ //Achievement
		public final static int stPlay=0;		//The player finished a game at the 1st time
		public final static int stPlayWithFd=1;	//The player finished a game with fd at the 1st time
		public final static int stWin=2;		//The player won a game at the 1st time
	}
	
	public class C{	//Constant
		//Message (Code passes by Msger)
		/**
		 * Show a Message to the user<br/>
		 * Parameter: int MsgID <br/>
		 * 0=Pass phone, 1=Making move, 2=Placing card <br/>
		 * 3=Slot used, 4=Need Select Card
		 */		
		public final static int showMessage=0;
		public final static int showPassPhone=1;
		public final static int changePhaseIndicator=2;
		public final static int hideMessage=3;
		public final static int displayCardMark=4;
		public final static int isPlacing=5;
		public final static int isNotPlacing=6;
		/**
		 * Place the hands into the specified slot.<br/>
		 * Please note that setCard2Placec(AbsHands) should be called before send this msg.<br/>
		 * Parameter: int SlotNo
		 */
		public final static int placeCard=7;
		/**
		 * Show an unclosable Message to the user<br/>
		 * Parameter: int MsgID <br/>
		 * 0=Pass phone, 1=Making move, 2=Placing card
		 * <br/>3=Slot used, 4=Need Select Card
		 */
		public final static int showUnclosableMsg=8;
		/**
		 * This message is for Hot-seat mode
		 */
		public final static int swapCoin=9;
		/**
		 * Load the main game view.<br/>
		 * Parameter: Object[]{int Mode, boolean Tutorial, boolean WhoGo1st}
		 */
		public final static int showMainView=75;
		public final static int showMenuView=76;
		public final static int initBluetooth=77;
		public final static int showBluetoothView=78;
		public final static int gameLoadingError=999;
		public final static int exitGame=79;
		
		//Socket related constant
		public final static int socketError=99;
		public final static int socketConnected=20;
		public final static int socketReceived=30;
		
		
		//Control
		public final static int playerTurn=50;

		//Setting of the game
		public final static int CoinMode=0;
		public final static int TTTMode=1;
		
		/**
		 * Show a GameObject at the top of the screen, the attachment should be a GameObject
		 */
		public final static int showGameObject=100;
		/**
		 * Remove a GameObject from the screen, the attachment should be a GameObject
		 */
		public final static int removeGameObject=101;
		
		//Menu item arrange
		/**
		 * Menu Scene ID: About
		 */
		public final static int About=1;
		/**
		 * Menu Scene ID: Game Option
		 */
		public final static int Start=0;
		/**
		 * Menu Scene ID: Option
		 */		
		public final static int Option=2;
		/**
		 * Menu Scene ID: Statistic
		 */				
		public final static int Stat=3;
		/**
		 * Menu Scene ID: Startup menu
		 */	
		public final static int Menu=4;
		
	}

	//public static float density=0;
	public static float scaleW=0;
	public static float scaleH=0;
	public static int screenW=0;
	public static int screenH=0;	
}
