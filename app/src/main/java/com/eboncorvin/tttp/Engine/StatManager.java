package com.eboncorvin.tttp.Engine;

import com.eboncorvin.tttp.Misc;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StatManager extends SQLiteOpenHelper{
	private SQLiteDatabase cachedb;
	private static final String dbname="gamestat.db";
	private static final int dbversion=2;
	private static StatManager sm;
	private static int[] stat;
	private static int[][] stat2;

	public static void initDB(Context context){
		sm=new StatManager(context);
	}
	
	public static void clearUp(){
		if(sm!=null){
			sm.cachedb.close();
			Misc.output("Database closed!");
		}
	}
	
	public StatManager(Context context){
		super(context,dbname,null,dbversion);
		cachedb=this.getWritableDatabase();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
			db.execSQL("Create Table pairstat" +
					"(typeid NUMBER PRIMARY KEY, int NUMBER);");
			
			db.execSQL("Create Table winlose" +
					"(typeid NUMBER PRIMARY KEY, Win NUMBER, Lose NUMBER);");
			for(int i=0;i<9;i++){
				db.execSQL("Insert into pairstat values" +
						"("+i+", 0);");
			}
			for(int i=0;i<3;i++){
				db.execSQL("Insert into winlose values" +
						"("+i+", 0, 0);");
			}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(oldVersion!=1)
			return;
		db.execSQL("Create Table achievement" +
					"(typeid NUMBER PRIMARY KEY, Progress NUMBER, DateTime TEXT);");
	}
	
	public static int[] readPairStat(){
		Cursor data=sm.cachedb.rawQuery("Select * From pairstat", null);
		if(data.getCount()!=0){
			data.moveToFirst();
			stat=new int[9];
			for(int i=0;i<9;i++){
				stat[i]=data.getInt(1);
				data.moveToNext();
			}
			data.close();
			return stat;
		}
		return null;
	}
	
	public static void writePairStat(int[] re){
		if(stat==null)
			readPairStat();
		for(int i=0;i<9;i++){
			ContentValues v=new ContentValues();
			v.put("int", stat[i]+re[i]);
			sm.cachedb.update("pairstat", v, "typeid="+i, null);
		}
	}
	
	public static int[][] readWinLose(){
		Cursor data=sm.cachedb.rawQuery("Select * From winlose", null);
		if(data.getCount()!=0){
			data.moveToFirst();
			stat2=new int[3][];
			for(int i=0;i<3;i++){
				stat2[i]=new int[2];
				stat2[i][0]=data.getInt(1);
				stat2[i][1]=data.getInt(2);
				data.moveToNext();
			}
			data.close();
			return stat2;
		}
		return null;
	}
	
	public static void writeWinLose(int opptype, boolean win){
		if(stat2==null)
			readWinLose();
		ContentValues v=new ContentValues();
		v.put((win?"Win":"Lose"), ++stat2[opptype][(win?0:1)]);
		sm.cachedb.update("winlose", v, "typeid="+opptype, null);
	}
}
