package com.eboncorvin.tttp;

import java.util.Locale;

import com.eboncorvin.tttp.R;
import com.eboncorvin.tttp.Engine.GameMode;
import com.eboncorvin.tttp.Engine.GameSave;
import com.eboncorvin.tttp.Engine.Setting;
import com.eboncorvin.tttp.Engine.SoundSystem;
import com.eboncorvin.tttp.Engine.StatManager;
import com.eboncorvin.tttp.GameObjects.GOMsgWindow;
import com.eboncorvin.tttp.GameObjects.GOStat;
import com.eboncorvin.tttp.GameObjects.ImgPreload;
import com.eboncorvin.tttp.GameObjects.ImgPremake;
import com.eboncorvin.tttp.Msger.MsgCallBack;
import com.eboncorvin.tttp.O.C;
import com.eboncorvin.tttp.Scenes.TVMain;
import com.eboncorvin.tttp.Scenes.TVMenu;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.TextView;

public class ActEntry extends Activity implements MsgCallBack, Runnable{
	private Msger cth=new Msger(this);
	private int loadtype=2;
	private boolean error=false;
	//Game parameter
	private GameMode tmp;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        GameSave.setCacheDir(getCacheDir());
        Setting.setSharedPreferences(getSharedPreferences("tttp",0));
        new Thread(this).start();
    }
    
    @Override
	public void run() {
    	Misc.output("Loading..."+loadtype);
		Looper.prepare();
		long startloadtime=System.currentTimeMillis();
		try{ 
			Resources r=getResources();
			Configuration c=new Configuration();
			switch(Setting.getLanguage()){
			case -1:
				break;
			case 0:
				c.locale=Locale.CHINESE;
				break;
			case 1:
				c.locale=Locale.ENGLISH;
			}
			r.updateConfiguration(c, r.getDisplayMetrics());
			//2 = initialize, 0 = menu view, 1 = main view
			switch(loadtype){
			case 2:
				StatManager.initDB(this);
				ImgPremake.initVar();
				SoundSystem.initSoundSystem(this);
			case 0:
				GOStat.initPairName(r);
				ImgPreload.unloadImage();
				ImgPremake.unloadImage();
				ImgPreload.PrepareObject(r, 0);
				GOMsgWindow.init(r);
				cth.run(100);
				break;
			case 1:
				ImgPreload.unloadImage();
				ImgPreload.PrepareObject(r, 1);
		        ImgPremake.makeCard();
		        ImgPremake.makeCardMark();
		        GOMsgWindow.init(r);
		        cth.run(101);
				break;
			}
	        Misc.output("Time taken to load: "+(System.currentTimeMillis()-startloadtime));
	    } catch(Throwable e){
	    	error=true;
	    	Misc.output("Loading error: "+e.toString());
	    	e.printStackTrace();
	    	cth.run(C.gameLoadingError, R.string.ifo_loadingerror);
	    }
	}
    
    @Override
	public void doMsg(int StatusCode, Object Attachment) {
		// TODO Auto-generated method stub
		switch(StatusCode){	
		case C.showMainView:
			loadtype=1;
			tmp=(GameMode)Attachment;
			setContentView(R.layout.loading);
			new Thread(this).start();
			break;
		case C.showMenuView:
			loadtype=0;
			setContentView(R.layout.loading);
			new Thread(this).start();
			break;
		case C.showBluetoothView:
			Intent i=new Intent("game.poker.tttp.WifiConnect");
			//startActivity(i);
			startActivityForResult(i, 99);
			break;
		case C.gameLoadingError:
			TextView t=(TextView) findViewById(R.id.textView1);
			t.setText((Integer)Attachment);
			error=true;
			break;
		case C.exitGame:
			finish();
			break;
		case 100:
			/*FrameLayout f=new FrameLayout(this);
			TextView text = new TextView(this);
			ScrollView s=new ScrollView(this);
        text.setTextSize(20);
        text.setText("lovehui");
        text.setHeight(30);
        text.setWidth(100);
        s.addView(text);
        f.addView(new TVMenu(this, cth);
        f.addView(s);*/
			setContentView(new TVMenu(this, cth));
			break;
		case 101:
			setContentView(new TVMain(this, cth, C.CoinMode, tmp));
			break;
		}
	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==99){
			if(resultCode==RESULT_OK)
				doMsg(C.showMainView,new GameMode(false, 
						data.getBooleanExtra("game.poker.tttp.isServer", false), 2));
		}
	}

	@Override
	public void onDestroy(){
		if(this.isFinishing())
			startExitGame();
		super.onDestroy();
	}

	public void startExitGame() {
		StatManager.clearUp();
		ImgPreload.unloadImage();
		ImgPremake.unloadImage();
		SoundSystem.unload();
		GOMsgWindow.exitCleanup();
		System.gc();
		Misc.output("All unloaded");
	}
	
	@Override
	public boolean onKeyUp (int keyCode, KeyEvent event){
		if(error)
			finish();
		return true;
	}
}