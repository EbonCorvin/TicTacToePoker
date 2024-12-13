package com.eboncorvin.tttp.Engine;


import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundSystem {
	private static boolean isenabled=true;
	private static SoundSystem ss;
	
	private int[] SoundResID=new int[]{
		R.raw.coindrop,
		R.raw.btnpressed,
		R.raw.comparewin,
		R.raw.comparelose,
		R.raw.selectcard,
	};

	private SoundPool sp=new SoundPool(5,AudioManager.STREAM_MUSIC,0);

	private int[] SoundID;

	public static void initSoundSystem(Context c){
		ss=new SoundSystem(c);
	}

	public static SoundSystem getSoundSystem(){
		return ss;
	}

	public static void setEnabled(boolean enabled) {
		isenabled = enabled;
	}

	public static void unload(){
		for(int si:ss.SoundID){
			ss.sp.unload(si);
		}
		ss.sp.release();
		Misc.output("Sound system is unloaded!");
	}

	private SoundSystem(Context context){
		if(sp==null){
			isenabled=false;
			return;
		}		
		isenabled=Setting.getSetting().EnableSound;
		int count=0;
		SoundID=new int[SoundResID.length];
		for(int sri:SoundResID){
			SoundID[count]=sp.load(context, sri, 1);
			count++;
		}
	}
	
	public void PlaySound(int ID, int LoopCount){
		if(!isenabled)
			return;
		sp.play(SoundID[ID], 1, 1, 5, LoopCount, 1);
	}
	
	public void PauseResumeSound(int StreamID){
		
	}
}
