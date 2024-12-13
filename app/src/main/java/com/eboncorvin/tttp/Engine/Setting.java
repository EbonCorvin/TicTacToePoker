package com.eboncorvin.tttp.Engine;

import android.content.SharedPreferences;

public class Setting {
	private static SharedPreferences sp;
	private static Setting gs;
	
	public boolean EnableSound;
	public boolean size;
	public boolean Color;
	//public String CPUName;
	public int Language;
	
	public static void setSharedPreferences(SharedPreferences sp) {
		Setting.sp = sp;
		getSetting();
	}

	public static Setting getSetting(){
		gs=new Setting();
		gs.EnableSound=sp.getBoolean("Sound", true);
		gs.size=sp.getBoolean("Size", true);
		gs.Color=sp.getBoolean("Color", true);
		//gs.CPUName=sp.getString("CPUName", "Mario");
		gs.Language=sp.getInt("Language", -1);
		return gs;
	}
	
	public static int getLanguage(){
		return gs.Language;
	}
	
	public static void setLanguage(int locale){
		SharedPreferences.Editor e=sp.edit();
		e.putInt("Language", locale);
		e.commit();
		getSetting();
	}
	
	public static void setSetting(Setting gs){
		SharedPreferences.Editor e=sp.edit();
		e.putBoolean("Sound", gs.EnableSound);
		e.putBoolean("Size", gs.size);
		e.putBoolean("Color", gs.Color);
		//e.putString("CPUName", gs.CPUName);
		e.commit();
		SoundSystem.setEnabled(gs.EnableSound);
		getSetting();
	}
}
